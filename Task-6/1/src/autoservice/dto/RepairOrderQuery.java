package autoservice.dto;

import autoservice.enums.OrderStatus;
import autoservice.enums.SortRepairOrders;
import autoservice.model.CarServiceMaster;

import java.time.LocalDate;


public record RepairOrderQuery(
        OrderStatus status,
        Boolean isRemoved,
        CarServiceMaster carServiceMaster,
        LocalDate startDate,
        LocalDate endDate,
        SortRepairOrders sortRepairOrders
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderStatus status;
        private Boolean isRemoved;
        private CarServiceMaster carServiceMaster;
        private LocalDate startDate;
        private LocalDate endDate;
        private SortRepairOrders sortOrders;

        private Builder() {}

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder isRemoved(Boolean isRemoved) {
            this.isRemoved = isRemoved;
            return this;
        }

        public Builder carServiceMaster(CarServiceMaster carServiceMaster) {
            this.carServiceMaster = carServiceMaster;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder sortOrders(SortRepairOrders sortRepairOrders) {
            this.sortOrders = sortRepairOrders;
            return this;
        }

        public RepairOrderQuery build() {
            return new RepairOrderQuery(status, isRemoved, carServiceMaster, startDate, endDate, sortOrders);
        }
    }
}
