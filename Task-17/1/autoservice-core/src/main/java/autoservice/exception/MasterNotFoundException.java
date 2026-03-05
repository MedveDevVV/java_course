package autoservice.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда мастер не найден.
 */
public class MasterNotFoundException extends AutoServiceException {

    public MasterNotFoundException(UUID masterId) {
        super(
                String.format("Мастер с ID: %s не найден в системе", masterId),
                ErrorCodes.NF_MASTER
        );
    }

    public MasterNotFoundException(String message) {
        super(message, ErrorCodes.NF_MASTER);
    }

    public MasterNotFoundException(String message, Throwable cause) {
        super(message, ErrorCodes.NF_MASTER, cause);
    }
}