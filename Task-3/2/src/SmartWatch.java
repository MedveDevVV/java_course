public class SmartWatch extends Electronics {
    SmartWatch(String title, Double weight, Electronics.OperatingSystem operatingSystem, String serial) {
        super(title, weight, operatingSystem);
        SERIAL = serial;
    }

    private final String SERIAL;

    public String getSERIAL() {
        return SERIAL;
    }
}
