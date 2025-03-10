import java.io.*;
import java.net.*;
import java.util.Random;

public class AbejaReina extends Thread {
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Abeja Reina: Esperando órdenes de limpieza...");
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String mensaje = in.readLine();
                    if ("SOLICITAR_ZONA".equals(mensaje)) {
                        String zona = "Zona " + (random.nextInt(10) + 1);
                        out.println(zona);
                        System.out.println("Abeja Reina: Asigna " + zona + " para limpiar.");
                    }
                    System.out.println("Abeja Reina: Esperando órdenes de limpieza...");
                } catch (IOException e) {
                    System.out.println("Abeja Reina: Error en la conexión con una limpiadora.");
                }
            }
        } catch (IOException e) {
            System.out.println("Abeja Reina: No se pudo iniciar el servidor.");
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

