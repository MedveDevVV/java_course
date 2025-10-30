package autoservice.model;

import java.time.LocalDate;

public abstract class Person {
    private final String fullName;

    private LocalDate dateOfBirth;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Person(String surname, String name, String patronymic, LocalDate dateOfBirth) {
        this.fullName = String.join(" ", surname, name, patronymic);
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName(){
        return fullName;
    }

}
