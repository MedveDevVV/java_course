package autoservice.service;

import autoservice.dto.CarServiceMastersQuery;
import autoservice.dto.OrderQuery;
import autoservice.model.CarServiceMaster;
import autoservice.model.Order;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;
import autoservice.repository.MasterRepository;
import autoservice.repository.OrderRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.repository.impl.RepairOrderRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class AutoServiceAdmin {
    private final MasterRepository masterRepository;
    private final WorkshopPlaceRepository workshopPlaceRepository;
    private final OrderRepository ordersRepository;
    private final OrderRepository deletedOrdersRepository = new RepairOrderRepository();


    public AutoServiceAdmin(MasterRepository masterRepository, WorkshopPlaceRepository workshopPlaceRepository,
                            OrderRepository ordersRepository) {
        this.masterRepository = masterRepository;
        this.workshopPlaceRepository = workshopPlaceRepository;
        this.ordersRepository = ordersRepository;
    }

    private List<Order> ordersFilter(OrderRepository orders, OrderQuery orderQuery) {
        return orders.getAllOrders().stream()
                .filter(o -> orderQuery.carServiceMaster() == null
                        || o.getAssignPerson().equals(orderQuery.carServiceMaster()))
                .filter(o -> orderQuery.status() == null
                        || o.getStatus().equals(orderQuery.status()))
                .filter(o -> orderQuery.startDate() == null
                        || o.getStartDate().isAfter(orderQuery.startDate()))
                .filter(o -> orderQuery.endDate() == null
                        || o.getEndDate().isBefore(orderQuery.endDate()))
                .toList();
    }

    private boolean isDateInRange(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
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

    public void removePlace(WorkshopPlace place) {
        workshopPlaceRepository.removePlace(place);
    }

    public UUID createRepairOrder(LocalDate creationDate, LocalDate start, LocalDate end,
                                  String description, CarServiceMaster master, WorkshopPlace place) {
        RepairOrder order = new RepairOrder(creationDate, start, end, description);
        order.assignPerson(master);
        order.setWorkshopPlace(place);
        ordersRepository.addOrder(order);
        return order.getId();
    }

    public void cancelOrder(UUID orderId) {
        RepairOrder order = ordersRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        ordersRepository.cancelOrder(order);
    }

    public void closedOrder(UUID orderId) {
        RepairOrder order = ordersRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        ordersRepository.closeOrder(order);
    }

    public void removeOrder(UUID orderId) {
        RepairOrder order = ordersRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        deletedOrdersRepository.addOrder(order);
        ordersRepository.removeOrder(order);
    }

    public void delayOrder(UUID orderId, Period period) {
        RepairOrder order = ordersRepository.getOrderById(orderId).orElse(null);
        if (order == null) return;
        order.setEndDate(order.getEndDate().plus(period));
        ordersRepository.updateOrder(order);
    }

    public Optional<RepairOrder> getOrderById(UUID orderId) {
        return ordersRepository.getOrderById(orderId);
    }

    public List<Order> getOrders(OrderQuery orderQuery) {
        List<Order> orders = new ArrayList<>();
        if (orderQuery.isRemoved() == null) {
            orders.addAll(ordersFilter(ordersRepository, orderQuery));
            orders.addAll(ordersFilter(deletedOrdersRepository, orderQuery));
        } else if (orderQuery.isRemoved()) {
            orders.addAll(ordersFilter(deletedOrdersRepository, orderQuery));
        } else {
            orders.addAll(ordersFilter(ordersRepository, orderQuery));
        }
        if (orderQuery.sortOrders() != null) {
            orders.sort(orderQuery.sortOrders().getComparator());
        }
        return orders;
    }

    public List<CarServiceMaster> getCarServiceMasters(CarServiceMastersQuery carServiceMastersQuery) {
        List<CarServiceMaster> masters = new ArrayList<>();
        if (carServiceMastersQuery.isOccupied() != null) {
            List<Order> orders = ordersRepository.getAllOrders();
            if (carServiceMastersQuery.isOccupied()) {
                for (Order order : orders) {
                    if (isDateInRange(carServiceMastersQuery.localDate(), order.getStartDate(), order.getEndDate())) {
                        masters.add((CarServiceMaster) order.getAssignPerson());
                    }
                }
            } else {
                masters.addAll(masterRepository.getAllMasters());
                for (Order order : orders) {
                    if (isDateInRange(carServiceMastersQuery.localDate(), order.getStartDate(), order.getEndDate())) {
                        masters.remove((CarServiceMaster) order.getAssignPerson());
                    }
                }
            }
        } else {
            masters.addAll(masterRepository.getAllMasters());
        }
        if (carServiceMastersQuery.sort() != null) {
            masters.sort(carServiceMastersQuery.sort().getComparator());
        } else {
            masters.sort(Comparator.comparing(CarServiceMaster::getFullName));
        }
        return masters;
    }

    public Optional<CarServiceMaster> getMasterByOrder(UUID orderId) {
        RepairOrder order = ordersRepository.getOrderById(orderId).orElse(null);
        if (order == null) return Optional.empty();
        return Optional.ofNullable((CarServiceMaster) order.getAssignPerson());
    }

    public List<WorkshopPlace> getAvailablePlaces(LocalDate localDate) {
        List<WorkshopPlace> availablePlaces = workshopPlaceRepository.getAllPlaces();
        List<Order> orders = ordersRepository.getAllOrders();
        for (Order order : orders) {
            if (isDateInRange(localDate, order.getStartDate(), order.getEndDate())) {
                availablePlaces.remove(((RepairOrder) order).getWorkshopPlace());
            }
        }
        return availablePlaces;
    }

    public int countAvailablePlaces(LocalDate date) {
        int countPlaces = getAvailablePlaces(date).size();
        int countMasters = (getCarServiceMasters(CarServiceMastersQuery.builder().
                localDate(date)
                .isOccupied(false)
                .build()))
                .size();
        return Math.min(countPlaces, countMasters);
    }

    public Optional<LocalDate> getFirstAvailableSlot(LocalDate date){
        int countAvailable = 0;
        LocalDate endDate = date.plusDays(7);
        while(date.isBefore(endDate)){
            countAvailable = countAvailablePlaces(date);
            if (countAvailable > 0) return Optional.of(date);
            date = date.plusDays(1);
        }
        return Optional.empty();
    }
}
