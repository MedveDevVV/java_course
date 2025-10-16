import java.util.ArrayList;
import java.util.Random;
// Задание: Написать программу,
// содержащую иерархии товаров для склада. Заполнить склад до предела и высчитать вес хранимого товара.
public class Main {
    public static void main(String[] args) {
        ArrayList<Products> warehouse = new ArrayList<>();
        final int CAPACITY = 5;
        warehouse.add(new Smartphone("iphone 25", (double) (new Random().nextInt(((250 - 150 + 1) + 150))) / 1000,
                Electronics.OperatingSystem.IOS, "356984215742369"));
        warehouse.add(new Smartphone("samsung 150", (double) (new Random().nextInt(((250 - 150 + 1) + 150))) / 1000,
                Electronics.OperatingSystem.ANDROID, "490154203237518"));
        warehouse.add(new SmartWatch("galaxy watch 30", (double) (new Random().nextInt(((80 - 50 + 1) + 50))) / 1000,
                Electronics.OperatingSystem.ANDROID, "490154203237518"));
        warehouse.add(new Fridge("polaris", (double) (new Random().nextInt(((15 - 3 + 1) + 3))),
                Appliances.InstallationType.PORTABLE, false));
        warehouse.add(new Toaster("BQ", (double) (new Random().nextInt(((2 - 1 + 1) + 1))),
                Appliances.InstallationType.PORTABLE, (new Random().nextInt(6 - 2 + 1) + 2)));

        System.out.println(calculateTotalWeight(warehouse));
    }

    static double calculateTotalWeight(ArrayList<Products> warehouse){
        double totalWeight = 0;
        for(var i : warehouse){
            totalWeight += i.getWeight();
        }
        return totalWeight;
    }

}