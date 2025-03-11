import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    static final int PUERTO = 5000; // Puerto donde escucha el servidor
    public Servidor() {
        try {
            // Crear un socket de servidor que escucha en el puerto 5000
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println("Escucho en el puerto: "+PUERTO);

            // Espera y acepta la conexión con el cliente
            Socket mi_cliente = skServidor.accept();
            System.out.println("Cliente atendido !!!");

            // Flujo de salida para enviar datos al cliente
            OutputStream aux = mi_cliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(aux);

            // Envía un mensaje al cliente
            flujo_salida.writeUTF("Buenos días Cliente !!!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
