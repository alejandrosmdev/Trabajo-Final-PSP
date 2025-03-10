import java.util.Random;
import java.util.concurrent.Semaphore;

public class Nodriza extends Thread {
    private int id;
    private Random random = new Random();
    private Semaphore disponible = new Semaphore(1);

    public Nodriza(int id) {
        super("Nodriza-" + id);
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Nodriza " + id + " ha comenzado a trabajar.");

        while (true) {
            try {
                // Esperar a que un zángano solicite alimento
                disponible.acquire();

                // Esperar a que llegue una solicitud (simulado con el semáforo)
                System.out.println("Nodriza " + id + " está esperando solicitudes de alimento.");

                // El método alimentarZangano será llamado por el zángano

            } catch (InterruptedException e) {
                System.out.println("Nodriza " + id + " ha sido interrumpida.");
                break;
            }
        }
    }

    public void alimentarZangano(int idZangano) throws InterruptedException {
        try {
            System.out.println("Nodriza " + id + ": Toma toma, cómetelo todo angelico!!! (Alimentando al Zángano " + idZangano + ")");

            // Tiempo aleatorio para alimentar (3-5 segundos)
            int tiempoAlimentacion = 3000 + random.nextInt(2001);
            Thread.sleep(tiempoAlimentacion);

            System.out.println("Nodriza " + id + " ha terminado de alimentar al Zángano " + idZangano +
                    " (Tiempo: " + (tiempoAlimentacion / 1000.0) + " segundos)");

        } finally {
            // Liberar el semáforo para estar disponible nuevamente
            disponible.release();
        }
    }

    public boolean estaDisponible() {
        return disponible.availablePermits() > 0;
    }

    public void solicitarAlimento(int idZangano) throws InterruptedException {
        if (disponible.tryAcquire()) {
            try {
                alimentarZangano(idZangano);
            } finally {
                disponible.release();
            }
            return;
        }
        throw new InterruptedException("Nodriza " + id + " no está disponible");
    }
}

