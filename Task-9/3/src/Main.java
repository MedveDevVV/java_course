import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    int num = ThreadLocalRandom.current().nextInt(100);
                    buffer.produce(num);
                    Thread.sleep(ThreadLocalRandom.current().nextInt(i < 9 ? 100 : 600) + 100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    buffer.consume();
                    Thread.sleep(ThreadLocalRandom.current().nextInt(i < 8 ? 600 : 50) + 100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}