import java.io.*;
import java.net.*;
import java.util.Random;

public class Zangano extends Thread {
    private static final String host_zangano = "localhost";
    private static final int puerto_zangano = 5000;
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
                int tiempoBusqueda = random.nextInt(6) + 5;
                System.out.println("Zángano-" + id + ": Buscando nueva reina durante " + tiempoBusqueda + " segundos.");
                try {
                    Thread.sleep(tiempoBusqueda * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (random.nextInt(10) == 0) {
                    System.out.println("Zángano-" + id + ": ¡He encontrado una nueva reina! Solicito la creación de una nueva abeja.");
                    crearAbeja();
                    // Finaliza su ciclo tras solicitar la creación
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
            socket = new Socket(host_zangano, puerto_zangano);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Zangano-" + id);

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
        } catch (IOException e) {
            System.out.println("Zángano-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            reconectar();
        }
    }

    private void crearAbeja() {
        try {
            out.writeUTF("CREAR_BEE");
            String respuesta = in.readUTF();
            System.out.println("Zángano-" + id + ": " + respuesta);

            // Se crea una nueva abeja según la respuesta del servidor
            if (respuesta.contains("Recolectora")) {
                Recolectora nueva = new Recolectora(random.nextInt(100) + 100);
                nueva.start();
                System.out.println("Zángano-" + id + ": Nueva Recolectora creada.");
            } else if (respuesta.contains("Limpiadora")) {
                Limpiadora nueva = new Limpiadora(random.nextInt(100) + 100);
                nueva.start();
                System.out.println("Zángano-" + id + ": Nueva Limpiadora creada.");
            } else if (respuesta.contains("Exploradora")) {
                Exploradora nueva = new Exploradora(random.nextInt(100) + 100);
                nueva.start();
                System.out.println("Zángano-" + id + ": Nueva Exploradora creada.");
            } else if (respuesta.contains("Zángano")) {
                Zangano nueva = new Zangano(random.nextInt(100) + 100);
                nueva.start();
                System.out.println("Zángano-" + id + ": Nuevo Zángano creado.");
            }
        } catch (IOException e) {
            System.out.println("Zángano-" + id + ": Error al solicitar creación de nueva abeja: " + e.getMessage());
            reconectar();
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

