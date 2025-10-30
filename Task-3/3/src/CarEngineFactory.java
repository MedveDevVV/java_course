public class CarEngineFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создан двигатель автомобиля");
        return new CarEngine();
    }
}
