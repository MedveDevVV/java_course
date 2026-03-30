package autoservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(schema = "service", name = "workshop_places")
public class WorkshopPlace implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 1001L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    protected WorkshopPlace() {
    }

    public WorkshopPlace(String name) {
        this.name = name;
    }

    public WorkshopPlace(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || id == null || ((WorkshopPlace) o).id == null || getClass() != o.getClass()) return false;
        return id.equals(((WorkshopPlace) o).id);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }
}