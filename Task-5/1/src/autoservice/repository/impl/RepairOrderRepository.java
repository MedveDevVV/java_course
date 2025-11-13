package autoservice.repository.impl;

import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;

import java.time.Period;
import java.util.*;

public class RepairOrderRepository implements OrderRepository<RepairOrder> {
    private final List<RepairOrder> orders = new ArrayList<>();

    private boolean areOrdersIsConflict(RepairOrder curOrder, RepairOrder nextOrder) {
        return !curOrder.getEndDate().isBefore(nextOrder.getStartDate())
                && (curOrder.getWorkshopPlace() == nextOrder.getWorkshopPlace()
                || curOrder.getAssignPerson() == nextOrder.getAssignPerson());
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
        return  orders.stream().filter(o -> o.getId().equals(orderId)).findFirst().map(RepairOrder::new);
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
}
