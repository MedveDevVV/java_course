package autoservice.controller;

import autoservice.dto.CarServiceMasterDTO;
import autoservice.mapper.CarServiceMasterMapper;
import autoservice.model.CarServiceMaster;
import autoservice.service.CarServiceMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/masters")
@RequiredArgsConstructor
@Tag(name = "Мастера", description = "API для управления мастерами автосервиса")
public class CarServiceMasterController {

    private final CarServiceMasterService masterService;
    private final CarServiceMasterMapper masterMapper;

    @GetMapping
    @Operation(summary = "Получить список всех мастеров")
    public List<CarServiceMasterDTO> getAllMasters() {
        log.info("Запрос списка всех мастеров");
        return masterService.getAllMasters().stream()
                .map(masterMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить мастера по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мастер найден"),
            @ApiResponse(responseCode = "404", description = "Мастер не найден")
    })
    public ResponseEntity<CarServiceMasterDTO> getMasterById(
            @Parameter(description = "UUID мастера", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        log.info("Запрос мастера по ID: {}", id);
        CarServiceMaster master = masterService.findById(id);
        return ResponseEntity.ok(masterMapper.toDTO(master));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск мастера по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Список мастеров, чье имя содержит указанную часть"
    )
    public List<CarServiceMasterDTO> searchByName(
            @Parameter(description = "Часть имени для поиска", required = true, example = "Иванов Иван Иванович")
            @RequestParam String name) {
        log.info("Поиск мастеров по имени: {}", name);
        return masterService.findByNameContaining(name).stream()
                .map(masterMapper::toDTO)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Создать нового мастера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Мастер создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    public ResponseEntity<CarServiceMasterDTO> createMaster(@Valid @RequestBody CarServiceMasterDTO masterDTO) {
        log.info("Создание нового мастера: {}", masterDTO.fullName());
        CarServiceMaster master = new CarServiceMaster(
                masterDTO.fullName(),
                masterDTO.dateOfBirth()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(masterMapper.toDTO(masterService.addMaster(master)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные мастера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Мастер не найден")
    })
    public CarServiceMasterDTO updateMaster(
            @Parameter(description = "UUID мастера", required = true)
            @PathVariable UUID id, @Valid @RequestBody CarServiceMasterDTO masterDTO) {
        log.info("Обновление мастера с ID: {}", id);
        CarServiceMaster masterDetails = new CarServiceMaster(
                masterDTO.fullName(),
                masterDTO.dateOfBirth()
        );
        CarServiceMaster updated = masterService.updateMaster(id, masterDetails);
        return masterMapper.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить мастера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Мастер удален"),
            @ApiResponse(responseCode = "404", description = "Мастер не найден")
    })
    public ResponseEntity<Void> deleteMaster(
            @Parameter(description = "UUID мастера", required = true)
            @PathVariable UUID id) {
        log.info("Удаление мастера с ID: {}", id);
        CarServiceMaster master = masterService.findById(id);
        masterService.removeMaster(master);
        return ResponseEntity.noContent().build();
    }
}