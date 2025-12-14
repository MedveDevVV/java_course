package autoservice.utils.csv;

import autoservice.model.Identifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CsvImporter {
    private static String detectDelimiter(String header) {
        if (header.contains(";")) return ";";
        if (header.contains(",")) return ",";
        return ";";
    }

    public static <T extends Identifiable> List<T> importFromCsv(
            Path filePath, Function<String[], T> fieldsToItem) throws IOException {

        List<T> items = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);

        if (lines.get(0).startsWith("\uFEFF")) {
            lines.set(0,lines.get(0).substring(1));
        }

        String delimiter = detectDelimiter(lines.get(0));
        for (String line : lines) {
            String[] fields = line.split(delimiter);
            T item = fieldsToItem.apply(fields);
            items.add(item);
        }

        return items;
    }
}