// Archivo en el proyecto del cliente
import java.io.*;
import java.net.*;
import java.util.Random;

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

            // Si no hay suficiente miel, esperar un tiempo antes de volver a intentar
            if (respuesta.contains("No hay suficiente miel")) {
                System.out.println("Zángano-" + id + ": Esperando a que haya más miel disponible...");
                Thread.sleep(5000); // Esperar 5 segundos
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