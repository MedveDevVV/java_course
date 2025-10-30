package autoservice.repository;

import autoservice.model.CarServiceMaster;

import java.util.List;
import java.util.Optional;

public interface MasterRepository{
    void addMaster(CarServiceMaster master);
    void removeMaster(CarServiceMaster master);
    List<CarServiceMaster> getAllMasters();
    Optional<CarServiceMaster> findMasterByFullName(String surname, String name, String patronymic);
}
