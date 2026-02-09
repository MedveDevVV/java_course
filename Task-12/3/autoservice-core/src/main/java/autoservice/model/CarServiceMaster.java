package autoservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class CarServiceMaster extends Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 4001L;

    protected CarServiceMaster() {
        super();
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public CarServiceMaster(String fullName, LocalDate dateOfBirth) {
        super(fullName, dateOfBirth);
    }

    /**
     * @param fullName Полное имя в формате "Фамилия Имя Отчество"
     */
    public CarServiceMaster(UUID id, String fullName, LocalDate dateOfBirth) {
        super(id, fullName, dateOfBirth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || id == null) return false;
        return id.equals(((CarServiceMaster) o).id);
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }
}
