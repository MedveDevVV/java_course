public class CarChassisFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создано шасси автомобиля");
        return new CarChassis();
    }
}
