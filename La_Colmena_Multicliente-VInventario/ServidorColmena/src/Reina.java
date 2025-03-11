import java.util.*;

/**
 * Representa a la Abeja Reina de la colmena.
 * Se encarga de asignar zonas a las limpiadoras.
 */
public class Reina {
    private Random random = new Random();
    private boolean running = true;

    /**
     * Asigna una zona aleatoria para limpiar
     * @return Nombre de la zona asignada
     */
    public String asignarZona() {
        return "Zona " + (random.nextInt(10) + 1);
    }

    /**
     * Detiene la actividad de la reina
     */
    public void detener() {
        running = false;
    }

    /**
     * Verifica si la reina sigue activa
     * @return true si la reina está activa, false en caso contrario
     */
    public boolean isRunning() {
        return running;
    }
}