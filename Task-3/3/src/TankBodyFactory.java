public class TankBodyFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создан кузов танка");
        return new TankBody();
    }
}
