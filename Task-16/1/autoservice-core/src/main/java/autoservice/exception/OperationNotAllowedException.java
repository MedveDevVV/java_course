package autoservice.exception;

/**
 * Исключение, когда операция не разрешена в текущем состоянии.
 */
public class OperationNotAllowedException extends AutoServiceException {

    public OperationNotAllowedException(String entity, String currentStatus, String operation) {
        super(
                String.format("Невозможно выполнить '%s' для %s в статусе '%s'",
                        operation, entity, currentStatus),
                ErrorCodes.BIZ_OPERATION_NOT_ALLOWED
        );
    }

    public OperationNotAllowedException(String message) {
        super(message, ErrorCodes.BIZ_OPERATION_NOT_ALLOWED);
    }
}