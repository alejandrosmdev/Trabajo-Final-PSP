import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Soldado extends Thread{
    private static final String HOST = "localhost";
    private static final int PUERTO1 = 5000;
    private static final int PUERTO2 = 5003;
    private static final Random random = new Random();

    private ServerSocket serverSocket;
    private int id;
    private boolean running = true;

    private String pass;

    public Soldado(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            Socket socket = null;
            try {
                socket = new Socket(HOST, PUERTO1);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println("Abeja soldado lista para pedir Contrasena a la Reina");
                out.println("SOLICITAR_PASS");
                String pass = in.readLine();

                if (pass != null) {
                    System.out.println("Contrasena recibida: "+ pass);
                    this.pass=pass;
                }

                in.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        // Ignorar
                    }
                }
                try {
                    Thread.sleep(5000); // Esperar antes de reintentar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                serverSocket = new ServerSocket(PUERTO2);
                System.out.println("Soldado: Esperando en la puerta de la colmena...");

                while (running && !Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();

                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        String mensaje = in.readLine();

                        if (mensaje != null && mensaje.startsWith("SOLICITAR_ENTRAR")&&mensaje.contains(pass)) {
                            Main.passwordQueue.put(this);
                            System.out.println("HAY ALGUIEN EN LA PUERTA DE LA COLMENA");
                            out.println("PERMISO_CONCEDIDO");
                        } else {
                            out.println("ERROR");
                        }

                        in.close();
                        out.close();
                        clientSocket.close();

                    } catch (IOException e) {
                        if (clientSocket != null && !clientSocket.isClosed()) {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // Ignorar
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                System.out.println("Soldado: No se pudo iniciar el servidor.");
            } finally {
                detener();
            }
        }
    }
    public void detener() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // Ignorar
            }
        }
    }
}
