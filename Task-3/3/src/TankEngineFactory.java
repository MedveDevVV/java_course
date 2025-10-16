public class TankEngineFactory implements ILineStep{
    @Override
    public IProductPart buildProductPart() {
        System.out.println("создан двигатель танка");
        return new TankEngine();
    }
}
