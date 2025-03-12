import abejas.*;
import servidor.ServidorColmena;

import java.io.*;
import java.net.*;
import java.util.*;


public class ColmenaMonocliente {
    private static List<Thread> abejas = new ArrayList<>();
    private static List<Zangano> zanganos = new ArrayList<>();
    private static List<Limpiadora> limpiadoras = new ArrayList<>();
    private static List<Recolectora> recolectoras = new ArrayList<>();
    private static Reina reina;
    private static List<Nodriza> nodrizas = new ArrayList<>();
    private static boolean servidorIniciado = false;
    private static boolean simulacionIniciada = false;
    private static ServidorColmena servidor;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        util.ColorConsole.printInfo("=== SIMULACIÓN DE COLMENA DE ABEJAS (MONOCLIENTE) ===");

        while (!salir) {
            mostrarMenu();
            int opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1:
                    iniciarServidor();
                    break;
                case 2:
                    detenerServidor();
                    break;
                case 3:
                    iniciarSimulacion();
                    break;
                case 4:
                    detenerSimulacion();
                    break;
                case 5:
                    mostrarEstadoAbejas();
                    break;
                case 6:
                    salir = true;
                    detenerSimulacion();
                    detenerServidor();
                    break;
                default:
                    util.ColorConsole.printInfo("Opción no válida. Intente de nuevo.");
            }
        }

        scanner.close();
        util.ColorConsole.printInfo("Simulación finalizada. ¡Hasta pronto!");
    }

    private static void mostrarMenu() {
        System.out.println("\n" + util.ColorConsole.CYAN + "=== MENÚ PRINCIPAL ===" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "1. Iniciar Servidor" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "2. Detener Servidor" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "3. Iniciar Simulación" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "4. Detener Simulación" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "5. Mostrar Estado de Abejas" + util.ColorConsole.RESET);
        System.out.println(util.ColorConsole.CYAN + "6. Salir" + util.ColorConsole.RESET);
        System.out.print(util.ColorConsole.CYAN + "Seleccione una opción: " + util.ColorConsole.RESET);
    }

    private static int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void iniciarServidor() {
        if (servidorIniciado) {
            util.ColorConsole.printInfo("El servidor ya está en ejecución.");
            return;
        }

        try {
            // Verificar si el servidor está disponible
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 5000), 1000);
                util.ColorConsole.printInfo("El servidor ya está en ejecución en otro proceso.");
                servidorIniciado = true;
                return;
            } catch (IOException e) {
                // El servidor no está disponible, lo iniciamos
            }

            // Iniciar el servidor
            servidor = ServidorColmena.getInstancia();
            servidorIniciado = true;
            util.ColorConsole.printInfo("Servidor iniciado correctamente.");
        } catch (Exception e) {
            util.ColorConsole.printInfo("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void detenerServidor() {
        if (!servidorIniciado) {
            util.ColorConsole.printInfo("El servidor no está en ejecución.");
            return;
        }

        // Detener la simulación primero si está en ejecución
        if (simulacionIniciada) {
            detenerSimulacion();
        }

        if (servidor != null) {
            servidor.cerrar();
            servidor = null;
            util.ColorConsole.printInfo("Servidor detenido.");
        } else {
            util.ColorConsole.printInfo("No se puede detener el servidor porque no fue iniciado por este cliente.");
        }

        servidorIniciado = false;
    }

    private static void iniciarSimulacion() {
        if (!servidorIniciado) {
            util.ColorConsole.printInfo("Debe iniciar el servidor antes de iniciar la simulación.");
            return;
        }

        if (simulacionIniciada) {
            util.ColorConsole.printInfo("La simulación ya está en ejecución.");
            return;
        }

        // Verificar que el servidor esté realmente disponible
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 5000), 1000);
        } catch (IOException e) {
            util.ColorConsole.printInfo("No se puede conectar al servidor. Asegúrese de que esté en ejecución.");
            servidorIniciado = false;
            return;
        }

        // Inicializar la reina
        reina = new Reina();
        reina.start();

        // Inicializar las nodrizas
        for (int i = 1; i <= 3; i++) {
            Nodriza nodriza = new Nodriza(i);
            nodrizas.add(nodriza);
            nodriza.start();
        }

        // Inicializar zánganos
        for (int i = 1; i <= 3; i++) {
            Zangano zangano = new Zangano(i);
            zanganos.add(zangano);
            abejas.add(zangano);
            zangano.start();
        }

        // Inicializar limpiadoras
        for (int i = 1; i <= 5; i++) {
            Limpiadora limpiadora = new Limpiadora(i);
            limpiadoras.add(limpiadora);
            abejas.add(limpiadora);
            limpiadora.start();
        }

        // Inicializar recolectoras
        for (int i = 1; i <= 10; i++) {
            Recolectora recolectora = new Recolectora(i);
            recolectoras.add(recolectora);
            abejas.add(recolectora);
            recolectora.start();
        }

        simulacionIniciada = true;
        util.ColorConsole.printInfo("Simulación iniciada con éxito.");
    }

    private static void detenerSimulacion() {
        if (!simulacionIniciada) {
            util.ColorConsole.printInfo("No hay simulación en ejecución.");
            return;
        }

        // Detener todas las abejas
        for (Thread abeja : abejas) {
            abeja.interrupt();
        }

        // Detener la reina y las nodrizas
        if (reina != null) {
            reina.detener();
        }

        for (Nodriza nodriza : nodrizas) {
            nodriza.detener();
        }

        // Esperar a que todas las abejas terminen
        for (Thread abeja : abejas) {
            try {
                abeja.join(1000);
            } catch (InterruptedException e) {
                // Ignorar
            }
        }

        // Limpiar las listas
        abejas.clear();
        zanganos.clear();
        limpiadoras.clear();
        recolectoras.clear();
        nodrizas.clear();
        reina = null;

        simulacionIniciada = false;
        util.ColorConsole.printInfo("Simulación detenida.");
    }

    private static void mostrarEstadoAbejas() {
        if (!simulacionIniciada) {
            util.ColorConsole.printInfo("No hay simulación en ejecución.");
            return;
        }

        util.ColorConsole.printInfo("\n===== ESTADO DE LA COLMENA =====");

        // Contar abejas conectadas
        int zanganosCon = 0, limpiadorasCon = 0, recolectorasCon = 0;

        for (Zangano z : zanganos) {
            if (z.estaConectado()) zanganosCon++;
        }

        for (Limpiadora l : limpiadoras) {
            if (l.estaConectado()) limpiadorasCon++;
        }

        for (Recolectora r : recolectoras) {
            if (r.estaConectado()) recolectorasCon++;
        }

        util.ColorConsole.printInfo("Zánganos: " + zanganosCon + "/" + zanganos.size() + " conectados");
        util.ColorConsole.printInfo("Limpiadoras: " + limpiadorasCon + "/" + limpiadoras.size() + " conectadas");
        util.ColorConsole.printInfo("Recolectoras: " + recolectorasCon + "/" + recolectoras.size() + " conectadas");
        util.ColorConsole.printInfo("Nodrizas: " + nodrizas.size() + " activas");
        util.ColorConsole.printInfo("Reina: " + (reina != null ? "Activa" : "Inactiva"));
        util.ColorConsole.printInfo("================================");
    }
}