package autoservice.model;

import autoservice.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class RepairOrder implements
        Order,
        OrderStatusOperation,
        OrderDateOperations,
        OrderPriceOperations,
        OrderAssignment , Serializable {
    @Serial
    private static final long serialVersionUID = 2001L;
    private UUID uuid;
    private LocalDate creationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private CarServiceMaster carServiceMaster;
    private WorkshopPlace place;
    private OrderStatus status;

    private String description;

    private Float totalPrice;
    protected RepairOrder() {
    }

    public RepairOrder(RepairOrder other) {
        this.uuid = other.uuid;
        this.creationDate = other.creationDate;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
        this.description = other.description;
        this.status = other.status;
        this.totalPrice = other.totalPrice;
        this.carServiceMaster = other.carServiceMaster;
        this.place = other.place;
    }

    public RepairOrder(LocalDate creationDate, LocalDate startDate, LocalDate endDate, String description) {
        this.uuid = UUID.randomUUID();
        this.creationDate = creationDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = OrderStatus.CREATED;
    }

    public RepairOrder(LocalDate creationDate, LocalDate startDate, LocalDate endDate, String description,
                       OrderStatus status, Float totalPrice, UUID uuid, CarServiceMaster carServiceMaster,
                       WorkshopPlace place) {
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.carServiceMaster = carServiceMaster;
        this.place = place;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
        this.totalPrice = totalPrice;
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
    public Person getCarServiceMaster() {
        return carServiceMaster;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
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
    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setCarServiceMaster(Person person) {
        this.carServiceMaster = (CarServiceMaster) person;
    }

    @Override
    public void cancel() {
        status = OrderStatus.CANCELLED;
    }

    @Override
    public void closed() {
        status = OrderStatus.CLOSED;
    }

    public UUID getCarServiceMasterId() {
        return carServiceMaster != null ? carServiceMaster.getId() : null;
    }

    public UUID getPlaceId() {
        return place != null ? place.getId() : null;
    }

    public WorkshopPlace getWorkshopPlace() {
        return place;
    }

    public void setWorkshopPlace(WorkshopPlace place) {
        this.place = place;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected void setStatus(OrderStatus status) {
        this.status = status;
    }

    protected void setId(UUID id){
        this.uuid = id;
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
}