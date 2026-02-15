package autoservice.repository.impl;

import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;
import autoservice.repository.TransactionalOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaOrderRepository extends AbstractJpaRepository
        implements OrderRepository<RepairOrder>, TransactionalOrderRepository<RepairOrder> {
    @Override
    public void addOrder(RepairOrder order) {
        executeInTransaction(em -> {
            if (order.getCarServiceMaster() != null) {
                order.setCarServiceMaster(em.merge(order.getCarServiceMaster()));
            }
            if (order.getWorkshopPlace() != null) {
                order.setWorkshopPlace(em.merge(order.getWorkshopPlace()));
            }
            em.persist(order);
        });
        logger.info("Добавлен заказ: {}", order.getId());
    }

    @Override
    public void updateOrder(RepairOrder order) {
        executeInTransaction(em -> {
            RepairOrder managedOrder = em.merge(order);
            if (order.getCarServiceMaster() != null) {
                managedOrder.setCarServiceMaster(em.merge(order.getCarServiceMaster()));
            }
            if (order.getWorkshopPlace() != null) {
                managedOrder.setWorkshopPlace(em.merge(order.getWorkshopPlace()));
            }
        });
        logger.info("Обновлен заказ: {}", order.getId());
    }

    @Override
    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return executeWithResult(em ->
                Optional.ofNullable(em.find(RepairOrder.class, orderId))
        );
    }

    @Override
    public void removeOrder(RepairOrder order) {
        executeInTransaction(em ->
                em.createQuery("delete from RepairOrder ro where ro.id = :id")
                        .setParameter("id", order.getId())
                        .executeUpdate()
        );
        logger.info("Удален заказ: {}", order.getId());
    }

    @Override
    public void cancelOrder(RepairOrder order) {
        executeInTransaction(em ->
                em.createQuery("update RepairOrder ro set ro.status = 'CANCELLED' where ro.id = :id")
                        .setParameter("id", order.getId())
                        .executeUpdate()
        );
        logger.info("Отменен заказ: {}", order.getId());
    }

    @Override
    public void closeOrder(RepairOrder order) {
        executeInTransaction(em ->
                em.createQuery("UPDATE RepairOrder ro set ro.status = 'CLOSED' where ro.id =:id")
                        .setParameter("id", order.getId())
        );
        logger.info("Завершен заказ: {}", order.getId());
    }

    @Override
    public List<RepairOrder> getAllOrders() {
        return executeWithResult(em ->
                em.createQuery("select ro from RepairOrder ro left join fetch ro.carServiceMaster " +
                                "left join fetch ro.place order by ro.startDate", RepairOrder.class)
                        .getResultList()
        );
    }

    @Override
    public boolean updateOrdersInTransaction(List<RepairOrder> orders) {
        return executeWithResultInTransaction(em -> {
            for (RepairOrder order : orders) {
                em.createQuery("update RepairOrder ro set ro.startDate = :startDate, " +
                                "ro.endDate = :endDate where ro.id = :id")
                        .setParameter("startDate", order.getStartDate())
                        .setParameter("endDate", order.getEndDate())
                        .setParameter("id", order.getId())
                        .executeUpdate();
            }
            return true;
        });
    }
}
