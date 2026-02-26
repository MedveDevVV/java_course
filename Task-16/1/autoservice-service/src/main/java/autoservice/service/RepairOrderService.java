package autoservice.service;

import autoservice.config.AppConfig;
import autoservice.dto.RepairOrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortRepairOrders;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.OrderRepository;
import autoservice.repository.impl.RepairOrderRepository;
import autoservice.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairOrderService {
    private final AppConfig appConfig;
    private final OrderRepository<RepairOrder> repairOrderRepository;
    private final RepairOrderRepository deletedOrdersRepository = new RepairOrderRepository();

    // ========== CRUD операции ==========

    public RepairOrder findById(UUID id) {
        log.debug("Поиск заказа по ID: {}", id);
        return repairOrderRepository.getOrderById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<RepairOrder> getAllOrders() {
        log.debug("Получение всех заказов");
        return repairOrderRepository.getAllOrders();
    }

    public List<RepairOrder> findOrdersByFilter(RepairOrderQuery query) {
        log.debug("Получение заказов по запросу: {}", query);

        List<RepairOrder> result = new ArrayList<>();
        if (query.isRemoved() == null) {
            result.addAll(getActiveOrders(query));
            result.addAll(getDeletedOrders(query));
        } else if (query.isRemoved()) {
            result.addAll(getDeletedOrders(query));
        } else {
            result.addAll(getActiveOrders(query));
        }
        if (query.sortRepairOrders() != null) {
            result.sort(query.sortRepairOrders().getComparator());
        }
        return result;
    }

    @Transactional
    public RepairOrder createOrder(LocalDate creationDate, LocalDate start, LocalDate end,
                                   String description, CarServiceMaster master, WorkshopPlace place) {
        log.info("Создание нового заказа: мастер={}, место={}, даты={}-{}",
                master.getFullName(), place.getName(), start, end);

        DateUtils.validateDateRange(start, end);

        RepairOrder order = new RepairOrder(creationDate, start, end, description);
        order.setCarServiceMaster(master);
        order.setWorkshopPlace(place);

        repairOrderRepository.addOrder(order);
        return order;
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        log.info("Отмена заказа с ID: {}", orderId);
        RepairOrder order = findById(orderId);
        repairOrderRepository.cancelOrder(order);
    }

    @Transactional
    public void closeOrder(UUID orderId) {
        log.info("Закрытие заказа с ID: {}", orderId);
        RepairOrder order = findById(orderId);
        repairOrderRepository.closeOrder(order);
    }

    @Transactional
    public void removeOrder(UUID orderId) {
        if (!appConfig.isCanDeleteOrders()) {
            log.warn("Попытка удаления заказа с ID {} запрещена конфигурацией", orderId);
            throw new OperationNotAllowedException("Удаление заказов запрещено");
        }

        log.info("Удаление заказа с ID: {}", orderId);
        RepairOrder order = findById(orderId);
        deletedOrdersRepository.addOrder(order);
        repairOrderRepository.removeOrder(order);
    }

    // ========== Специальные операции ==========

    /**
     * Переносит заказ с автоматическим разрешением конфликтов расписания
     */
    @Transactional
    public void delayOrder(UUID orderId, Period period) {
        if (!appConfig.isCanDelayOrders()) {
            log.warn("Попытка переноса заказа с ID {} запрещена конфигурацией", orderId);
            throw new OperationNotAllowedException("Смещение времени заказа запрещено");
        }

        log.info("Перенос заказа с ID: {} на {} дней", orderId, period.getDays());

        RepairOrder modifiedOrder = findById(orderId);
        RepairOrderQuery query = RepairOrderQuery.builder()
                .status(OrderStatus.CREATED)
                .startDate(modifiedOrder.getStartDate())
                .carServiceMaster(modifiedOrder.getCarServiceMaster())
                .workshopPlace(modifiedOrder.getWorkshopPlace())
                .sortOrders(SortRepairOrders.START_DATE)
                .build();

        List<RepairOrder> orders = getActiveOrders(query);

        if (orders.isEmpty()) {
            return;
        }

        RepairOrder current = orders.get(0);
        current.setEndDate(modifiedOrder.getEndDate().plus(period));
        List<RepairOrder> ordersToUpdate = new ArrayList<>();
        ordersToUpdate.add(current);

        for (int i = 0; i + 1 < orders.size(); ++i) {
            RepairOrder next = orders.get(i + 1);
            if (hasScheduleConflict(current, next)) {
                resolveScheduleConflict(current, next);
                ordersToUpdate.add(next);
                current = next;
            } else {
                break;
            }
        }

        for (RepairOrder order : ordersToUpdate) {
            repairOrderRepository.updateOrder(order);
        }
    }

    public List<CarServiceMaster> findOccupiedMastersOnDate(LocalDate date) {
        return repairOrderRepository.findMastersWithCreatedOrdersOnDate(date);
    }

    public List<RepairOrder> findCreatedOrdersByDate(LocalDate date) {
        return repairOrderRepository.findCreatedOrdersByDate(date);
    }

    // ========== Вспомогательные методы ==========

    private List<RepairOrder> getActiveOrders(RepairOrderQuery query) {
        return filterOrders(repairOrderRepository, query);
    }

    private List<RepairOrder> getDeletedOrders(RepairOrderQuery query) {
        return filterOrders(deletedOrdersRepository, query);
    }

    private List<RepairOrder> filterOrders(OrderRepository<RepairOrder> repository,
                                           RepairOrderQuery query) {
        return repository.getAllOrders().stream()
                .filter(o -> query.carServiceMaster() == null
                        || o.getCarServiceMaster().equals(query.carServiceMaster()))
                .filter(o -> query.workshopPlace() == null
                        || o.getWorkshopPlace().equals(query.workshopPlace()))
                .filter(o -> query.status() == null
                        || o.getStatus().equals(query.status()))
                .filter(o -> query.startDate() == null
                        || !o.getStartDate().isBefore(query.startDate()))
                .filter(o -> query.endDate() == null
                        || !o.getEndDate().isAfter(query.endDate()))
                .toList();
    }

    private boolean hasScheduleConflict(RepairOrder current, RepairOrder next) {
        return !current.getEndDate().isBefore(next.getStartDate())
                && (current.getWorkshopPlace().equals(next.getWorkshopPlace())
                || current.getCarServiceMaster().equals(next.getCarServiceMaster()));
    }

    private void resolveScheduleConflict(RepairOrder current, RepairOrder next) {
        Period period = (Period.between(next.getStartDate(), current.getEndDate())).plus(Period.ofDays(1));
        next.setStartDate(next.getStartDate().plus(period));
        next.setEndDate(next.getEndDate().plus(period));
    }
}