import autoservice.controller.RepairOrderController;
import autoservice.dto.CreateRepairOrderRequest;
import autoservice.dto.RepairOrderDTO;
import autoservice.dto.SearchRepairOrderRequest;
import autoservice.enums.OrderStatus;
import autoservice.exception.InvalidDateException;
import autoservice.exception.MasterNotAssignedException;
import autoservice.exception.OrderNotFoundException;
import autoservice.handler.GlobalExceptionHandler;
import autoservice.mapper.RepairOrderMapper;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.service.OrderServiceFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RepairOrderControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    OrderServiceFacade service;

    @Mock
    RepairOrderMapper mapper;

    private UUID orderId1;
    private UUID orderId2;
    private UUID masterId;
    private UUID placeId;
    private CarServiceMaster master;
    private RepairOrder order1;
    private RepairOrder order2;
    private RepairOrderDTO dto1;
    private RepairOrderDTO dto2;

    @BeforeEach
    void setup() {
        RepairOrderController controller = new RepairOrderController(service, mapper);

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        jsonConverter.setObjectMapper(objectMapper);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setMessageInterpolator(new ParameterMessageInterpolator());
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .setValidator(validator)
                .build();

        orderId1 = UUID.randomUUID();
        orderId2 = UUID.randomUUID();
        masterId = UUID.randomUUID();
        placeId = UUID.randomUUID();

        master = new CarServiceMaster(masterId, "Иванов Иван Иванович", LocalDate.of(1985, 3, 11));
        WorkshopPlace place = new WorkshopPlace(placeId, "Пост 1");

        order1 = new RepairOrder(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 3),
                "Замена масла", OrderStatus.CREATED, 2500f, orderId1, master, place);
        order2 = new RepairOrder(LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 6), LocalDate.of(2026, 3, 7),
                "Диагностика", OrderStatus.CREATED, 1500f, orderId2, master, place);

        dto1 = new RepairOrderDTO(orderId1, masterId, "Иванов Иван Иванович", placeId, "Пост 1",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 3),
                "Замена масла", OrderStatus.CREATED, 2500f);
        dto2 = new RepairOrderDTO(orderId2, masterId, "Иванов Иван Иванович", placeId, "Пост 1",
                LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 6), LocalDate.of(2026, 3, 7),
                "Диагностика", OrderStatus.CREATED, 1500f);
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() throws Exception {
        when(service.getAllOrders()).thenReturn(List.of(order1, order2));
        when(mapper.toDTO(order1)).thenReturn(dto1);
        when(mapper.toDTO(order2)).thenReturn(dto2);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(orderId1.toString()))
                .andExpect(jsonPath("$[1].id").value(orderId2.toString()));

        verify(service).getAllOrders();
        verify(mapper).toDTO(order1);
        verify(mapper).toDTO(order2);
    }

    @Test
    void getAllOrders_WhenNoOrders_ShouldReturnEmptyList() throws Exception {
        when(service.getAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).getAllOrders();
        verifyNoInteractions(mapper);
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        when(service.getOrderById(orderId1)).thenReturn(order1);
        when(mapper.toDTO(order1)).thenReturn(dto1);

        mockMvc.perform(get("/api/orders/" + orderId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId1.toString()))
                .andExpect(jsonPath("$.description").value("Замена масла"));

        verify(service).getOrderById(orderId1);
        verify(mapper).toDTO(order1);
    }

    @Test
    void getOrderById_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        when(service.getOrderById(orderId1)).thenThrow(new OrderNotFoundException(orderId1));

        mockMvc.perform(get("/api/orders/" + orderId1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_ORDER_001")))
                .andExpect(jsonPath("$.message", is("Заказ с ID: " + orderId1 + " не найден в системе")));

        verify(service).getOrderById(orderId1);
    }

    @Test
    void createOrder_WithValidData_ShouldCreateOrder() throws Exception {
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(
                masterId, placeId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Плановое ТО");

        when(service.createOrder(any(CreateRepairOrderRequest.class))).thenReturn(order1);
        when(mapper.toDTO(order1)).thenReturn(dto1);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId1.toString()));

        verify(service).createOrder(any(CreateRepairOrderRequest.class));
        verify(mapper).toDTO(order1);
    }

    @Test
    void createOrder_WhenDescriptionIsBlank_ShouldReturnBadRequest() throws Exception {
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(
                masterId, placeId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VAL_GENERAL_001")));

        verifyNoInteractions(service, mapper);
    }

    @Test
    void cancelOrder_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/orders/" + orderId1 + "/cancel"))
                .andExpect(status().isOk());

        verify(service).cancelOrder(orderId1);
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new OrderNotFoundException(orderId1)).when(service).cancelOrder(orderId1);

        mockMvc.perform(put("/api/orders/" + orderId1 + "/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_ORDER_001")));

        verify(service).cancelOrder(orderId1);
    }

    @Test
    void closeOrder_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/orders/" + orderId1 + "/close"))
                .andExpect(status().isOk());

        verify(service).closeOrder(orderId1);
    }

    @Test
    void closeOrder_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new OrderNotFoundException(orderId1)).when(service).closeOrder(orderId1);

        mockMvc.perform(put("/api/orders/" + orderId1 + "/close"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_ORDER_001")));

        verify(service).closeOrder(orderId1);
    }

    @Test
    void delayOrder_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/orders/" + orderId1 + "/delay").param("days", "3"))
                .andExpect(status().isOk());

        verify(service).delayOrder(orderId1, 3);
    }

    @Test
    void delayOrder_WhenInvalidDays_ShouldReturnBadRequest() throws Exception {
        doThrow(new InvalidDateException("Количество дней не может быть отрицательным"))
                .when(service).delayOrder(orderId1, -1);

        mockMvc.perform(put("/api/orders/" + orderId1 + "/delay").param("days", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VAL_DATE_001")));

        verify(service).delayOrder(orderId1, -1);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/orders/" + orderId1))
                .andExpect(status().isNoContent());

        verify(service).deleteOrder(orderId1);
    }

    @Test
    void deleteOrder_WhenOrderNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new OrderNotFoundException(orderId1)).when(service).deleteOrder(orderId1);

        mockMvc.perform(delete("/api/orders/" + orderId1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_ORDER_001")));

        verify(service).deleteOrder(orderId1);
    }

    @Test
    void searchOrders_ShouldReturnMatchingOrders() throws Exception {
        when(service.searchOrders(any(SearchRepairOrderRequest.class))).thenReturn(List.of(order1));
        when(mapper.toDTO(order1)).thenReturn(dto1);

        mockMvc.perform(get("/api/orders/search")
                        .param("status", "CREATED")
                        .param("masterId", masterId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(orderId1.toString()));

        verify(service).searchOrders(any(SearchRepairOrderRequest.class));
        verify(mapper).toDTO(order1);
    }

    @Test
    void searchOrders_WhenNoMatches_ShouldReturnEmptyList() throws Exception {
        when(service.searchOrders(any(SearchRepairOrderRequest.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/search")
                        .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).searchOrders(any(SearchRepairOrderRequest.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void getMasterByOrderId_ShouldReturnMaster() throws Exception {
        when(service.getMasterByOrderId(orderId1)).thenReturn(master);

        mockMvc.perform(get("/api/orders/" + orderId1 + "/master"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(masterId.toString()))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван Иванович"));

        verify(service).getMasterByOrderId(orderId1);
    }

    @Test
    void getMasterByOrderId_WhenMasterNotAssigned_ShouldReturnNotFound() throws Exception {
        when(service.getMasterByOrderId(orderId1)).thenThrow(new MasterNotAssignedException(orderId1));

        mockMvc.perform(get("/api/orders/" + orderId1 + "/master"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_MASTER_002")))
                .andExpect(jsonPath("$.message", is("На заказ " + orderId1 + " не назначен мастер")));

        verify(service).getMasterByOrderId(orderId1);
    }

    @Test
    void getNextAvailableSlot_ShouldReturnDate() throws Exception {
        LocalDate nextDate = LocalDate.of(2026, 4, 1);
        when(service.getNextAvailableSlot(eq(nextDate))).thenReturn(Optional.of(nextDate));

        mockMvc.perform(get("/api/orders/available-slots/next")
                        .param("fromDate", "2026-04-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(anyOf(is("\"2026-04-01\""), is("[2026,4,1]"))));

        verify(service).getNextAvailableSlot(nextDate);
    }

    @Test
    void getNextAvailableSlot_WhenNoSlots_ShouldReturnNoContent() throws Exception {
        LocalDate fromDate = LocalDate.of(2026, 4, 1);
        when(service.getNextAvailableSlot(eq(fromDate))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/available-slots/next")
                        .param("fromDate", "2026-04-01"))
                .andExpect(status().isNoContent());

        verify(service).getNextAvailableSlot(fromDate);
        verify(service, never()).deleteOrder(any(UUID.class));
    }
}

