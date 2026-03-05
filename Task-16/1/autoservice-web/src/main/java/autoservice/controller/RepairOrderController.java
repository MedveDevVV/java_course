package autoservice.controller;

import autoservice.dto.CreateRepairOrderRequest;
import autoservice.dto.RepairOrderDTO;
import autoservice.dto.SearchRepairOrderRequest;
import autoservice.mapper.RepairOrderMapper;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.service.OrderServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Заказы на ремонт", description = "API для управления заказами на ремонт")
public class RepairOrderController {

    private final OrderServiceFacade orderService;
    private final RepairOrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "Получить список всех заказов")
    public List<RepairOrderDTO> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(orderMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID заказа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<RepairOrderDTO> getOrderById(
            @Parameter(description = "UUID заказа", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        RepairOrder order = orderService.getOrderById(id);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ на ремонт")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "404", description = "Мастер или рабочее место не найдены")
    })
    public ResponseEntity<RepairOrderDTO> createOrder(@Valid @RequestBody CreateRepairOrderRequest request) {
        RepairOrder order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toDTO(order));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Отменить заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ отменен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "UUID заказа", required = true)
            @PathVariable UUID id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "Закрыть заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ закрыт"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> closeOrder(
            @Parameter(description = "UUID заказа", required = true)
            @PathVariable UUID id) {
        orderService.closeOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/delay")
    @Operation(summary = "Отложить заказ на указанное количество дней")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ отложен"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "400", description = "Неверное количество дней")
    })
    public ResponseEntity<Void> delayOrder(
            @Parameter(description = "UUID заказа", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Количество дней для откладывания", required = true, example = "3")
            @RequestParam int days) {
        orderService.delayOrder(id, days);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заказ удален"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "UUID заказа", required = true)
            @PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск заказов с фильтрами")
    public ResponseEntity<List<RepairOrderDTO>> searchOrders(@Valid SearchRepairOrderRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.searchOrders(request).stream().map(orderMapper::toDTO).toList());
    }

    @GetMapping("/{id}/master")
    @Operation(summary = "Получить мастера по ID заказа")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мастер найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден или мастер не назначен")
    })
    public ResponseEntity<CarServiceMaster> getMasterByOrderId(
            @Parameter(description = "UUID заказа", required = true)
            @PathVariable UUID id) {
        CarServiceMaster master = orderService.getMasterByOrderId(id);
        return ResponseEntity.ok(master);
    }

    @GetMapping("/available-slots/next")
    @Operation(summary = "Найти ближайшее доступное время для записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Доступная дата найдена"),
            @ApiResponse(responseCode = "204", description = "Нет доступных дат в ближайшие 7 дней")
    })
    public ResponseEntity<LocalDate> getNextAvailableSlot(
            @Parameter(description = "Дата начала поиска (по умолчанию сегодня)", example = "2024-01-20")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate) {
        Optional<LocalDate> availableSlot = orderService.getNextAvailableSlot(fromDate);

        return availableSlot
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
