import java.io.*;
import java.net.*;
import java.util.Random;

public class Limpiadora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;

    public Limpiadora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                solicitarZona();

                // Esperar un tiempo antes de solicitar otra zona
                try {
                    Thread.sleep(random.nextInt(5000) + 2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
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
            out.writeUTF("Limpiadora-" + id);

            // Recibir confirmación
            String respuesta = in.readUTF();
            System.out.println("Limpiadora-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Limpiadora-" + id + ": Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void solicitarZona() {
        try {
            out.writeUTF("SOLICITAR_ZONA");
            String zona = in.readUTF();
            System.out.println("Limpiadora-" + id + ": Recibida orden de limpiar " + zona);

            int tiempoLimpieza = random.nextInt(8) + 3; // 3 a 10 segundos
            System.out.println("Limpiadora-" + id + ": Limpiando " + zona + " durante " + tiempoLimpieza + " segundos.");
            Thread.sleep(tiempoLimpieza * 1000);
            System.out.println("Limpiadora-" + id + ": " + zona + " limpia.");
        } catch (IOException e) {
            System.out.println("Limpiadora-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            reconectar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            System.out.println("Limpiadora-" + id + ": Intentando reconectar en 5 segundos...");
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
                System.out.println("Limpiadora-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Limpiadora-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }
}

