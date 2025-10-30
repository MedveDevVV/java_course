public class CarBodyFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создан кузов автомобиля");
        return new CarBody();
    }
}
