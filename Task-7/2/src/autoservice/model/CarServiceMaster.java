package autoservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class CarServiceMaster extends Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 4001L;

    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    public CarServiceMaster(String fullName, LocalDate dateOfBirth) {
        super(fullName, dateOfBirth);
    }

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
