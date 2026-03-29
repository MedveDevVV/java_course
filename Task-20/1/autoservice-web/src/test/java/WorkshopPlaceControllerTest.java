import autoservice.controller.WorkshopPlaceController;
import autoservice.dto.WorkshopPlaceDTO;
import autoservice.exception.DuplicateEntityException;
import autoservice.exception.WorkshopPlaceNotFoundException;
import autoservice.handler.GlobalExceptionHandler;
import autoservice.mapper.WorkshopPlaceMapper;
import autoservice.model.WorkshopPlace;
import autoservice.service.WorkshopPlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class WorkshopPlaceControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    WorkshopPlaceService service;

    @Mock
    WorkshopPlaceMapper mapper;

    private UUID id1;
    private UUID id2;
    private WorkshopPlace place1;
    private WorkshopPlace place2;
    private WorkshopPlaceDTO dto1;
    private WorkshopPlaceDTO dto2;

    @BeforeEach
    void setup() {
        WorkshopPlaceController controller = new WorkshopPlaceController(service, mapper);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setMessageInterpolator(new ParameterMessageInterpolator());
        validator.afterPropertiesSet();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();

        place1 = new WorkshopPlace(id1, "Гараж 1");
        place2 = new WorkshopPlace(id2, "Гараж 2");

        dto1 = new WorkshopPlaceDTO(id1, "Гараж 1");
        dto2 = new WorkshopPlaceDTO(id2, "Гараж 2");
    }

    @Test
    void getAllPlaces_ShouldReturnAllPlaces() throws Exception {
        when(service.getAllPlaces()).thenReturn(List.of(place1, place2));
        when(mapper.toDTO(place1)).thenReturn(dto1);
        when(mapper.toDTO(place2)).thenReturn(dto2);

        mockMvc.perform(get("/api/workshop-places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Гараж 1"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("Гараж 2"));

        verify(service).getAllPlaces();
        verify(mapper).toDTO(place1);
        verify(mapper).toDTO(place2);
    }

    @Test
    void getAllPlaces_WhenNoPlaces_ShouldReturnEmptyList() throws Exception {
        when(service.getAllPlaces()).thenReturn(List.of());

        mockMvc.perform(get("/api/workshop-places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).getAllPlaces();
        verifyNoInteractions(mapper);
    }

    @Test
    void getPlaceById_ShouldReturnPlace() throws Exception {
        when(service.findById(id1)).thenReturn(place1);
        when(mapper.toDTO(place1)).thenReturn(dto1);

        mockMvc.perform(get("/api/workshop-places/" + id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id1.toString()))
                .andExpect(jsonPath("$.name").value("Гараж 1"));

        verify(service).findById(id1);
        verify(mapper).toDTO(place1);
    }

    @Test
    void getPlaceById_WhenPlaceNotFound_ShouldReturnNotFound() throws Exception {
        when(service.findById(id1)).thenThrow(new WorkshopPlaceNotFoundException(id1));

        mockMvc.perform(get("/api/workshop-places/" + id1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_PLACE_001")))
                .andExpect(jsonPath("$.message", is("Рабочее место с ID: " + id1 + " не найдено в системе")));

        verify(service).findById(id1);
    }

    @Test
    void searchByName_ShouldReturnMatchingPlaces() throws Exception {
        when(service.findByNameContaining("Гараж")).thenReturn(List.of(place1));
        when(mapper.toDTO(place1)).thenReturn(dto1);

        mockMvc.perform(get("/api/workshop-places/search").param("name", "Гараж"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Гараж 1"));

        verify(service).findByNameContaining("Гараж");
        verify(mapper).toDTO(place1);
    }

    @Test
    void searchByName_WhenNoMatches_ShouldReturnEmptyList() throws Exception {
        when(service.findByNameContaining("Нет")).thenReturn(List.of());

        mockMvc.perform(get("/api/workshop-places/search").param("name", "Нет"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).findByNameContaining("Нет");
        verifyNoInteractions(mapper);
    }

    @Test
    void createPlace_WithValidData_ShouldCreatePlace() throws Exception {
        WorkshopPlaceDTO request = new WorkshopPlaceDTO(null, "Новый бокс");
        WorkshopPlace savedPlace = new WorkshopPlace(UUID.randomUUID(), "Новый бокс");
        WorkshopPlaceDTO savedDto = new WorkshopPlaceDTO(savedPlace.getId(), savedPlace.getName());

        when(service.addPlace(any(WorkshopPlace.class))).thenReturn(savedPlace);
        when(mapper.toDTO(savedPlace)).thenReturn(savedDto);

        mockMvc.perform(post("/api/workshop-places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedPlace.getId().toString()))
                .andExpect(jsonPath("$.name").value("Новый бокс"));

        verify(service).addPlace(any(WorkshopPlace.class));
        verify(mapper).toDTO(savedPlace);
    }

    @Test
    void createPlace_WhenNameAlreadyExists_ShouldReturnConflict() throws Exception {
        WorkshopPlaceDTO request = new WorkshopPlaceDTO(null, "Гараж 1");

        when(service.addPlace(any(WorkshopPlace.class)))
                .thenThrow(new DuplicateEntityException("Рабочее место", "именем", "Гараж 1"));

        mockMvc.perform(post("/api/workshop-places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("BIZ_DUPLICATE_001")));

        verify(service).addPlace(any(WorkshopPlace.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void updatePlace_WithValidData_ShouldReturnUpdatedPlace() throws Exception {
        WorkshopPlaceDTO request = new WorkshopPlaceDTO(null, "Обновленный бокс");
        WorkshopPlace updatedPlace = new WorkshopPlace(id1, "Обновленный бокс");
        WorkshopPlaceDTO updatedDto = new WorkshopPlaceDTO(id1, "Обновленный бокс");

        when(service.updatePlace(eq(id1), any(WorkshopPlace.class))).thenReturn(updatedPlace);
        when(mapper.toDTO(updatedPlace)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/workshop-places/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id1.toString()))
                .andExpect(jsonPath("$.name").value("Обновленный бокс"));

        verify(service).updatePlace(eq(id1), any(WorkshopPlace.class));
        verify(mapper).toDTO(updatedPlace);
    }

    @Test
    void updatePlace_WhenPlaceNotFound_ShouldReturnNotFound() throws Exception {
        WorkshopPlaceDTO request = new WorkshopPlaceDTO(null, "Обновленный бокс");

        when(service.updatePlace(eq(id1), any(WorkshopPlace.class)))
                .thenThrow(new WorkshopPlaceNotFoundException(id1));

        mockMvc.perform(put("/api/workshop-places/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_PLACE_001")))
                .andExpect(jsonPath("$.message", is("Рабочее место с ID: " + id1 + " не найдено в системе")));

        verify(service).updatePlace(eq(id1), any(WorkshopPlace.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void deletePlace_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/workshop-places/" + id1))
                .andExpect(status().isNoContent());

        verify(service).removePlace(id1);
    }

    @Test
    void deletePlace_WhenPlaceNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new WorkshopPlaceNotFoundException(id1)).when(service).removePlace(id1);

        mockMvc.perform(delete("/api/workshop-places/" + id1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_PLACE_001")))
                .andExpect(jsonPath("$.message", is("Рабочее место с ID: " + id1 + " не найдено в системе")));

        verify(service).removePlace(id1);
        verify(service, never()).findById(id1);
    }
}

