package autoservice.repository;

import autoservice.model.Order;

import java.util.List;

public interface TransactionalOrderRepository<T extends Order> {
    boolean updateOrdersInTransaction(List<T> orders);
}

