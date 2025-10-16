import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Month;
import java.util.UUID;

// электронный администратор автосервиса

public class Main {
    public static void main(String[] args) {

        MasterRepository masterRepo = new GarageMasterRepository();
        WorkshopPlaceRepository placeRepo = new GarageWorkshopPlaceRepository();
        OrderRepository orderRepo = new RepairOrderRepository();

        AutoServiceAdmin admin = new AutoServiceAdmin(masterRepo, placeRepo, orderRepo);

        CarServiceMaster master_1 = new CarServiceMaster("Иван", "Иванов",
                "Иванович", LocalDate.of(1980, 1, 1));
        CarServiceMaster master_2 = new CarServiceMaster("Петр", "Петров",
                "Петрович", LocalDate.of(1990, 12, 12));

        WorkshopPlace place_1 = new WorkshopPlace("Место 1");
        WorkshopPlace place_2 = new WorkshopPlace("Место 2");

        // Добавление в систему
        admin.addMaster(master_1);
        admin.addMaster(master_2);

        admin.addWorkshopPlace(place_1);
        admin.addWorkshopPlace(place_2);

        // Создание заказа
        LocalDateTime time_1 = LocalDateTime.of(2025, Month.JULY,13,12,30);
        UUID idOrder1 = admin.createRepairOrder(time_1, time_1.plusHours(2),
                "Ремонт двигателя", master_1, place_1);

        System.out.println("\nСоздан заказ:");
        System.out.println(admin.getOrderInfo(idOrder1));

        LocalDateTime time_2 = LocalDateTime.of(2025, Month.JULY, 13, 14, 35);
        UUID idOrder2 = admin.createRepairOrder(time_2, time_2.plusHours(1),
                "Замена тормозных колодок", master_1, place_1);

        System.out.println("\nСоздан заказ:");
        System.out.println(admin.getOrderInfo(idOrder2));


        // Перенос заказа
        admin.delayOrder(idOrder1, Duration.ofHours(1));
        System.out.println("\nЗаказ задерживается, новая информация о заказе:");
        System.out.println(admin.getOrderInfo(idOrder1));

        System.out.println("\nЗаказ смещен, новая информация о заказе:");
        System.out.println(admin.getOrderInfo(idOrder2));

        // Завершение заказа
        admin.closedOrder(idOrder1);
        System.out.println("\nЗаказ завершен: ");
        System.out.println(admin.getOrderInfo(idOrder1));
    }
}