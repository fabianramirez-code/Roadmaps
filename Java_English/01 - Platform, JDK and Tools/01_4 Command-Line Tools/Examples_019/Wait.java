import java.util.concurrent.TimeUnit;

public class Wait {

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread (() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "worker");
        worker.start();
        worker.join();
    }
}