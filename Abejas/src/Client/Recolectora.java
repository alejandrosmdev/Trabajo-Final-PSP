package Client;

import java.util.Random;

public class Recolectora {
    public static void main(String[] args) {
        Random random = new Random();

        while (true) {
            // Simular el tiempo de producción de miel (4 a 8 segunodos)
            int tiempoProduccion = 4 + random.nextInt(5);
            System.out.println("Recolectora produciendo miel durante " + tiempoProduccion + " segundos");

            try {
                Thread.sleep(tiempoProduccion * 1000);
            } catch (InterruptedException e) {
                System.out.println("Error durante la producción de miel " + e.getMessage());
            }

            System.out.println("Producción de miel completada");

            // Simular descando (2 a 5 segundos)
            int tiempoDescanso = 2 + random.nextInt(4);
            System.out.println("Recolectora descansando durante " + tiempoDescanso + " segundos");

            try {
                Thread.sleep(tiempoDescanso * 1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("Recolectora lista para producir más miel");
        }
    }
}
