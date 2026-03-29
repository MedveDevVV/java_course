package autoservice.service;

import autoservice.exception.DaoException;
import autoservice.exception.MasterNotFoundException;
import autoservice.exception.ResourceBusyException;
import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceMasterServiceTest {

    @Mock
    private MasterRepository masterRepository;

    private CarServiceMasterService carServiceMasterService;

    @BeforeEach
    void setup() {
        carServiceMasterService = new CarServiceMasterService(masterRepository);
    }

    @Test
    void getAllMasters_ShouldReturnAllMasters() {
        CarServiceMaster master1 = new CarServiceMaster(UUID.randomUUID(), "Иванов Иван Иванович",
                LocalDate.of(1980, 5, 20));
        CarServiceMaster master2 = new CarServiceMaster("Петров Петр Петрович",
                LocalDate.of(1990, 1, 10));
        List<CarServiceMaster> expectedList = List.of(master1, master2);

        when(masterRepository.getAllMasters()).thenReturn(expectedList);

        List<CarServiceMaster> actualList = carServiceMasterService.getAllMasters();

        assertEquals(2, actualList.size());
        assertTrue(actualList.contains(master1));
        assertTrue(actualList.contains(master2));
        verify(masterRepository).getAllMasters();
    }

    @Test
    void findById_ShouldReturnMaster() {
        UUID masterId = UUID.randomUUID();
        CarServiceMaster master = new CarServiceMaster(masterId, "Иванов Иван Иванович",
                LocalDate.of(1980, 5, 20));

        when(masterRepository.findMasterById(masterId)).thenReturn(Optional.of(master));

        CarServiceMaster result = carServiceMasterService.findById(masterId);

        assertEquals(master, result);
        verify(masterRepository).findMasterById(masterId);
    }

    @Test
    void findById_ShouldThrowMasterNotFoundException_WhenMasterNotFound() {
        UUID masterId = UUID.randomUUID();

        when(masterRepository.findMasterById(masterId)).thenReturn(Optional.empty());

        MasterNotFoundException exception = assertThrows(
                MasterNotFoundException.class,
                () -> carServiceMasterService.findById(masterId)
        );

        assertTrue(exception.getMessage().contains(masterId.toString()));
        verify(masterRepository).findMasterById(masterId);
    }

    @Test
    void findByNameContaining_ShouldReturnListOfMasters() {
        CarServiceMaster master1 = new CarServiceMaster(UUID.randomUUID(), "Петров Петр Петрович",
                LocalDate.of(1990, 1, 10));
        CarServiceMaster master2 = new CarServiceMaster(UUID.randomUUID(), "Петухов Сергей Петрович",
                LocalDate.of(1994, 12, 11));
        List<CarServiceMaster> expectedList = List.of(master1, master2);
        String namePart = "Петр";

        when(masterRepository.findMastersByFullNameContaining(namePart)).thenReturn(expectedList);

        List<CarServiceMaster> actualList = carServiceMasterService.findByNameContaining(namePart);

        assertEquals(2, actualList.size());
        assertTrue(actualList.contains(master1));
        assertTrue(actualList.contains(master2));
        verify(masterRepository).findMastersByFullNameContaining(namePart);
    }

    @Test
    void addMaster_ShouldAddNewMaster() {
        CarServiceMaster master = new CarServiceMaster("Петров Петр Петрович",
                LocalDate.of(1990, 1, 10));

        CarServiceMaster result = carServiceMasterService.addMaster(master);

        assertEquals(result, master);
        verify(masterRepository).addMaster(master);
    }

    @Test
    void updateMaster_ShouldUpdateMaster() {
        UUID masterId = UUID.randomUUID();
        CarServiceMaster master = new CarServiceMaster(masterId, "Иванов Иван Иванович",
                LocalDate.of(1980, 5, 20));
        CarServiceMaster masterDetails = new CarServiceMaster("Петров Петр Петрович",
                LocalDate.of(1990, 1, 10));
        when(masterRepository.findMasterById(masterId)).thenReturn(Optional.of(master));

        CarServiceMaster result = carServiceMasterService.updateMaster(masterId, masterDetails);

        assertEquals(result, master);
        assertEquals(masterDetails.getFullName(), master.getFullName());
        assertEquals(masterDetails.getDateOfBirth(), master.getDateOfBirth());
        verify(masterRepository).findMasterById(masterId);
    }

    @Test
    void updateMaster_ShouldThrowMasterNotFoundException_WhenMasterNotFound() {
        UUID masterId = UUID.randomUUID();
        CarServiceMaster masterDetails = new CarServiceMaster(
                "Иванов Иван Иванович",
                LocalDate.of(1990, 5, 12)
        );

        when(masterRepository.findMasterById(masterId)).thenReturn(Optional.empty());

        MasterNotFoundException exception = assertThrows(MasterNotFoundException.class,
                () -> carServiceMasterService.updateMaster(masterId, masterDetails));

        assertTrue(exception.getMessage().contains(masterId.toString()));
        verify(masterRepository).findMasterById(masterId);
    }

    @Test
    void removeMaster_ShouldRemoveMaster() {
        CarServiceMaster master = new CarServiceMaster(
                UUID.randomUUID(),
                "Иванов Иван Иванович",
                LocalDate.of(1980, 5, 20)
        );

        carServiceMasterService.removeMaster(master);

        verify(masterRepository).removeMaster(master);
    }

    @Test
    void removeMaster_WhenMasterHasOrders_ShouldThrowResourceBusyException() {
        CarServiceMaster master = new CarServiceMaster(
                UUID.randomUUID(),
                "Иванов Иван Иванович",
                LocalDate.of(1980, 5, 20)
        );
        SQLException sqlException = new SQLException("Foreign key violation");
        ConstraintViolationException violationException =
                new ConstraintViolationException("Foreign key violation", sqlException, "fk_order_master");
        DaoException daoException = new DaoException("Нарушение ограничений", violationException);
        doThrow(daoException).when(masterRepository).removeMaster(master);

        ResourceBusyException exception = assertThrows(ResourceBusyException.class,
                () -> carServiceMasterService.removeMaster(master));

        assertTrue(exception.getMessage().contains(master.getFullName()));
        verify(masterRepository).removeMaster(master);
    }
}
