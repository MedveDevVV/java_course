import autoservice.controller.CarServiceMasterController;
import autoservice.dto.CarServiceMasterDTO;
import autoservice.exception.MasterNotFoundException;
import autoservice.handler.GlobalExceptionHandler;
import autoservice.mapper.CarServiceMasterMapper;
import autoservice.model.CarServiceMaster;
import autoservice.service.CarServiceMasterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)

public class CarServiceControllerTest {
    private MockMvc mockMvc;

    @Mock
    CarServiceMasterService service;

    @Mock
    CarServiceMasterMapper mapper;

    CarServiceMasterController controller;
    ObjectMapper objectMapper;

    private UUID id1;
    private UUID id2;
    private CarServiceMaster master1;
    private CarServiceMaster master2;
    private CarServiceMasterDTO dto1;
    private CarServiceMasterDTO dto2;

    @BeforeEach
    public void setup() {
        controller = new CarServiceMasterController(service, mapper);

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        jsonConverter.setObjectMapper(objectMapper);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setMessageInterpolator(new ParameterMessageInterpolator());
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(jsonConverter)
                .setValidator(validator)
                .build();

        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
        String fullNameMaster1 = "Иванов Иван Иванович";
        String fullNameMaster2 = "Петров Петр Петрович";

        master1 = new CarServiceMaster(id1, fullNameMaster1,
                LocalDate.of(1980, 5, 20));
        master2 = new CarServiceMaster(id2, fullNameMaster2,
                LocalDate.of(1990, 1, 10));
        dto1 = new CarServiceMasterDTO(id1, fullNameMaster1,
                LocalDate.of(1980, 5, 20));
        dto2 = new CarServiceMasterDTO(id2, fullNameMaster2,
                LocalDate.of(1990, 1, 10));
    }

