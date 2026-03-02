package autoservice.mapper;

import autoservice.dto.CarServiceMasterDTO;
import autoservice.model.CarServiceMaster;
import org.springframework.stereotype.Component;

@Component
public class CarServiceMasterMapper {

    public CarServiceMasterDTO toDTO(CarServiceMaster master) {
        return new CarServiceMasterDTO(
                master.getId(),
                master.getFullName(),
                master.getDateOfBirth()
        );
    }
}