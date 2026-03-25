package autoservice.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull String userName,
        @NotNull String password) {
}
