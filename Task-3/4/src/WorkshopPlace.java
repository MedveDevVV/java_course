public class WorkshopPlace {
    private final String name;
    private Boolean occupied;

    public WorkshopPlace(String name) {
        this.name = name;
        this.occupied = false;
    }

    String getName() {
        return name;
    }

    boolean isOccupied(){
        return occupied;
    }

    void setOccupied(boolean occupied){
        this.occupied = occupied;
    }
}