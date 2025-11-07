package autoservice.model;

import java.time.LocalDate;

public class CarServiceMaster extends Person {

    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    public CarServiceMaster(String fullName, LocalDate dateOfBirth) {
        super(fullName, dateOfBirth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getFullName().equals(((CarServiceMaster) o).getFullName());
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

}
