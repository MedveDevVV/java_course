package autoservice.enums;

import autoservice.model.Order;

import java.util.Comparator;

public enum SortOrders {
    CREATION_DATE(Comparator.comparing(Order::getCreationDate)),
    START_DATE(Comparator.comparing(Order::getStartDate)),
    END_DATE(Comparator.comparing(Order::getEndDate)),
    TOTAL_PRICE(Comparator.comparing(Order::getTotalPrice));

    private final Comparator<Order> comparator;

    SortOrders(Comparator<Order> comparator){
        this.comparator = comparator;
    }

    public Comparator<Order> getComparator(){
        return comparator;
    }
}
