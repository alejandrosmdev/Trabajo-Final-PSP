import java.io.IOException;
import java.util.Random;

public class AbejaLimpiadora extends AbejaCliente {
    private final Random random = new Random();

    public AbejaLimpiadora(int id, ClienteColmena cliente) {
        super(id, cliente);
    }

    @Override
    public void run() {
        System.out.println("\u001B[34m[LIMPIADORA-" + id + "] Limpiadora lista para trabajar\u001B[0m");

        while (running && cliente.estaConectado()) {
            try {
                // Solicitar una zona para limpiar
                String zona = cliente.solicitarZonaLimpieza();
                System.out.println("\u001B[34m[LIMPIADORA-" + id + "] Voy a limpiar la zona: " +
                        zona + "\u001B[0m");

                // Tiempo aleatorio de limpieza entre 3 y 10 segundos
                int tiempoLimpieza = random.nextInt(8) + 3;
                System.out.println("\u001B[34m[LIMPIADORA-" + id + "] Limpiando durante " +
                        tiempoLimpieza + " segundos\u001B[0m");

                // Simular el tiempo de limpieza
                Thread.sleep(tiempoLimpieza * 1000);

                System.out.println("\u001B[34m[LIMPIADORA-" + id + "] He terminado de limpiar la zona: " +
                        zona + "\u001B[0m");

            } catch (IOException e) {
                System.out.println("\u001B[31m[ERROR] Limpiadora-" + id +
                        " error de comunicación: " + e.getMessage() + "\u001B[0m");
                running = false;
            } catch (InterruptedException e) {
                if (running) {
                    System.out.println("\u001B[31m[ERROR] Limpiadora-" + id +
                            " interrumpida: " + e.getMessage() + "\u001B[0m");
                }
            }
        }

        System.out.println("\u001B[34m[LIMPIADORA-" + id + "] Limpiadora ha terminado su trabajo\u001B[0m");
    }
}

