package autoservice.dto;

import autoservice.enums.OrderStatus;
import autoservice.enums.SortRepairOrders;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

public record SearchRepairOrderRequest(
        OrderStatus status,
        UUID masterId,
        UUID workshopPlaceId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        SortRepairOrders sortBy
){}