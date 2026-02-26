package autoservice.repository.impl;

import autoservice.enums.OrderStatus;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RepairOrderRepository implements OrderRepository<RepairOrder>, Serializable {
    @Serial
    private static final long serialVersionUID = 8001L;
    private List<RepairOrder> orders = new ArrayList<>();

    private boolean areOrdersIsConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        return !curOrder.getEndDate().isBefore(nextOrder.getStartDate())
                && (curOrder.getWorkshopPlace() == nextOrder.getWorkshopPlace()
                || curOrder.getCarServiceMaster() == nextOrder.getCarServiceMaster());
    }

    private void resolveScheduleConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        Period period = (Period.between(nextOrder.getStartDate(), curOrder.getEndDate())).plus(Period.ofDays(1));
        nextOrder.setStartDate(nextOrder.getStartDate().plus(period));
        nextOrder.setEndDate(nextOrder.getEndDate().plus(period));
    }

    @Override
    public void addOrder(RepairOrder order) {
        orders.add(order);
    }

    @Override
    public void updateOrder(RepairOrder modifiedOrder) {
        orders.get(orders.indexOf(modifiedOrder)).setEndDate(modifiedOrder.getEndDate());
        RepairOrder curOrder = modifiedOrder;
        orders.sort(Comparator.comparing(RepairOrder::getStartDate));

        for (int i = orders.indexOf(curOrder); i + 1 < orders.size(); ++i) {
            RepairOrder nextOrder = orders.get(i + 1);
            if (areOrdersIsConflict(curOrder, nextOrder)) {
                resolveScheduleConflict(curOrder, nextOrder);
                curOrder = nextOrder;
            } else break;
        }
    }

    @Override
    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return  orders.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .map(RepairOrder::new);
    }

    @Override
    public void removeOrder(RepairOrder order) {
        orders.remove(order);
    }

    @Override
    public void cancelOrder(RepairOrder order) {
        orders.get(orders.indexOf(order)).cancel();
    }

    @Override
    public void closeOrder(RepairOrder order) {
        orders.get(orders.indexOf(order)).closed();
    }

    @Override
    public List<RepairOrder> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public void setAllOrders(List<RepairOrder> orders) {
        this.orders = new ArrayList<>(orders);
    }

    public List<RepairOrder> findByStatus(OrderStatus status) {
        return orders.stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<RepairOrder> findByCarServiceMasterId(UUID masterId) {
        return orders.stream()
                .filter(o -> o.getCarServiceMaster() != null
                        && o.getCarServiceMaster().getId().equals(masterId))
                .collect(Collectors.toList());
    }

    public List<RepairOrder> findByStatusAndCarServiceMaster(OrderStatus status, CarServiceMaster master) {
        return orders.stream()
                .filter(o -> o.getStatus() == status
                        && o.getCarServiceMaster() != null
                        && o.getCarServiceMaster().equals(master))
                .collect(Collectors.toList());
    }

    public List<RepairOrder> findCreatedOrdersByDate(LocalDate date) {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CREATED)
                .filter(o -> !date.isBefore(o.getStartDate()) && !date.isAfter(o.getEndDate()))
                .collect(Collectors.toList());
    }

    public List<CarServiceMaster> findMastersWithCreatedOrdersOnDate(LocalDate date) {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CREATED)
                .filter(o -> !date.isBefore(o.getStartDate()) && !date.isAfter(o.getEndDate()))
                .map(RepairOrder::getCarServiceMaster)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
