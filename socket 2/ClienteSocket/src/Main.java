import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int puerto = 3000;
        String host = "localhost";

        try {
            System.out.println("Cliente intentando conectar en la dirección " + host + ":" + puerto);
            Socket skCliente = new Socket(host, puerto);

            System.out.println("Cliente conectado en la dirección: " + host + ":" + puerto);
            OutputStream outputStream = skCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Enviar mensaje al servidor
            String mensaje = "Hola Mundo";
            System.out.println("Voy a pedir el siguiente mensaje: " + mensaje);
            dataOutputStream.writeUTF(mensaje);
            System.out.println("Mensaje enviado");

            // Recibir confirmación del servidor
            InputStream inputStream = skCliente.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            String respuesta = dataInputStream.readUTF();
            System.out.println("Respuesta del servidor: " + respuesta);

            dataOutputStream.close();
            dataInputStream.close();
            skCliente.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}