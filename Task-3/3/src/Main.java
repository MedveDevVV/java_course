

public class Main {
    public static void main(String[] args) {
        IAssemblyLine iAssemblyLine = new CarAssemblyLine(new CarChassisFactory(),
                new CarBodyFactory(), new CarEngineFactory());
        Car car = new Car();
        iAssemblyLine.assembleProduct(car);

        car.carEngine.engineStart();

        iAssemblyLine = new TankAssemblyLine(new TankBodyFactory(),
                new TankEngineFactory(), new TankTurretFactory());
        Tank tank = new Tank();
        iAssemblyLine.assembleProduct(tank);
    }
}