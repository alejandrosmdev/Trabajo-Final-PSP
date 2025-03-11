import java.io.*;
import java.net.*;
import java.util.Random;

public class Recolectora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO_NODRIZA_BASE = 6001;
    private static Random random = new Random();
    private int id;

    public Recolectora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Hacer miel
                int tiempoProduccion = random.nextInt(5) + 4; // 4-8 segundos
                System.out.println("Recolectora " + id + ": Produciendo miel durante " + tiempoProduccion + " segundos");
                Thread.sleep(tiempoProduccion * 1000);

                // Cantidad de miel producida (1-3 unidades)
                int cantidadMiel = random.nextInt(3) + 1;

                // Enviar miel a una nodriza
                boolean entregada = false;
                while (!entregada) {
                    // Intentar con una nodriza aleatoria
                    int idNodriza = random.nextInt(3) + 1; // 1-3
                    entregada = enviarMielANodriza(idNodriza, cantidadMiel);

                    if (!entregada) {
                        // Esperar un poco antes de intentar con otra nodriza
                        Thread.sleep(1000);
                    }
                }

                // Descansar
                int tiempoDescanso = random.nextInt(4) + 2; // 2-5 segundos
                System.out.println("Recolectora " + id + ": Descansando durante " + tiempoDescanso + " segundos");
                Thread.sleep(tiempoDescanso * 1000);
            } catch (InterruptedException | IOException e) {
                System.out.println("Error en recolectora " + id + ": " + e.getMessage());
            }
        }
    }

    private boolean enviarMielANodriza(int idNodriza, int cantidadMiel) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket(HOST, PUERTO_NODRIZA_BASE + idNodriza);
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Enviar ID de la recolectora y cantidad de miel
            salida.writeUTF("MIEL:" + id + ":" + cantidadMiel);

            // Verificar si la nodriza está ocupada
            try {
                String respuesta = entrada.readUTF();
                if (respuesta.equals("OCUPADA")) {
                    System.out.println("Recolectora " + id + ": Nodriza " + idNodriza + " está ocupada, intentaré con otra");
                    return false;
                }
            } catch (EOFException e) {
                // No hay respuesta, asumimos que la miel fue entregada
            }

            System.out.println("Recolectora " + id + ": " + cantidadMiel + " unidades de miel entregadas a nodriza " + idNodriza);
            return true;
        } catch (IOException e) {
            System.out.println("Recolectora " + id + ": Error conectando con nodriza " + idNodriza + ": " + e.getMessage());
            return false;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
