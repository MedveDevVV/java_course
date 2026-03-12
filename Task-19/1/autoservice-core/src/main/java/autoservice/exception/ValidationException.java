package autoservice.exception;

/**
 * Исключение для ошибок валидации данных.
 */
public class ValidationException extends AutoServiceException {

    public ValidationException(String message) {
        super(message, ErrorCodes.VAL_GENERAL);
    }

    public ValidationException(String field, String problem) {
        super(
                String.format("Поле '%s': %s", field, problem),
                ErrorCodes.VAL_GENERAL
        );
    }
}