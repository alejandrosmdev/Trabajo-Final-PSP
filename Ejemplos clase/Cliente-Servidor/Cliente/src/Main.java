import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;

// Este código es de cliente
public class Main {
    public static void main(String[] args) {
        try {
            // El cliente crea un socket y se conecta a la dirección 192.168.104.57:5000
            System.out.println("Cliente intentando conectar");
            Socket skCliente = new Socket("localhost", 5000);

            // Se obtiene el flujo de salida del socket para enviar datos al servidor
            // Se envuelve el flujo de salida en un DataOutputStream para facilitar el envío de datos primitivos
            System.out.println("Cliente conectado !!!");
            OutputStream aux = skCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(aux);

            // Se escribe el número 7 en el flujo de salida y se manda al servidor
            System.out.println("Voy a pedir la operación 7");
            flujo_salida.writeInt(7);
            System.out.println("Operación 7 pedida !!!");

            // Cerrar socket y flujos para ahorrar recursos
            flujo_salida.close();
            skCliente.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}