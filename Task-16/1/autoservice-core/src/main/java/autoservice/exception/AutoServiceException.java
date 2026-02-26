package autoservice.exception;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Базовое исключение для всего приложения AutoService.
 * Содержит поля для логирования и обработки ошибок.
 * Все специфичные исключения должны наследоваться от этого класса.
 */
@Getter
public class AutoServiceException extends RuntimeException {

    private final LocalDateTime timestamp;
    private final ErrorCodes errorCode;

    public AutoServiceException(String message, ErrorCodes errorCode) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }

    public AutoServiceException(String message, ErrorCodes errorCode, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
    }
}