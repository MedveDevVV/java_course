package autoservice.utils.csv;

import autoservice.model.WorkshopPlace;

import java.util.UUID;
import java.util.function.Function;

public class PlaceCsvHelper {
    public static final String[] PLACE_HEADER = {"id", "name"};

    public static Function<WorkshopPlace, String[]> placeToFields = place -> {
        String[] fields = new String[2];
        fields[0] = place.getId().toString();
        fields[1] = place.getName();
        return fields;
    };

    public static Function<String[], WorkshopPlace> fieldsToPlace = fields -> {
        UUID id = UUID.fromString(fields[0]);
        String name = fields[1];
        return new WorkshopPlace(id, name);
    };
}