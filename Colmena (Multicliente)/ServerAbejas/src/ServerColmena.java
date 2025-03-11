import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerColmena {
    static final int puerto_colmena = 5000;
    private ServerSocket serverSocket;

    private static ServerColmena instancia;

    public static synchronized ServerColmena getInstancia() {
        if (instancia == null) {
            instancia = new ServerColmena();
        }
        return instancia;
    }

    private ServerColmena() {
        try {
            serverSocket = new ServerSocket(puerto_colmena);
            System.out.println("Servidor Colmena iniciado en el puerto_colmena: " + puerto_colmena);
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
            System.out.println("Error al aceptar conexión: " + e.getMessage());
            return null;
        }
    }

    public void cerrar() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor Colmena cerrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }
}

