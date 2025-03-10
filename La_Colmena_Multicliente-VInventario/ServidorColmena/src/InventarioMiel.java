import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class InventarioMiel {
    // Singleton para asegurar que solo hay una instancia del inventario
    private static InventarioMiel instancia;

    // Recurso: miel
    private int cantidadMiel;

    // Umbrales de alerta
    private final int UMBRAL_BAJO_MIEL = 10;
    private final int UMBRAL_CRITICO_MIEL = 5;

    // Control de concurrencia
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition mielDisponible = lock.newCondition();

    private InventarioMiel() {
        this.cantidadMiel = 20;  // Valor inicial
    }

    public static synchronized InventarioMiel getInstancia() {
        if (instancia == null) {
            instancia = new InventarioMiel();
        }
        return instancia;
    }

    // Método para añadir miel al inventario
    public void agregarMiel(int cantidad) {
        lock.lock();
        try {
            cantidadMiel += cantidad;
            System.out.println("Inventario: Se han añadido " + cantidad + " unidades de miel. Total: " + cantidadMiel);

            // Notificar que hay miel disponible
            mielDisponible.signalAll();

            // Verificar si se ha superado el umbral bajo
            verificarUmbral();
        } finally {
            lock.unlock();
        }
    }

    // Método para consumir miel del inventario
    public boolean consumirMiel(int cantidad) {
        lock.lock();
        try {
            // Verificar si hay suficiente miel
            if (this.cantidadMiel < cantidad) {
                System.out.println("Inventario: No hay suficiente miel para consumir " + cantidad + " unidades.");
                return false;
            }

            // Consumir la miel
            this.cantidadMiel -= cantidad;

            System.out.println("Inventario: Se han consumido " + cantidad + " unidades de miel. Quedan: " + this.cantidadMiel);

            verificarUmbral();
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Método para esperar hasta que haya suficiente miel
    public boolean esperarMielSuficiente(int cantidad) {
        lock.lock();
        try {
            while (this.cantidadMiel < cantidad) {
                System.out.println("Inventario: Esperando que haya suficiente miel (" + cantidad + " unidades)...");
                try {
                    mielDisponible.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Verificar si la miel está por debajo del umbral
    private void verificarUmbral() {
        if (cantidadMiel < UMBRAL_CRITICO_MIEL) {
            System.out.println("¡ALERTA CRÍTICA! Nivel de miel extremadamente bajo: " + cantidadMiel);
        } else if (cantidadMiel < UMBRAL_BAJO_MIEL) {
            System.out.println("¡ALERTA! Nivel de miel bajo: " + cantidadMiel);
        }
    }

    // Método para obtener la cantidad actual de miel
    public int getCantidadMiel() {
        lock.lock();
        try {
            return cantidadMiel;
        } finally {
            lock.unlock();
        }
    }

    // Método para obtener un informe del inventario
    public String obtenerInforme() {
        lock.lock();
        try {
            return "=== INFORME DE INVENTARIO DE MIEL ===\n" +
                    "Miel: " + cantidadMiel + " unidades\n" +
                    "===================================";
        } finally {
            lock.unlock();
        }
    }
}