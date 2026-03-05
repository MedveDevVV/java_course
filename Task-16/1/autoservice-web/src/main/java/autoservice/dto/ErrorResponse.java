package autoservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO для передачи информации об ошибке клиенту.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // поле не включается в JSON если null
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // формат сериализации даты/времени в JSON.
        LocalDateTime timestamp,
        String errorCode,
        String message,
        String details,
        String path                        // Путь к API, где произошла ошибка, например: "/api/orders/123"
) {

    public ErrorResponse(String errorCode, String message, String details, String path) {
        this(LocalDateTime.now(), errorCode, message, details, path);
    }

    public ErrorResponse(String errorCode, String message, String path) {
        this(LocalDateTime.now(), errorCode, message, null, path);
    }
}