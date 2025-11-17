package autoservice.model;

import java.time.LocalDate;
import java.util.UUID;

public class CarServiceMaster extends Person {

    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    public CarServiceMaster(UUID id, String fullName, LocalDate dateOfBirth) {
        super(id, fullName, dateOfBirth);
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
