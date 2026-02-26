package autoservice.service;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.exception.MasterNotAssignedException;
import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoServiceFacade {

    private final CarServiceMasterService masterService;
    private final WorkshopPlaceService workshopPlaceService;
    private final RepairOrderService repairOrderService;

    @Transactional
    public RepairOrder createRepairOrder(LocalDate creationDate, LocalDate start, LocalDate end,
                                  String description, UUID masterId, UUID placeId) {
        log.debug("Создание заказа через фасад: masterId={}, placeId={}", masterId, placeId);
        CarServiceMaster master = masterService.findById(masterId);
        WorkshopPlace place = workshopPlaceService.findById(placeId);

        return repairOrderService.createOrder(creationDate, start, end, description, master, place);
    }

    public List<WorkshopPlace> getAvailablePlacesOnDate(LocalDate date) {
        log.debug("Получение доступных рабочих мест на дату: {}", date);
        List<WorkshopPlace> availablePlaces = workshopPlaceService.getAllPlaces();
        List<RepairOrder> orders = repairOrderService.findCreatedOrdersByDate(date);

        for (RepairOrder order : orders) {
            availablePlaces.remove(order.getWorkshopPlace());
        }
        return availablePlaces;
    }

    public List<CarServiceMaster> findAvailableMastersOnDate(LocalDate date) {
        log.debug("Получение доступных мастеров на дату: {}", date);
        List<CarServiceMaster> masters = masterService.getAllMasters();
        masters.removeAll(repairOrderService.findOccupiedMastersOnDate(date));
        return masters;
    }

    public List<CarServiceMaster> getCarServiceMasters(CarServiceMastersQuery query) {
        log.debug("Получение мастеров по запросу: {}", query);
        Objects.requireNonNull(query, "carServiceMastersQuery cannot be null");
        List<CarServiceMaster> masters;
        if (query.isOccupied() != null)
            masters = query.isOccupied() ? repairOrderService.findOccupiedMastersOnDate(query.localDate())
                    : findAvailableMastersOnDate(query.localDate());
        else masters = masterService.getAllMasters();

        if (query.sort() != null)
            masters.sort(query.sort().getComparator());
        else masters.sort(Comparator.comparing(CarServiceMaster::getFullName));

        return masters;
    }

    public int countAvailablePlacesOnDate(LocalDate date) {
        log.debug("Подсчет доступных мест на дату: {}", date);
        int availablePlaces = getAvailablePlacesOnDate(date).size();
        int availableMasters = findAvailableMastersOnDate(date).size();
        return Math.min(availablePlaces, availableMasters);
    }

    public Optional<LocalDate> getFirstAvailableSlot(LocalDate date) {
        log.debug("Поиск первого доступного слота с даты: {}", date);
        LocalDate endDate = date.plusDays(7);
        while (date.isBefore(endDate)) {
            if (countAvailablePlacesOnDate(date) > 0) {
                log.debug("Найден первый доступный слот: {}", date);
                return Optional.of(date);
            }
            date = date.plusDays(1);
        }
        log.debug("Доступный слот не найден в течение 7 дней");
        return Optional.empty();
    }

    public CarServiceMaster getMasterByOrderId(UUID orderId) {
        log.debug("Получение мастера по ID заказа: {}", orderId);
        RepairOrder order = repairOrderService.findById(orderId);
        if (order.getCarServiceMaster() == null) {
            throw new MasterNotAssignedException(orderId);
        }
        return order.getCarServiceMaster();
    }
}
