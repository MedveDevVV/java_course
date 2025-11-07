package autoservice.repository;

import autoservice.model.Order;
import autoservice.model.RepairOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository <T extends Order> {
    void addOrder(T order);
    void updateOrder(T order);
    public Optional<T> getOrderById(UUID orderId);
    void removeOrder(T order);
    void cancelOrder(T order);
    void closeOrder(T order);
    public List<T> getAllOrders();
}