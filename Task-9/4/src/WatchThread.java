import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class WatchThread implements Runnable {
    private final int interval;

    public WatchThread(int intervalMillisecond) {
        this.interval = intervalMillisecond;

    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(interval);
                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
