package autoservice.repository.impl;

import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class GarageWorkshopPlaceRepository implements WorkshopPlaceRepository, Serializable {
    @Serial
    private static final long serialVersionUID = 7001L;
    private Map<String, WorkshopPlace> places = new HashMap<>();

    @Override
    public void addPlace(WorkshopPlace place) {
        places.putIfAbsent(place.getName(), place);
    }

    @Override
    public void removePlace(WorkshopPlace place) {
        places.remove(place.getName());
    }

    @Override
    public List<WorkshopPlace> getAllPlaces() {
        return new ArrayList<>(places.values());
    }

    @Override
    public WorkshopPlace findByName(String name) {
        return places.get(name);
    }

    @Override
    public void setAllPlaces(List<WorkshopPlace> places) {
        this.places = new HashMap<>();
        for (WorkshopPlace place : places) {
            this.places.put(place.getName(), place);
        }
    }
}
