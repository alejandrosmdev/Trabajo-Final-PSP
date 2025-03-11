package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ManejadorZangano implements Runnable {
    private Socket socket;

    public ManejadorZangano(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            Random random = new Random();

            while (true) {
                // Recibir solicitud del zángano
                String mensaje;
                try {
                    mensaje = entrada.readUTF();
                } catch (IOException e) {
                    System.out.println("Se perdió la conexión con un zángano.");
                    break;
                }

                if (mensaje == null || mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Un zángano se ha desconectado.");
                    break;
                }

                System.out.println("Mensaje recibido del zángano: " + mensaje);

                // Generar tiempo de alimentación aleatorio (entre 3 y 5 segundos)
                int tiempoAlimentacion = 3 + random.nextInt(3);
                salida.writeInt(tiempoAlimentacion);
                salida.flush();

                System.out.println("Alimentando zángano durante " + tiempoAlimentacion + " segundos...");

                // Esperar el tiempo de alimentación antes de cerrar la conexión
                try {
                    Thread.sleep(tiempoAlimentacion * 1000);
                } catch (InterruptedException e) {
                    System.out.println("⚠️ Interrupción inesperada mientras se alimentaba un zángano.");
                }

                // Esperar confirmación zangano
                String confirmacion = entrada.readUTF();
                if (confirmacion.equalsIgnoreCase("Comida recibida")) {
                    System.out.println("Zángano confirmó recepción de alimento.");
                } else {
                    System.out.println("No se recibió confirmación del zángano.");
                }
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
