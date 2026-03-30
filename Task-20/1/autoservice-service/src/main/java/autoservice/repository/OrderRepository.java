package autoservice.repository;

import autoservice.dto.RepairOrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.model.CarServiceMaster;
import autoservice.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository <T extends Order> {
    void addOrder(T order);
    void updateOrder(T order);
    Optional<T> getOrderById(UUID orderId);
    void removeOrder(UUID orderId);
    void cancelOrder(T order);
    void closeOrder(T order);
    List<T> getAllOrders();
    List<T> findByStatus(OrderStatus status);
    List<T> findByCarServiceMasterId(UUID masterId);
    List<T> findByStatusAndCarServiceMaster(OrderStatus status, CarServiceMaster master);
    List<T> findCreatedOrdersByDate(LocalDate date);
    List<CarServiceMaster> findMastersWithCreatedOrdersOnDate(LocalDate date);
    List<T> findOrders(RepairOrderQuery query);
}