package autoservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public abstract class Person implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3001L;
    private UUID id;
    private String fullName;
    private LocalDate dateOfBirth;

    protected Person(){
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

    @Override
    public UUID getId(){
        return id;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    protected void setId(UUID id){
        this.id = id;
    }
}
