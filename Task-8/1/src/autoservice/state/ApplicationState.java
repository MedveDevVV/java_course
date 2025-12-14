package autoservice.state;

import autoservice.model.CarServiceMaster;
import autoservice.model.RepairOrder;
import autoservice.model.WorkshopPlace;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationState implements Serializable {
    @Serial
    private static final long serialVersionUID = 9001L;

    private List<CarServiceMaster> masters;
    private List<WorkshopPlace> places;
    private List<RepairOrder> repairOrders;

    public ApplicationState() {
        this.places = new ArrayList<>();
        this.repairOrders = new ArrayList<>();
        this.masters = new ArrayList<>();
    }

    public ApplicationState(List<CarServiceMaster> masters,
                            List<WorkshopPlace> places,
                            List<RepairOrder> repairOrders) {
        this.masters = masters;
        this.places = places;
        this.repairOrders = repairOrders;
    }

    public List<CarServiceMaster> getMasters() {
        return new ArrayList<>(masters);
    }

    public List<WorkshopPlace> getPlaces() {
        return new ArrayList<>(places);
    }

    public List<RepairOrder> getRepairOrders() {
        return new ArrayList<>(repairOrders);
    }

    public void setPlaces(List<WorkshopPlace> places) {
        this.places = new ArrayList<>(places);
    }

    public void setMasters(List<CarServiceMaster> masters) {
        this.masters = new ArrayList<>(masters);
    }

    public void setRepairOrders(List<RepairOrder> repairOrders) {
        this.repairOrders = new ArrayList<>(repairOrders);
    }

    public boolean isEmpty() {
        return masters.isEmpty() && places.isEmpty() &&
                repairOrders.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ApplicationState{masters=%d, places=%d, repairOrders=%d}",
                masters.size(), places.size(), repairOrders.size());
    }
}
