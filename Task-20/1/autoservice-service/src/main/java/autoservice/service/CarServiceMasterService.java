package autoservice.service;

import autoservice.exception.DaoException;
import autoservice.exception.DuplicateEntityException;
import autoservice.exception.MasterNotFoundException;
import autoservice.exception.ResourceBusyException;
import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;
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
public class CarServiceMasterService {

    private final MasterRepository masterRepository;

    // ========== Операции чтения ==========

    public List<CarServiceMaster> getAllMasters() {
        log.debug("Получение всех мастеров");
        return masterRepository.getAllMasters();
    }

    public CarServiceMaster findById(UUID id) {
        log.debug("Поиск мастера по ID: {}", id);
        return masterRepository.findMasterById(id)
                .orElseThrow(() -> new MasterNotFoundException(id));
    }

    public List<CarServiceMaster> findByNameContaining(String namePart) {
        log.debug("Поиск мастеров по части имени: {}", namePart);
        return masterRepository.findMastersByFullNameContaining(namePart);
    }

    // ========== Операции изменения ==========

    @Transactional
    public CarServiceMaster addMaster(CarServiceMaster master) {
        log.info("Добавление мастера: {}", master.getFullName());
        masterRepository.addMaster(master);
        return master;
    }

    @Transactional
    public CarServiceMaster updateMaster(UUID id, CarServiceMaster masterDetails) {
        log.info("Обновление мастера с ID: {}", id);
        CarServiceMaster master = findById(id);
        master.setFullName(masterDetails.getFullName());
        master.setDateOfBirth(masterDetails.getDateOfBirth());
        return master;
    }

    @Transactional
    public void removeMaster(CarServiceMaster master) {
        log.info("Удаление мастера с ID: {}", master.getId());
        try {
            masterRepository.removeMaster(master);
        } catch (DaoException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                log.error("Ошибка. Мастер '{}' используется в заказах и не может быть удален.",
                        master.getFullName(), e);
                throw new ResourceBusyException("Мастер", master.getFullName(), null, null);
            }
            throw e;
        }
    }
}