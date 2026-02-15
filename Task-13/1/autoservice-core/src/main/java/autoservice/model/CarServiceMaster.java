package autoservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(schema = "service", name = "car_service_masters")
public class CarServiceMaster extends Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 4001L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    protected UUID id;

    @Column(name = "full_name", nullable = false, length = 100)
    protected String fullName;

    @Column(name = "date_of_birth")
    protected LocalDate dateOfBirth;

    protected CarServiceMaster() {
        super();
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public CarServiceMaster(String fullName, LocalDate dateOfBirth) {
        super(fullName, dateOfBirth);
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public CarServiceMaster(UUID id, String fullName, LocalDate dateOfBirth) {
        super(id, fullName, dateOfBirth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || id == null) return false;
        return id.equals(((CarServiceMaster) o).id);
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }
}
