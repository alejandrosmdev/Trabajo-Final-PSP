import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AbejaReina extends Thread {
    private boolean running = true;
    private final List<String> zonasColmena;
    private final Random random = new Random();

    public AbejaReina() {
        // Inicializar las zonas de la colmena
        zonasColmena = new ArrayList<>();
        zonasColmena.add("Entrada");
        zonasColmena.add("Cámara de cría");
        zonasColmena.add("Almacén de miel");
        zonasColmena.add("Almacén de polen");
        zonasColmena.add("Pasillo central");
        zonasColmena.add("Zona de descanso");

        // Iniciar el hilo de la reina
        this.start();
    }

    @Override
    public void run() {
        System.out.println("\u001B[36m[REINA] La reina ha comenzado a dar órdenes\u001B[0m");

        while (running) {
            try {
                // La reina simplemente espera a que las limpiadoras soliciten zonas
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("\u001B[31m[ERROR] La reina ha sido interrumpida: " +
                        e.getMessage() + "\u001B[0m");
                running = false;
            }
        }

        System.out.println("\u001B[36m[REINA] La reina ha dejado de dar órdenes\u001B[0m");
    }

    public synchronized String asignarZonaLimpieza() {
        // Seleccionar una zona aleatoria para limpiar
        int indice = random.nextInt(zonasColmena.size());
        String zona = zonasColmena.get(indice);

        System.out.println("\u001B[36m[REINA] He asignado la zona '" + zona +
                "' para limpiar\u001B[0m");

        return zona;
    }

    public void detener() {
        running = false;
        this.interrupt();
    }
}

