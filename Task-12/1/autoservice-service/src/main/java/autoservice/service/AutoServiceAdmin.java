package autoservice.service;

import autoservice.config.AppConfig;
import autoservice.di.annotation.Inject;
import autoservice.dto.CarServiceMastersQuery;
import autoservice.dto.RepairOrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortCarServiceMasters;
import autoservice.enums.SortRepairOrders;
import autoservice.exception.InvalidDateException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.MasterRepository;
import autoservice.repository.OrderRepository;
import autoservice.repository.TransactionalOrderRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.repository.impl.RepairOrderRepository;
import autoservice.utils.csv.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class AutoServiceAdmin {
    @Inject
    private MasterRepository masterRepository;
    @Inject
    private WorkshopPlaceRepository workshopPlaceRepository;
    @Inject
    private OrderRepository<RepairOrder> repairOrderRepository;
    @Inject
    private AppConfig appConfig;
    private final RepairOrderRepository deletedOrdersRepository = new RepairOrderRepository();

    public AutoServiceAdmin() {
    }

    private List<RepairOrder> repairOrdersFilter(OrderRepository<RepairOrder> repairOrders, RepairOrderQuery repairOrderQuery) {
        return repairOrders.getAllOrders().stream()
                .filter(o -> repairOrderQuery.carServiceMaster() == null
                        || o.getCarServiceMaster().equals(repairOrderQuery.carServiceMaster()))
                .filter(o -> repairOrderQuery.workshopPlace() == null
                        || o.getWorkshopPlace().equals(repairOrderQuery.workshopPlace()))
                .filter(o -> repairOrderQuery.status() == null
                        || o.getStatus().equals(repairOrderQuery.status()))
                .filter(o -> repairOrderQuery.startDate() == null
                        || !o.getStartDate().isBefore(repairOrderQuery.startDate()))
                .filter(o -> repairOrderQuery.endDate() == null
                        || !o.getEndDate().isAfter(repairOrderQuery.endDate()))
                .toList();
    }

    private boolean areOrdersIsConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        return !curOrder.getEndDate().isBefore(nextOrder.getStartDate())
                && (curOrder.getWorkshopPlace().equals(nextOrder.getWorkshopPlace())
               || curOrder.getCarServiceMaster().equals(nextOrder.getCarServiceMaster()));
    }

    private void resolveScheduleConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        Period period = (Period.between(nextOrder.getStartDate(), curOrder.getEndDate())).plus(Period.ofDays(1));
        nextOrder.setStartDate(nextOrder.getStartDate().plus(period));
        nextOrder.setEndDate(nextOrder.getEndDate().plus(period));
    }

    private boolean isDateInRange(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }

    private void updateOrderAssociations(RepairOrder targetOrder, RepairOrder csvOrder,
                                         Map<UUID, CarServiceMaster> mastersById,
                                         Map<UUID, WorkshopPlace> workshopPlaces) {
        if (csvOrder.getCarServiceMaster() != null) {
            CarServiceMaster master = mastersById.get(csvOrder.getCarServiceMaster().getId());
            targetOrder.setCarServiceMaster(master);
        }
        if (csvOrder.getWorkshopPlace() != null) {
            WorkshopPlace place = workshopPlaces.get(csvOrder.getWorkshopPlace().getId());
            targetOrder.setWorkshopPlace(place);
        }
    }

    public void addMaster(CarServiceMaster master) {
        masterRepository.addMaster(master);
    }

    public void removeMaster(CarServiceMaster master) {
        masterRepository.removeMaster(master);
    }

    /**
     * @param place новое рабочее место
     * @throws OperationNotAllowedException если в конфигурации установлен запрет добавления рабочего места
     */
    public void addWorkshopPlace(WorkshopPlace place) {
        if (!appConfig.isCanAddPlaces())
            throw new OperationNotAllowedException("Добавление рабочих мест запрещено");
        workshopPlaceRepository.addPlace(place);
    }

    /**
     * @param place рабочее место, которое нужно удалить
     * @throws OperationNotAllowedException если в конфигурации установлен запрет удаления рабочего места
     */
    public void removePlace(WorkshopPlace place) {
        if (!appConfig.isCanRemovePlaces())
            throw new OperationNotAllowedException("Удаление рабочих мест запрещено");
        workshopPlaceRepository.removePlace(place);
    }

    public UUID createRepairOrder(LocalDate creationDate, LocalDate start, LocalDate end,
                                  String description, CarServiceMaster master, WorkshopPlace place) {
        if (start.isBefore(LocalDate.now()))
            throw new InvalidDateException("Дата начала не может быть в прошлом: " + start);
        if (end.isBefore(start))
            throw new InvalidDateException("Дата окончания не может быть раньше начала: " + end);

        RepairOrder order = new RepairOrder(creationDate, start, end, description);
        order.setCarServiceMaster(master);
        order.setWorkshopPlace(place);
        repairOrderRepository.addOrder(order);
        return order.getId();
    }

    /**
     * Отменяет заказ
     *
     * @param orderId ID заказа для отмены
     * @throws OrderNotFoundException если заказ не найден
     */
    public void cancelOrder(UUID orderId) {
        RepairOrder order = getOrderById(orderId);
        repairOrderRepository.cancelOrder(order);
    }

    /**
     * Завершает заказ
     *
     * @param orderId ID заказа для завершения
     * @throws OrderNotFoundException если заказ не найден
     */
    public void closedOrder(UUID orderId) {
        RepairOrder order = getOrderById(orderId);
        repairOrderRepository.closeOrder(order);
    }

    /**
     * Убирает заказ из основного репозитория в репозиторий удаленных заказов
     *
     * @param orderId ID заказа для завершения
     * @throws OrderNotFoundException       если заказ не найден
     * @throws OperationNotAllowedException если в конфигурации установлен запрет удаления заказов
     */
    public void removeOrder(UUID orderId) {
        if (!appConfig.isCanDeleteOrders())
            throw new OperationNotAllowedException("Удаление заказов запрещено");
        RepairOrder order = getOrderById(orderId);
        deletedOrdersRepository.addOrder(order);
        repairOrderRepository.removeOrder(order);
    }

    /**
     * Смещает дату окончания заказа на указанный период
     *
     * @param orderId ID заказа для завершения
     * @param period  период, на который нужно перенести заказ
     * @throws OrderNotFoundException       если заказ не найден
     * @throws OperationNotAllowedException если в конфигурации установлен запрет смещения времени заказа
     */
    public void delayOrder(UUID orderId, Period period) {
        if (!appConfig.isCanDelayOrders())
            throw new OperationNotAllowedException("Смещение времени заказа запрещено");
        RepairOrder modifiedOrder = getOrderById(orderId);
        List<RepairOrder> orders = getRepairOrders(RepairOrderQuery.builder()
                .status(OrderStatus.CREATED)
                .startDate(modifiedOrder.getStartDate())
                .carServiceMaster((CarServiceMaster) modifiedOrder.getCarServiceMaster())
                .workshopPlace(modifiedOrder.getWorkshopPlace())
                .sortOrders(SortRepairOrders.START_DATE)
                .build());

        if (orders.isEmpty()) return;

        RepairOrder curOrder = orders.get(0);
        curOrder.setEndDate(modifiedOrder.getEndDate().plus(period));
        List<RepairOrder> ordersToUpdate = new ArrayList<>();
        ordersToUpdate.add(curOrder);

        for (int i = 0; i + 1 < orders.size(); ++i) {
            RepairOrder nextOrder = orders.get(i + 1);
            if (areOrdersIsConflict(curOrder, nextOrder)) {
                resolveScheduleConflict(curOrder, nextOrder);
                ordersToUpdate.add(nextOrder);
                curOrder = nextOrder;
            } else break;
        }


        if(repairOrderRepository instanceof TransactionalOrderRepository){
            boolean success = ((TransactionalOrderRepository<RepairOrder>) repairOrderRepository)
                    .updateOrdersInTransaction(ordersToUpdate);
            if (!success){
                throw  new RuntimeException("Ошибка: Не удалось перенести заказы.");
            }
        } else {
            for (RepairOrder order : ordersToUpdate){
                repairOrderRepository.updateOrder(order);
            }
        }
    }

    /**
     * Находит заказ по ID
     *
     * @param orderId ID заказа
     * @return найденный заказ
     * @throws OrderNotFoundException если заказ не найден
     */
    public RepairOrder getOrderById(UUID orderId) {
        Optional<RepairOrder> order = repairOrderRepository.getOrderById(orderId);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Заказ не найден: " + orderId);
        }
        return order.get();
    }

    public List<RepairOrder> getRepairOrders(RepairOrderQuery repairOrderQuery) {
        List<RepairOrder> orders = new ArrayList<>();
        if (repairOrderQuery.isRemoved() == null) {
            orders.addAll(repairOrdersFilter(repairOrderRepository, repairOrderQuery));
            orders.addAll(repairOrdersFilter(deletedOrdersRepository, repairOrderQuery));
        } else if (repairOrderQuery.isRemoved()) {
            orders.addAll(repairOrdersFilter(deletedOrdersRepository, repairOrderQuery));
        } else {
            orders.addAll(repairOrdersFilter(repairOrderRepository, repairOrderQuery));
        }
        if (repairOrderQuery.sortRepairOrders() != null) {
            orders.sort(repairOrderQuery.sortRepairOrders().getComparator());
        }
        return orders;
    }

    private List<CarServiceMaster> findOccupiedMasters(CarServiceMastersQuery carServiceMastersQuery) {
        List<CarServiceMaster> masters = new ArrayList<>();
        List<RepairOrder> orders = repairOrderRepository.getAllOrders();
        for (RepairOrder order : orders) {
            if (isDateInRange(carServiceMastersQuery.localDate(), order.getStartDate(), order.getEndDate())) {
                masters.add((CarServiceMaster) order.getCarServiceMaster());
            }
        }
        return masters;
    }

    private List<CarServiceMaster> findAvailableMasters(CarServiceMastersQuery carServiceMastersQuery) {
        List<RepairOrder> orders = getRepairOrders(RepairOrderQuery.builder()
                .status(OrderStatus.CREATED)
                .build());
        List<CarServiceMaster> masters = masterRepository.getAllMasters();
        for (RepairOrder order : orders) {
            if (isDateInRange(carServiceMastersQuery.localDate(), order.getStartDate(), order.getEndDate())) {
                masters.remove((CarServiceMaster) order.getCarServiceMaster());
            }
        }
        return masters;
    }

    public List<CarServiceMaster> getCarServiceMasters() {
        List<CarServiceMaster> masters = new ArrayList<>(masterRepository.getAllMasters());
        masters.sort(Comparator.comparing(CarServiceMaster::getFullName));
        return masters;
    }

    public List<CarServiceMaster> getCarServiceMasters(CarServiceMastersQuery carServiceMastersQuery) {
        Objects.requireNonNull(carServiceMastersQuery, "carServiceMastersQuery cannot be null");
        List<CarServiceMaster> masters;
        if (carServiceMastersQuery.isOccupied() != null)
            masters = carServiceMastersQuery.isOccupied() ? findOccupiedMasters(carServiceMastersQuery)
                    : findAvailableMasters(carServiceMastersQuery);
        else masters = masterRepository.getAllMasters();

        if (carServiceMastersQuery.sort() != null)
            masters.sort(carServiceMastersQuery.sort().getComparator());
        else masters.sort(Comparator.comparing(CarServiceMaster::getFullName));

        return masters;
    }

    /**
     * Находит мастера по назначенного на заказ
     *
     * @param orderId ID заказа
     * @return Optional с мастером, если мастер назначен, иначе empty
     * @throws OrderNotFoundException если заказ не найден
     */
    public Optional<CarServiceMaster> getMasterByOrderId(UUID orderId) {
        RepairOrder order = getOrderById(orderId);
        CarServiceMaster master = (CarServiceMaster) order.getCarServiceMaster();
        return (master != null) ? Optional.of(master) : Optional.empty();
    }

    public List<WorkshopPlace> getAvailablePlaces(LocalDate localDate) {
        List<WorkshopPlace> availablePlaces = workshopPlaceRepository.getAllPlaces();
        List<RepairOrder> orders = getRepairOrders(RepairOrderQuery.builder()
                .status(OrderStatus.CREATED)
                .build());
        for (RepairOrder order : orders) {
            if (isDateInRange(localDate, order.getStartDate(), order.getEndDate())) {
                availablePlaces.remove(order.getWorkshopPlace());
            }
        }
        return availablePlaces;
    }

    public int countAvailablePlaces(LocalDate date) {
        int countPlaces = getAvailablePlaces(date).size();
        int countMasters = (getCarServiceMasters(CarServiceMastersQuery.builder().
                localDate(date)
                .isOccupied(false)
                .sort(SortCarServiceMasters.NAME)
                .build()))
                .size();
        return Math.min(countPlaces, countMasters);
    }

    public Optional<LocalDate> getFirstAvailableSlot(LocalDate date) {
        LocalDate endDate = date.plusDays(7);
        while (date.isBefore(endDate)) {
            if (countAvailablePlaces(date) > 0) return Optional.of(date);
            date = date.plusDays(1);
        }
        return Optional.empty();
    }

    public void importMastersFromCsv(Path filePath) throws IOException {
        List<CarServiceMaster> masters = CsvImporter.importFromCsv(
                filePath, MasterCsvHelper.fieldsToMaster);
        Map<UUID, CarServiceMaster> mastersById = masterRepository.getAllMasters().stream()
                .collect(Collectors.toMap(CarServiceMaster::getId, m -> m));

        for (CarServiceMaster master : masters) {
            CarServiceMaster existing = mastersById.get(master.getId());

            if (existing != null) {
                existing.setFullName(master.getFullName());
                existing.setDateOfBirth(master.getDateOfBirth());
            } else {
                masterRepository.addMaster(master);
            }
        }
    }

    public void exportMastersToCsv(Path filePath) throws IOException {
        CsvExporter.exportToCsv(
                masterRepository.getAllMasters(),
                filePath,
                MasterCsvHelper.masterToFields);
    }

    public void importPlacesFromCsv(Path filePath) throws IOException {
        List<WorkshopPlace> places = CsvImporter.importFromCsv(
                filePath, PlaceCsvHelper.fieldsToPlace);

        for (WorkshopPlace place : places) {
            WorkshopPlace existing = workshopPlaceRepository.findByName(place.getName());
            if (existing != null && existing.getId().equals(place.getId())) {
                existing.setName(place.getName());
            } else {
                workshopPlaceRepository.addPlace(place);
            }
        }
    }

    public void exportPlacesToCsv(Path filePath) throws IOException {
        CsvExporter.exportToCsv(
                workshopPlaceRepository.getAllPlaces(),
                filePath,
                PlaceCsvHelper.placeToFields);
    }

    public void importOrdersFromCsv(Path filePath) throws IOException {
        List<RepairOrder> importFromCsv = CsvImporter.importFromCsv(
                filePath, OrderCsvHelper.fieldsToOrder);
        Map<UUID, CarServiceMaster> mastersById = masterRepository.getAllMasters().stream()
                .collect(Collectors.toMap(CarServiceMaster::getId, m -> m));
        Map<UUID, WorkshopPlace> workshopPlaces = workshopPlaceRepository.getAllPlaces().stream()
                .collect(Collectors.toMap(WorkshopPlace::getId, p -> p));

        for (RepairOrder csvOrder : importFromCsv) {
            Optional<RepairOrder> existing = repairOrderRepository.getOrderById(csvOrder.getId());
            if (existing.isPresent()) {
                RepairOrder existingOrder = existing.get();
                existingOrder.setStartDate(csvOrder.getStartDate());
                existingOrder.setEndDate(csvOrder.getEndDate());
                existingOrder.setDescription(csvOrder.getDescription());
                existingOrder.setTotalPrice(csvOrder.getTotalPrice());
                // обновление связей для существующего заказа
                updateOrderAssociations(existingOrder, csvOrder, mastersById, workshopPlaces);
            } else {
                // связи для новых заказов
                updateOrderAssociations(csvOrder, csvOrder, mastersById, workshopPlaces);
                repairOrderRepository.addOrder(csvOrder);
            }
        }
    }

    public void exportOrdersToCsv(Path filePath) throws IOException {
        CsvExporter.exportToCsv(
                repairOrderRepository.getAllOrders(),
                filePath,
                OrderCsvHelper.orderToFields);
    }


}





















