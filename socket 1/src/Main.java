import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // Codigo de servidor
        int puerto = 12345;
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor esperando conexiones en el puerto " + puerto);

            // Espera conexión del cliente
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress());

            // Flujo de entrada y salida
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // Leer mensaje del cliente
            String mensaje = entrada.readLine();
            salida.println("Mensaje recibido: " + mensaje);

            // Responde al cliente
            salida.println("Servidor recibido: " + mensaje);

            // Cerrar conexiones
            entrada.close();
            salida.close();
            socket.close();
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Diferencias entre este ejemplo y el de Diego.
/*
Flujo de datos: Diego usa DataOutputStream y DataInputStream    |   Este usa PrintWriter y BufferedReader
DataInputStream y DataOutputStream es más adecuado para manejar datos primitivos y binarios.

Manejo de errores: Diego lanza RuntimeException en catch    | Este muestra e.printStackTrace()
 */