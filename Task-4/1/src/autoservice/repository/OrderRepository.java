package autoservice.repository;

import autoservice.model.Order;
import autoservice.model.RepairOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void addOrder(Order order);
    void updateOrder(Order order);
    public Optional<RepairOrder> getOrderById(UUID orderId);
    void removeOrder(Order order);
    void cancelOrder(Order order);
    void closeOrder(Order order);
    List<Order> getAllOrders();
}