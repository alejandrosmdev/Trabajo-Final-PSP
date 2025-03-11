package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ManejadorLimpiadora implements Runnable{
    private Socket socket;
    private static final String[] ZONAS = {"Zona A", "Zona B", "Zona C", "Zona D"};

    public ManejadorLimpiadora(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            Random random = new Random();

            while (true) {
                // Recibir solicitud de la limpiadora
                String mensaje = entrada.readUTF();
                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Una limpiadora se ha desconectado");
                    break;
                }

                System.out.println("Recibido mensaje de la limpiadora: " + mensaje);

                // Asingar una zona aleatoria
                String zona = ZONAS[random.nextInt(ZONAS.length)];
                int tiempoLimpieza = 3 + random.nextInt(8); // Tiempo entre 3 y 10 segundos

                // Enviar datos a la limpiadora
                salida.writeUTF(zona);
                salida.writeInt(tiempoLimpieza);
                salida.flush();

                System.out.println("Orden enviada: Limpiar " + zona + " en " + tiempoLimpieza + " segundos");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
