import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class Soldado extends Thread {
    private static final int puerto_soldado= 5002;
    private static final int PUERTO = 5000;
    private static final String host_soldado = "localhost";
    private ServerSocket serverSocket;
    private String password;
    private ReentrantLock lock = new ReentrantLock();
    private boolean ocupada = false;
    private boolean running = true;

    public Soldado () {}

    @Override
    public void run() {
        try {
            solicitarPassword();
            serverSocket = new ServerSocket(puerto_soldado);
            System.out.println("Soldado guardian de la colmena activo en " + puerto_soldado);
            while (running) {
                Socket socket = serverSocket.accept();
                new Thread(() -> protegerPuerta(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Soldado: Error en la puerta" + e.getMessage());
        }
    }

    private void protegerPuerta(Socket socket) {
        try {
            lock.lock();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("¿Cuál es la contraseña?");
            String respuesta_abeja = in.readUTF();

            if (respuesta_abeja.equals(password)) {
                out.writeUTF("Acceso concedido");
                System.out.println("Soldado: Adelante pasa");
            } else {
                out.writeUTF("Acceso denegado");
                System.out.println("Soldado: Fuera");
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("Soldado: Erro en la comunicacion " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private String solicitarPassword() {
        try {
            Socket socket = new Socket(host_soldado, PUERTO);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Soldado");
            String respuesta = in.readUTF();
            System.out.println("Soldado: " + respuesta);

            out.writeUTF("SOLICITAR_PASS");
            password = in.readUTF();
            System.out.println("La reina me ha dicho que la password es: " + password);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return password;
    }
}
