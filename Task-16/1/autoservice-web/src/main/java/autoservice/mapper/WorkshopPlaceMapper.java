package autoservice.mapper;

import autoservice.dto.WorkshopPlaceDTO;
import autoservice.model.WorkshopPlace;
import org.springframework.stereotype.Component;

@Component
public class WorkshopPlaceMapper {

    public WorkshopPlaceDTO toDTO(WorkshopPlace place) {
        return new WorkshopPlaceDTO(
                place.getId(),
                place.getName()
        );
    }
}