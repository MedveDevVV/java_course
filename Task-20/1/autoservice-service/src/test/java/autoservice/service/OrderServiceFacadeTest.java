package autoservice.service;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.dto.CreateRepairOrderRequest;
import autoservice.dto.RepairOrderQuery;
import autoservice.dto.SearchRepairOrderRequest;
import autoservice.enums.OrderStatus;
import autoservice.enums.SortCarServiceMasters;
import autoservice.enums.SortRepairOrders;
import autoservice.exception.DaoException;
import autoservice.exception.MasterNotAssignedException;
import autoservice.exception.MasterNotFoundException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceFacadeTest {

    @Mock
    private CarServiceMasterService masterService;

    @Mock
    private WorkshopPlaceService placeService;

    @Mock
    private RepairOrderService orderService;

    private OrderServiceFacade facade;

    @BeforeEach
    void setup() {
        facade = new OrderServiceFacade(masterService, placeService, orderService);
    }

    @Test
    void createOrder_ShouldCreateOrder_WhenMasterAndPlaceExist() {
        UUID masterId = UUID.randomUUID();
        UUID placeId = UUID.randomUUID();
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(masterId, placeId, start, end, "Диагностика");
        CarServiceMaster master = createMaster("Иванов Иван Иванович");
        WorkshopPlace place = createPlace("Пост 1");
        RepairOrder expectedOrder = createOrder(master, place, start, end);

        when(masterService.findById(masterId)).thenReturn(master);
        when(placeService.findById(placeId)).thenReturn(place);
        when(orderService.createOrder(any(LocalDate.class), eq(start), eq(end), eq("Диагностика"), eq(master), eq(place)))
                .thenReturn(expectedOrder);

        RepairOrder actualOrder = facade.createOrder(request);

        assertEquals(expectedOrder, actualOrder);
        verify(masterService).findById(masterId);
        verify(placeService).findById(placeId);
        verify(orderService).createOrder(any(LocalDate.class), eq(start), eq(end), eq("Диагностика"), eq(master), eq(place));
    }

    @Test
    void createOrder_ShouldThrowMasterNotFoundException_WhenMasterNotFound() {
        UUID masterId = UUID.randomUUID();
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(
                masterId,
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "Диагностика"
        );

        when(masterService.findById(masterId)).thenThrow(new MasterNotFoundException(masterId));

        MasterNotFoundException exception = assertThrows(MasterNotFoundException.class, () -> facade.createOrder(request));

        assertTrue(exception.getMessage().contains(masterId.toString()));
        verify(masterService).findById(masterId);
        verify(placeService, never()).findById(any());
        verify(orderService, never()).createOrder(any(), any(), any(), any(), any(), any());
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        RepairOrder order1 = createOrder(createMaster("Иванов Иван Иванович"), createPlace("Пост 1"),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        RepairOrder order2 = createOrder(createMaster("Петров Петр Петрович"), createPlace("Пост 2"),
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
        List<RepairOrder> expectedOrders = List.of(order1, order2);

        when(orderService.getAllOrders()).thenReturn(expectedOrders);

        List<RepairOrder> actualOrders = facade.getAllOrders();

        assertEquals(expectedOrders.size(), actualOrders.size());
        assertTrue(actualOrders.contains(order1));
        assertTrue(actualOrders.contains(order2));
        verify(orderService).getAllOrders();
    }

    @Test
    void getAllOrders_ShouldPropagateDaoException() {
        DaoException daoException = new DaoException("Ошибка чтения заказов");
        when(orderService.getAllOrders()).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> facade.getAllOrders());

        assertEquals("Ошибка чтения заказов", exception.getMessage());
        verify(orderService).getAllOrders();
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        UUID orderId = UUID.randomUUID();
        RepairOrder expectedOrder = createOrder(createMaster("Иванов Иван Иванович"), createPlace("Пост 1"),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(orderService.findById(orderId)).thenReturn(expectedOrder);

        RepairOrder actualOrder = facade.getOrderById(orderId);

        assertEquals(expectedOrder, actualOrder);
        verify(orderService).findById(orderId);
    }

    @Test
    void getOrderById_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderService.findById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> facade.getOrderById(orderId));

        assertTrue(exception.getMessage().contains(orderId.toString()));
        verify(orderService).findById(orderId);
    }

    @Test
    void searchOrders_ShouldBuildAndPassQuery_WhenMasterFilterProvided() {
        UUID masterId = UUID.randomUUID();
        SearchRepairOrderRequest request = new SearchRepairOrderRequest(
                OrderStatus.CREATED,
                masterId,
                UUID.randomUUID(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                SortRepairOrders.START_DATE,
                false
        );
        CarServiceMaster master = createMaster("Иванов Иван Иванович");
        RepairOrder order = createOrder(master, createPlace("Пост 1"), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(masterService.findById(masterId)).thenReturn(master);
        when(orderService.findOrdersByFilter(any(RepairOrderQuery.class))).thenReturn(List.of(order));

        List<RepairOrder> actualOrders = facade.searchOrders(request);

        ArgumentCaptor<RepairOrderQuery> queryCaptor = ArgumentCaptor.forClass(RepairOrderQuery.class);
        verify(orderService).findOrdersByFilter(queryCaptor.capture());
        RepairOrderQuery actualQuery = queryCaptor.getValue();

        assertEquals(1, actualOrders.size());
        assertTrue(actualOrders.contains(order));
        assertEquals(request.status(), actualQuery.status());
        assertEquals(master, actualQuery.carServiceMaster());
        assertEquals(request.startDate(), actualQuery.startDate());
        assertEquals(request.endDate(), actualQuery.endDate());
        assertEquals(request.sortBy(), actualQuery.sortRepairOrders());
        assertFalse(actualQuery.isRemoved());
    }

    @Test
    void searchOrders_ShouldThrowMasterNotFoundException_WhenMasterFilterNotFound() {
        UUID masterId = UUID.randomUUID();
        SearchRepairOrderRequest request = new SearchRepairOrderRequest(
                OrderStatus.CREATED,
                masterId,
                null,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                SortRepairOrders.START_DATE,
                false
        );

        when(masterService.findById(masterId)).thenThrow(new MasterNotFoundException(masterId));

        MasterNotFoundException exception = assertThrows(MasterNotFoundException.class, () -> facade.searchOrders(request));

        assertTrue(exception.getMessage().contains(masterId.toString()));
        verify(masterService).findById(masterId);
        verify(orderService, never()).findOrdersByFilter(any());
    }

    @Test
    void cancelOrder_ShouldDelegateToOrderService() {
        UUID orderId = UUID.randomUUID();

        facade.cancelOrder(orderId);

        verify(orderService).cancelOrder(orderId);
    }

    @Test
    void cancelOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        doThrow(new OrderNotFoundException(orderId)).when(orderService).cancelOrder(orderId);

        assertThrows(OrderNotFoundException.class, () -> facade.cancelOrder(orderId));

        verify(orderService).cancelOrder(orderId);
    }

    @Test
    void closeOrder_ShouldDelegateToOrderService() {
        UUID orderId = UUID.randomUUID();

        facade.closeOrder(orderId);

        verify(orderService).closeOrder(orderId);
    }

    @Test
    void closeOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        doThrow(new OrderNotFoundException(orderId)).when(orderService).closeOrder(orderId);

        assertThrows(OrderNotFoundException.class, () -> facade.closeOrder(orderId));

        verify(orderService).closeOrder(orderId);
    }

    @Test
    void delayOrder_ShouldConvertDaysToPeriodAndDelegate() {
        UUID orderId = UUID.randomUUID();

        facade.delayOrder(orderId, 4);

        verify(orderService).delayOrder(orderId, Period.ofDays(4));
    }

    @Test
    void delayOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        doThrow(new OrderNotFoundException(orderId)).when(orderService).delayOrder(orderId, Period.ofDays(2));

        assertThrows(OrderNotFoundException.class, () -> facade.delayOrder(orderId, 2));

        verify(orderService).delayOrder(orderId, Period.ofDays(2));
    }

    @Test
    void deleteOrder_ShouldDelegateToOrderService() {
        UUID orderId = UUID.randomUUID();

        facade.deleteOrder(orderId);

        verify(orderService).removeOrder(orderId);
    }

    @Test
    void deleteOrder_ShouldThrowOperationNotAllowedException_WhenDeletionDisabled() {
        UUID orderId = UUID.randomUUID();
        doThrow(new OperationNotAllowedException("Удаление запрещено")).when(orderService).removeOrder(orderId);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class,
                () -> facade.deleteOrder(orderId));

        assertTrue(exception.getMessage().contains("Удаление запрещено"));
        verify(orderService).removeOrder(orderId);
    }

    @Test
    void getMasterByOrderId_ShouldReturnAssignedMaster() {
        UUID orderId = UUID.randomUUID();
        CarServiceMaster master = createMaster("Иванов Иван Иванович");
        RepairOrder order = createOrder(master, createPlace("Пост 1"), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(orderService.findById(orderId)).thenReturn(order);

        CarServiceMaster actualMaster = facade.getMasterByOrderId(orderId);

        assertEquals(master, actualMaster);
        verify(orderService).findById(orderId);
    }

    @Test
    void getMasterByOrderId_ShouldThrowMasterNotAssignedException_WhenMasterMissing() {
        UUID orderId = UUID.randomUUID();
        RepairOrder order = createOrder(createMaster("Иванов Иван Иванович"), createPlace("Пост 1"),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        order.setCarServiceMaster(null);

        when(orderService.findById(orderId)).thenReturn(order);

        MasterNotAssignedException exception = assertThrows(MasterNotAssignedException.class,
                () -> facade.getMasterByOrderId(orderId));

        assertTrue(exception.getMessage().contains(orderId.toString()));
        verify(orderService).findById(orderId);
    }

    @Test
    void getCarServiceMasters_ShouldReturnSortedOccupiedMasters_WhenQueryRequestsOccupied() {
        LocalDate date = LocalDate.now().plusDays(2);
        CarServiceMaster master1 = createMaster("Яковлев Яков Яковлевич");
        CarServiceMaster master2 = createMaster("Андреев Андрей Андреевич");
        CarServiceMastersQuery query = CarServiceMastersQuery.builder()
                .localDate(date)
                .isOccupied(true)
                .sort(SortCarServiceMasters.NAME)
                .build();

        when(orderService.findOccupiedMastersOnDate(date)).thenReturn(new ArrayList<>(List.of(master1, master2)));

        List<CarServiceMaster> actualMasters = facade.getCarServiceMasters(query);

        assertEquals(2, actualMasters.size());
        assertEquals(master2, actualMasters.get(0));
        assertEquals(master1, actualMasters.get(1));
        verify(orderService).findOccupiedMastersOnDate(date);
    }

    @Test
    void getCarServiceMasters_ShouldThrowNullPointerException_WhenQueryIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.getCarServiceMasters(null));

        assertTrue(exception.getMessage().contains("carServiceMastersQuery cannot be null"));
        verifyNoInteractions(masterService, orderService);
    }

    @Test
    void findAvailableMastersOnDate_ShouldReturnOnlyAvailableMasters() {
        LocalDate date = LocalDate.now().plusDays(1);
        CarServiceMaster availableMaster = createMaster("Иванов Иван Иванович");
        CarServiceMaster occupiedMaster = createMaster("Петров Петр Петрович");

        when(masterService.getAllMasters()).thenReturn(new ArrayList<>(List.of(availableMaster, occupiedMaster)));
        when(orderService.findOccupiedMastersOnDate(date)).thenReturn(List.of(occupiedMaster));

        List<CarServiceMaster> actualMasters = facade.findAvailableMastersOnDate(date);

        assertEquals(1, actualMasters.size());
        assertTrue(actualMasters.contains(availableMaster));
        verify(masterService).getAllMasters();
        verify(orderService).findOccupiedMastersOnDate(date);
    }

    @Test
    void findAvailableMastersOnDate_ShouldPropagateDaoException() {
        LocalDate date = LocalDate.now().plusDays(1);
        DaoException daoException = new DaoException("Ошибка получения мастеров");
        when(masterService.getAllMasters()).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> facade.findAvailableMastersOnDate(date));

        assertEquals("Ошибка получения мастеров", exception.getMessage());
        verify(masterService).getAllMasters();
        verify(orderService, never()).findOccupiedMastersOnDate(any());
    }

    @Test
    void getAvailablePlacesOnDate_ShouldReturnOnlyAvailablePlaces() {
        LocalDate date = LocalDate.now().plusDays(2);
        WorkshopPlace availablePlace = createPlace("Пост 1");
        WorkshopPlace occupiedPlace = createPlace("Пост 2");
        RepairOrder occupiedOrder = createOrder(createMaster("Иванов Иван Иванович"), occupiedPlace,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));

        when(placeService.getAllPlaces()).thenReturn(new ArrayList<>(List.of(availablePlace, occupiedPlace)));
        when(orderService.findCreatedOrdersByDate(date)).thenReturn(List.of(occupiedOrder));

        List<WorkshopPlace> actualPlaces = facade.getAvailablePlacesOnDate(date);

        assertEquals(1, actualPlaces.size());
        assertTrue(actualPlaces.contains(availablePlace));
        verify(placeService).getAllPlaces();
        verify(orderService).findCreatedOrdersByDate(date);
    }

    @Test
    void getAvailablePlacesOnDate_ShouldPropagateDaoException() {
        LocalDate date = LocalDate.now().plusDays(2);
        DaoException daoException = new DaoException("Ошибка получения заказов");

        when(placeService.getAllPlaces()).thenReturn(new ArrayList<>());
        when(orderService.findCreatedOrdersByDate(date)).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> facade.getAvailablePlacesOnDate(date));

        assertEquals("Ошибка получения заказов", exception.getMessage());
        verify(placeService).getAllPlaces();
        verify(orderService).findCreatedOrdersByDate(date);
    }

    @Test
    void countAvailablePlacesOnDate_ShouldReturnMinimumOfPlacesAndMasters() {
        LocalDate date = LocalDate.now().plusDays(3);
        OrderServiceFacade facadeSpy = spy(new OrderServiceFacade(masterService, placeService, orderService));
        WorkshopPlace place = createPlace("Пост 1");
        CarServiceMaster master1 = createMaster("Иванов Иван Иванович");
        CarServiceMaster master2 = createMaster("Петров Петр Петрович");

        doReturn(List.of(place)).when(facadeSpy).getAvailablePlacesOnDate(date);
        doReturn(List.of(master1, master2)).when(facadeSpy).findAvailableMastersOnDate(date);

        int availableSlots = facadeSpy.countAvailablePlacesOnDate(date);

        assertEquals(1, availableSlots);
        verify(facadeSpy).getAvailablePlacesOnDate(date);
        verify(facadeSpy).findAvailableMastersOnDate(date);
    }

    @Test
    void countAvailablePlacesOnDate_ShouldPropagateDaoException() {
        LocalDate date = LocalDate.now().plusDays(3);
        DaoException daoException = new DaoException("Ошибка получения мест");
        when(placeService.getAllPlaces()).thenThrow(daoException);

        DaoException exception = assertThrows(DaoException.class, () -> facade.countAvailablePlacesOnDate(date));

        assertEquals("Ошибка получения мест", exception.getMessage());
        verify(placeService).getAllPlaces();
    }

    @Test
    void getNextAvailableSlot_ShouldDelegateToGetFirstAvailableSlot() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        Optional<LocalDate> expectedDate = Optional.of(fromDate.plusDays(2));
        OrderServiceFacade facadeSpy = spy(new OrderServiceFacade(masterService, placeService, orderService));

        doReturn(expectedDate).when(facadeSpy).getFirstAvailableSlot(fromDate);

        Optional<LocalDate> actualDate = facadeSpy.getNextAvailableSlot(fromDate);

        assertEquals(expectedDate, actualDate);
        verify(facadeSpy).getFirstAvailableSlot(fromDate);
    }

    @Test
    void getNextAvailableSlot_ShouldPropagateExceptionFromGetFirstAvailableSlot() {
        LocalDate fromDate = LocalDate.now().plusDays(1);
        OrderServiceFacade facadeSpy = spy(new OrderServiceFacade(masterService, placeService, orderService));

        doThrow(new RuntimeException("Ошибка поиска слота"))
                .when(facadeSpy).getFirstAvailableSlot(any(LocalDate.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> facadeSpy.getNextAvailableSlot(fromDate));

        assertEquals("Ошибка поиска слота", exception.getMessage());
        verify(facadeSpy).getFirstAvailableSlot(fromDate);
    }

    @Test
    void getFirstAvailableSlot_ShouldReturnDate_WhenSlotExistsWithinSevenDays() {
        LocalDate date = LocalDate.now().plusDays(1);
        OrderServiceFacade facadeSpy = spy(new OrderServiceFacade(masterService, placeService, orderService));

        doReturn(0).when(facadeSpy).countAvailablePlacesOnDate(any(LocalDate.class));
        doReturn(2).when(facadeSpy).countAvailablePlacesOnDate(date.plusDays(1));

        Optional<LocalDate> actualDate = facadeSpy.getFirstAvailableSlot(date);

        assertTrue(actualDate.isPresent());
        assertEquals(date.plusDays(1), actualDate.get());
    }

    @Test
    void getFirstAvailableSlot_ShouldReturnEmpty_WhenNoSlotExistsWithinSevenDays() {
        LocalDate date = LocalDate.now().plusDays(1);
        OrderServiceFacade facadeSpy = spy(new OrderServiceFacade(masterService, placeService, orderService));

        doReturn(0).when(facadeSpy).countAvailablePlacesOnDate(any(LocalDate.class));

        Optional<LocalDate> actualDate = facadeSpy.getFirstAvailableSlot(date);

        assertFalse(actualDate.isPresent());
        verify(facadeSpy, times(7)).countAvailablePlacesOnDate(any(LocalDate.class));
    }

    private CarServiceMaster createMaster(String fullName) {
        return new CarServiceMaster(UUID.randomUUID(), fullName, LocalDate.of(1985, 5, 20));
    }

    private WorkshopPlace createPlace(String name) {
        return new WorkshopPlace(UUID.randomUUID(), name);
    }

    private RepairOrder createOrder(CarServiceMaster master, WorkshopPlace place, LocalDate startDate, LocalDate endDate) {
        return new RepairOrder(
                LocalDate.now(),
                startDate,
                endDate,
                "Тестовый заказ",
                OrderStatus.CREATED,
                1000f,
                UUID.randomUUID(),
                master,
                place
        );
    }
}

