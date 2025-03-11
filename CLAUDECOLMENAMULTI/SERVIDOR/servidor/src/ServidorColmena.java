import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorColmena {
    static final int PUERTO = 5000;
    private ServerSocket skServidor;
    private boolean running = false;
    private List<HiloCliente> clientes = new ArrayList<>();
    private AbejaReina reina;
    private List<AbejaNodrizaServidor> nodrizas = new ArrayList<>();
    private ExecutorService pool;

    public ServidorColmena(int numNodrizas) {
        try {
            skServidor = new ServerSocket(PUERTO);
            System.out.println("\u001B[35m[SERVIDOR] Iniciado en el puerto: " + PUERTO + "\u001B[0m");

            // Crear la reina (singleton)
            reina = new AbejaReina();

            // Crear las nodrizas
            for (int i = 0; i < numNodrizas; i++) {
                AbejaNodrizaServidor nodriza = new AbejaNodrizaServidor(i);
                nodrizas.add(nodriza);
                nodriza.start();
            }

            // Pool de hilos para manejar múltiples clientes
            pool = Executors.newCachedThreadPool();

            running = true;
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] Error al iniciar el servidor: " + e.getMessage() + "\u001B[0m");
        }
    }

    public void iniciar() {
        new Thread(() -> {
            while (running) {
                try {
                    Socket cliente = skServidor.accept();
                    System.out.println("\u001B[35m[SERVIDOR] Cliente conectado desde: " +
                            cliente.getInetAddress() + "\u001B[0m");

                    // Crear un nuevo hilo para manejar este cliente
                    HiloCliente hiloCliente = new HiloCliente(cliente, this);
                    clientes.add(hiloCliente);
                    pool.execute(hiloCliente);

                } catch (IOException e) {
                    if (running) {
                        System.out.println("\u001B[31m[ERROR] Error al aceptar conexión: " +
                                e.getMessage() + "\u001B[0m");
                    }
                }
            }
        }).start();
    }

    public void detener() {
        running = false;
        try {
            // Detener todos los hilos de cliente
            for (HiloCliente cliente : clientes) {
                cliente.detener();
            }

            // Detener todas las nodrizas
            for (AbejaNodrizaServidor nodriza : nodrizas) {
                nodriza.detener();
            }

            // Detener la reina
            reina.detener();

            // Cerrar el pool de hilos
            pool.shutdown();

            // Cerrar el socket del servidor
            if (skServidor != null && !skServidor.isClosed()) {
                skServidor.close();
            }

            System.out.println("\u001B[35m[SERVIDOR] Detenido correctamente\u001B[0m");
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] Error al detener el servidor: " +
                    e.getMessage() + "\u001B[0m");
        }
    }

    public AbejaReina getReina() {
        return reina;
    }

    public AbejaNodrizaServidor getNodrizaDisponible() {
        // Buscar una nodriza disponible aleatoriamente
        List<AbejaNodrizaServidor> disponibles = new ArrayList<>();
        for (AbejaNodrizaServidor nodriza : nodrizas) {
            if (nodriza.estaDisponible()) {
                disponibles.add(nodriza);
            }
        }

        if (disponibles.isEmpty()) {
            return null;
        }

        // Seleccionar una nodriza aleatoria de las disponibles
        int indice = (int) (Math.random() * disponibles.size());
        return disponibles.get(indice);
    }

    public static void main(String[] args) {
        // Crear el servidor con 3 nodrizas
        ServidorColmena servidor = new ServidorColmena(3);
        servidor.iniciar();

        // Mantener el servidor en ejecución
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servidor.detener();
        }));
    }
}

