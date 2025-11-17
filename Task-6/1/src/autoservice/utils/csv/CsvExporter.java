package autoservice.utils.csv;

import autoservice.model.Identifiable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class CsvExporter {
    public static <T extends Identifiable> void exportToCsv(
            List<T> items,
            Path filePath,
            Function<T, String[]> itemToFields) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("\uFEFF");
            for (T item : items) {
                String[] fields = itemToFields.apply(item);
                writer.write(String.join(";", fields));
                writer.newLine();
            }
        }
    }
}