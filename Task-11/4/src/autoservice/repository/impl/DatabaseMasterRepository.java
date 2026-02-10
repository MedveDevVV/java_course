package autoservice.repository.impl;

import autoservice.dao.CarServiceMasterDAO;
import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;

import java.util.List;
import java.util.Optional;

public class DatabaseMasterRepository implements MasterRepository {
    private final CarServiceMasterDAO masterDAO;

    public DatabaseMasterRepository(){
        this.masterDAO = CarServiceMasterDAO.getINSTANCE();
    }

    @Override
    public void addMaster(CarServiceMaster master) {
        masterDAO.save(master);
    }

    @Override
    public void removeMaster(CarServiceMaster master) {
        masterDAO.delete(master);
    }

    @Override
    public List<CarServiceMaster> getAllMasters() {
        return masterDAO.findAll();
    }

    @Override
    public Optional<CarServiceMaster> findMasterByFullName(String fullName) {
        return masterDAO.findByFullName(fullName);
    }
}
