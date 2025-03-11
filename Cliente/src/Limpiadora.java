import java.io.*;
import java.net.*;
import java.util.Random;

public class Limpiadora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO_REINA = 5000;
    private static Random random = new Random();
    private int id;

    public Limpiadora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Solicitar zona a la reina
                int zona = solicitarZona();

                // Limpiar zona
                int tiempoLimpieza = random.nextInt(8) + 3; // 3-10 segundos
                System.out.println("Limpiadora " + id + ": Limpiando zona " + zona + " durante " + tiempoLimpieza + " segundos");
                Thread.sleep(tiempoLimpieza * 1000);

                System.out.println("Limpiadora " + id + ": Zona " + zona + " limpia. Solicitando nueva zona.");
            } catch (InterruptedException | IOException e) {
                System.out.println("Error en limpiadora " + id + ": " + e.getMessage());
            }
        }
    }

    private int solicitarZona() throws IOException {
        Socket socket = new Socket(HOST, PUERTO_REINA);
        try (
                DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
                DataInputStream entrada = new DataInputStream(socket.getInputStream())
        ) {
            // Enviar ID de la limpiadora junto con la solicitud
            salida.writeUTF("SOLICITAR_ZONA:" + id);
            String respuesta = entrada.readUTF();

            if (respuesta.startsWith("ZONA:")) {
                return Integer.parseInt(respuesta.substring(5));
            } else {
                return -1; // Error
            }
        } finally {
            socket.close();
        }
    }
}
