package autoservice.dto;

import java.time.LocalDate;

public record CarServiceMasterFilter(String name, LocalDate dateOfBirth, int limit, int offset) {
}