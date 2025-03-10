import java.io.*;
import java.net.*;
import java.util.Random;

public class Nodriza extends Thread {
    private static final int PUERTO = 5001;
    private static final Random random = new Random();
    private ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Nodriza: Esperando solicitudes de alimento...");
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String mensaje = in.readLine();
                    if ("SOLICITAR_ALIMENTO".equals(mensaje)) {
                        int tiempoAlimentacion = random.nextInt(3) + 3; // 3 a 5 segundos
                        System.out.println("Nodriza: Alimentando a un zángano durante " + tiempoAlimentacion + " segundos.");
                        Thread.sleep(tiempoAlimentacion * 1000);
                        out.println("Alimentación completada en " + tiempoAlimentacion + " segundos.");
                    }
                    System.out.println("Nodriza: Esperando solicitudes de alimento...");

                } catch (IOException | InterruptedException e) {
                    System.out.println("Nodriza: Error en la conexión con un zángano.");
                }
            }
        } catch (IOException e) {
            System.out.println("Nodriza: No se pudo iniciar el servidor.");
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

