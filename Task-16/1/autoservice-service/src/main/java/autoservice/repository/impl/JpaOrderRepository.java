package autoservice.repository.impl;

import autoservice.enums.OrderStatus;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.repository.OrderRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaOrderRepository extends AbstractJpaRepository
        implements OrderRepository<RepairOrder> {
    public JpaOrderRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void addOrder(RepairOrder order) {
        execute("Добавление заказа",
                em -> {
                    if (order.getCarServiceMaster() != null) {
                        order.setCarServiceMaster(em.merge(order.getCarServiceMaster()));
                    }
                    if (order.getWorkshopPlace() != null) {
                        order.setWorkshopPlace(em.merge(order.getWorkshopPlace()));
                    }
                    em.persist(order);
                },
                order.getId()
        );
    }

    @Override
    public void updateOrder(RepairOrder order) {
        execute("Обновление заказа",
                em -> {
                    RepairOrder managedOrder = em.merge(order);
                    if (order.getCarServiceMaster() != null) {
                        managedOrder.setCarServiceMaster(em.merge(order.getCarServiceMaster()));
                    }
                    if (order.getWorkshopPlace() != null) {
                        managedOrder.setWorkshopPlace(em.merge(order.getWorkshopPlace()));
                    }
                },
                order.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return executeWithResult("Получение заказа по ID",
                em ->
                        Optional.ofNullable(em.find(RepairOrder.class, orderId)),
                orderId.toString()
        );
    }

    @Override
    public void removeOrder(RepairOrder order) {
        execute("Удаление заказа",
                em ->
                        em.createQuery("delete from RepairOrder ro where ro.id = :id")
                                .setParameter("id", order.getId())
                                .executeUpdate(),
                order.getId()
        );
    }

    @Override
    public void cancelOrder(RepairOrder order) {
        execute("Отмена заказа",
                em ->
                        em.createQuery("update RepairOrder ro set ro.status = 'CANCELLED' where ro.id = :id")
                                .setParameter("id", order.getId())
                                .executeUpdate(),
                order.getId()
        );
    }

    @Override
    public void closeOrder(RepairOrder order) {
        execute("Завершение заказа",
                em ->
                        em.createQuery("UPDATE RepairOrder ro set ro.status = 'CLOSED' where ro.id =:id")
                                .setParameter("id", order.getId())
                                .executeUpdate(),
                order.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getAllOrders() {
        return executeWithResult("Получение всех заказов",
                em ->
                        em.createQuery("select ro from RepairOrder ro left join fetch ro.carServiceMaster " +
                                        "left join fetch ro.place order by ro.startDate", RepairOrder.class)
                                .getResultList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> findByStatus(OrderStatus status) {
        return executeWithResult("Поиск заказов по статусу",
                em -> em.createQuery("SELECT o FROM RepairOrder o WHERE o.status = :status", RepairOrder.class)
                        .setParameter("status", status)
                        .getResultList(),
                status
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> findByCarServiceMasterId(UUID masterId) {
        return executeWithResult("Поиск заказов по мастеру",
                em -> em.createQuery("SELECT o FROM RepairOrder o WHERE o.carServiceMaster.id = :masterId", RepairOrder.class)
                        .setParameter("masterId", masterId)
                        .getResultList(),
                masterId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> findByStatusAndCarServiceMaster(OrderStatus status, CarServiceMaster master) {
        return executeWithResult("Поиск заказов по статусу и мастеру",
                em -> em.createQuery("SELECT o FROM RepairOrder o WHERE o.status = :status AND o.carServiceMaster = :master",
                                RepairOrder.class)
                        .setParameter("status", status)
                        .setParameter("master", master)
                        .getResultList(),
                status, master.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> findCreatedOrdersByDate(LocalDate date) {
        return executeWithResult("Поиск созданных заказов на дату",
                em -> em.createQuery("SELECT o FROM RepairOrder o WHERE o.status = 'CREATED' " +
                                "AND :date BETWEEN o.startDate AND o.endDate", RepairOrder.class)
                        .setParameter("date", date)
                        .getResultList(),
                date
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarServiceMaster> findMastersWithCreatedOrdersOnDate(LocalDate date) {
        return executeWithResult("Поиск занятых мастеров на дату",
                em -> em.createQuery("SELECT DISTINCT o.carServiceMaster FROM RepairOrder o " +
                                        "WHERE o.status = 'CREATED' AND :date BETWEEN o.startDate AND o.endDate",
                                CarServiceMaster.class)
                        .setParameter("date", date)
                        .getResultList(),
                date
        );
    }
}
