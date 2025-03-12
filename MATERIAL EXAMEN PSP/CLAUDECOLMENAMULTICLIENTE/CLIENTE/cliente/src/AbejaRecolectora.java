import java.io.IOException;
import java.util.Random;

public class AbejaRecolectora extends AbejaCliente {
    private final Random random = new Random();

    public AbejaRecolectora(int id, ClienteColmena cliente) {
        super(id, cliente);
    }

    @Override
    public void run() {
        System.out.println("\u001B[35m[RECOLECTORA-" + id + "] Recolectora lista para hacer miel\u001B[0m");

        while (running && cliente.estaConectado()) {
            try {
                // Hacer miel
                int tiempoRecoleccion = random.nextInt(5) + 4; // Entre 4 y 8 segundos
                System.out.println("\u001B[35m[RECOLECTORA-" + id + "] Haciendo miel durante " +
                        tiempoRecoleccion + " segundos\u001B[0m");

                // Simular el tiempo de recolección
                Thread.sleep(tiempoRecoleccion * 1000);

                // Tiempo de descanso
                int tiempoDescanso = random.nextInt(4) + 2; // Entre 2 y 5 segundos

                // Notificar al servidor
                cliente.notificarRecoleccion(tiempoRecoleccion, tiempoDescanso);

                System.out.println("\u001B[35m[RECOLECTORA-" + id + "] Descansando durante " +
                        tiempoDescanso + " segundos\u001B[0m");

                // Simular el tiempo de descanso
                Thread.sleep(tiempoDescanso * 1000);

            } catch (IOException e) {
                System.out.println("\u001B[31m[ERROR] Recolectora-" + id +
                        " error de comunicación: " + e.getMessage() + "\u001B[0m");
                running = false;
            } catch (InterruptedException e) {
                if (running) {
                    System.out.println("\u001B[31m[ERROR] Recolectora-" + id +
                            " interrumpida: " + e.getMessage() + "\u001B[0m");
                }
            }
        }

        System.out.println("\u001B[35m[RECOLECTORA-" + id + "] Recolectora ha terminado su trabajo\u001B[0m");
    }
}

