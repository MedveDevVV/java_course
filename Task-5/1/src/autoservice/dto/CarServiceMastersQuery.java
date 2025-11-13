package autoservice.dto;

import autoservice.enums.SortCarServiceMasters;

import java.time.LocalDate;
import java.util.Objects;

public record CarServiceMastersQuery(
        LocalDate localDate,
        Boolean isOccupied,
        SortCarServiceMasters sort
) {
    public CarServiceMastersQuery {
        Objects.requireNonNull(localDate, "localDate cannot be null");
    }

        public static Builder builder() {
        return new Builder();
    }

        public static final class Builder {
            private LocalDate localDate;
            private Boolean isOccupied;
            private SortCarServiceMasters sort;

            private Builder() {}

            public Builder localDate(LocalDate localDate) {
                this.localDate = Objects.requireNonNull(localDate);
                return this;
            }

            public Builder isOccupied(Boolean isOccupied) {
                this.isOccupied = isOccupied;
                return this;
            }

            public Builder sort(SortCarServiceMasters sort) {
                this.sort = sort;
                return this;
            }

            public CarServiceMastersQuery build() {
                return new CarServiceMastersQuery(localDate, isOccupied, sort);
            }
        }
}