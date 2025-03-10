import java.io.*;
import java.net.*;
import java.util.Random;

public class Limpiadora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (Socket socket = new Socket(HOST, PUERTO);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("SOLICITAR_ZONA");
                String zona = in.readLine();
                System.out.println("Limpiadora: Recibida orden de limpiar " + zona);

                int tiempoLimpieza = random.nextInt(8) + 3; // 3 a 10 segundos
                System.out.println("Limpiadora: Limpiando " + zona + " durante " + tiempoLimpieza + " segundos.");
                Thread.sleep(tiempoLimpieza * 1000);
                System.out.println("Limpiadora: " + zona + " limpia.");
            } catch (IOException | InterruptedException e) {
                System.out.println("Limpiadora: Error al comunicarse con la Reina.");
                try {
                    Thread.sleep(5000); // Esperar 5 segundos antes de intentar de nuevo
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

