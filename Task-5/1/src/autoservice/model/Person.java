package autoservice.model;

import java.time.LocalDate;

public abstract class Person {
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
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName(){
        return fullName;
    }

}
