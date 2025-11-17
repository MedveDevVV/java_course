package autoservice.repository;

import autoservice.model.WorkshopPlace;

import java.time.LocalDate;
import java.util.List;

public interface WorkshopPlaceRepository {
    void addPlace(WorkshopPlace place);
    void removePlace(WorkshopPlace place);
    List<WorkshopPlace> getAllPlaces();
    WorkshopPlace findByName(String name);
}
