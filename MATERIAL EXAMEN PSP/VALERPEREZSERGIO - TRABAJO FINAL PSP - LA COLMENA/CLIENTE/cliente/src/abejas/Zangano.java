package abejas;

import util.ColorConsole;

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
    private boolean conectado = false;

    public Zangano(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted() && conectado) {
                int tiempoBusqueda = random.nextInt(6) + 5; // 5 a 10 segundos
                ColorConsole.printZangano("Zángano-" + id + ": Buscando nueva reina durante " + tiempoBusqueda + " segundos.");
                try {
                    Thread.sleep(tiempoBusqueda * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (random.nextInt(10) == 0) { // 1 en 10 posibilidades
                    ColorConsole.printZangano("Zángano-" + id + ": ¡He encontrado una nueva reina! Me voy a fundar una nueva colmena.");
                    break;
                } else {
                    ColorConsole.printZangano("Zángano-" + id + ": No encontré una nueva reina. Volviendo a la colmena para alimentarme.");
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
            ColorConsole.printZangano("Zángano-" + id + ": " + respuesta);
            conectado = true;
        } catch (IOException e) {
            ColorConsole.printZangano("Zángano-" + id + ": Error al conectar con el servidor: " + e.getMessage());
            conectado = false;
        }
    }

    private void alimentarse() {
        try {
            if (!conectado) {
                reconectar();
                if (!conectado) return;
            }

            out.writeUTF("SOLICITAR_ALIMENTO");
            String respuesta = in.readUTF();
            ColorConsole.printZangano("Zángano-" + id + ": " + respuesta);
        } catch (IOException e) {
            ColorConsole.printZangano("Zángano-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            conectado = false;
            reconectar();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            ColorConsole.printZangano("Zángano-" + id + ": Intentando reconectar en 5 segundos...");
            Thread.sleep(5000);
            conectarAlServidor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void desconectar() {
        try {
            if (conectado && out != null) {
                out.writeUTF("DESCONECTAR");
                String respuesta = in.readUTF();
                ColorConsole.printZangano("Zángano-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            conectado = false;
        } catch (IOException e) {
            ColorConsole.printZangano("Zángano-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        return conectado;
    }
}