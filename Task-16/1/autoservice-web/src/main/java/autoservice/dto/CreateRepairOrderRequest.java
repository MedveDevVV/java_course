package autoservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateRepairOrderRequest(
        @NotNull UUID masterId,
        @NotNull UUID workshopPlaceId,
        @FutureOrPresent LocalDate startDate,
        @FutureOrPresent LocalDate endDate,
        @NotBlank String description
) {}