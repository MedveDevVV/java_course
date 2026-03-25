package autoservice.model;

import autoservice.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public interface Order extends Identifiable {
    UUID getId();

    String getDescription();

    OrderStatus getStatus();

    LocalDate getCreationDate();
}

