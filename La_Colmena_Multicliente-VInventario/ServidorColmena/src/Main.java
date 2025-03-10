import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Inicializar el inventario de miel (se crea automáticamente al obtener la instancia)
        InventarioMiel inventarioMiel = InventarioMiel.getInstancia();

        // Iniciar el monitor de inventario de miel
        MonitorInventarioMiel monitorInventario = new MonitorInventarioMiel();
        monitorInventario.start();

        // Iniciar la Reina
        Reina reina = new Reina();
        reina.start();

        // Iniciar las Nodrizas
        List<Nodriza> nodrizas = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Nodriza nodriza = new Nodriza(i);
            nodrizas.add(nodriza);
            nodriza.start();
        }

        // Configurar las nodrizas en el HiloServicioAbeja
        HiloServicioAbeja.setNodrizas(nodrizas);

        // Iniciar el servidor
        ServidorColmena servidor = ServidorColmena.getInstancia();

        System.out.println("Servidor de la Colmena iniciado. Esperando conexiones...");

        // Crear un hilo para aceptar conexiones
        Thread hiloConexiones = new Thread(() -> {
            while (true) {
                java.net.Socket clienteSocket = servidor.aceptarConexion();
                if (clienteSocket != null) {
                    HiloServicioAbeja hiloServicio = new HiloServicioAbeja(clienteSocket, reina);
                    hiloServicio.start();
                }
            }
        });
        hiloConexiones.start();

        // Esperar a que el usuario presione Enter para terminar
        System.out.println("Presione Enter para terminar el servidor...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

        // Detener todos los procesos
        monitorInventario.detener();
        reina.detener();
        for (Nodriza nodriza : nodrizas) {
            nodriza.detener();
        }

        servidor.cerrar();
        System.out.println("Servidor terminado.");
        System.exit(0);
    }
}