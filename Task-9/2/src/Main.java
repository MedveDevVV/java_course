public class Main{
    public static void main(String[] args) throws InterruptedException {
        Thread thread0 = new Thread(new MyRunnable());
        Thread thread1 = new Thread(new MyRunnable());

        thread0.start();
        Thread.sleep(100);
        thread1.start();

    }
}