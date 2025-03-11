package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Reina {
    public static void main(String[] args) {
        int puerto = 5000; // Puerto por el que escucha la reina

        try (ServerSocket serverSocket = new ServerSocket(puerto))  {
            System.out.println("Reina esperando limpiadoras en el puerto " + puerto);

            while (true) {
                // Aceptar conexiones de una limpiadora
                Socket socket = serverSocket.accept();
                System.out.println("Una limpiadora se ha conectado");

                // Manejamos la limpiadora en un nuevo hilo
                new Thread(new ManejadorLimpiadora(socket)).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

