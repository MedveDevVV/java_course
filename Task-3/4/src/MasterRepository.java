import java.util.List;

public interface MasterRepository{
    void addMaster(CarServiceMaster master);
    void removeMaster(CarServiceMaster master);
    List<CarServiceMaster> getAllMasters();
}
