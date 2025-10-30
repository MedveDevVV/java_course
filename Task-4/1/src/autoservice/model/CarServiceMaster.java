package autoservice.model;

import java.time.LocalDate;

public class CarServiceMaster extends Person {

    public CarServiceMaster(String surname, String name, String patronymic, LocalDate dateOfBirth) {
        super(surname, name, patronymic, dateOfBirth);
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
