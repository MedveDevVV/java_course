package autoservice.exception;

import java.util.UUID;

/**
 * Исключение, когда у заказа нет назначенного мастера.
 */
public class MasterNotAssignedException extends AutoServiceException {

    public MasterNotAssignedException(UUID orderId) {
        super(
                String.format("На заказ %s не назначен мастер", orderId),
                ErrorCodes.NF_MASTER_NOT_ASSIGNED
        );
    }

    public MasterNotAssignedException(String message) {
        super(message, ErrorCodes.NF_MASTER_NOT_ASSIGNED);
    }
}