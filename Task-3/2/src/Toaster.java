public class Toaster extends Appliances {
    Toaster(String title, Double weight, Appliances.InstallationType installationType, int slotsCount) {
        super(title, weight, installationType);
        this.slotsCount = slotsCount;
    }
    private final int slotsCount;

    public int getSlotsCount() {
        return slotsCount;
    }
}
