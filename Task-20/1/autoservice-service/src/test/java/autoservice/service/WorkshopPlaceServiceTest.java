package autoservice.service;

import autoservice.config.AppConfig;
import autoservice.exception.DaoException;
import autoservice.exception.DuplicateEntityException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.WorkshopPlaceNotFoundException;
import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkshopPlaceServiceTest {

    @Mock
    private WorkshopPlaceRepository repository;

    @Mock
    AppConfig appConfig;

    private WorkshopPlaceService service;

    @BeforeEach
    void setup() {
        service = new WorkshopPlaceService(appConfig, repository);
    }

    @Test
    void getAllPlaces_ShouldReturnAllPlaces() {
        WorkshopPlace place1 = new WorkshopPlace(UUID.randomUUID(), "Место 1");
        WorkshopPlace place2 = new WorkshopPlace(UUID.randomUUID(), "Место 2");
        List<WorkshopPlace> expectedList = List.of( place1, place2);

        when(repository.getAllPlaces()).thenReturn(expectedList);

        List<WorkshopPlace> actualPlaces = service.getAllPlaces();

        assertEquals(expectedList.size(), actualPlaces.size());
        assertTrue(actualPlaces.contains(place1));
        assertTrue(actualPlaces.contains(place2));
        verify(repository).getAllPlaces();
    }

    @Test
    void findById_ShouldReturnWorkshopPlace() {
        UUID placeId = UUID.randomUUID();
        WorkshopPlace expectedPlace = new WorkshopPlace(placeId, "Место 1");

        when(repository.findById(placeId)).thenReturn(Optional.of(expectedPlace));

        WorkshopPlace actualPlace = service.findById(placeId);

        assertEquals(expectedPlace, actualPlace);
        verify(repository).findById(placeId);
    }

    @Test
    void findById_ShouldThrowWorkshopNotFoundException_WhenPlaceNotFound() {
        UUID placeId = UUID.randomUUID();

        when(repository.findById(placeId)).thenReturn(Optional.empty());

        WorkshopPlaceNotFoundException exception = assertThrows(WorkshopPlaceNotFoundException.class, () -> {
            service.findById(placeId);
        });

        assertTrue(exception.getMessage().contains(placeId.toString()));
        verify(repository).findById(placeId);
    }

    @Test
    void finByNameContaining_ShouldReturnListOfWorkshopPlace() {
        String namePart = "подъемник";
        WorkshopPlace place1 = new WorkshopPlace(UUID.randomUUID(), "Подъемник 1");
        WorkshopPlace place2 = new WorkshopPlace(UUID.randomUUID(), "Подъемник 2");
        List<WorkshopPlace> expectedList = List.of(place1, place2);

        when(repository.findByNameContaining(namePart)).thenReturn(expectedList);

        List<WorkshopPlace> actualPlaces = service.findByNameContaining(namePart);

        assertEquals(expectedList.size(), actualPlaces.size());
        assertTrue(actualPlaces.contains(place1));
        assertTrue(actualPlaces.contains(place2));
        verify(repository).findByNameContaining(namePart);
    }

    @Test
    void addPlace_ShouldAddNewPlace() {
        WorkshopPlace expectedPlace = new WorkshopPlace(UUID.randomUUID(), "Место 1");

        when(appConfig.isCanAddPlaces()).thenReturn(true);

        WorkshopPlace  actualPlace = service.addPlace(expectedPlace);

        assertEquals(expectedPlace, actualPlace);
        verify(repository).addPlace(expectedPlace);
        verify(appConfig).isCanAddPlaces();
    }

    @Test
    void addPlace_ShouldThrowDuplicatePlaceException_WhenPlaceAlreadyExists() {
        WorkshopPlace place = new WorkshopPlace(UUID.randomUUID(), "Место 1");
        SQLException sqlException = new SQLException("Duplicate entry", "23000", 1062);
        ConstraintViolationException constraintViolationException =
                new ConstraintViolationException("Duplicate entry", sqlException, "workshop_place_name_unique");
        DaoException daoException = new DaoException("Нарушение ограничений", constraintViolationException);

        when(appConfig.isCanAddPlaces()).thenReturn(true);
        doThrow(daoException).when(repository).addPlace(place);

        DuplicateEntityException exception = assertThrows(DuplicateEntityException.class, () -> {
            service.addPlace(place);
        });

        assertTrue(exception.getMessage().contains(place.getName()));
        verify(appConfig).isCanAddPlaces();
        verify(repository).addPlace(place);
    }

    @Test
    void addPlace_ShouldThrowOperationNotAllowedException_WhenAddingPlacesIsDisabledByConfig() {
        WorkshopPlace place = new WorkshopPlace(UUID.randomUUID(), "Место 1");

        when(appConfig.isCanAddPlaces()).thenReturn(false);
        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class, () -> {
            service.addPlace(place);
        });

        verify(appConfig).isCanAddPlaces();
        verify(repository, never()).addPlace(place);
    }

    @Test
    void updatePlace_ShouldUpdatePlace() {
        UUID placeId = UUID.randomUUID();
        WorkshopPlace place = new WorkshopPlace(placeId, "Место 1");
        WorkshopPlace placeDetails = new WorkshopPlace("подъемник");

        when(repository.findById(placeId)).thenReturn(Optional.of(place));

        WorkshopPlace actualPlace = service.updatePlace(placeId, placeDetails);

        assertEquals(place, actualPlace);
        assertEquals(placeDetails.getName(), place.getName());
        verify(repository).findById(placeId);
    }

    @Test
    void removePlace_ShouldRemovePlace() {
        UUID placeId = UUID.randomUUID();
        WorkshopPlace place = new WorkshopPlace(placeId, "Место 1");

        when(appConfig.isCanRemovePlaces()).thenReturn(true);
        when(repository.findById(placeId)).thenReturn(Optional.of(place));

        service.removePlace(placeId);

        verify(appConfig).isCanRemovePlaces();
        verify(repository).findById(placeId);
        verify(repository).removePlace(place);
    }

    @Test
    void removePlace_ShouldThrowOperationNotAllowedException_WhenRemovingPlacesIsDisabledByConfig() {
        UUID placeId = UUID.randomUUID();

        when(appConfig.isCanRemovePlaces()).thenReturn(false);

        OperationNotAllowedException exception = assertThrows(OperationNotAllowedException.class, () -> {
            service.removePlace(placeId);
        });

        verify(appConfig).isCanRemovePlaces();
        verify(repository, never()).findById(placeId);
        verify(repository, never()).removePlace(any());
    }
}
