import java.util.Random;

public class Recolectora extends Thread {
    private static final Random random = new Random();
    private boolean running = true;

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            int tiempoProduccion = random.nextInt(5) + 4; // 4 a 8 segundos
            System.out.println("Recolectora: Produciendo miel durante " + tiempoProduccion + " segundos.");
            try {
                Thread.sleep(tiempoProduccion * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            System.out.println("Recolectora: Miel producida.");

            int tiempoDescanso = random.nextInt(4) + 2; // 2 a 5 segundos
            System.out.println("Recolectora: Descansando durante " + tiempoDescanso + " segundos.");
            try {
                Thread.sleep(tiempoDescanso * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void detener() {
        running = false;
    }
}