package autoservice.exception;

import java.time.LocalDate;

/**
 * Исключение, когда ресурс занят (мастер или рабочее место).
 */
public class ResourceBusyException extends AutoServiceException {

    public ResourceBusyException(String resourceType, String resourceName,
                                 LocalDate from, LocalDate to) {
        super(
                String.format("%s '%s' занят(а) с %s по %s",
                        resourceType, resourceName, from, to),
                ErrorCodes.BIZ_RESOURCE_BUSY
        );
    }

    public ResourceBusyException(String message) {
        super(message, ErrorCodes.BIZ_RESOURCE_BUSY);
    }
}