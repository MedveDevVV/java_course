package autoservice.model;

import autoservice.enums.OrderStatus;

import java.time.LocalDate;
import java.util.UUID;

public class RepairOrder implements Order {
    private final UUID uuid;
    private CarServiceMaster carServiceMaster;
    private WorkshopPlace place;
    private LocalDate startDate;
    private final LocalDate creationDate;
    private LocalDate endDate;
    private String description;
    private OrderStatus status;
    private Float totalPrice;

    public RepairOrder(LocalDate creationDate, LocalDate startDate, LocalDate endDate, String description) {
        this.uuid = UUID.randomUUID();
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = OrderStatus.CREATED;
    }

    @Override
    public Float getTotalPrice() {
        return totalPrice;
    }

    @Override
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Override
    public void cancel() {
        status = OrderStatus.CANCELLED;
    }

    @Override
    public void closed() {
        status = OrderStatus.CLOSED;
    }

    @Override
    public void assignPerson(Person person) {
        this.carServiceMaster = (CarServiceMaster) person;
    }

    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public Person getAssignPerson() {
        return carServiceMaster;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "model.Order{" + " uuid=" + uuid + ", status=" + status +
                "\nDescription=" + description + "\nPlace=" + place +
                ", carServiceMaster=" + carServiceMaster +
                ", starDate=" + startDate +
                ", endDate=" + endDate + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairOrder order = (RepairOrder) o;
        return uuid.equals(order.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void setWorkshopPlace(WorkshopPlace place) {
        this.place = place;
    }

    public WorkshopPlace getWorkshopPlace() {
        return place;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}