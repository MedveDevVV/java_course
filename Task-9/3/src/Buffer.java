import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int maxSize;

    public Buffer(int size) {
        this.maxSize = size;
    }

    public synchronized void produce(int value) throws InterruptedException {
        while (queue.size() == maxSize) {
            System.out.println("Производитель ждет");
            wait();
        }
        queue.add(value);
        System.out.printf("Произведено: %s; (размер: %s)\n", value, queue.size());
        notifyAll();
    }

    public synchronized void consume() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println("Потребитель ждет");
            wait();
        }
        int value = queue.poll();
        System.out.printf("Потреблено: %s; (размер: %s)\n", value, queue.size());
        notifyAll();
    }
}