    @Test
    void getAllMasters_ShouldReturnAllMasters() throws Exception {
        List<CarServiceMaster> list = List.of(master1, master2);
        List<CarServiceMasterDTO> expectedDtos = List.of(dto1, dto2);

        when(service.getAllMasters()).thenReturn(list);
        when(mapper.toDTO(master1)).thenReturn(dto1);
        when(mapper.toDTO(master2)).thenReturn(dto2);

        mockMvc.perform(get("/api/masters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedDtos.size()))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].fullName").value("Иванов Иван Иванович"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("1980-05-20"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].fullName").value("Петров Петр Петрович"))
                .andExpect(jsonPath("$[1].dateOfBirth").value("1990-01-10"));

        verify(service).getAllMasters();
        verify(mapper).toDTO(master1);
        verify(mapper).toDTO(master2);
    }

    @Test
    void getAllMasters_WhenNoMasters_ShouldReturnEmptyList() throws Exception {
        when(service.getAllMasters()).thenReturn(List.of());

        mockMvc.perform(get("/api/masters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).getAllMasters();
        verifyNoInteractions(mapper);
    }

    @Test
    void getMasterById_ShouldReturnMaster() throws Exception {
        when(service.findById(id1)).thenReturn(master1);
        when(mapper.toDTO(master1)).thenReturn(dto1);

        mockMvc.perform(get("/api/masters/" + id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id1.toString()))
                .andExpect(jsonPath("$.fullName").value("Иванов Иван Иванович"))
                .andExpect(jsonPath("$.dateOfBirth").value("1980-05-20"));

        verify(service).findById(id1);
        verify(mapper).toDTO(master1);
    }

    @Test
    void getMasterById_WhenMasterNotFound_ShouldReturnNotFound() throws Exception {
        when(service.findById(id1)).thenThrow(new MasterNotFoundException(id1));

        mockMvc.perform(get("/api/masters/" + id1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_MASTER_001")))
                .andExpect(jsonPath("$.message", is("Мастер с ID: " + id1 + " не найден в системе")));

        verify(service).findById(id1);
    }

    @Test
    void searchByName_ShouldReturnMatchingMasters() throws Exception {
        String partName = "Иван";
        List<CarServiceMaster> listOfMasters = List.of(master1);
        List<CarServiceMasterDTO> listOfDtos = List.of(dto1);

        when(service.findByNameContaining(partName)).thenReturn(listOfMasters);
        when(mapper.toDTO(master1)).thenReturn(dto1);

        mockMvc.perform(get("/api/masters/search")
                        .param("name", partName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listOfDtos.size()))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].fullName").value("Иванов Иван Иванович"))
                .andExpect(jsonPath("$[0].dateOfBirth").value("1980-05-20"));

        verify(service).findByNameContaining(partName);
        verify(mapper).toDTO(master1);
    }

    @Test
    void searchByName_WhenNoMatches_ShouldReturnEmptyList() throws Exception {
        String partName = "Неизвестный";

        when(service.findByNameContaining(partName)).thenReturn(List.of());

        mockMvc.perform(get("/api/masters/search")
                        .param("name", partName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).findByNameContaining(partName);
        verifyNoInteractions(mapper);
    }

    @Test
    void createMaster_WithValidData_ShouldCreateMaster() throws Exception {
        CarServiceMaster savedMaster = new CarServiceMaster(dto1.fullName(), dto1.dateOfBirth());
        CarServiceMasterDTO requestDto = new CarServiceMasterDTO(null, dto1.fullName(), dto1.dateOfBirth());

        when(service.addMaster(any(CarServiceMaster.class))).thenReturn(savedMaster);
        when(mapper.toDTO(savedMaster)).thenReturn(dto1);

        mockMvc.perform(post("/api/masters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value(savedMaster.getFullName()))
                .andExpect(jsonPath("$.dateOfBirth").value("1980-05-20"));

        verify(service).addMaster(any(CarServiceMaster.class));
        verify(mapper).toDTO(savedMaster);
    }

    @Test
    void createMaster_WhenFullNameIsBlank_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/masters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CarServiceMasterDTO(
                                null, "", LocalDate.of(1990, 5, 20)))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service, mapper);
    }

    @Test
    void updateMaster_WithValidData_ShouldReturnUpdatedMaster() throws Exception {
        CarServiceMasterDTO requestDto = new CarServiceMasterDTO(
                null,
                "Сидоров Сидор Сидорович",
                LocalDate.of(1988, 2, 15)
        );
        CarServiceMaster updatedMaster = new CarServiceMaster(
                id1,
                requestDto.fullName(),
                requestDto.dateOfBirth()
        );
        CarServiceMasterDTO updatedDto = new CarServiceMasterDTO(
                id1,
                requestDto.fullName(),
                requestDto.dateOfBirth()
        );

        when(service.updateMaster(any(UUID.class), any(CarServiceMaster.class))).thenReturn(updatedMaster);
        when(mapper.toDTO(updatedMaster)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/masters/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id1.toString()))
                .andExpect(jsonPath("$.fullName").value("Сидоров Сидор Сидорович"))
                .andExpect(jsonPath("$.dateOfBirth").value("1988-02-15"));

        verify(service).updateMaster(any(UUID.class), any(CarServiceMaster.class));
        verify(mapper).toDTO(updatedMaster);
    }

    @Test
    void updateMaster_WhenMasterNotFound_ShouldReturnNotFound() throws Exception {
        CarServiceMasterDTO requestDto = new CarServiceMasterDTO(
                null,
                "Сидоров Сидор Сидорович",
                LocalDate.of(1988, 2, 15)
        );

        when(service.updateMaster(any(UUID.class), any(CarServiceMaster.class)))
                .thenThrow(new MasterNotFoundException(id1));

        mockMvc.perform(put("/api/masters/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_MASTER_001")))
                .andExpect(jsonPath("$.message", is("Мастер с ID: " + id1 + " не найден в системе")));

        verify(service).updateMaster(any(UUID.class), any(CarServiceMaster.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void updateMaster_WhenFullNameIsBlank_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/masters/" + id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CarServiceMasterDTO(
                                null, "", LocalDate.of(1990, 5, 20)))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service, mapper);
    }

    @Test
    void deleteMaster_ShouldReturnNoContent() throws Exception {
        when(service.findById(id1)).thenReturn(master1);

        mockMvc.perform(delete("/api/masters/" + id1))
                .andExpect(status().isNoContent());

        verify(service).findById(id1);
        verify(service).removeMaster(master1);
    }

    @Test
    void deleteMaster_WhenMasterNotFound_ShouldReturnNotFound() throws Exception {
        when(service.findById(id1)).thenThrow(new MasterNotFoundException(id1));

        mockMvc.perform(delete("/api/masters/" + id1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("NF_MASTER_001")))
                .andExpect(jsonPath("$.message", is("Мастер с ID: " + id1 + " не найден в системе")));

        verify(service).findById(id1);
        verify(service, never()).removeMaster(any(CarServiceMaster.class));
    }
}
