public class Colmena {
    public static void main(String[] args) {
        // Crear la colmena con sus componentes
        Colmena colmena = new Colmena();

        // Iniciar la simulación
        colmena.iniciarSimulacion();
    }

    // Número de abejas de cada tipo
    private static final int NUM_LIMPIADORAS = 5;
    private static final int NUM_NODRIZAS = 3;
    private static final int NUM_RECOLECTORAS = 4;
    private static final int NUM_ZANGANOS = 3;

    // Componentes de la colmena
    private AbejaReina reina;
    private Limpiadora[] limpiadoras;
    private Nodriza[] nodrizas;
    private Recolectora[] recolectoras;
    private Zangano[] zanganos;

    public Colmena() {
        // Inicializar componentes
        reina = new AbejaReina();

        limpiadoras = new Limpiadora[NUM_LIMPIADORAS];
        for (int i = 0; i < NUM_LIMPIADORAS; i++) {
            limpiadoras[i] = new Limpiadora(i + 1, reina);
        }

        nodrizas = new Nodriza[NUM_NODRIZAS];
        for (int i = 0; i < NUM_NODRIZAS; i++) {
            nodrizas[i] = new Nodriza(i + 1);
        }

        recolectoras = new Recolectora[NUM_RECOLECTORAS];
        for (int i = 0; i < NUM_RECOLECTORAS; i++) {
            recolectoras[i] = new Recolectora(i + 1);
        }

        zanganos = new Zangano[NUM_ZANGANOS];
        for (int i = 0; i < NUM_ZANGANOS; i++) {
            zanganos[i] = new Zangano(i + 1, nodrizas);
        }
    }

    public void iniciarSimulacion() {
        // Iniciar todos los hilos
        reina.start();

        for (Limpiadora limpiadora : limpiadoras) {
            limpiadora.start();
        }

        for (Nodriza nodriza : nodrizas) {
            nodriza.start();
        }

        for (Recolectora recolectora : recolectoras) {
            recolectora.start();
        }

        for (Zangano zangano : zanganos) {
            zangano.start();
        }
    }
}

