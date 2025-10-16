import java.time.Duration;
import java.util.*;

public class RepairOrderRepository implements OrderRepository {
    private final List<RepairOrder> orders = new ArrayList<>();

    @Override
    public void addOrder(Order order) {
        orders.add((RepairOrder) order);
    }

    @Override
    public void updateOrder(Order modifiedOrder) {
        RepairOrder curOrder = (RepairOrder) modifiedOrder;
        RepairOrder nextOrder;
        Duration duration;
        orders.sort(Comparator.comparing(Order::getStartTime));
        int curIndex = orders.indexOf((RepairOrder) curOrder);

        while (curIndex + 1 < orders.size()) {
            nextOrder = orders.get(curIndex + 1);
            if (curOrder.getEndTime().isAfter(nextOrder.getStartTime())
                    && (curOrder.getWorkshopPlace() == nextOrder.getWorkshopPlace()
                    || curOrder.getAssignPerson() == nextOrder.getAssignPerson())) {
                duration = Duration.between(nextOrder.getStartTime(), curOrder.getEndTime());
                curOrder = nextOrder;
                curIndex += 1;
                curOrder.setStartDateTime(curOrder.getStartTime().plus(duration));
                curOrder.setEndDateTime(curOrder.getEndTime().plus(duration));
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
