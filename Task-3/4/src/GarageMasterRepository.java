import java.util.ArrayList;
import java.util.List;

public class GarageMasterRepository implements MasterRepository{
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
}
