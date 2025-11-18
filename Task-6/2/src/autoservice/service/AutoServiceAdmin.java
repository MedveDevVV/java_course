package autoservice.service;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.dto.RepairOrderQuery;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.MasterRepository;
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
    private final MasterRepository masterRepository;
    private final WorkshopPlaceRepository workshopPlaceRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final RepairOrderRepository deletedOrdersRepository = new RepairOrderRepository();


    public AutoServiceAdmin(MasterRepository masterRepository, WorkshopPlaceRepository workshopPlaceRepository,
                            RepairOrderRepository repairOrderRepository) {
        this.masterRepository = masterRepository;
        this.workshopPlaceRepository = workshopPlaceRepository;
        this.repairOrderRepository = repairOrderRepository;
    }

    private List<RepairOrder> repairOrdersFilter(RepairOrderRepository repairOrders, RepairOrderQuery repairOrderQuery) {
        return repairOrders.getAllOrders().stream()
                .map(o -> (RepairOrder) o)
                .filter(o -> repairOrderQuery.carServiceMaster() == null
                        || o.getAssignPerson().equals(repairOrderQuery.carServiceMaster()))
                .filter(o -> repairOrderQuery.status() == null
                        || o.getStatus().equals(repairOrderQuery.status()))
                .filter(o -> repairOrderQuery.startDate() == null
                        || o.getStartDate().isAfter(repairOrderQuery.startDate()))
                .filter(o -> repairOrderQuery.endDate() == null
                        || o.getEndDate().isBefore(repairOrderQuery.endDate()))
                .toList();
    }

    private boolean isDateInRange(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }

    private void updateOrderAssociations(RepairOrder targetOrder, RepairOrder csvOrder,
                                         Map<UUID, CarServiceMaster> mastersById,
                                         Map<UUID, WorkshopPlace> workshopPlaces) {
        if (csvOrder.getAssignPerson() != null) {
            CarServiceMaster master = mastersById.get(csvOrder.getAssignPerson().getId());
            targetOrder.assignPerson(master);
        }
        if (csvOrder.getWorkshopPlace() != null) {
            WorkshopPlace place =  workshopPlaces.get(csvOrder.getWorkshopPlace().getId());
            targetOrder.setWorkshopPlace(place);
        }
    }

    public void addMaster(CarServiceMaster master) {
        masterRepository.addMaster(master);
    }

    public void removeMaster(CarServiceMaster master) {
        masterRepository.removeMaster(master);
    }

    public void addWorkshopPlace(WorkshopPlace place) {
        workshopPlaceRepository.addPlace(place);
    }

    public void removePlace(WorkshopPlace place) {
        workshopPlaceRepository.removePlace(place);
    }

    public UUID createRepairOrder(LocalDate creationDate, LocalDate start, LocalDate end,
                                  String description, CarServiceMaster master, WorkshopPlace place) {
        RepairOrder order = new RepairOrder(creationDate, start, end, description);
        order.assignPerson(master);
        order.setWorkshopPlace(place);
        repairOrderRepository.addOrder(order);
        return order.getId();
    }

    public void cancelOrder(UUID orderId) {
        RepairOrder order = repairOrderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        repairOrderRepository.cancelOrder(order);
    }

    public void closedOrder(UUID orderId) {
        RepairOrder order = repairOrderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        repairOrderRepository.closeOrder(order);
    }

    public void removeOrder(UUID orderId) {
        RepairOrder order = repairOrderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        deletedOrdersRepository.addOrder(order);
        repairOrderRepository.removeOrder(order);
    }

    public void delayOrder(UUID orderId, Period period) {
        RepairOrder order = repairOrderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        order.setEndDate(order.getEndDate().plus(period));
        repairOrderRepository.updateOrder(order);
    }

    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return repairOrderRepository.getOrderById(orderId);
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
                masters.add((CarServiceMaster) order.getAssignPerson());
            }
        }
        return masters;
    }

    private List<CarServiceMaster> findAvailableMasters(CarServiceMastersQuery carServiceMastersQuery) {
        List<RepairOrder> orders = repairOrderRepository.getAllOrders();
        List<CarServiceMaster> masters = masterRepository.getAllMasters();
        for (RepairOrder order : orders) {
            if (isDateInRange(carServiceMastersQuery.localDate(), order.getStartDate(), order.getEndDate())) {
                masters.remove((CarServiceMaster) order.getAssignPerson());
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

    public Optional<CarServiceMaster> getMasterByOrder(UUID orderId) {
        RepairOrder order = repairOrderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return Optional.empty();
        return Optional.ofNullable((CarServiceMaster) order.getAssignPerson());
    }

    public List<WorkshopPlace> getAvailablePlaces(LocalDate localDate) {
        List<WorkshopPlace> availablePlaces = workshopPlaceRepository.getAllPlaces();
        List<RepairOrder> orders = repairOrderRepository.getAllOrders();
        for (RepairOrder order : orders) {
            if (isDateInRange(localDate, order.getStartDate(), order.getEndDate())) {
                availablePlaces.remove(((RepairOrder) order).getWorkshopPlace());
            }
        }
        return availablePlaces;
    }

    public int countAvailablePlaces(LocalDate date) {
        int countPlaces = getAvailablePlaces(date).size();
        int countMasters = (getCarServiceMasters(CarServiceMastersQuery.builder().
                localDate(date)
                .isOccupied(false)
                .build()))
                .size();
        return Math.min(countPlaces, countMasters);
    }

    public Optional<LocalDate> getFirstAvailableSlot(LocalDate date) {
        int countAvailable = 0;
        LocalDate endDate = date.plusDays(7);
        while (date.isBefore(endDate)) {
            countAvailable = countAvailablePlaces(date);
            if (countAvailable > 0) return Optional.of(date);
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
                updateOrderAssociations(existingOrder,csvOrder,mastersById,workshopPlaces);
            } else {
                // связи для новых заказов
                updateOrderAssociations(csvOrder,csvOrder,mastersById,workshopPlaces);
                repairOrderRepository.addOrder(csvOrder);
            }
        }
    }

    public void exportOrdersToCsv(Path filePath) throws IOException {
        CsvExporter.exportToCsv(
                repairOrderRepository.getAllOrders().stream()
                        .map(o -> (RepairOrder) o)
                        .collect(Collectors.toList()),
                filePath,
                OrderCsvHelper.orderToFields);
    }
}





















