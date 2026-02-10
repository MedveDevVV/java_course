package autoservice.model;

import java.time.LocalDate;

public interface OrderDateOperations {
    void setStartDate(LocalDate startDate);
    void setEndDate(LocalDate endDate);
    LocalDate getStartDate();
    LocalDate getEndDate();
}
