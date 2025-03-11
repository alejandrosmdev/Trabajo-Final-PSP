import java.util.Random;

public class ReinaServidor implements Runnable {
    private final Random random = new Random();
    private int lastZone = -1; // Almacena la última zona generada

    @Override
    public void run() {
        while (true) {
            try {
                // Espera a que una limpiadora solicite una orden
                ClientHandler cleaner = MultiClientServer.orderQueue.take();
                int newZone;

                // Genera una nueva zona asegurándose de que no sea igual a la anterior
                do {
                    newZone = random.nextInt(10);
                } while (newZone == lastZone);

                lastZone = newZone;
                String zone = "Zona-" + newZone;

                System.out.println("Reina (Servidor): asignando " + zone + " a limpiadora " + cleaner.clientId);
                // Envía la orden directamente al cliente limpiador
                cleaner.sendMessage("LIMPIAR:" + zone);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
