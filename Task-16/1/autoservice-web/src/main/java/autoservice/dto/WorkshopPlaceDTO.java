package autoservice.dto;

import java.util.UUID;

public record WorkshopPlaceDTO(
        UUID id,
        String name
) {}