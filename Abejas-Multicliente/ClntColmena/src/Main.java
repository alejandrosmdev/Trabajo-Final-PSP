import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Thread> abejas = new ArrayList<>();

        // Inicializar 3 zanganos
        for (int i = 0; i < 1; i++) {
            abejas.add(new Zangano(i));
        }

        // Inicializar 5 limpiadores
        for (int i = 0; i < 1; i++) {
            abejas.add(new Limpiadora(i));
        }

        // Inicializar 10 recolectoras
        for (int i = 0; i < 1; i++) {
            abejas.add(new Recolectora(i));
        }

        // Iniciar todos los hilos
        for (Thread abeja : abejas) {
            abeja.start();
        }

        // Esperar a que el usuario presione enter para terminar la simulación
        System.out.println("Presione Enter para terminar la simulacion...");
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

        System.out.println("Simulación terminada");
    }
}