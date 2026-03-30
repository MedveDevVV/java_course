package autoservice.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда заказ не найден.
 */
public class OrderNotFoundException extends AutoServiceException {

    public OrderNotFoundException(UUID orderId) {
        super(
                String.format("Заказ с ID: %s не найден в системе", orderId),
                ErrorCodes.NF_ORDER
        );
    }

    public OrderNotFoundException(String message) {
        super(message, ErrorCodes.NF_ORDER);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, ErrorCodes.NF_ORDER, cause);
    }
}