package autoservice.service;

import autoservice.config.AppConfig;
import autoservice.exception.DaoException;
import autoservice.exception.DuplicateEntityException;
import autoservice.exception.OperationNotAllowedException;
import autoservice.exception.WorkshopPlaceNotFoundException;
import autoservice.model.WorkshopPlace;
import autoservice.repository.WorkshopPlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkshopPlaceService {
    private final AppConfig appConfig;
    private final WorkshopPlaceRepository workshopPlaceRepository;

    // ========== Операции чтения ==========

    public List<WorkshopPlace> getAllPlaces() {
        log.debug("Получение всех рабочих мест");
        return workshopPlaceRepository.getAllPlaces();
    }

    public WorkshopPlace findById(UUID id) {
        log.debug("Поиск рабочего места по ID: {}", id);
        return workshopPlaceRepository.findById(id)
                .orElseThrow(() -> new WorkshopPlaceNotFoundException(id));
    }

    public List<WorkshopPlace> findByNameContaining(String namePart) {
        return workshopPlaceRepository.findByNameContaining(namePart);
    }

    // ========== Операции изменения ==========

    @Transactional
    public WorkshopPlace addPlace(WorkshopPlace place) {
        if (!appConfig.isCanAddPlaces()) {
            log.warn("Попытка добавления рабочего места '{}' запрещена конфигурацией", place.getName());
            throw new OperationNotAllowedException("Добавление рабочих мест запрещено");
        }

        log.info("Добавление рабочего места с именем: {}", place.getName());
        try {
            workshopPlaceRepository.addPlace(place);
            return place;
        } catch (DaoException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                log.error("Ошибка. Рабочее место с именем '{}' уже существует.", place.getName(), e);
                throw new DuplicateEntityException("Рабочее место", "именем", place.getName());
            }
            throw e;
        }
    }

    @Transactional
    public WorkshopPlace updatePlace(UUID id, WorkshopPlace placeDetails) {
        log.info("Обновление рабочего места с ID: {}", id);
        WorkshopPlace place = findById(id);
        place.setName(placeDetails.getName());
        return place;
    }

    @Transactional
    public void removePlace(UUID id) {
        if (!appConfig.isCanRemovePlaces()) {
            log.warn("Попытка удаления рабочего места с ID {} запрещена конфигурацией", id);
            throw new OperationNotAllowedException("Удаление рабочих мест запрещено");
        }

        log.info("Удаление рабочего места с ID: {}", id);
        WorkshopPlace place = findById(id);
        workshopPlaceRepository.removePlace(place);
    }
}