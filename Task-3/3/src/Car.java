public class Car implements IProduct{
    CarBody carBody;
    CarEngine carEngine;
    CarChassis carChassis;

    @Override
    public void installFirstPart(IProductPart carChassis) {
        this.carChassis = (CarChassis) carChassis;
    }

    @Override
    public void installSecondPart(IProductPart carBody) {
        this.carBody = (CarBody) carBody;
    }

    @Override
    public void installThirdPart(IProductPart carEngine) {
        this.carEngine = (CarEngine) carEngine;
    }
}
