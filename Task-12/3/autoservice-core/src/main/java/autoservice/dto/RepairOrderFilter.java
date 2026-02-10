package autoservice.dto;

import autoservice.enums.OrderStatus;
import autoservice.model.CarServiceMaster;
import autoservice.model.WorkshopPlace;

import java.time.LocalDate;

public record RepairOrderFilter(
        LocalDate creationDate,
        LocalDate startDate,
        LocalDate endDate,
        CarServiceMaster carServiceMaster,
        WorkshopPlace place,
        OrderStatus status,
        String description,
        Float totalPrice,
        int limit,
        int offset) {
}