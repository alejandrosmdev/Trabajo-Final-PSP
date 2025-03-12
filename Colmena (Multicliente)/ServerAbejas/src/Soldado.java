import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Soldado extends Thread {
    private static final int puerto_soldado = 5002;
    private ServerSocket serverSocket;
    private String password;
    private Lock lock = new ReentrantLock();
    private boolean running = true;

    public Soldado(String password) {
        this.password = password;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto_soldado);
            System.out.println("[Soldado]: Puerta de la colmena activa en el puerto " + puerto_soldado);

            while (running) {
                Socket socket = serverSocket.accept();
                new Thread(() -> custiodarPuerta(socket)).start();
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("[Soldado]: Error en el servidor: " + e.getMessage());
            }
        }
    }

    private void custiodarPuerta(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            String tipoAbeja = in.readUTF();
            String accion = in.readUTF();

            if (accion.equals("SALIR")) {
                System.out.println("[Soldado]: " + tipoAbeja + " ha salido de la colmena");
                out.writeUTF("Puedes salir");
            } else if (accion.equals("ENTRAR")) {
                String clave = in.readUTF();
                lock.lock(); // Garantiza que solo una abeja sea revisada a la vez
                try {
                    if (clave.equals(password)) {
                        System.out.println("[Soldado]: " + tipoAbeja + " ha ingresado en la colmena correctamente");
                        out.writeUTF("Bienvenido de vuelta");
                    } else {
                        System.out.println("[Soldado]: " + tipoAbeja + " intentó entrar con una clave incorrecta");
                        out.writeUTF("Acceso denegado");
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            System.out.println("[Soldado]: Error al manejar abeja: " + e.getMessage());
        }
    }

    public void detener() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("[Soldado]: Error al cerrar el servidor: " + e.getMessage());
        }
    }
}
