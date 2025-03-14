import java.util.Random;

public class ReinaServidor implements Runnable {
    private final Random random = new Random();
    private int lastZone = -1; // Almacena la última zona generada
    
    

    @Override
    public void run() {
        while (true) {
            try {
                // Espera a que una limpiadora solicite una orden
                ClientHandler abejas = MultiClientServer.orderQueue.take();
                int newZone;
                int password = 9856;
                if (abejas.beeType=="LIMPIADORA") {
                    // Genera una nueva zona asegurándose de que no sea igual a la anterior
                    do {
                        newZone = random.nextInt(10);
                    } while (newZone == lastZone);

                    lastZone = newZone;
                    String zone = "Zona-" + newZone;

                    System.out.println("Reina (Servidor): asignando " + zone + " a limpiadora " + abejas.clientId);
                    // Envía la orden directamente al cliente limpiador
                    abejas.sendMessage("LIMPIAR:" + zone);
                } else if (abejas.beeType=="SOLDADO") {
                    System.out.println("Reina (Servidor): proporcionando contraseña " + password + " al soldado " + abejas.clientId);
                    abejas.sendMessage("CONTRASENA: "+ password);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
