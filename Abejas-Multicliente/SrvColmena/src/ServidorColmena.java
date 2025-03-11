import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorColmena {
    static final int PUERTO = 5000;
    private ServerSocket serverSocket;

    // Singleton para asegurar que solo hay una instancia del servidor
    private static ServidorColmena instancia;

    public static synchronized ServidorColmena getInstancia() {
        if (instancia == null) {
            instancia = new ServidorColmena();
        }
        return instancia;
    }

    private ServidorColmena() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor colmena iniciado en el puerto: " + PUERTO);
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Socket aceptarConexion() {
        try {
            Socket clienteSocket = serverSocket.accept();
            System.out.println("Nueva conexión aceptada: " + clienteSocket.getInetAddress().getHostAddress());
            return clienteSocket;
        } catch (IOException e) {
            System.out.println("Error al aceptar conexion: " + e.getMessage());
            return null;
        }
    }

    public void cerrar() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor colmena cerrado");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }
}
