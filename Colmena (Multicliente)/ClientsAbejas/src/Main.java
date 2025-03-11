import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Thread> abejas = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            abejas.add(new Zangano(i));
        }

        for (int i = 1; i <= 5; i++) {
            abejas.add(new Limpiadora(i));
        }

        for (int i = 1; i <= 10; i++) {
            abejas.add(new Recolectora(i));
        }

        for (Thread abeja : abejas) {
            abeja.start();
        }

        System.out.println("Presione Enter para terminar la simulación...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

        for (Thread abeja : abejas) {
            abeja.interrupt();
        }

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
