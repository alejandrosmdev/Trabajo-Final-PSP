import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiClientServer {
    public static final int PORT = 5000;
    // Mapa de todos los clientes conectados
    public static Map<Integer, ClientHandler> clients = new HashMap<>();

    // Colas para solicitudes:
    // Para las limpiadoras que piden orden (funcionalidad de la Reina)
    public static BlockingQueue<ClientHandler> orderQueue = new LinkedBlockingQueue<>();
    // Para los zánganos que piden alimento (funcionalidad de la Nodriza)
    public static BlockingQueue<ClientHandler> foodQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<ClientHandler> passwordQueue = new LinkedBlockingQueue<>();

    private static int clientIdCounter = 1;

    public static synchronized int getNextClientId() {
        return clientIdCounter++;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado en el puerto " + PORT);

            // Iniciar tarea de la Reina (para atender solicitudes de limpiadoras)
            new Thread(new ReinaServidor()).start();
            // Iniciar tarea de la Nodriza (para atender solicitudes de alimento de zánganos)
            new Thread(new NodrizaServidor()).start();
            new Thread(new SoldadoServidor()).start();

            // Bucle para aceptar conexiones de clientes
            while (true) {
                Socket socket = serverSocket.accept();
                int clientId = getNextClientId();
                ClientHandler handler = new ClientHandler(socket, clientId);
                clients.put(clientId, handler);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
