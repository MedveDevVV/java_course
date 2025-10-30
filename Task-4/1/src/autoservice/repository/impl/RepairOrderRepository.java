package autoservice.repository.impl;

import autoservice.model.Order;
import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;

import java.time.Period;
import java.util.*;

public class RepairOrderRepository implements OrderRepository {
    private final List<RepairOrder> orders = new ArrayList<>();

    private boolean areOrdersIsConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        return !curOrder.getEndDate().isBefore(nextOrder.getStartDate())
                && (curOrder.getWorkshopPlace() == nextOrder.getWorkshopPlace()
                || curOrder.getAssignPerson() == nextOrder.getAssignPerson());
    }

    @Override
    public void addOrder(Order order) {
        orders.add((RepairOrder) order);
    }

    @Override
    public void updateOrder(Order modifiedOrder) {
        RepairOrder curOrder = (RepairOrder) modifiedOrder;
        RepairOrder nextOrder;
        Period period;
        orders.sort(Comparator.comparing(Order::getStartDate));
        int curIndex = orders.indexOf(curOrder);

        while (curIndex + 1 < orders.size()) {
            nextOrder = orders.get(curIndex + 1);
            if (areOrdersIsConflict(curOrder, nextOrder)) {
                period = (Period.between(nextOrder.getStartDate(), curOrder.getEndDate())).plus(Period.ofDays(1));
                curOrder = nextOrder;
                curIndex += 1;
                curOrder.setStartDate(curOrder.getStartDate().plus(period));
                curOrder.setEndDate(curOrder.getEndDate().plus(period));
            } else break;
        }
    }

    @Override
    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return orders.stream().filter(o -> o.getId().equals(orderId)).findFirst();
    }

    @Override
    public void removeOrder(Order order) {
        orders.remove((RepairOrder) order);
    }

    @Override
    public void cancelOrder(Order order) {
        orders.get(orders.indexOf((RepairOrder) order)).cancel();
    }

    @Override
    public void closeOrder(Order order) {
        orders.get(orders.indexOf((RepairOrder) order)).closed();
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}
