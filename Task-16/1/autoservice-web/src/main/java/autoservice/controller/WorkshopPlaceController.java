package autoservice.controller;

import autoservice.dto.WorkshopPlaceDTO;
import autoservice.mapper.WorkshopPlaceMapper;
import autoservice.model.WorkshopPlace;
import autoservice.service.WorkshopPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/workshop-places")
@RequiredArgsConstructor
@Tag(name = "Рабочие места", description = "API для управления рабочими местами в автосервисе")
public class WorkshopPlaceController {
    private final WorkshopPlaceService placeService;
    private final WorkshopPlaceMapper placeMapper;

    @GetMapping
    @Operation(summary = "Получить список всех рабочих мест")
    public List<WorkshopPlaceDTO> getAllPlaces(){
        log.info("Запрос списка всех рабочих мест");
        return placeService.getAllPlaces().stream()
                .map(placeMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить рабочее место по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Рабочее место найдено"),
            @ApiResponse(responseCode = "404", description = "Рабочее место не найдено")
    })
    public ResponseEntity<WorkshopPlaceDTO> getPlaceById(
            @Parameter(description = "UUID рабочего места", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        log.info("Запрос рабочего места по ID: {}", id);
        WorkshopPlace place = placeService.findById(id);
        return ResponseEntity.ok(placeMapper.toDTO(place));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск рабочих мест по названию")
    public List<WorkshopPlaceDTO> searchByName(
            @Parameter(description = "Часть названия для поиска", required = true, example = "Гараж")
            @RequestParam String name) {
        log.info("Поиск рабочих мест по названию: {}", name);
        return placeService.findByName(name).stream()
                .map(placeMapper::toDTO)
                .toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить рабочее место")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Рабочее место удалено"),
            @ApiResponse(responseCode = "404", description = "Рабочее место не найдено")
    })
    public ResponseEntity<Void> deletePlace(
            @Parameter(description = "UUID рабочего места", required = true)
            @PathVariable UUID id) {
        log.info("Удаление рабочего места с ID: {}", id);
        placeService.removePlaceById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Создать новое рабочее место")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Рабочее место создано"),
            @ApiResponse(responseCode = "400", description = "Неверные данные"),
            @ApiResponse(responseCode = "409", description = "Рабочее место с таким именем уже существует")
    })
    public ResponseEntity<WorkshopPlaceDTO> createPlace(@Valid @RequestBody WorkshopPlaceDTO placeDTO) {
        log.info("Создание нового рабочего места: {}", placeDTO.name());
        WorkshopPlace place = new WorkshopPlace(placeDTO.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(placeMapper.toDTO(placeService.addPlace(place)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить данные рабочего места")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Рабочее место не найдено")
    })
    public WorkshopPlaceDTO updatePlace(
            @Parameter(description = "UUID рабочего места", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody WorkshopPlaceDTO placeDTO) {
        log.info("Обновление рабочего места с ID: {}", id);
        WorkshopPlace placeDetails = new WorkshopPlace(placeDTO.name());
        WorkshopPlace updated = placeService.updatePlace(id, placeDetails);
        return placeMapper.toDTO(updated);
    }
}