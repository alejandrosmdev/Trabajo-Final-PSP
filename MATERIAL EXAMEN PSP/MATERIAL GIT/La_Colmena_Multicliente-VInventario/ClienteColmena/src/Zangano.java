import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Representa a un zángano que busca reinas y solicita alimento.
 */
public class Zangano extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;

    public Zangano(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                int tiempoBusqueda = random.nextInt(6) + 5; // 5 a 10 segundos
                System.out.println("Zángano-" + id + ": Buscando nueva reina durante " + tiempoBusqueda + " segundos.");
                try {
                    Thread.sleep(tiempoBusqueda * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (random.nextInt(10) == 0) { // 1 en 10 posibilidades
                    System.out.println("Zángano-" + id + ": ¡He encontrado una nueva reina! Me voy a fundar una nueva colmena.");
                    break;
                } else {
                    System.out.println("Zángano-" + id + ": No encontré una nueva reina. Volviendo a la colmena para alimentarme.");
                    alimentarse();
                }
            }
        } finally {
            desconectar();
        }
    }

    private void conectarAlServidor() {
        try {
            socket = new Socket(HOST, PUERTO);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Identificarse con el servidor
            out.writeUTF("Zangano-" + id);

            // Recibir confirmación
            String respuesta = in.readUTF();
            System.out.println("Zángano-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Zángano-" + id + ": Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void alimentarse() {
        try {
            out.writeUTF("SOLICITAR_ALIMENTO");
            String respuesta = in.readUTF();
            System.out.println("Zángano-" + id + ": " + respuesta);

            // Si hay un mensaje de espera, esperar antes de continuar
            if (respuesta.contains("Espera un momento") || respuesta.contains("No hay suficiente miel")) {
                int tiempoEspera = random.nextInt(3) + 3; // 3-5 segundos
                System.out.println("Zángano-" + id + ": Esperando " + tiempoEspera + " segundos antes de intentar de nuevo...");
                Thread.sleep(tiempoEspera * 1000);
                alimentarse(); // Intentar alimentarse de nuevo
            }
        } catch (IOException e) {
            System.out.println("Zángano-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            reconectar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            System.out.println("Zángano-" + id + ": Intentando reconectar en 5 segundos...");
            Thread.sleep(5000);
            conectarAlServidor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void desconectar() {
        try {
            if (out != null) {
                out.writeUTF("DESCONECTAR");
                String respuesta = in.readUTF();
                System.out.println("Zángano-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Zángano-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }
}