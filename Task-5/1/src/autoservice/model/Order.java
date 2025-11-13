package autoservice.model;

import autoservice.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public interface Order {
    public UUID getId();
    public String getDescription();
    public OrderStatus getStatus();
    public LocalDate getCreationDate();
}
