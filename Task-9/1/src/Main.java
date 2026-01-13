public class Main {
    static final Object MONITOR = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread thread0 = new Thread(() -> {
            System.out.println("работает поток " + Thread.currentThread().getName());
            System.out.printf("Поток %s находится в состоянии %s\n", Thread.currentThread().getName(), Thread.currentThread().getState());
            try {
                block();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread thread2 = new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread2.start();
            try {
                thread2.join(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                thread2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread thread1 = new Thread(() -> {
            System.out.println("работает поток" + Thread.currentThread().getName());
            try {
                block();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("создан поток: " + thread0.getName());
        System.out.printf("Поток %s находится в состоянии %s\n", thread0.getName(), thread0.getState());

        thread1.start();
        Thread.sleep(100);

        thread0.start();
        Thread.sleep(100);
        System.out.printf("Поток %s находится в состоянии %s\n", thread0.getName(), thread0.getState());
        Thread.sleep(900);
        System.out.printf("Поток %s находится в состоянии %s\n", thread0.getName(), thread0.getState());
        Thread.sleep(1900);
        System.out.printf("Поток %s находится в состоянии %s\n", thread0.getName(), thread0.getState());

        thread0.join();
        System.out.printf("Поток %s находится в состоянии %s\n", thread0.getName(), thread0.getState());
    }

    static void block() throws InterruptedException {
        synchronized (Main.MONITOR) {
            Thread.sleep(1000);
        }
    }
}