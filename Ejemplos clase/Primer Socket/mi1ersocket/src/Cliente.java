import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Cliente {
    static final String HOST = "localhost"; // Dirección del servidor
    static final int PUERTO = 5000; // Puerto del servidor

    public Cliente() {
        try {
            // Crear un socket para conectarse al servidor en localhost:5000
            Socket skCliente = new Socket(HOST, PUERTO);

            // Flujo de entrada para recibir datos del servidor
            InputStream aux = skCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(aux);

            // Leer y mostrar el mensaje enviado por el servidor.
            System.out.println(flujo_entrada.readUTF());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
