import java.util.Random;

public class Recolectora extends Thread {
    private int id;
    private Random random = new Random();

    public Recolectora(int id) {
        super("Recolectora-" + id);
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Recolectora " + id + " ha comenzado a trabajar.");

        while (true) {
            try {
                // Hacer miel
                System.out.println("Recolectora " + id + ": Haciendo miel soy feliz...");

                // Tiempo aleatorio para hacer miel (4-8 segundos)
                int tiempoHacerMiel = 4000 + random.nextInt(4001);
                Thread.sleep(tiempoHacerMiel);

                System.out.println("Recolectora " + id + ": Uyyyy cuanta miel he hecho en este rato!!! " +
                        "(Tiempo: " + (tiempoHacerMiel / 1000.0) + " segundos)");

                // Descansar
                System.out.println("Recolectora " + id + ": A ver si descanso un poquico");

                // Tiempo aleatorio para descansar (2-5 segundos)
                int tiempoDescanso = 2000 + random.nextInt(3001);
                Thread.sleep(tiempoDescanso);

                System.out.println("Recolectora " + id + " ha terminado de descansar " +
                        "(Tiempo: " + (tiempoDescanso / 1000.0) + " segundos)");

            } catch (InterruptedException e) {
                System.out.println("Recolectora " + id + " ha sido interrumpida.");
                break;
            }
        }
    }
}

