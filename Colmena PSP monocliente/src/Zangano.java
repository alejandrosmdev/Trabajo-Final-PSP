import java.util.Random;

public class Zangano extends Thread {
    private int id;
    private Nodriza[] nodrizas;
    private Random random = new Random();
    private boolean activo = true;

    public Zangano(int id, Nodriza[] nodrizas) {
        super("Zangano-" + id);
        this.id = id;
        this.nodrizas = nodrizas;
    }

    @Override
    public void run() {
        System.out.println("Zángano " + id + " ha comenzado a trabajar.");

        while (activo) {
            try {
                // Buscar una nueva reina
                System.out.println("Zángano " + id + ": A ver si encuentro una pájara güena... digoooo una abeja reina!!!");

                // Tiempo aleatorio para buscar (5-10 segundos)
                int tiempoBusqueda = 5000 + random.nextInt(5001);
                Thread.sleep(tiempoBusqueda);

                // Probabilidad 1/10 de encontrar una reina
                if (random.nextInt(10) == 0) {
                    System.out.println("Zángano " + id + " ha encontrado una nueva reina y se va con ella! " +
                            "(Tiempo de búsqueda: " + (tiempoBusqueda / 1000.0) + " segundos)");
                    activo = false;
                    break;
                } else {
                    System.out.println("Zángano " + id + " no ha encontrado una reina " +
                            "(Tiempo de búsqueda: " + (tiempoBusqueda / 1000.0) + " segundos)");

                    // Pedir alimento a una nodriza aleatoria
                    System.out.println("Zángano " + id + ": Teno Hambre!!!");

                    boolean alimentado = false;
                    while (!alimentado && activo) {
                        // Seleccionar una nodriza aleatoria
                        Nodriza nodriza = nodrizas[random.nextInt(nodrizas.length)];

                        try {
                            if (nodriza.estaDisponible()) {
                                nodriza.solicitarAlimento(id);
                                alimentado = true;
                            } else {
                                // Esperar un poco antes de intentar con otra nodriza
                                Thread.sleep(500);
                            }
                        } catch (InterruptedException e) {
                            // Intentar con otra nodriza
                            System.out.println("Zángano " + id + " no pudo ser alimentado por la nodriza, intentando con otra...");
                        }
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("Zángano " + id + " ha sido interrumpido.");
                break;
            }
        }

        if (!activo) {
            System.out.println("Zángano " + id + " ha abandonado la colmena.");
        }
    }
}

