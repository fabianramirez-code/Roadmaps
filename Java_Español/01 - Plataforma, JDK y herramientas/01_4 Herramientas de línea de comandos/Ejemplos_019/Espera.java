import java.util.concurrent.TimeUnit;

public class Espera {

    public static void main(String[] args) throws InterruptedException {
        Thread trabajador = new Thread (() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "trabajador");
        trabajador.start();
        trabajador.join();
    }
}