import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;

public class AbejaReina extends Thread {
    private static final String[] ZONAS = {
            "Entrada de la colmena", "Panal central", "Cámara de cría",
            "Almacén de miel", "Zona de puesta", "Panal superior",
            "Panal inferior", "Zona de danza"
    };

    private Random random = new Random();
    private BlockingQueue<String> ordenesQueue = new LinkedBlockingQueue<>();

    public AbejaReina() {
        super("Reina");
    }

    @Override
    public void run() {
        System.out.println("La Abeja Reina ha comenzado a dar órdenes.");

        while (true) {
            try {
                // Esperar a que una limpiadora solicite una orden
                String solicitud = ordenesQueue.take();

                // Generar una zona aleatoria para limpiar
                String zona = ZONAS[random.nextInt(ZONAS.length)];

                System.out.println("Reina: Ordeno limpiar la zona: " + zona + " - " + solicitud);

                // Devolver la orden a la limpiadora
                synchronized (solicitud) {
                    solicitud.notifyAll();
                }

                // Pequeña pausa entre órdenes
                Thread.sleep(500);

            } catch (InterruptedException e) {
                System.out.println("La Reina ha sido interrumpida.");
                break;
            }
        }
    }

    public String solicitarOrden(int idLimpiadora) throws InterruptedException {
        String solicitud = "Solicitud-" + idLimpiadora;
        ordenesQueue.put(solicitud);

        synchronized (solicitud) {
            solicitud.wait();
        }

        return ZONAS[random.nextInt(ZONAS.length)];
    }
}

