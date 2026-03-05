package autoservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class Person implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3001L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    protected UUID id;

    @Column(name = "full_name", nullable = false)
    protected String fullName;

    @Column(name = "date_of_birth")
    protected LocalDate dateOfBirth;
    protected Person() {
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public Person(String fullName, LocalDate dateOfBirth) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public Person(UUID id, String fullName, LocalDate dateOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }
}
