public abstract class Appliances extends Products {
    Appliances(String title, Double weight, InstallationType installationType) {
        super(title, weight);
        this.installationType = installationType;
    }
    public enum InstallationType{
        BUILT_IN,
        FREESTANDING,
        PORTABLE
    }

    private final InstallationType installationType;

    public InstallationType getInstallationType() {
        return installationType;
    }
}
