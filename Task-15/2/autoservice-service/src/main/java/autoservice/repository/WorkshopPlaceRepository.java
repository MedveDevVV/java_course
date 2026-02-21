package autoservice.repository;

import autoservice.model.WorkshopPlace;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public interface WorkshopPlaceRepository {
    void addPlace(WorkshopPlace place);
    void removePlace(WorkshopPlace place);
    List<WorkshopPlace> getAllPlaces();
    WorkshopPlace findByName(String name);
}
