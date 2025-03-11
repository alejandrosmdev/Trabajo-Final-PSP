import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        String servidor = "localhost"; // Dirección del servidor
        int puerto = 12345; // Puerto del servidor

        try (Socket socket = new Socket(servidor, puerto)) {
            System.out.println("Conectado al servidor en " + servidor + " : " + puerto);

            // Flujo de entrada y salida
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // Enviar mensaje al servidor
            String message = "¡Hola, servidor!";
            salida.println(message);
            System.out.println("Mensaje recibido: " + message);

            // Recibir respuesta del servidor
            String respuesta = entrada.readLine();
            System.out.println("Respuesta del servidro: " + respuesta);

            // Cerrar conexiones
            entrada.close();
            salida.close();
            System.out.println("Conexión cerrada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
