package autoservice.service;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.dto.CreateRepairOrderRequest;
import autoservice.dto.RepairOrderQuery;
import autoservice.dto.SearchRepairOrderRequest;
import autoservice.exception.MasterNotAssignedException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceFacade {

    private final CarServiceMasterService masterService;
    private final WorkshopPlaceService placeService;
    private final RepairOrderService orderService;

    // ========== Методы управления заказами ==========

    @Transactional
    public RepairOrder createOrder(CreateRepairOrderRequest request) {
        log.debug("Создание заказа через фасад: masterId={}, placeId={}", request.masterId(), request.workshopPlaceId());
        CarServiceMaster master = masterService.findById(request.masterId());
        WorkshopPlace place = placeService.findById(request.workshopPlaceId());

        return orderService.createOrder(LocalDate.now(), request.startDate(), request.endDate(),
                request.description(), master, place);
    }

    public List<RepairOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    public RepairOrder getOrderById(UUID id) {
        return orderService.findById(id);
    }

    public List<RepairOrder> searchOrders(SearchRepairOrderRequest request) {
        log.info("Поиск заказов с фильтрами: статус={}, мастер={}, дата с={}, по={}",
                request.status(), request.masterId(), request.startDate(), request.endDate());

        CarServiceMaster master = null;
        if (request.masterId() != null) {
            master = masterService.findById(request.masterId());
        }

        RepairOrderQuery query = RepairOrderQuery.builder()
                .status(request.status())
                .carServiceMaster(master)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .sortOrders(request.sortBy())
                .isRemoved(request.isRemoved())
                .build();

        return orderService.findOrdersByFilter(query);
    }

    @Transactional
    public void cancelOrder(UUID id) {
        orderService.cancelOrder(id);
    }

    @Transactional
    public void closeOrder(UUID id) {
        orderService.closeOrder(id);
    }

    @Transactional
    public void delayOrder(UUID id, int days) {
        orderService.delayOrder(id, Period.ofDays(days));
    }

    @Transactional
    public void deleteOrder(UUID id) {
        orderService.removeOrder(id);
    }

    // ========== Методы работы с мастерами ==========

    public CarServiceMaster getMasterByOrderId(UUID orderId) {
        log.debug("Получение мастера по ID заказа: {}", orderId);
        RepairOrder order = orderService.findById(orderId);
        if (order.getCarServiceMaster() == null) {
            throw new MasterNotAssignedException(orderId);
        }
        return order.getCarServiceMaster();
    }

    public List<CarServiceMaster> getCarServiceMasters(CarServiceMastersQuery query) {
        log.debug("Получение мастеров по запросу: {}", query);
        Objects.requireNonNull(query, "carServiceMastersQuery cannot be null");
        List<CarServiceMaster> masters;
        if (query.isOccupied() != null)
            masters = query.isOccupied() ? orderService.findOccupiedMastersOnDate(query.localDate())
                    : findAvailableMastersOnDate(query.localDate());
        else masters = masterService.getAllMasters();

        if (query.sort() != null)
            masters.sort(query.sort().getComparator());
        else masters.sort(Comparator.comparing(CarServiceMaster::getFullName));

        return masters;
    }

    public List<CarServiceMaster> findAvailableMastersOnDate(LocalDate date) {
        log.debug("Получение доступных мастеров на дату: {}", date);
        List<CarServiceMaster> masters = masterService.getAllMasters();
        masters.removeAll(orderService.findOccupiedMastersOnDate(date));
        return masters;
    }

    // ========== Методы работы с рабочими местами ==========

    public List<WorkshopPlace> getAvailablePlacesOnDate(LocalDate date) {
        log.debug("Получение доступных рабочих мест на дату: {}", date);
        List<WorkshopPlace> availablePlaces = placeService.getAllPlaces();
        List<RepairOrder> orders = orderService.findCreatedOrdersByDate(date);

        for (RepairOrder order : orders) {
            availablePlaces.remove(order.getWorkshopPlace());
        }
        return availablePlaces;
    }

    public int countAvailablePlacesOnDate(LocalDate date) {
        log.debug("Подсчет доступных мест на дату: {}", date);
        int availablePlaces = getAvailablePlacesOnDate(date).size();
        int availableMasters = findAvailableMastersOnDate(date).size();
        return Math.min(availablePlaces, availableMasters);
    }

    // ========== Методы поиска доступных слотов ==========

    public Optional<LocalDate> getNextAvailableSlot(LocalDate fromDate) {
        log.info("Поиск следующего доступного слота начиная с даты: {}", fromDate);
        LocalDate searchDate = (fromDate != null) ? fromDate : LocalDate.now();
        return getFirstAvailableSlot(searchDate);
    }

    public Optional<LocalDate> getFirstAvailableSlot(LocalDate date) {
        log.debug("Поиск первого доступного слота с даты: {}", date);
        LocalDate endDate = date.plusDays(7);
        while (date.isBefore(endDate)) {
            if (countAvailablePlacesOnDate(date) > 0) {
                log.debug("Найден первый доступный слот: {}", date);
                return Optional.of(date);
            }
            date = date.plusDays(1);
        }
        log.debug("Доступный слот не найден в течение 7 дней");
        return Optional.empty();
    }
}
