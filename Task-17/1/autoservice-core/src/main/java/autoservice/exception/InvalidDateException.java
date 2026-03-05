package autoservice.exception;

/**
 * Исключение для ошибок валидации дат.
 */
public class InvalidDateException extends AutoServiceException {

    public InvalidDateException(String message) {
        super(message, ErrorCodes.VAL_DATE_RANGE);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, ErrorCodes.VAL_DATE_RANGE, cause);
    }
}