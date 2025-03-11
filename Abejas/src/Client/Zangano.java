package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Zangano {
    public static void main(String[] args) {
        String servidorNodriza = "localhost"; // Dirección del servidor en Nodrizas
        int puertoNodriza = 5001; // Puerto por el que escucha la nodriza

        Random random = new Random();

        while (true) {
            // Simular búsqueda de una nueva Reina (5 a 10 segundos)
            int tiempoBusqueda = 5 + random.nextInt(6);
            System.out.println("Zangano buscando una nueva reina durante " + tiempoBusqueda + " segundos");

            try {
                Thread.sleep(tiempoBusqueda * 1000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            // 1 de cada 10 intentos, el zángano encuentra una Reina y se va
            if (random.nextInt(10) == 0) {
                System.out.println("Zangano encontró una nueva Reina. Abandonando la colmena");
                break;
            }

            // Si no encuentra Reina, solicita comida a una nódriza
            try (Socket socket = new Socket(servidorNodriza, puertoNodriza);
                 DataInputStream entrada = new DataInputStream(socket.getInputStream());
                 DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

                System.out.println("Zangano pidiendo alimento a una Nodriza...");

                // Enviar solicitud
                salida.writeUTF("Necesito alimento");
                salida.flush();

                // Recibir tiempo de alimentación
                int tiempoAlimentacion = entrada.readInt();
                System.out.println("Nodriza alimentando al zangano durante " + tiempoAlimentacion);

                // Simular tiempo de alimentacion
                Thread.sleep(tiempoAlimentacion * 1000);

                // Confirmar recepcion de alimento antes de cerrar
                salida.writeUTF("Comida recibida");
                salida.flush();

                System.out.println("Zangano alimentado, volviendo a buscar reina");
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
