import java.io.IOException;
import java.util.Random;

public class AbejaZangano extends AbejaCliente {
    private final Random random = new Random();

    public AbejaZangano(int id, ClienteColmena cliente) {
        super(id, cliente);
    }

    @Override
    public void run() {
        System.out.println("\u001B[33m[ZÁNGANO-" + id + "] Zángano listo para buscar reinas\u001B[0m");

        while (running && cliente.estaConectado()) {
            try {
                // Buscar reinas fuera de la colmena
                int tiempoBusqueda = random.nextInt(6) + 5; // Entre 5 y 10 segundos
                System.out.println("\u001B[33m[ZÁNGANO-" + id + "] Buscando reinas durante " +
                        tiempoBusqueda + " segundos\u001B[0m");

                // Simular el tiempo de búsqueda
                Thread.sleep(tiempoBusqueda * 1000);

                // Probabilidad de 1/10 de encontrar una reina
                int probabilidad = random.nextInt(10);
                if (probabilidad == 0) {
                    // Encontró una reina, se va de la colmena
                    System.out.println("\u001B[33m[ZÁNGANO-" + id +
                            "] ¡He encontrado una reina! Me voy a fundar una nueva colmena\u001B[0m");
                    running = false;
                } else {
                    // No encontró una reina, vuelve a pedir alimento
                    System.out.println("\u001B[33m[ZÁNGANO-" + id +
                            "] No he encontrado reina, vuelvo a pedir alimento\u001B[0m");

                    // Solicitar alimento a una nodriza
                    boolean alimentado = cliente.solicitarAlimento();

                    if (!alimentado) {
                        System.out.println("\u001B[33m[ZÁNGANO-" + id +
                                "] No hay nodrizas disponibles, esperaré un momento\u001B[0m");
                        Thread.sleep(2000); // Esperar 2 segundos antes de volver a intentar
                    } else {
                        System.out.println("\u001B[33m[ZÁNGANO-" + id +
                                "] He sido alimentado, ahora volveré a buscar reinas\u001B[0m");
                    }
                }

            } catch (IOException e) {
                System.out.println("\u001B[31m[ERROR] Zángano-" + id +
                        " error de comunicación: " + e.getMessage() + "\u001B[0m");
                running = false;
            } catch (InterruptedException e) {
                if (running) {
                    System.out.println("\u001B[31m[ERROR] Zángano-" + id +
                            " interrumpido: " + e.getMessage() + "\u001B[0m");
                }
            }
        }

        System.out.println("\u001B[33m[ZÁNGANO-" + id + "] Zángano ha terminado su trabajo\u001B[0m");
    }
}

