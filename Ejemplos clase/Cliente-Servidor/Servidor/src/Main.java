import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        try {
            // Se crea un socket que escuchará en el puerto 5000
            Socket skCliente;
            System.out.println("Creando el server socket");
            ServerSocket skServidor = new ServerSocket(5000);

            // El servidor espera hasta que un  cliente se conecte
            System.out.println("Esperando al cliente");
            skCliente = skServidor.accept();

            // El servidor obtiene el flujo de entrada del socket para recibir datos del cliente
            // Se envuelve el flujo de entrada en DataInputStream para leer datos primitivos
            System.out.println("Atendiendo al cliente");
            InputStream aux = skCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(aux);

            // Lee un numero entero enviado por el cliente y lo muestra
            int lectura = flujo_entrada.readInt();
            System.out.println("El cliente ha pedido la operación "+lectura);

            // Cerrar socket para liberar recursos
            flujo_entrada.close();
            skServidor.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}