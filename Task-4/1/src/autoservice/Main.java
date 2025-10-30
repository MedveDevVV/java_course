package autoservice;

import autoservice.model.CarServiceMaster;
import autoservice.model.WorkshopPlace;
import autoservice.repository.MasterRepository;
import autoservice.repository.OrderRepository;
import autoservice.repository.WorkshopPlaceRepository;
import autoservice.repository.impl.GarageMasterRepository;
import autoservice.repository.impl.GarageWorkshopPlaceRepository;
import autoservice.repository.impl.RepairOrderRepository;
import autoservice.service.AutoServiceAdmin;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

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
        LocalDate date_1 = LocalDate.of(2025,07,13);
        UUID idOrder1 = admin.createRepairOrder(date_1, date_1, date_1,
                "Ремонт двигателя", master_1, place_1);

        System.out.println("\nСоздан заказ_1:");
        System.out.println(admin.getOrderById(idOrder1));

        LocalDate date_2 = LocalDate.of(2025, 07, 13);
        UUID idOrder2 = admin.createRepairOrder(date_1, date_2, date_2.plusDays(1),
                "Замена тормозных колодок", master_1, place_2);

        System.out.println("\nСоздан заказ_2:");
        System.out.println(admin.getOrderById(idOrder2));


        // Перенос заказа
        admin.delayOrder(idOrder1, Period.ofDays(1));
        System.out.println("\nЗаказ задерживается, новая информация о заказе_1:");
        System.out.println(admin.getOrderById(idOrder1));

        System.out.println("\nНовая информация о заказе_2:");
        System.out.println(admin.getOrderById(idOrder2));

        // Поиск ближайшей свободной даты
        System.out.println("\n");
        System.out.println(admin.getFirstAvailableSlot(date_1));
        System.out.println("\n");

        // Завершение заказа
        admin.closedOrder(idOrder1);
        System.out.println("\nЗаказ завершен: ");
        System.out.println(admin.getOrderById(idOrder1));


    }
}