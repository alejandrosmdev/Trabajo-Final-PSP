import java.util.*;


public class Reina {
    private Random random = new Random();
    private boolean running = true;

    // Asigna una zona aleatoria para limpiar
    public String asignarZona() {
        return "Zona " + (random.nextInt(10) + 1);
    }

    //Detiene la actividad de la reina
    public void detener() {
        running = false;
    }

     // Verifica si la reina sigue activa
    public boolean isRunning() {
        return running;
    }
}