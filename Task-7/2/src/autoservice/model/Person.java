package autoservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public abstract class Person implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3001L;
    private final UUID id;
    private String fullName;

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    private LocalDate dateOfBirth;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    public Person(String fullName, LocalDate dateOfBirth) {
        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    public Person(UUID id, String fullName, LocalDate dateOfBirth) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName(){
        return fullName;
    }

    @Override
    public UUID getId(){
        return id;
    }
}
