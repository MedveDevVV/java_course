import java.time.LocalDate;

public abstract class Person {
    private String name;
    private String surname;
    private String patronymic;

    private LocalDate dateOfBirth;

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Person(String name, String surname, String patronymic, LocalDate dateOfBirth) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName(){
        return surname + " " + name + " " + patronymic;
    }
}
