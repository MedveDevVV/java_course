public abstract class Electronics extends Products {
    Electronics(String title, Double weight, OperatingSystem operatingSystem) {
        super(title, weight);
        this.operatingSystem = operatingSystem;
    }

    public enum OperatingSystem {
        ANDROID,
        IOS,
        WINDOWS,
        MAC_OS,
        LINUX,
        CHROME_OS,
        HARMONY_OS,
        OTHER
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    private final OperatingSystem operatingSystem;
}
