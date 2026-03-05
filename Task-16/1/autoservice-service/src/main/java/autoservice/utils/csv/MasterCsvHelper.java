package autoservice.utils.csv;

import autoservice.model.CarServiceMaster;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Function;

public class MasterCsvHelper {
    public static final String[] MASTER_HEADER = {"id", "surname", "name", "patronymic", "dateOfBirth"};

    public static Function<CarServiceMaster, String[]> masterToFields = master -> {
        String[] fields = new String[5];
        fields[0] = master.getId().toString();
        fields[1] = master.getFullName().split(" ")[0]; // surname
        fields[2] = master.getFullName().split(" ")[1]; // name
        fields[3] = master.getFullName().split(" ")[2]; // patronymic
        fields[4] = master.getDateOfBirth().toString();
        return fields;
    };

    public static Function<String[], CarServiceMaster> fieldsToMaster = fields -> {
        UUID id = UUID.fromString(fields[0]);
        String surname = fields[1];
        String name = fields[2];
        String patronymic = fields[3];
        LocalDate dob = LocalDate.parse(fields[4]);
        return new CarServiceMaster(id,
                String.join(" ", surname, name, patronymic),
                dob);
    };
}