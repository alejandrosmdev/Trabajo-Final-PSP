import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Iniciando colmena...");

        // Inicializar 3 limpiadoras
        Limpiadora limpiadora1 = new Limpiadora(1);
        Limpiadora limpiadora2 = new Limpiadora(2);
        Limpiadora limpiadora3 = new Limpiadora(3);

        // Inicializar 2 recolectoras
        Recolectora recolectora1 = new Recolectora(1);
        Recolectora recolectora2 = new Recolectora(2);

        // Inicializar 4 zánganos
        Zangano zangano1 = new Zangano(1);
        Zangano zangano2 = new Zangano(2);
        Zangano zangano3 = new Zangano(3);
        Zangano zangano4 = new Zangano(4);

        // Iniciar limpiadoras
        limpiadora1.start();
        limpiadora2.start();
        limpiadora3.start();

        // Iniciar recolectoras
        recolectora1.start();
        recolectora2.start();

        // Iniciar zánganos
        zangano1.start();
        zangano2.start();
        zangano3.start();
        zangano4.start();

        System.out.println("Colmena iniciada correctamente");
    }
}