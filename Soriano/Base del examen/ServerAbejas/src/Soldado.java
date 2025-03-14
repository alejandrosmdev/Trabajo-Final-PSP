import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Soldado {
    private static Soldado instancia = null;
    private String password;
    private Lock lock = new ReentrantLock();

    private Soldado() {}

    public static Soldado getInstancia() {
        if (instancia == null) {
            instancia = new Soldado();
        }
        return instancia;
    }

    public void recibirPassword(String password) {
        this.password = password;
        System.out.println("[Soldado]: Recibí la contraseña de la reina: " + password);
    }

    public boolean verificarPassword(String intento) {
        return intento.equals(this.password);
    }

    public void intentarEntrada(String abeja, String intentoPassword) {
        lock.lock();
        try {
            if (verificarPassword(intentoPassword)) {
                System.out.println("[Soldado]: " + abeja + " ha ingresado a la colmena.");
            } else {
                System.out.println("[Soldado]: " + abeja + " tiene contraseña incorrecta. ¡No puede entrar!");
            }
        } finally {
            lock.unlock();
        }
    }
}
