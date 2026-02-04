package autoservice.repository.impl;

import autoservice.dao.RepairOrderDAO;
import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;
import autoservice.repository.TransactionalOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatabaseOrderRepository implements OrderRepository<RepairOrder>, TransactionalOrderRepository<RepairOrder> {
    private final RepairOrderDAO orderDAO;

    public DatabaseOrderRepository() {
        this.orderDAO = RepairOrderDAO.getINSTANCE();
    }

    @Override
    public List<RepairOrder> getAllOrders() {
        return orderDAO.findAll();
    }

    @Override
    public void addOrder(RepairOrder order) {
        orderDAO.save(order);
    }

    @Override
    public void updateOrder(RepairOrder modifiedOrder) {
        orderDAO.update(modifiedOrder);
    }

    @Override
    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return orderDAO.findById(orderId);
    }

    @Override
    public void removeOrder(RepairOrder order) {
        orderDAO.delete(order);
    }

    @Override
    public void cancelOrder(RepairOrder order) {
        order.cancel();
        orderDAO.update(order);
    }

    @Override
    public void closeOrder(RepairOrder order) {
        order.closed();
        orderDAO.update(order);
    }

    @Override
    public boolean updateOrdersInTransaction(List<RepairOrder> orders){
        return orderDAO.updateOrdersInTransaction(orders);
    }
}
