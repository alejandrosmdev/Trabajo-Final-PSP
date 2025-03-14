import java.io.*;
import java.net.*;
import java.util.Random;

public class Recolectora extends Thread {
    private static final String host_recolectora = "localhost";
    private static final int puerto_recolectora = 5000;
    private static final int puerto_soldado = 5002;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;
    private String password;

    public Recolectora(int id, String password) {
        this.id = id;
        this.password = password;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted()) {
                producirMiel();
                entrarColmena();

                int tiempoDescanso = random.nextInt(4) + 2; // 2 a 5 segundos
                System.out.println("Recolectora-" + id + ": Descansando durante " + tiempoDescanso + " segundos.");
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
            socket = new Socket(host_recolectora, puerto_recolectora);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Recolectora-" + id);

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
            System.out.println("Recolectora-" + id + ": " + cantidadMiel + " unidades de miel producidas.");

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

    private void entrarColmena() {
        try {
            Socket socket = new Socket(host_recolectora, puerto_soldado);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Intentando entrar en la colmena...");
            String solicitud = in.readUTF();
            System.out.println("[Soldado]: " + solicitud);
            out.writeUTF(password);
            String respuesta = in.readUTF();
            System.out.println(respuesta);
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al comunicarse con el soldado: " + e.getMessage());
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

