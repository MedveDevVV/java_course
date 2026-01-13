import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Thread thread0 = new Thread(new WatchThread(1000));
        thread0.setDaemon(true);
        thread0.start();
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }
}