package autoservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class WorkshopPlace implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 1001L;
    private UUID id;
    private String name;

    protected WorkshopPlace(){
    }

    public WorkshopPlace(String name) {
        this.name = name;
    }

    public WorkshopPlace(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public UUID getId(){
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(UUID id) {
        if (this.id != null){
            throw new IllegalStateException("ID уже установлен");
        }
        this.id = id;
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