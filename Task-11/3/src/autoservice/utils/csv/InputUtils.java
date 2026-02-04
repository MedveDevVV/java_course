package autoservice.utils.csv;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputUtils {
    public static int readNumberInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextInt()) {
                System.out.println("Ошибка: введите число!");
                scanner.nextLine();
                continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice >= min && choice <= max) {
                return choice;
            }
            System.out.println("Ошибка: введите число от " + min + " до " + max);
        }
    }

    public static LocalDate readDateInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: ввод даты не соответствует шаблону");
            }
        }
    }

}
