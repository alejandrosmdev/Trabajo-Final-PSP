import java.io.*;
import java.net.*;
import java.util.*;

public class Reina extends Thread {
    private static final int PUERTO_REINA = 5001;
    private ServerSocket serverSocket;
    private boolean running = true;
    private Random random = new Random();
    private List<Socket> limpiadoras = new ArrayList<>();
    private List<DataOutputStream> limpiadorasOut = new ArrayList<>();

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO_REINA);
            System.out.println("Reina: Esperando limpiadoras en el puerto " + PUERTO_REINA);

            // Hilo para aceptar conexiones de limpiadoras
            Thread aceptadorConexiones = new Thread(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        String mensaje = in.readUTF();
                        if (mensaje.startsWith("Limpiadora-")) {
                            System.out.println("Reina: " + mensaje + " se ha conectado");
                            synchronized (limpiadoras) {
                                limpiadoras.add(socket);
                                limpiadorasOut.add(out);
                            }

                            // Crear un hilo para atender a esta limpiadora
                            new Thread(() -> atenderLimpiadora(socket, in, out, mensaje)).start();
                        }
                    } catch (IOException e) {
                        if (running) {
                            System.out.println("Reina: Error al aceptar conexión: " + e.getMessage());
                        }
                    }
                }
            });
            aceptadorConexiones.start();

            // La reina sigue haciendo sus tareas
            while (running) {
                try {
                    Thread.sleep(5000); // La reina descansa entre tareas
                } catch (InterruptedException e) {
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("Reina: Error al iniciar servidor: " + e.getMessage());
        } finally {
            cerrarServidor();
        }
    }

    private void atenderLimpiadora(Socket socket, DataInputStream in, DataOutputStream out, String idLimpiadora) {
        try {
            while (running && !socket.isClosed()) {
                String solicitud = in.readUTF();
                if (solicitud.equals("SOLICITAR_ZONA")) {
                    String zona = asignarZona();
                    out.writeUTF(zona);
                    System.out.println("Reina: Asignada " + zona + " a " + idLimpiadora);
                } else if (solicitud.equals("DESCONECTAR")) {
                    out.writeUTF("Desconexión aceptada");
                    System.out.println("Reina: " + idLimpiadora + " se ha desconectado");
                    synchronized (limpiadoras) {
                        int index = limpiadoras.indexOf(socket);
                        if (index != -1) {
                            limpiadoras.remove(index);
                            limpiadorasOut.remove(index);
                        }
                    }
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Reina: Error al comunicarse con " + idLimpiadora + ": " + e.getMessage());
            try {
                socket.close();
            } catch (IOException ex) {
                // Ignorar
            }
            synchronized (limpiadoras) {
                int index = limpiadoras.indexOf(socket);
                if (index != -1) {
                    limpiadoras.remove(index);
                    limpiadorasOut.remove(index);
                }
            }
        }
    }

    private void cerrarServidor() {
        running = false;
        try {
            // Cerrar todas las conexiones con limpiadoras
            synchronized (limpiadoras) {
                for (int i = 0; i < limpiadoras.size(); i++) {
                    try {
                        limpiadorasOut.get(i).writeUTF("TERMINAR");
                        limpiadoras.get(i).close();
                    } catch (IOException e) {
                        // Ignorar
                    }
                }
                limpiadoras.clear();
                limpiadorasOut.clear();
            }

            // Cerrar el servidor
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Reina: Servidor cerrado");
            }
        } catch (IOException e) {
            System.out.println("Reina: Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public String asignarZona() {
        return "Zona " + (random.nextInt(10) + 1);
    }

    public void detener() {
        running = false;
        interrupt();
    }
}

