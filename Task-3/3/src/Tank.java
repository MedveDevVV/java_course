public class Tank implements IProduct{
    TankBody tankBody;
    TankEngine tankEngine;
    TankTurret tankTurret;

    @Override
    public void installFirstPart(IProductPart tankBody) {
        this.tankBody = (TankBody) tankBody;
    }

    @Override
    public void installSecondPart(IProductPart tankEngine) {
        this.tankEngine = (TankEngine) tankEngine;
    }

    @Override
    public void installThirdPart(IProductPart tankTurret) {
        this.tankTurret = (TankTurret) tankTurret;
    }
}
