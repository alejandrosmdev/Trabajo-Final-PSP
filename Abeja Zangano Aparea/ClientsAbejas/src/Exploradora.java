import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Exploradora extends Thread {
    private static final String host_exploradora = "localhost";
    private static final int puerto_exploradora = 5000; // Usamos el mismo puerto que las otras abejas
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;

    public Exploradora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                explorar();

                int tiempoDescanso = random.nextInt(4) + 2; // 2 a 5 segundos
                System.out.println("Exploradora-" + id + ": Descansando durante " + tiempoDescanso + " segundos.");
                try {
                    Thread.sleep(tiempoDescanso * 1000);
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
            socket = new Socket(host_exploradora, puerto_exploradora);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Enviamos identificación al servidor
            out.writeUTF("Exploradora-" + id);
            String respuesta = in.readUTF();
            System.out.println("Exploradora-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Exploradora-" + id + ": Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void explorar() {
        try {
            // Simulamos que la exploradora envía una solicitud de exploración
            System.out.println("Exploradora-" + id + ": Iniciando exploración.");
            out.writeUTF("EXPLORAR");

            // Se espera una respuesta del servidor (si se implementa la funcionalidad en el servidor)
            String respuesta = in.readUTF();
            System.out.println("Exploradora-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Exploradora-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            reconectar();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            System.out.println("Exploradora-" + id + ": Intentando reconectar en 5 segundos...");
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
                System.out.println("Exploradora-" + id + ": " + respuesta);
            }
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Exploradora-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }
}
