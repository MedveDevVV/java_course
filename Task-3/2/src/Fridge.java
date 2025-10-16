public class Fridge extends Appliances {
    Fridge(String title, Double weight, Appliances.InstallationType installationType, boolean hasFreezer) {
        super(title, weight, installationType);
        this.hasFreezer = hasFreezer;
    }
    private final boolean hasFreezer;

    public boolean isHasFreezer() {
        return hasFreezer;
    }
}
