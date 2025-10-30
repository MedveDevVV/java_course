package autoservice.model;

import autoservice.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public interface Order {
    public String getDescription();
    public OrderStatus getStatus();
    public UUID getId();
    public void setStartDate(LocalDate startDate);
    public void setEndDate(LocalDate endDate);
    public void setTotalPrice(Float totalPrice);
    public void cancel();
    public void closed();
    public void assignPerson(Person person);
    public LocalDate getStartDate();
    public LocalDate getEndDate();
    public Float getTotalPrice();
    public Person getAssignPerson();
    public LocalDate getCreationDate();
}
