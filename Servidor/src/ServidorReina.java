import java.io.*;
import java.net.*;
import java.util.Random;

public class ServidorReina {
    private static final int PUERTO_REINA = 5000;
    private static Random random = new Random();

    public static void main(String[] args) {
        try (ServerSocket servidorReina = new ServerSocket(PUERTO_REINA)) {
            System.out.println("Servidor Reina iniciado en puerto " + PUERTO_REINA);

            while (true) {
                Socket socket = servidorReina.accept();
                new Thread(new AtenderLimpiadora(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error en el servidor Reina: " + e.getMessage());
        }
    }

    static class AtenderLimpiadora implements Runnable {
        private Socket socket;

        public AtenderLimpiadora(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    DataInputStream entrada = new DataInputStream(socket.getInputStream());
                    DataOutputStream salida = new DataOutputStream(socket.getOutputStream())
            ) {
                String mensaje = entrada.readUTF();

                // Extraer ID de la limpiadora del mensaje
                if (mensaje.startsWith("SOLICITAR_ZONA:")) {
                    int idLimpiadora = Integer.parseInt(mensaje.substring(15));
                    int zona = random.nextInt(100) + 1; // Zonas del 1 al 100

                    salida.writeUTF("ZONA:" + zona);
                    System.out.println("Reina: Asignada zona " + zona + " a limpiadora " + idLimpiadora);
                }
            } catch (IOException e) {
                System.out.println("Error atendiendo limpiadora: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error cerrando socket: " + e.getMessage());
                }
            }
        }
    }
}
