import java.util.ArrayList;

public class CarAssemblyLine implements IAssemblyLine {
    ArrayList<ILineStep> iLineSteps;

    public CarAssemblyLine(CarChassisFactory carChassisFactory, CarBodyFactory carBodyFactory, CarEngineFactory carEngineFactory) {
        iLineSteps = new ArrayList<>();
        iLineSteps.add(carChassisFactory);
        iLineSteps.add(carBodyFactory);
        iLineSteps.add(carEngineFactory);
        System.out.println("создана линия сборки автомобилей");
    }
    @Override
    public IProduct assembleProduct(IProduct iProduct) {
        iProduct.installFirstPart(iLineSteps.get(0).buildProductPart());
        System.out.println("на автомобиль установлено шасси");
        iProduct.installSecondPart(iLineSteps.get(1).buildProductPart());
        System.out.println("на автомобиль установлен кузов");
        iProduct.installThirdPart(iLineSteps.get(2).buildProductPart());
        System.out.println("на автомобиль установлен двигатель");
        return iProduct;
    }
}
