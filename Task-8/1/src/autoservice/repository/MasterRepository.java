package autoservice.repository;

import autoservice.model.CarServiceMaster;

import java.util.List;
import java.util.Optional;

public interface MasterRepository{
    void addMaster(CarServiceMaster master);
    void removeMaster(CarServiceMaster master);
    List<CarServiceMaster> getAllMasters();
    /**@param fullName Полное имя в формате "Фамилия Имя Отчество"*/
    Optional<CarServiceMaster> findMasterByFullName(String fullName);
    void setAllMasters(List<CarServiceMaster> masters);
}
