/**
 * Gestiona el inventario de miel de la colmena utilizando sincronización.
 */
public class InventarioMiel {
    // Singleton para asegurar que solo hay una instancia del inventario
    private static InventarioMiel instancia;

    // Recurso: miel
    private int cantidadMiel;

    // Umbrales de alerta
    private final int UMBRAL_BAJO_MIEL = 10;
    private final int UMBRAL_CRITICO_MIEL = 5;

    private InventarioMiel() {
        this.cantidadMiel = 0;  // Valor inicial
    }

    public static synchronized InventarioMiel getInstancia() {
        if (instancia == null) {
            instancia = new InventarioMiel();
        }
        return instancia;
    }

    // Método para añadir miel al inventario
    public synchronized void agregarMiel(int cantidad) {
        cantidadMiel += cantidad;
        System.out.println("Inventario: Se han añadido " + cantidad + " unidades de miel. Total: " + cantidadMiel);

        // Notificar que hay miel disponible
        notifyAll();

        // Verificar si se ha superado el umbral bajo
        verificarUmbral();
    }

    // Método para consumir miel del inventario
    public synchronized boolean consumirMiel(int cantidad) {
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
    }

    // Método para esperar hasta que haya suficiente miel
    public synchronized boolean esperarMielSuficiente(int cantidad) {
        while (this.cantidadMiel < cantidad) {
            System.out.println("Inventario: Esperando que haya suficiente miel (" + cantidad + " unidades)...");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
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
    public synchronized int getCantidadMiel() {
        return cantidadMiel;
    }

    // Método para obtener un informe del inventario
    public synchronized String obtenerInforme() {
        return "=== INFORME DE INVENTARIO DE MIEL ===\n" +
                "Miel: " + cantidadMiel + " unidades\n" +
                "===================================";
    }
}