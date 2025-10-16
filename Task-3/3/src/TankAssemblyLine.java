import java.util.ArrayList;

public class TankAssemblyLine implements IAssemblyLine{
    ArrayList<ILineStep> iLineSteps;

    public TankAssemblyLine(TankBodyFactory tankBodyFactory, TankEngineFactory tankEngineFactory, TankTurretFactory tankTurretFactory) {
        this.iLineSteps = new ArrayList<>();
        iLineSteps.add(tankBodyFactory);
        iLineSteps.add(tankEngineFactory);
        iLineSteps.add(tankTurretFactory);
    }

    @Override
    public IProduct assembleProduct(IProduct iProduct) {
        iProduct.installFirstPart(iLineSteps.get(0).buildProductPart());
        System.out.println("на танк установлено кузов");
        iProduct.installSecondPart(iLineSteps.get(1).buildProductPart());
        System.out.println("на танк установлен двигатель");
        iProduct.installThirdPart(iLineSteps.get(2).buildProductPart());
        System.out.println("на танк установлен башня");
        return iProduct;
    }
}
