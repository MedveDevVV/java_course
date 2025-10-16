public abstract class Products {
    private final Double weight;
    private final String title;
    private Integer binLocation;

    public String getTitle() {
        return title;
    }

    public void setBinLocation(Integer binLocation) {
        this.binLocation = binLocation;
    }

    public Integer getBinLocation() {
        return binLocation;
    }

    public Double getWeight() {
        return weight;
    }

    Products(String title, Double weight) {
        this.title = title;
        this.weight = weight;
    }
}