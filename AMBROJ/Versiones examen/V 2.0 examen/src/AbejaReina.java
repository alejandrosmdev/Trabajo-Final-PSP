import java.io.*;
import java.net.*;
import java.util.Random;

public class AbejaReina extends Thread {
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private ServerSocket serverSocket;
    private boolean running = true;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Abeja Reina: Esperando órdenes de limpieza...");

            while (running && !Thread.currentThread().isInterrupted()) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String mensaje = in.readLine();

                    if (mensaje != null && mensaje.startsWith("SOLICITAR_ZONA")) {
                        String zona = "Zona " + (random.nextInt(10) + 1);
                        out.println(zona);
                        System.out.println("Abeja Reina: Asigna " + zona + " para limpiar.");
                    } else if (mensaje != null && mensaje.startsWith("SOLICITAR_PASS")) {
                        String pass= "9856";
                        out.println(pass);
                    }else {
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
                }
            }
        } catch (IOException e) {
            System.out.println("Abeja Reina: No se pudo iniciar el servidor.");
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