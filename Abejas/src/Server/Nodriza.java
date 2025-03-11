package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Nodriza {
    public static void main(String[] args) {
        int puerto = 5001; // Puerto por el que escucha la nodriza

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Nodriza esperando zánganos en el puerto: " + puerto);

            while (true) {
                // Aceptar conexión de un zángano
                Socket socket = serverSocket.accept();
                System.out.println("Un zángano se ha conectado");

                // Manejamos el zángano en un nuevo hilo
                new Thread(new ManejadorZangano(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
