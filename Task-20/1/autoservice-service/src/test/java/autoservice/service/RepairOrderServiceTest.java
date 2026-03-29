package autoservice.service;

import autoservice.config.AppConfig;
import autoservice.dto.RepairOrderQuery;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortRepairOrders;
import autoservice.exception.DaoException;
import autoservice.exception.InvalidDateException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairOrderServiceTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private OrderRepository<RepairOrder> repository;

    private RepairOrderService service;
    private CarServiceMaster master;
    private WorkshopPlace place;

    @BeforeEach
    void setup() {
        service = new RepairOrderService(appConfig, repository);
        master = new CarServiceMaster(UUID.randomUUID(), "Иванов Иван Иванович", LocalDate.of(1985, 5, 20));
        place = new WorkshopPlace(UUID.randomUUID(), "Подъемник 1");
    }

    @Test
    void findById_ShouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        RepairOrder order = createOrderEntity(orderId, 
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                OrderStatus.CREATED);

        when(repository.getOrderById(orderId)).thenReturn(Optional.of(order));

        RepairOrder result = service.findById(orderId);

        assertEquals(order, result);
        verify(repository).getOrderById(orderId);
    }

    @Test
    void findById_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(repository.getOrderById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> service.findById(orderId));

        assertTrue(exception.getMessage().contains(orderId.toString()));
        verify(repository).getOrderById(orderId);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        RepairOrder order1 = createOrderEntity(UUID.randomUUID(), 
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                OrderStatus.CREATED);
        RepairOrder order2 = createOrderEntity(UUID.randomUUID(),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                OrderStatus.CLOSED);
        List<RepairOrder> expected = List.of(order1, order2);

        when(repository.getAllOrders()).thenReturn(expected);

        List<RepairOrder> actual = service.getAllOrders();

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.contains(order1));
        assertTrue(actual.contains(order2));
        verify(repository).getAllOrders();
    }

    @Test
    void getAllOrders_ShouldPropagateDaoException() {
        DaoException daoException = new DaoException("Ошибка чтения заказов");
        when(repository.getAllOrders()).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> service.getAllOrders());

        assertEquals("Ошибка чтения заказов", exception.getMessage());
        verify(repository).getAllOrders();
    }

    @Test
    void findOrdersByFilter_ShouldReturnOrdersFromRepository() {
        RepairOrderQuery query = RepairOrderQuery.builder()
                .status(OrderStatus.CREATED)
                .carServiceMaster(master)
                .workshopPlace(place)
                .sortOrders(SortRepairOrders.START_DATE)
                .isRemoved(false)
                .build();
        RepairOrder order = createOrderEntity(UUID.randomUUID(), LocalDate.now().plusDays(2), LocalDate.now().plusDays(3),
                OrderStatus.CREATED);
        List<RepairOrder> expected = List.of(order);

        when(repository.findOrders(query)).thenReturn(expected);

        List<RepairOrder> actual = service.findOrdersByFilter(query);

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.contains(order));
        verify(repository).findOrders(query);
    }

    @Test
    void findOrdersByFilter_ShouldPropagateDaoException() {
        RepairOrderQuery query = RepairOrderQuery.builder().build();
        DaoException daoException = new DaoException("Ошибка фильтрации");
        when(repository.findOrders(query)).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> service.findOrdersByFilter(query));

        assertEquals("Ошибка фильтрации", exception.getMessage());
        verify(repository).findOrders(query);
    }

    @Test
    void createOrder_ShouldCreateAndPersistOrder() {
        LocalDate creationDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        String description = "Замена тормозных колодок";

        RepairOrder created = service.createOrder(creationDate, startDate, endDate, description, master, place);

        assertEquals(creationDate, created.getCreationDate());
        assertEquals(startDate, created.getStartDate());
        assertEquals(endDate, created.getEndDate());
        assertEquals(description, created.getDescription());
        assertEquals(OrderStatus.CREATED, created.getStatus());
        assertEquals(master, created.getCarServiceMaster());
        assertEquals(place, created.getWorkshopPlace());
        verify(repository).addOrder(created);
    }

    @Test
    void createOrder_ShouldThrowInvalidDateException_WhenEndBeforeStart() {
        LocalDate creationDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().plusDays(3);
        LocalDate endDate = LocalDate.now().plusDays(1);

        InvalidDateException exception = assertThrows(InvalidDateException.class,
                () -> service.createOrder(creationDate, startDate, endDate, "Описание", master, place));

        assertTrue(exception.getMessage().contains("Дата окончания"));
        verify(repository, never()).addOrder(any());
    }

    @Test
    void cancelOrder_ShouldCancelExistingOrder() {
        UUID orderId = UUID.randomUUID();
        RepairOrder order = createOrderEntity(orderId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                OrderStatus.CREATED);
        when(repository.getOrderById(orderId)).thenReturn(Optional.of(order));

        service.cancelOrder(orderId);

        verify(repository).getOrderById(orderId);
        verify(repository).cancelOrder(order);
    }

    @Test
    void cancelOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(repository.getOrderById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.cancelOrder(orderId));

        verify(repository).getOrderById(orderId);
        verify(repository, never()).cancelOrder(any());
    }

    @Test
    void closeOrder_ShouldCloseExistingOrder() {
        UUID orderId = UUID.randomUUID();
        RepairOrder order = createOrderEntity(orderId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                OrderStatus.CREATED);
        when(repository.getOrderById(orderId)).thenReturn(Optional.of(order));

        service.closeOrder(orderId);

        verify(repository).getOrderById(orderId);
        verify(repository).closeOrder(order);
    }

    @Test
    void closeOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(repository.getOrderById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.closeOrder(orderId));

        verify(repository).getOrderById(orderId);
        verify(repository, never()).closeOrder(any());
    }

    @Test
    void removeOrder_ShouldRemoveOrder_WhenConfigAllows() {
        UUID orderId = UUID.randomUUID();
        when(appConfig.isCanDeleteOrders()).thenReturn(true);

        service.removeOrder(orderId);

        verify(appConfig).isCanDeleteOrders();
        verify(repository).removeOrder(orderId);
    }

    @Test
    void removeOrder_ShouldThrowOperationNotAllowedException_WhenDeletionDisabledByConfig() {
        UUID orderId = UUID.randomUUID();
        when(appConfig.isCanDeleteOrders()).thenReturn(false);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> service.removeOrder(orderId));

        assertTrue(exception.getMessage().contains("Удаление заказов запрещено"));
        verify(appConfig).isCanDeleteOrders();
        verify(repository, never()).removeOrder(any());
    }

    @Test
    void delayOrder_ShouldShiftCurrentAndConflictingOrder_WhenDelayAllowed() {
        UUID orderId = UUID.randomUUID();
        LocalDate start = LocalDate.now().plusDays(3);
        LocalDate end = LocalDate.now().plusDays(5);
        RepairOrder modifiedOrder = createOrderEntity(orderId, start, end, OrderStatus.CREATED);
        RepairOrder current = createOrderEntity(UUID.randomUUID(), start, end, OrderStatus.CREATED);
        RepairOrder next = createOrderEntity(UUID.randomUUID(), end.plusDays(2), end.plusDays(3), OrderStatus.CREATED);
        Period delay = Period.ofDays(2);

        when(appConfig.isCanDelayOrders()).thenReturn(true);
        when(repository.getOrderById(orderId)).thenReturn(Optional.of(modifiedOrder));
        when(repository.findOrders(any(RepairOrderQuery.class))).thenReturn(List.of(current, next));

        service.delayOrder(orderId, delay);

        ArgumentCaptor<RepairOrderQuery> queryCaptor = ArgumentCaptor.forClass(RepairOrderQuery.class);
        verify(repository).findOrders(queryCaptor.capture());
        RepairOrderQuery actualQuery = queryCaptor.getValue();

        assertEquals(OrderStatus.CREATED, actualQuery.status());
        assertEquals(modifiedOrder.getStartDate(), actualQuery.startDate());
        assertEquals(modifiedOrder.getCarServiceMaster(), actualQuery.carServiceMaster());
        assertEquals(modifiedOrder.getWorkshopPlace(), actualQuery.workshopPlace());
        assertEquals(SortRepairOrders.START_DATE, actualQuery.sortRepairOrders());
        assertFalse(actualQuery.isRemoved());

        assertEquals(end.plusDays(2), current.getEndDate());
        assertEquals(end.plusDays(3), next.getStartDate());
        assertEquals(end.plusDays(4), next.getEndDate());
        verify(repository, times(2)).updateOrder(any(RepairOrder.class));
    }

    @Test
    void delayOrder_ShouldReturnWithoutUpdates_WhenNoOrdersFoundByFilter() {
        UUID orderId = UUID.randomUUID();
        LocalDate start = LocalDate.now().plusDays(3);
        LocalDate end = LocalDate.now().plusDays(5);
        RepairOrder modifiedOrder = createOrderEntity(orderId, start, end, OrderStatus.CREATED);

        when(appConfig.isCanDelayOrders()).thenReturn(true);
        when(repository.getOrderById(orderId)).thenReturn(Optional.of(modifiedOrder));
        when(repository.findOrders(any(RepairOrderQuery.class))).thenReturn(List.of());

        service.delayOrder(orderId, Period.ofDays(2));

        verify(appConfig).isCanDelayOrders();
        verify(repository).getOrderById(orderId);
        verify(repository).findOrders(any(RepairOrderQuery.class));
        verify(repository, never()).updateOrder(any());
    }

    @Test
    void delayOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        when(appConfig.isCanDelayOrders()).thenReturn(true);
        when(repository.getOrderById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> service.delayOrder(orderId, Period.ofDays(1)));

        assertTrue(exception.getMessage().contains(orderId.toString()));
        verify(appConfig).isCanDelayOrders();
        verify(repository).getOrderById(orderId);
        verify(repository, never()).findOrders(any());
        verify(repository, never()).updateOrder(any());
    }

    @Test
    void delayOrder_ShouldThrowOperationNotAllowedException_WhenDelayDisabledByConfig() {
        UUID orderId = UUID.randomUUID();
        when(appConfig.isCanDelayOrders()).thenReturn(false);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> service.delayOrder(orderId, Period.ofDays(1)));

        assertTrue(exception.getMessage().contains("Смещение времени заказа запрещено"));
        verify(appConfig).isCanDelayOrders();
        verifyNoInteractions(repository);
    }

    @Test
    void findOccupiedMastersOnDate_ShouldReturnOccupiedMasters() {
        LocalDate date = LocalDate.now().plusDays(5);
        CarServiceMaster secondMaster = new CarServiceMaster(UUID.randomUUID(), "Петров Петр Петрович",
                LocalDate.of(1990, 4, 10));
        List<CarServiceMaster> expected = List.of(master, secondMaster);

        when(repository.findMastersWithCreatedOrdersOnDate(date)).thenReturn(expected);

        List<CarServiceMaster> actual = service.findOccupiedMastersOnDate(date);

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.contains(master));
        assertTrue(actual.contains(secondMaster));
        verify(repository).findMastersWithCreatedOrdersOnDate(date);
    }

    @Test
    void findOccupiedMastersOnDate_ShouldPropagateDaoException() {
        LocalDate date = LocalDate.now().plusDays(5);
        DaoException daoException = new DaoException("Ошибка получения занятых мастеров");
        when(repository.findMastersWithCreatedOrdersOnDate(date)).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> service.findOccupiedMastersOnDate(date));

        assertEquals("Ошибка получения занятых мастеров", exception.getMessage());
        verify(repository).findMastersWithCreatedOrdersOnDate(date);
    }

    @Test
    void findCreatedOrdersByDate_ShouldReturnOrders() {
        LocalDate date = LocalDate.now().plusDays(4);
        RepairOrder order = createOrderEntity(UUID.randomUUID(), date, date.plusDays(1), OrderStatus.CREATED);
        List<RepairOrder> expected = List.of(order);

        when(repository.findCreatedOrdersByDate(date)).thenReturn(expected);

        List<RepairOrder> actual = service.findCreatedOrdersByDate(date);

        assertEquals(expected.size(), actual.size());
        assertTrue(actual.contains(order));
        verify(repository).findCreatedOrdersByDate(date);
    }

    @Test
    void findCreatedOrdersByDate_ShouldPropagateDaoException() {
        LocalDate date = LocalDate.now().plusDays(4);
        DaoException daoException = new DaoException("Ошибка получения созданных заказов");
        when(repository.findCreatedOrdersByDate(date)).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> service.findCreatedOrdersByDate(date));

        assertEquals("Ошибка получения созданных заказов", exception.getMessage());
        verify(repository).findCreatedOrdersByDate(date);
    }

    private RepairOrder createOrderEntity(UUID id, LocalDate start, LocalDate end, OrderStatus status) {
        return new RepairOrder(
                LocalDate.now(),
                start,
                end,
                "Тестовый заказ",
                status,
                1000f,
                id,
                master,
                place
        );
    }

}
