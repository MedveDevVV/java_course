package autoservice.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда рабочее место не найдено.
 */
public class WorkshopPlaceNotFoundException extends AutoServiceException {

    public WorkshopPlaceNotFoundException(UUID placeId) {
        super(
                String.format("Рабочее место с ID: %s не найдено в системе", placeId),
                ErrorCodes.NF_WORKSHOP_PLACE
        );
    }

    public WorkshopPlaceNotFoundException(String message) {
        super(message, ErrorCodes.NF_WORKSHOP_PLACE);
    }

    public WorkshopPlaceNotFoundException(String message, Throwable cause) {
        super(message, ErrorCodes.NF_WORKSHOP_PLACE, cause);
    }
}