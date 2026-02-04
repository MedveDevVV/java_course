package autoservice.enums;

import autoservice.model.Order;
import autoservice.model.RepairOrder;

import java.util.Comparator;

public enum SortRepairOrders {
    CREATION_DATE(Comparator.comparing(RepairOrder::getCreationDate)),
    START_DATE(Comparator.comparing(RepairOrder::getStartDate)),
    END_DATE(Comparator.comparing(RepairOrder::getEndDate)),
    TOTAL_PRICE(Comparator.comparing(RepairOrder::getTotalPrice));

    private final Comparator<RepairOrder> comparator;

    SortRepairOrders(Comparator<RepairOrder> comparator){
        this.comparator = comparator;
    }

    public Comparator<RepairOrder> getComparator(){
        return comparator;
    }
}
