package autoservice.utils;

import autoservice.exception.InvalidDateException;

import java.time.LocalDate;
import java.util.Objects;

public class DateUtils {

    /**
     * Проверяет корректность диапазона дат
     *
     * @param start начальная дата диапазона (не может быть null)
     * @param end конечная дата диапазона (не может быть null)
     * @throws InvalidDateException если начальная дата находится в прошлом
     *                              или конечная дата раньше начальной
     * @throws NullPointerException если start или end равны null
     */
    public static void validateDateRange(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "Дата начала не может быть null");
        Objects.requireNonNull(end, "Дата окончания не может быть null");

        if (start.isBefore(LocalDate.now()))
            throw new InvalidDateException("Дата начала не может быть в прошлом: " + start);
        if (end.isBefore(start))
            throw new InvalidDateException(
                    String.format("Дата окончания (%s) не может быть раньше даты начала (%s)", end, start)
                    );
    }

    public static boolean isDateInRange(LocalDate dateToCheck,
                                        LocalDate startDate,
                                        LocalDate endDate) {

        Objects.requireNonNull(dateToCheck, "Проверяемая дата не может быть null");
        Objects.requireNonNull(startDate, "Дата начала не может быть null");
        Objects.requireNonNull(endDate, "Дата окончания не может быть null");

        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }
}