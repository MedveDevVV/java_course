package autoservice.exception;

import java.time.LocalDate;

/**
 * Исключение для конфликтов в расписании.
 */
public class ScheduleConflictException extends AutoServiceException {

    public ScheduleConflictException(LocalDate date, String resourceName, String resourceId) {
        super(
                String.format("Конфликт расписания на %s. %s %s уже занят(а).",
                        date, resourceName, resourceId),
                ErrorCodes.BIZ_SCHEDULE_CONFLICT
        );
    }

    public ScheduleConflictException(String message) {
        super(message, ErrorCodes.BIZ_SCHEDULE_CONFLICT);
    }
}