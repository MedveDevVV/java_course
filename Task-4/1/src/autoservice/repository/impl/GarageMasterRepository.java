package autoservice.repository.impl;

import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GarageMasterRepository implements MasterRepository {
    private final List<CarServiceMaster> masters = new ArrayList<>();

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
    public Optional<CarServiceMaster> findMasterByFullName(String surname, String name, String patronymic){
        String fullName = String.join(" ", surname, name, patronymic);
        return masters.stream().filter(o -> o.getFullName().equals(fullName)).findFirst();
    }
}
