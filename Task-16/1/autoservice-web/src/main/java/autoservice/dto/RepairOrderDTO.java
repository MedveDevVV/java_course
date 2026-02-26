package autoservice.dto;

import autoservice.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public record RepairOrderDTO(
        UUID id,
        UUID masterId,
        String masterName,
        UUID workshopPlaceId,
        String workshopPlaceName,
        LocalDate creationDate,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        OrderStatus status,
        Float totalPrice
) {}