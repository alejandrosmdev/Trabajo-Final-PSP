import java.io.*;
import java.net.*;
import java.util.Random;

public class Nodriza extends Thread {
    private static final int PUERTO = 5001;
    private static final Random random = new Random();
    private ServerSocket serverSocket;
    private boolean running = true;
    private int id;


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Nodriza: Esperando solicitudes de alimento...");

            while (running && !Thread.currentThread().isInterrupted()) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String mensaje = in.readLine();

                    if (mensaje != null && mensaje.startsWith("SOLICITAR_ALIMENTO")) {
                        int tiempoAlimentacion = random.nextInt(3) + 3; // 3 a 5 segundos
                        System.out.println("Nodriza: Alimentando a un zángano durante " + tiempoAlimentacion + " segundos.");
                        Thread.sleep(tiempoAlimentacion * 1000);
                        out.println("Alimentación completada en " + tiempoAlimentacion + " segundos.");
                    } else {
                        out.println("ERROR");
                    }

                    in.close();
                    out.close();
                    clientSocket.close();

                } catch (IOException e) {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        try {
                            clientSocket.close();
                        } catch (IOException ex) {
                            // Ignorar
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            System.out.println("Nodriza: No se pudo iniciar el servidor.");
        } finally {
            detener();
        }
    }

    public void detener() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // Ignorar
            }
        }
    }
}