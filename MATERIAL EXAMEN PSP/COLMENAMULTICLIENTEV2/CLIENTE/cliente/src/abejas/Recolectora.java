package abejas;

import util.ColorConsole;

import java.io.*;
import java.net.*;
import java.util.Random;


public class Recolectora extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;
    private static final Random random = new Random();
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private int id;
    private boolean conectado = false;

    public Recolectora(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        try {
            conectarAlServidor();

            while (!Thread.currentThread().isInterrupted() && conectado) {
                producirMiel();

                int tiempoDescanso = random.nextInt(4) + 2; // 2 a 5 segundos
                ColorConsole.printRecolectora("Recolectora-" + id + ": Descansando durante " + tiempoDescanso + " segundos.");
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

            // Identificarse con el servidor
            out.writeUTF("Recolectora-" + id);

            // Recibir confirmación
            String respuesta = in.readUTF();
            ColorConsole.printRecolectora("Recolectora-" + id + ": " + respuesta);
            conectado = true;
        } catch (IOException e) {
            ColorConsole.printRecolectora("Recolectora-" + id + ": Error al conectar con el servidor: " + e.getMessage());
            conectado = false;
        }
    }

    private void producirMiel() {
        try {
            int tiempoProduccion = random.nextInt(5) + 4; // 4 a 8 segundos
            ColorConsole.printRecolectora("Recolectora-" + id + ": Produciendo miel durante " + tiempoProduccion + " segundos.");
            Thread.sleep(tiempoProduccion * 1000);

            int cantidadMiel = random.nextInt(10) + 1;
            ColorConsole.printRecolectora("Recolectora-" + id + ": " + cantidadMiel + " unidades de miel producidas.");

            if (!conectado) {
                reconectar();
                if (!conectado) return;
            }

            // Reportar al servidor
            out.writeUTF("REPORTAR_MIEL");
            out.writeInt(cantidadMiel);
            String respuesta = in.readUTF();
            ColorConsole.printRecolectora("Recolectora-" + id + ": " + respuesta);
        } catch (IOException e) {
            ColorConsole.printRecolectora("Recolectora-" + id + ": Error al comunicarse con el servidor: " + e.getMessage());
            conectado = false;
            reconectar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void reconectar() {
        desconectar();
        try {
            ColorConsole.printRecolectora("Recolectora-" + id + ": Intentando reconectar en 5 segundos...");
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
                ColorConsole.printRecolectora("Recolectora-" + id + ": " + respuesta);
            }

            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            conectado = false;
        } catch (IOException e) {
            ColorConsole.printRecolectora("Recolectora-" + id + ": Error al desconectar: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        return conectado;
    }
}