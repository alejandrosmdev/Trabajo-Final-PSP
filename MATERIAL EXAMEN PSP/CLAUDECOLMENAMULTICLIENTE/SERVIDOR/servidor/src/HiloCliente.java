import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class HiloCliente implements Runnable {
    private Socket socket;
    private ServidorColmena servidor;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean running = true;
    private int clienteId;
    private static int contadorClientes = 0;

    public HiloCliente(Socket socket, ServidorColmena servidor) {
        this.socket = socket;
        this.servidor = servidor;
        this.clienteId = ++contadorClientes;

        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            // Enviar mensaje de bienvenida al cliente
            out.writeUTF("Bienvenido a la Colmena! Tu ID es: " + clienteId);
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] Error al inicializar el hilo del cliente: " +
                    e.getMessage() + "\u001B[0m");
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                // Leer el tipo de solicitud del cliente
                int tipoSolicitud = in.readInt();

                switch (tipoSolicitud) {
                    case 1: // Solicitud de zona a limpiar (Limpiadora)
                        procesarSolicitudLimpiadora();
                        break;
                    case 2: // Solicitud de alimento (Zángano)
                        procesarSolicitudZangano();
                        break;
                    case 3: // Notificación de recolección (Recolectora)
                        procesarNotificacionRecolectora();
                        break;
                    case 0: // Desconexión
                        running = false;
                        break;
                    default:
                        out.writeUTF("Tipo de solicitud no reconocido");
                        break;
                }
            }
        } catch (SocketException se) {
            System.out.println("\u001B[35m[SERVIDOR] Cliente " + clienteId +
                    " desconectado abruptamente\u001B[0m");
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] Error en el hilo del cliente " +
                    clienteId + ": " + e.getMessage() + "\u001B[0m");
        } finally {
            cerrarConexion();
        }
    }

    private void procesarSolicitudLimpiadora() throws IOException {
        // Obtener una zona para limpiar de la reina
        String zona = servidor.getReina().asignarZonaLimpieza();
        out.writeUTF(zona);
    }

    private void procesarSolicitudZangano() throws IOException {
        // Buscar una nodriza disponible
        AbejaNodrizaServidor nodriza = servidor.getNodrizaDisponible();

        if (nodriza != null) {
            // La nodriza está disponible
            out.writeBoolean(true);
            out.writeInt((int) nodriza.getId());

            // Marcar a la nodriza como ocupada y obtener el tiempo de alimentación
            int tiempoAlimentacion = nodriza.alimentarZangano();
            out.writeInt(tiempoAlimentacion);
        } else {
            // No hay nodrizas disponibles
            out.writeBoolean(false);
        }
    }

    private void procesarNotificacionRecolectora() throws IOException {
        // Recibir información de la recolectora
        int tiempoRecoleccion = in.readInt();
        int tiempoDescanso = in.readInt();

        // Simplemente registrar la actividad
        System.out.println("\u001B[33m[RECOLECTORA] Cliente " + clienteId +
                " ha hecho miel en " + tiempoRecoleccion +
                " segundos y descansará " + tiempoDescanso + " segundos\u001B[0m");

        // Confirmar recepción
        out.writeBoolean(true);
    }

    public void detener() {
        running = false;
        cerrarConexion();
    }

    private void cerrarConexion() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("\u001B[35m[SERVIDOR] Conexión con cliente " +
                    clienteId + " cerrada\u001B[0m");
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] Error al cerrar la conexión con cliente " +
                    clienteId + ": " + e.getMessage() + "\u001B[0m");
        }
    }
}

