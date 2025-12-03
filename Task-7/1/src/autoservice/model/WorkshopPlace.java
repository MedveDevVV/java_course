package autoservice.model;

import java.util.Objects;
import java.util.UUID;

public class WorkshopPlace implements Identifiable{
    private final UUID id;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public WorkshopPlace(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public WorkshopPlace(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public UUID getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkshopPlace that = (WorkshopPlace) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}