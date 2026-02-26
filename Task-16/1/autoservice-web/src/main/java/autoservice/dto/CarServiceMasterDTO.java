package autoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record CarServiceMasterDTO(
        UUID id,
        @Schema(description = "Полное имя мастера (Фамилия Имя Отчество)", example = "Иванов Иван Иванович")
        @NotBlank(message = "Полное имя обязательно")
        String fullName,
        LocalDate dateOfBirth
) {}