import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Recolectora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;

    public Recolectora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                producirMiel();

                int tiempoDescanso = random.nextInt(4) + 2;
                System.out.println("Recolectora-" + id + ": Descansando durante " + tiempoDescanso + " segnudos");
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
            socket = new Socket(HOST, PUERTO);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Identificarse con el sevidor
            out.writeUTF("Recolectora-" + id);

            // Recibir confirmación
            String respuesta = in.readUTF();
            System.out.println("Recolectora-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Recolectora-" + id + ": Error al conectar con el servidor: " + e.getMessage());
        }
    }

    private void producirMiel() {
        try {
            int tiempoProduccion = random.nextInt(5) + 4;
            System.out.println("Recolectora-" + id + ": Produciendo miel durante " + tiempoProduccion + " segundos.");
            Thread.sleep(tiempoProduccion * 1000);

            int cantidadMiel = random.nextInt(10) + 1;
            System.out.println("Recolectora-" + id + ": " + cantidadMiel + " unidades de miel producidas");

            // Reportar al servidor
            out.writeUTF("REPORTAR_MIEL");
            out.writeInt(cantidadMiel);
            String respuesta = in.readUTF();
            System.out.println("Recolectora-" + id + ": " + respuesta);
        } catch (IOException e) {
            System.out.println("Recolectora-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            reconectar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            System.out.println("Recolectora-" + id + ": Intentando reconectar en 5 segundos...");
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
                System.out.println("Recolectora-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Recolectora-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }
}
