import java.util.List;

public interface WorkshopPlaceRepository {
    void addPlace(WorkshopPlace place);
    boolean removePlace(WorkshopPlace place);
    List<WorkshopPlace> getAllPlaces();
    WorkshopPlace findByName(String name);
    List<WorkshopPlace> getAvailablePlaces();
}
