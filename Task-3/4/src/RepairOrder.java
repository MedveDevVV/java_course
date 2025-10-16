import java.time.LocalDateTime;
import java.util.UUID;

public class RepairOrder implements Order {
    private final UUID uuid;
    private CarServiceMaster carServiceMaster;
    private WorkshopPlace place;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String description;
    private OrderStatus status;

    public RepairOrder(LocalDateTime startDateTime, LocalDateTime endDateTime, String description) {
        this.uuid = UUID.randomUUID();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.status = OrderStatus.CREATED;
    }

    @Override
    public void cancel() {
        status = OrderStatus.CANCELLED;
        place.setOccupied(false);
    }

    @Override
    public void closed() {
        status = OrderStatus.CLOSED;
        place.setOccupied(false);
    }

    @Override
    public void assignPerson(Person person) {
        this.carServiceMaster = (CarServiceMaster) person;
    }

    public void setWorkshopPlace(WorkshopPlace place) {
        this.place = place;
        place.setOccupied(true);
    }

    @Override
    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Override
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Person getAssignPerson() {
        return carServiceMaster;
    }

    public WorkshopPlace getWorkshopPlace() {
        return place;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startDateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endDateTime;
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
        return "Order{" + " uuid=" + uuid + ", status=" + status +
                "\nDescription=" + description + "\nPlace=" + place +
                ", carServiceMaster=" + carServiceMaster + ", starTime=" +
                startDateTime + ", endTime" + endDateTime + '}';
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