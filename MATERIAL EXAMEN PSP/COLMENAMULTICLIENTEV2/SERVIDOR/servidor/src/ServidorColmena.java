import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServidorColmena {
    static final int PUERTO = 5000;
    private ServerSocket serverSocket;
    private boolean running = true;
    private List<HiloServicioAbeja> hilosServicio = Collections.synchronizedList(new ArrayList<>());
    private Random random = new Random();

    // Estado de la reina
    private boolean reinaOcupada = false;

    // Estado de las nodrizas (ID -> ocupada)
    private Map<Integer, Boolean> nodrizasOcupadas = new ConcurrentHashMap<>();

    // Singleton para asegurar que solo hay una instancia del servidor
    private static ServidorColmena instancia;

    public static synchronized ServidorColmena getInstancia() {
        if (instancia == null) {
            instancia = new ServidorColmena();
        }
        return instancia;
    }

    private ServidorColmena() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor Colmena iniciado en el puerto: " + PUERTO);

            // Inicializar 3 nodrizas
            for (int i = 1; i <= 3; i++) {
                nodrizasOcupadas.put(i, false);
            }

            // Agregar un hook de apagado para cerrar el servidor correctamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                cerrar();
            }));

            // Iniciar el hilo principal para aceptar conexiones
            iniciarAceptadorConexiones();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void iniciarAceptadorConexiones() {
        new Thread(() -> {
            while (running) {
                Socket clienteSocket = null;
                try {
                    clienteSocket = serverSocket.accept();
                    clienteSocket.setKeepAlive(true); // Mantener la conexión activa
                    System.out.println("Nueva conexión aceptada desde: " + clienteSocket.getInetAddress().getHostAddress());

                    // Crear un nuevo hilo de servicio para esta conexión
                    HiloServicioAbeja hiloServicio = new HiloServicioAbeja(clienteSocket, this);
                    hilosServicio.add(hiloServicio);
                    hiloServicio.start();
                } catch (SocketException se) {
                    // No mostrar error si el servidor está cerrándose
                    if (running) {
                        System.out.println("Error en el socket al aceptar conexión: " + se.getMessage());
                    }
                } catch (IOException e) {
                    // No mostrar error si el servidor está cerrándose
                    if (running && e.getMessage() != null && !e.getMessage().equals("null")) {
                        System.out.println("Error al aceptar conexión: " + e.getMessage());
                    }
                } catch (Exception e) {
                    // No mostrar error si el mensaje es null
                    if (running && e.getMessage() != null && !e.getMessage().equals("null")) {
                        System.out.println("Error inesperado al aceptar conexión: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    // Método para verificar si la reina está ocupada
    public synchronized boolean reinaEstaOcupada() {
        return reinaOcupada;
    }

    // Método para asignar una zona (simula el comportamiento de la reina)
    public synchronized String asignarZona() {
        reinaOcupada = true;
        System.out.println("Reina: Asignando zona...");

        // Simular tiempo que tarda en asignar una zona (entre 1 y 3 segundos)
        try {
            int tiempoAsignacion = random.nextInt(3) + 1;
            Thread.sleep(tiempoAsignacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String zona = "Zona " + (random.nextInt(10) + 1);
        System.out.println("Reina: Zona asignada: " + zona);
        reinaOcupada = false;
        return zona;
    }

    // Método para obtener una nodriza libre
    public synchronized int obtenerNodrizaLibre() {
        for (Map.Entry<Integer, Boolean> entry : nodrizasOcupadas.entrySet()) {
            if (!entry.getValue()) {
                return entry.getKey(); // Devolver ID de la nodriza libre
            }
        }
        return -1; // No hay nodrizas libres
    }

    // Método para simular la alimentación por una nodriza
    public synchronized int alimentarZangano(int idNodriza, String zangano) {
        // Verificar que la nodriza existe y está libre
        if (!nodrizasOcupadas.containsKey(idNodriza) || nodrizasOcupadas.get(idNodriza)) {
            return -1; // Error: nodriza no existe o está ocupada
        }

        // Marcar la nodriza como ocupada
        nodrizasOcupadas.put(idNodriza, true);
        System.out.println("Nodriza-" + idNodriza + ": Alimentando a " + zangano);

        // Simular tiempo de alimentación
        int tiempoAlimentacion = random.nextInt(3) + 3; // 3 a 5 segundos
        try {
            Thread.sleep(tiempoAlimentacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Marcar la nodriza como libre nuevamente
        nodrizasOcupadas.put(idNodriza, false);
        System.out.println("Nodriza-" + idNodriza + ": " + zangano + " alimentado");

        return tiempoAlimentacion;
    }

    public void eliminarHiloServicio(HiloServicioAbeja hilo) {
        hilosServicio.remove(hilo);
    }

    public void cerrar() {
        running = false;

        // Cerrar todos los hilos de servicio
        for (HiloServicioAbeja hilo : new ArrayList<>(hilosServicio)) {
            hilo.cerrarConexion();
        }
        hilosServicio.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor Colmena cerrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Iniciar el servidor
        ServidorColmena servidor = ServidorColmena.getInstancia();

        // Mantener el servidor en ejecución hasta que se presione Enter
        System.out.println("Presione Enter para detener el servidor...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

        // Cerrar el servidor
        servidor.cerrar();
        System.exit(0);
    }
}