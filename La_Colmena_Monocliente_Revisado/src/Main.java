import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Thread> abejas = new ArrayList<>();

        // Inicializar una Abeja Reina
        AbejaReina reina = new AbejaReina();
        abejas.add(reina);

        // Inicializar 3 Nodrizas
        for (int i = 0; i < 3; i++) {
            abejas.add(new Nodriza());
        }

        // Inicializar 3 Zánganos
        for (int i = 0; i < 3; i++) {
            abejas.add(new Zangano());
        }

        // Inicializar 3 Limpiadoras
        for (int i = 0; i < 5; i++) {
            abejas.add(new Limpiadora(i + 1));
        }

        // Inicializar 3 Recolectoras
        for (int i = 0; i < 3; i++) {
            abejas.add(new Recolectora());
        }

        // Iniciar todos los hilos
        for (Thread abeja : abejas) {
            abeja.start();
        }

        // Esperar a que el usuario presione Enter para terminar la simulación
        System.out.println("Presione Enter para terminar la simulación...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Interrumpir todos los hilos
        for (Thread abeja : abejas) {
            abeja.interrupt();
        }

        // Esperar a que todos los hilos terminen
        for (Thread abeja : abejas) {
            try {
                abeja.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulación terminada.");
    }
}