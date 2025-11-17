package autoservice.dto;

import autoservice.enums.OrderStatus;
import autoservice.enums.SortOrders;
import autoservice.model.CarServiceMaster;

import java.time.LocalDate;


public record OrderQuery(
        OrderStatus status,
        Boolean isRemoved,
        CarServiceMaster carServiceMaster,
        LocalDate startDate,
        LocalDate endDate,
        SortOrders sortOrders
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
        private SortOrders sortOrders;

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

        public Builder sortOrders(SortOrders sortOrders) {
            this.sortOrders = sortOrders;
            return this;
        }

        public OrderQuery build() {
            return new OrderQuery(status, isRemoved, carServiceMaster, startDate, endDate, sortOrders);
        }
    }
}
