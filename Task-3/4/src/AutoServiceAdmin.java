import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class AutoServiceAdmin {
    private final MasterRepository masterRepository;
    private final WorkshopPlaceRepository workshopPlaceRepository;
    private final OrderRepository orderRepository;

    public AutoServiceAdmin(MasterRepository masterRepository, WorkshopPlaceRepository workshopPlaceRepository, OrderRepository orderRepository) {
        this.masterRepository = masterRepository;
        this.workshopPlaceRepository = workshopPlaceRepository;
        this.orderRepository = orderRepository;
    }

    public void addMaster(CarServiceMaster master) {
        masterRepository.addMaster(master);
    }

    public void removeMaster(CarServiceMaster master) {
        masterRepository.removeMaster(master);
    }

    public void addWorkshopPlace(WorkshopPlace place) {
        workshopPlaceRepository.addPlace(place);
    }

    public void setMasterRepository(WorkshopPlace place) {
        workshopPlaceRepository.removePlace(place);
    }

    public UUID createRepairOrder(LocalDateTime start, LocalDateTime end,
                                  String description, CarServiceMaster master, WorkshopPlace place) {
        RepairOrder order = new RepairOrder(start, end, description);
        order.assignPerson(master);
        order.setWorkshopPlace(place);
        place.setOccupied(true);
        orderRepository.addOrder(order);
        return order.getId();
    }

    public void cancelOrder(UUID orderId) {
        RepairOrder order = orderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        orderRepository.cancelOrder(order);
    }

    public void closedOrder(UUID orderId){
        RepairOrder order = orderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        orderRepository.closeOrder(order);
    }

    public void delayOrder(UUID orderId, Duration duration) {
        RepairOrder order = orderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        order.setEndDateTime(order.getEndTime().plus(duration));
        orderRepository.updateOrder(order);
    }

    public String getOrderInfo(UUID orderId){
        RepairOrder order = orderRepository.getOrderById(orderId).orElse(null);
        if (order == null) return "Заказ не найден";
        return order.toString();
    }
}
