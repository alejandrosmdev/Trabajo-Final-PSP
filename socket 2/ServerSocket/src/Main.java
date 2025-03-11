import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int puerto = 3000;

        try {
            Socket skCliente;
            System.out.println("Creando el cliente");
            ServerSocket skServidor = new ServerSocket(puerto);
            System.out.println("Servidor escuchando en el puerto: " + puerto);

            System.out.println("Esperando al cliente");
            skCliente = skServidor.accept();

            System.out.println("Atendiendo al cliente");
            InputStream inputStream = skCliente.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            // Leer mensaje cliente
            String mensaje = dataInputStream.readUTF();
            System.out.println("El cliente ha enviado el mensaje: " + mensaje);

            // Devolver al remitente
            OutputStream outputStream = skCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("El servidor ha recibido: " + mensaje);
            System.out.println("Mensaje reenviado al cliente");

            dataInputStream.close();
            dataOutputStream.close();
            skCliente.close();
            skServidor.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}