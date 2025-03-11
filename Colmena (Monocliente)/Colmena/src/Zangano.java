import java.io.*;
import java.net.*;
import java.util.Random;

public class Zangano extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO_NODRIZA = 5001;
    private static final Random random = new Random();

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            int tiempoBusqueda = random.nextInt(6) + 5; // 5 a 10 segundos
            System.out.println("Zángano: Buscando nueva reina durante " + tiempoBusqueda + " segundos.");
            try {
                Thread.sleep(tiempoBusqueda * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (random.nextInt(10) == 0) { // 1 en 10 posibilidades
                System.out.println("Zángano: ¡He encontrado una nueva reina! Me voy a fundar una nueva colmena.");
                break;
            } else {
                System.out.println("Zángano: No encontré una nueva reina. Volviendo a la colmena para alimentarme.");
                alimentarse();
            }
        }
    }

    private void alimentarse() {
        try (Socket socket = new Socket(HOST, PUERTO_NODRIZA);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("SOLICITAR_ALIMENTO");
            String respuesta = in.readLine();
            System.out.println("Zángano: " + respuesta);
        } catch (IOException e) {
            System.out.println("Zángano: Error al comunicarse con la Nodriza.");
            try {
                Thread.sleep(5000); // Esperar 5 segundos antes de intentar de nuevo
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

