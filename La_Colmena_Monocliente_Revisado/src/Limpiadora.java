import java.io.*;
import java.net.*;
import java.util.Random;

public class Limpiadora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private int id;
    private boolean running = true;

    public Limpiadora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            Socket socket = null;
            try {
                socket = new Socket(HOST, PUERTO);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("SOLICITAR_ZONA");
                String zona = in.readLine();

                if (zona != null && !zona.equals("ERROR")) {
                    System.out.println("Limpiadora: Recibida orden de limpiar " + zona);

                    int tiempoLimpieza = random.nextInt(8) + 3; // 3 a 10 segundos
                    System.out.println("Limpiadora: Limpiando " + zona + " durante " + tiempoLimpieza + " segundos.");
                    Thread.sleep(tiempoLimpieza * 1000);
                    System.out.println("Limpiadora: " + zona + " limpia.");
                }

                in.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        // Ignorar
                    }
                }
                try {
                    Thread.sleep(5000); // Esperar antes de reintentar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void detener() {
        running = false;
    }
}