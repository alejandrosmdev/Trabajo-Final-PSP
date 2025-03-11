package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Limpiadora {
    public static void main(String[] args) {
        String servidor = "localhost"; // Dirección del sevidor Reina
        int puerto = 5000; // Mismo puerto que la reina

        try (Socket socket = new Socket(servidor, puerto);
             DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Limpiadora conectada a la Reina en el puerto " + puerto);

            Random random = new Random();

            while (true) {
                // Solicitar orden a la Reina
                salida.writeUTF("Solicitar orden");
                salida.flush();

                // Recibir zona y tiempo de limpieza
                String zona = entrada.readUTF();
                int tiempoLimpieza = entrada.readInt();

                System.out.println("Limpiando " + zona + " en " + tiempoLimpieza + " segundos");

                // Simular tiempo de limpieza
                Thread.sleep(tiempoLimpieza * 1000);

                System.out.println("Limpieza en zona " + zona + " completada. Pidiendo nueva orden");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
