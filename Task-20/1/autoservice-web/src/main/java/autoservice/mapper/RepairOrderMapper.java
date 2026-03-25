package autoservice.mapper;

import autoservice.dto.RepairOrderDTO;
import autoservice.model.RepairOrder;
import org.springframework.stereotype.Component;

@Component
public class RepairOrderMapper {

    public RepairOrderDTO toDTO(RepairOrder order) {
        return new RepairOrderDTO(
                order.getId(),
                order.getCarServiceMaster() != null ? order.getCarServiceMaster().getId() : null,
                order.getCarServiceMaster() != null ? order.getCarServiceMaster().getFullName() : null,
                order.getWorkshopPlace() != null ? order.getWorkshopPlace().getId() : null,
                order.getWorkshopPlace() != null ? order.getWorkshopPlace().getName() : null,
                order.getCreationDate(),
                order.getStartDate(),
                order.getEndDate(),
                order.getDescription(),
                order.getStatus(),
                order.getTotalPrice()
        );
    }
}