package autoservice.model;

import java.time.LocalDate;

public interface OrderDateOperations {
    public void setStartDate(LocalDate startDate);
    public void setEndDate(LocalDate endDate);
    public LocalDate getStartDate();
    public LocalDate getEndDate();
}
