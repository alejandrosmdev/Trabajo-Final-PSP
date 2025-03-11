import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Aplicación para inspeccionar el estado de la colmena.
 */
public class InspectorColmena {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Inspector de la Colmena");
        System.out.println("======================");

        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1. Ver informe de inventario de miel");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            if (opcion == 0) {
                break;
            }

            switch (opcion) {
                case 1:
                    verInformeInventarioMiel();
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Inspector de la Colmena finalizado.");
    }

    private static void verInformeInventarioMiel() {
        Socket socket = null;
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            socket = new Socket(HOST, PUERTO);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Identificarse con el servidor
            out.writeUTF("Inspector");

            // Recibir confirmación
            String respuesta = in.readUTF();
            System.out.println("Servidor: " + respuesta);

            // Solicitar informe de inventario
            out.writeUTF("SOLICITAR_INFORME_MIEL");

            // Recibir informe
            String informe = in.readUTF();
            System.out.println("\n" + informe);

            // Desconectar
            out.writeUTF("DESCONECTAR");
            respuesta = in.readUTF();
            System.out.println("Servidor: " + respuesta);

        } catch (IOException e) {
            System.out.println("Error al comunicarse con el servidor: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}