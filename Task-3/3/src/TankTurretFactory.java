public class TankTurretFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создана башня танка");
        return new TankTurret();
    }
}
