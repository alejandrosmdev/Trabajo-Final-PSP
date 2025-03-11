import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Reina reina = new Reina();
        reina.start();

        List<Nodriza> nodrizas = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Nodriza nodriza = new Nodriza(i);
            nodrizas.add(nodriza);
            nodriza.start();
        }

        ThreadServicio.setNodrizas(nodrizas);

        ServerColmena servidor = ServerColmena.getInstancia();

        System.out.println("Servidor iniciado");

        Thread hiloConexiones = new Thread(() -> {
            while (true) {
                java.net.Socket clienteSocket = servidor.aceptarConexion();
                if (clienteSocket != null) {
                    ThreadServicio hiloServicio = new ThreadServicio(clienteSocket, reina);
                    hiloServicio.start();
                }
            }
        });
        hiloConexiones.start();

        System.out.println("[Enter] para cerrar el servidor");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

        reina.detener();
        for (Nodriza nodriza : nodrizas) {
            nodriza.detener();
        }

        servidor.cerrar();
        System.out.println("Servidor terminado.");
        System.exit(0);
    }
}

