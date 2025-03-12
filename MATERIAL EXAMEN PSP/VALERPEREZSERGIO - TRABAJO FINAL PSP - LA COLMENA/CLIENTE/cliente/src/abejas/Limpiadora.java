package abejas;

import util.ColorConsole;

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
    private boolean conectado = false;

    public Limpiadora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted() && conectado) {
                solicitarZona();

                // Esperar un tiempo antes de solicitar otra zona
                try {
                    Thread.sleep(random.nextInt(3000) + 1000);
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
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": " + respuesta);
            conectado = true;
        } catch (IOException e) {
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Error al conectar con el servidor: " + e.getMessage());
            conectado = false;
        }
    }

    private void solicitarZona() {
        try {
            if (!conectado) {
                reconectar();
                if (!conectado) return;
            }

            out.writeUTF("SOLICITAR_ZONA");
            String zona = in.readUTF();
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Recibida orden de limpiar " + zona);

            int tiempoLimpieza = random.nextInt(8) + 3; // 3 a 10 segundos
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Limpiando " + zona + " durante " + tiempoLimpieza + " segundos.");
            Thread.sleep(tiempoLimpieza * 1000);
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": " + zona + " limpia.");
        } catch (IOException e) {
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            conectado = false;
            reconectar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Intentando reconectar en 5 segundos...");
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
                ColorConsole.printLimpiadora("Limpiadora-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            conectado = false;
        } catch (IOException e) {
            ColorConsole.printLimpiadora("Limpiadora-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        return conectado;
    }
}