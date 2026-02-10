package autoservice.repository.impl;

import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GarageMasterRepository implements MasterRepository, Serializable {
    @Serial
    private static final long serialVersionUID = 5001L;
    private List<CarServiceMaster> masters = new ArrayList<>();

    @Override
    public void addMaster(CarServiceMaster master) {
        masters.add(master);
    }

    @Override
    public void removeMaster(CarServiceMaster master) {
        masters.remove(master);
    }

    @Override
    public List<CarServiceMaster> getAllMasters() {
        return new ArrayList<>(masters);
    }

    @Override
    public Optional<CarServiceMaster> findMasterByFullName(String fullName){
        return masters.stream().filter(o -> o.getFullName().equals(fullName)).findFirst();
    }

    @Override
    public void setAllMasters(List<CarServiceMaster> masters) {
        this.masters = new ArrayList<>(masters);
    }
}
