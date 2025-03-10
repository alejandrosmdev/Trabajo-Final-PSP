import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Thread> abejas = new ArrayList<>();

        // Inicializar 3 Zánganos
        for (int i = 1; i <= 3; i++) {
            abejas.add(new Zangano(i));
        }

        // Inicializar 5 Limpiadoras
        for (int i = 1; i <= 5; i++) {
            abejas.add(new Limpiadora(i));
        }

        // Inicializar 10 Recolectoras
        for (int i = 1; i <= 3; i++) {
            abejas.add(new Recolectora(i));
        }

        // Iniciar todos los hilos
        for (Thread abeja : abejas) {
            abeja.start();
        }

        // Esperar a que el usuario presione Enter para terminar la simulación
        System.out.println("Presione Enter para terminar la simulación...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

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
