import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Zangano extends Thread{
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;

    public Zangano (int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                int tiempoBusqueda = random.nextInt(6) + 5;
                System.out.println("Zangano-" + id + ": Buscando nueva reina durante " + tiempoBusqueda + " segundos");
                try {
                    Thread.sleep(tiempoBusqueda * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (random.nextInt(10) == 0) {
                    System.out.println("Zangano-" + id + ": ¡He encontrado una nueva reina! Me voy");
                    break;
                } else {
                    System.out.println("Zangano-" + id + ": No encontre una nueva reina. Volviendo a la colmena");
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
            System.out.println("Zangano-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Zangano-" + id + ": Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void alimentarse() {
        try {
            out.writeUTF("SOLICITAR_ALIMENTO");
            String respuesta = in.readUTF();
            System.out.println("Zangano-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Zangano-" + id + ": Error al comunicarse con el sevidor: " + e.getMessage());
            reconectar();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            System.out.println("Zangano-" + id + ": Intentando reconectar en 5 segundos...");
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
                System.out.println("Zangano-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Zangano-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }
}
