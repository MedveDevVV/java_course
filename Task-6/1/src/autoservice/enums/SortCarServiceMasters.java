package autoservice.enums;

import autoservice.model.CarServiceMaster;

import java.util.Comparator;

public enum SortCarServiceMasters {
    NAME(Comparator.comparing(CarServiceMaster::getFullName));

    private final Comparator<CarServiceMaster> comparator;

    SortCarServiceMasters(Comparator<CarServiceMaster> comparator){
        this.comparator = comparator;
    }

    public Comparator<CarServiceMaster> getComparator(){
        return comparator;
    }
}
