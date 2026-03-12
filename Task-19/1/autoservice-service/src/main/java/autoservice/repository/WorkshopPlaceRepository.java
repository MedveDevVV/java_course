package autoservice.repository;

import autoservice.model.WorkshopPlace;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface WorkshopPlaceRepository {
    void addPlace(WorkshopPlace place);
    void removePlace(WorkshopPlace place);
    List<WorkshopPlace> getAllPlaces();
    Optional<WorkshopPlace> findByName(String name);
    Optional<WorkshopPlace> findById(UUID id);
    List<WorkshopPlace> findByNameContaining(String name);
}
