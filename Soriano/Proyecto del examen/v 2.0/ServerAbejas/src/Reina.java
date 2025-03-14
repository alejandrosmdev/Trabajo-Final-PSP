import java.io.*;
import java.net.*;
import java.util.*;

public class Reina extends Thread {
    private static final int puerto = 5001;
    private ServerSocket serverSocket;
    private boolean running = true;
    private Random random = new Random();
    private List<Socket> limpiadoras = new ArrayList<>();
    private List<DataOutputStream> limpiadorasOut = new ArrayList<>();
    private String password = "Yipikaiyee...";

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("[Reina]: Esperando limpiadoras en el puerto " + puerto);

            Thread aceptadorConexiones = new Thread(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                        String mensaje = in.readUTF();
                        if (mensaje.startsWith("Limpiadora-")) {
                            System.out.println("[Reina]: " + mensaje + " se ha conectado");
                            synchronized (limpiadoras) {
                                limpiadoras.add(socket);
                                limpiadorasOut.add(out);
                            }

                            new Thread(() -> atenderLimpiadora(socket, in, out, mensaje)).start();
                        } else if (mensaje.startsWith("Soldado-")) {
                            System.out.println("[Reina]: se ha conectado");
                            new Thread(() -> atenderSoldado(socket, in, out)).start();
                        }
                    } catch (IOException e) {
                        if (running) {
                            System.out.println("[Reina]: Error al aceptar conexión: " + e.getMessage());
                        }
                    }
                }
            });
            aceptadorConexiones.start();

            while (running) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("[Reina]: Error al iniciar servidor: " + e.getMessage());
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
                    System.out.println("[Reina]: Asignada " + zona + " a " + idLimpiadora);
                } else if (solicitud.equals("DESCONECTAR")) {
                    out.writeUTF("Desconexión aceptada");
                    System.out.println("[Reina]: " + idLimpiadora + " se ha desconectado");
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
            System.out.println("[Reina]: Error al comunicarse con " + idLimpiadora + ": " + e.getMessage());
            try {
                socket.close();
            } catch (IOException ex) {
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

    private void atenderSoldado(Socket socket, DataInputStream in, DataOutputStream out) {
        try {
            while (running && !socket.isClosed()) {
                String solicitud = in.readUTF();
                if (solicitud.equals("SOLICITAR_PASS")) {
                    String pass = getPassword();
                    out.writeUTF(pass);
                    System.out.println("[Reina]: Contraseña concedida");
                } else if (solicitud.equals("DESCONECTAR")) {
                    out.writeUTF("Desconexión aceptada");
                    System.out.println("[Reina]: El soldado se ha desconectado");
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("[Reina]: Error al comunicarse con el soldado" + e.getMessage());
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    private void cerrarServidor() {
        running = false;
        try {
            synchronized (limpiadoras) {
                for (int i = 0; i < limpiadoras.size(); i++) {
                    try {
                        limpiadorasOut.get(i).writeUTF("TERMINAR");
                        limpiadoras.get(i).close();
                    } catch (IOException e) {
                    }
                }
                limpiadoras.clear();
                limpiadorasOut.clear();
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[Reina]: Servidor cerrado");
            }
        } catch (IOException e) {
            System.out.println("[Reina]: Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public String asignarZona() {
        return "Zona " + (random.nextInt(10) + 1);
    }

    public String getPassword() {
        return password;
    }

    public void detener() {
        running = false;
        interrupt();
    }
}

