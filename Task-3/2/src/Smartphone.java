public class Smartphone extends Electronics {
    Smartphone(String title, Double weight, Electronics.OperatingSystem operatingSystem, String imei) {
        super(title, weight, operatingSystem);
        IMEI = imei;
    }

    private final String IMEI;

    public String getIMEI() {
        return IMEI;
    }
}
