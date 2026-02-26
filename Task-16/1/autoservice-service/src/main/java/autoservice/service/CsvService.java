package autoservice.service;

import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.MasterRepository;
import autoservice.repository.OrderRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.utils.csv.CsvExporter;
import autoservice.utils.csv.CsvImporter;
import autoservice.utils.csv.MasterCsvHelper;
import autoservice.utils.csv.OrderCsvHelper;
import autoservice.utils.csv.PlaceCsvHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для импорта/экспорта данных в формате CSV
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CsvService {
    private final MasterRepository masterRepository;
    private final WorkshopPlaceRepository workshopPlaceRepository;
    private final OrderRepository<RepairOrder> repairOrderRepository;

    // ========== Импорт/Экспорт мастеров ==========

    public void importMastersFromCsv(Path filePath) throws IOException {
        log.info("Импорт мастеров из CSV файла: {}", filePath);
        List<CarServiceMaster> masters = CsvImporter.importFromCsv(
                filePath, MasterCsvHelper.fieldsToMaster);
        Map<UUID, CarServiceMaster> mastersById = masterRepository.getAllMasters().stream()
                .collect(Collectors.toMap(CarServiceMaster::getId, m -> m));

        int updated = 0;
        int added = 0;
        for (CarServiceMaster master : masters) {
            CarServiceMaster existing = mastersById.get(master.getId());

            if (existing != null) {
                existing.setFullName(master.getFullName());
                existing.setDateOfBirth(master.getDateOfBirth());
                updated++;
            } else {
                masterRepository.addMaster(master);
                added++;
            }
        }
        log.info("Импорт мастеров завершен: обновлено={}, добавлено={}", updated, added);
    }

    public void exportMastersToCsv(Path filePath) throws IOException {
        log.info("Экспорт мастеров в CSV файл: {}", filePath);
        List<CarServiceMaster> masters = masterRepository.getAllMasters();
        CsvExporter.exportToCsv(masters, filePath, MasterCsvHelper.masterToFields);
        log.info("Экспорт мастеров завершен: экспортировано {} записей", masters.size());
    }

    // ========== Импорт/Экспорт рабочих мест ==========

    public void importPlacesFromCsv(Path filePath) throws IOException {
        log.info("Импорт рабочих мест из CSV файла: {}", filePath);
        List<WorkshopPlace> places = CsvImporter.importFromCsv(
                filePath, PlaceCsvHelper.fieldsToPlace);

        int updated = 0;
        int added = 0;
        for (WorkshopPlace place : places) {
            Optional<WorkshopPlace> existing = workshopPlaceRepository.findByName(place.getName());
            if (existing.isPresent()) {
                WorkshopPlace existingPlace = existing.get();
                if (existingPlace.getId().equals(place.getId())) {
                    existingPlace.setName(place.getName());
                    updated++;
                } else {
                    workshopPlaceRepository.addPlace(place);
                    added++;
                }
            }
        }
        log.info("Импорт рабочих мест завершен: обновлено={}, добавлено={}", updated, added);
    }

    public void exportPlacesToCsv(Path filePath) throws IOException {
        log.info("Экспорт рабочих мест в CSV файл: {}", filePath);
        List<WorkshopPlace> places = workshopPlaceRepository.getAllPlaces();
        CsvExporter.exportToCsv(places, filePath, PlaceCsvHelper.placeToFields);
        log.info("Экспорт рабочих мест завершен: экспортировано {} записей", places.size());
    }

    // ========== Импорт/Экспорт заказов ==========

    public void importOrdersFromCsv(Path filePath) throws IOException {
        log.info("Импорт заказов из CSV файла: {}", filePath);
        List<RepairOrder> importFromCsv = CsvImporter.importFromCsv(
                filePath, OrderCsvHelper.fieldsToOrder);
        Map<UUID, CarServiceMaster> mastersById = masterRepository.getAllMasters().stream()
                .collect(Collectors.toMap(CarServiceMaster::getId, m -> m));
        Map<UUID, WorkshopPlace> workshopPlaces = workshopPlaceRepository.getAllPlaces().stream()
                .collect(Collectors.toMap(WorkshopPlace::getId, p -> p));

        int updated = 0;
        int added = 0;
        for (RepairOrder csvOrder : importFromCsv) {
            Optional<RepairOrder> existing = repairOrderRepository.getOrderById(csvOrder.getId());
            if (existing.isPresent()) {
                RepairOrder existingOrder = existing.get();
                existingOrder.setStartDate(csvOrder.getStartDate());
                existingOrder.setEndDate(csvOrder.getEndDate());
                existingOrder.setDescription(csvOrder.getDescription());
                existingOrder.setTotalPrice(csvOrder.getTotalPrice());
                // обновление связей для существующего заказа
                updateOrderAssociations(existingOrder, csvOrder, mastersById, workshopPlaces);
                updated++;
            } else {
                // связи для новых заказов
                updateOrderAssociations(csvOrder, csvOrder, mastersById, workshopPlaces);
                repairOrderRepository.addOrder(csvOrder);
                added++;
            }
        }
        log.info("Импорт заказов завершен: обновлено={}, добавлено={}", updated, added);
    }

    public void exportOrdersToCsv(Path filePath) throws IOException {
        log.info("Экспорт заказов в CSV файл: {}", filePath);
        List<RepairOrder> orders = repairOrderRepository.getAllOrders();
        CsvExporter.exportToCsv(orders, filePath, OrderCsvHelper.orderToFields);
        log.info("Экспорт заказов завершен: экспортировано {} записей", orders.size());
    }

    // ========== Вспомогательные методы ==========

    private void updateOrderAssociations(RepairOrder targetOrder, RepairOrder csvOrder,
                                         Map<UUID, CarServiceMaster> mastersById,
                                         Map<UUID, WorkshopPlace> workshopPlaces) {
        if (csvOrder.getCarServiceMaster() != null) {
            CarServiceMaster master = mastersById.get(csvOrder.getCarServiceMaster().getId());
            targetOrder.setCarServiceMaster(master);
        }
        if (csvOrder.getWorkshopPlace() != null) {
            WorkshopPlace place = workshopPlaces.get(csvOrder.getWorkshopPlace().getId());
            targetOrder.setWorkshopPlace(place);
        }
    }
}