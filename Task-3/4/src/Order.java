import java.time.LocalDateTime;
import java.util.UUID;

public interface Order {
    public String getDescription();
    public OrderStatus getStatus();
    public LocalDateTime getStartTime();
    public LocalDateTime getEndTime();
    public UUID getId();
    public void setStartDateTime(LocalDateTime startDateTime);
    public void setEndDateTime(LocalDateTime endDateTime);
    public void cancel();
    public void closed();
    public void assignPerson(Person person);
    public Person getAssignPerson();
}
