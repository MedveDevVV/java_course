package autoservice.repository.impl;

import autoservice.dao.WorkshopPlaceDAO;
import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;

import java.util.List;
import java.util.Optional;

public class DatabaseWorkshopPlaceRepository implements WorkshopPlaceRepository {
    private final WorkshopPlaceDAO placeDAO;

    public DatabaseWorkshopPlaceRepository() {
        this.placeDAO = WorkshopPlaceDAO.getINSTANCE();
    }

    @Override
    public void addPlace(WorkshopPlace place) {
        placeDAO.save(place);
    }

    @Override
    public void removePlace(WorkshopPlace place) {
        placeDAO.delete(place);
    }

    @Override
    public List<WorkshopPlace> getAllPlaces() {
        return placeDAO.findAll();
    }

    @Override
    public WorkshopPlace findByName(String name) {
        Optional<WorkshopPlace> workshopPlace =  placeDAO.findByName(name);
        return workshopPlace.orElse(null);
    }
}
