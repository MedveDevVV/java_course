public class MyRunnable implements Runnable {
    public static final Object MONITOR = new Object();
    public static String previousThread;

    @Override
    public void run() {
        for (int i = 0; i < 3; ++i) {
            while (previousThread == Thread.currentThread().getName()){}
            synchronized (MONITOR){
                MyRunnable.previousThread = Thread.currentThread().getName();
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
