import java.util.Random;

public class AbejaNodrizaServidor extends Thread {
    private final int id;
    private boolean disponible = true;
    private boolean running = true;
    private final Random random = new Random();

    public AbejaNodrizaServidor(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("\u001B[32m[NODRIZA-" + id + "] Nodriza lista para alimentar zánganos\u001B[0m");

        while (running) {
            try {
                // Esperar a que llegue una solicitud de alimentación
                Thread.sleep(500);
            } catch (InterruptedException e) {
                if (running) {
                    System.out.println("\u001B[31m[ERROR] Nodriza-" + id +
                            " interrumpida: " + e.getMessage() + "\u001B[0m");
                }
            }
        }

        System.out.println("\u001B[32m[NODRIZA-" + id + "] Nodriza ha terminado su servicio\u001B[0m");
    }

    public synchronized boolean estaDisponible() {
        return disponible;
    }

    public synchronized int alimentarZangano() {
        // Marcar como ocupada
        disponible = false;

        // Tiempo aleatorio de alimentación entre 3 y 5 segundos
        int tiempoAlimentacion = random.nextInt(3) + 3;

        System.out.println("\u001B[32m[NODRIZA-" + id + "] Alimentando a un zángano durante " +
                tiempoAlimentacion + " segundos\u001B[0m");

        // Simular el tiempo de alimentación
        new Thread(() -> {
            try {
                Thread.sleep(tiempoAlimentacion * 1000);

                // Marcar como disponible nuevamente
                synchronized (this) {
                    disponible = true;
                    System.out.println("\u001B[32m[NODRIZA-" + id +
                            "] He terminado de alimentar al zángano\u001B[0m");
                }
            } catch (InterruptedException e) {
                System.out.println("\u001B[31m[ERROR] Nodriza-" + id +
                        " interrumpida durante alimentación: " + e.getMessage() + "\u001B[0m");
            }
        }).start();

        return tiempoAlimentacion;
    }

    /*public long getId() {
        return int;
    }*/

    public void detener() {
        running = false;
        this.interrupt();
    }
}

