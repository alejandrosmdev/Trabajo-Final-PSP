package abejas;

import util.ColorConsole;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class ClienteColmena {
    private static final String HOST = "localhost";
    private static final int PUERTO_SERVIDOR = 5000;
    private static List<Thread> abejas = new ArrayList<>();
    private static List<Zangano> zanganos = new ArrayList<>();
    private static List<Limpiadora> limpiadoras = new ArrayList<>();
    private static List<Recolectora> recolectoras = new ArrayList<>();
    private static Reina reina;
    private static List<Nodriza> nodrizas = new ArrayList<>();
    private static boolean servidorIniciado = false;
    private static Process procesoServidor = null;
    private static BufferedReader servidorReader = null;
    private static Thread hiloLectorServidor = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

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
                    ColorConsole.printInfo("Opción no válida. Intente de nuevo.");
            }
        }

        scanner.close();
        System.exit(0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== CLIENTE COLMENA DE ABEJAS =====");
        System.out.println("1. Iniciar Servidor");
        System.out.println("2. Detener Servidor");
        System.out.println("3. Iniciar Simulación");
        System.out.println("4. Detener Simulación");
        System.out.println("5. Mostrar Estado de Abejas");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
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
            ColorConsole.printInfo("El servidor ya está en ejecución.");
            return;
        }

        try {
            // Verificar si el servidor está disponible
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(HOST, PUERTO_SERVIDOR), 1000);
                ColorConsole.printInfo("El servidor ya está en ejecución en otro proceso.");
                servidorIniciado = true;
                return;
            } catch (IOException e) {
                // El servidor no está disponible, lo iniciamos
            }

            // Iniciar el servidor en un proceso separado
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            String classpath = System.getProperty("java.class.path");
            String className = "Servidor.ServidorColmena";

            ProcessBuilder builder = new ProcessBuilder(
                    javaBin, "-cp", classpath, className
            );

            builder.redirectErrorStream(true);
            procesoServidor = builder.start();

            // Leer la salida del proceso del servidor
            servidorReader = new BufferedReader(new InputStreamReader(procesoServidor.getInputStream()));

            // Iniciar un hilo para leer la salida del servidor
            hiloLectorServidor = new Thread(() -> {
                try {
                    String line;
                    while ((line = servidorReader.readLine()) != null) {
                        System.out.println("[Servidor] " + line);
                        if (line.contains("Servidor Colmena iniciado")) {
                            servidorIniciado = true;
                        }
                    }
                } catch (IOException e) {
                    // El proceso del servidor ha terminado
                    if (servidorIniciado) {
                        ColorConsole.printInfo("El servidor ha terminado inesperadamente.");
                        servidorIniciado = false;
                    }
                }
            });
            hiloLectorServidor.setDaemon(true);
            hiloLectorServidor.start();

            // Esperar a que el servidor se inicie (máximo 10 segundos)
            for (int i = 0; i < 20; i++) {
                if (servidorIniciado) {
                    break;
                }
                Thread.sleep(500);
            }

            // Verificar si el servidor está disponible
            if (servidorIniciado) {
                ColorConsole.printInfo("Servidor iniciado correctamente.");
            } else {
                // Intentar conectar para confirmar
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(HOST, PUERTO_SERVIDOR), 1000);
                    servidorIniciado = true;
                    ColorConsole.printInfo("Servidor iniciado correctamente.");
                } catch (IOException e) {
                    ColorConsole.printInfo("Error al iniciar el servidor o tiempo de espera agotado.");
                    if (procesoServidor != null) {
                        procesoServidor.destroy();
                        procesoServidor = null;
                    }
                }
            }
        } catch (Exception e) {
            ColorConsole.printInfo("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void detenerServidor() {
        if (!servidorIniciado) {
            ColorConsole.printInfo("El servidor no está en ejecución.");
            return;
        }

        if (procesoServidor != null) {
            // Intentar cerrar el servidor de forma ordenada
            try (Socket socket = new Socket(HOST, PUERTO_SERVIDOR)) {
                // Solo conectar para enviar una señal
            } catch (IOException e) {
                // Ignorar errores
            }

            // Esperar un poco para que el servidor se cierre ordenadamente
            try {
                if (procesoServidor.waitFor(3, TimeUnit.SECONDS)) {
                    ColorConsole.printInfo("Servidor detenido correctamente.");
                } else {
                    // Si no se cierra en 3 segundos, forzar el cierre
                    procesoServidor.destroy();
                    ColorConsole.printInfo("Servidor forzado a detenerse.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                procesoServidor.destroy();
                ColorConsole.printInfo("Servidor forzado a detenerse.");
            }

            procesoServidor = null;

            // Cerrar el lector si está abierto
            if (servidorReader != null) {
                try {
                    servidorReader.close();
                } catch (IOException e) {
                    // Ignorar
                }
                servidorReader = null;
            }
        } else {
            ColorConsole.printInfo("No se puede detener el servidor porque no fue iniciado por este cliente.");
        }

        servidorIniciado = false;
    }

    private static void iniciarSimulacion() {
        if (!servidorIniciado) {
            ColorConsole.printInfo("Debe iniciar el servidor antes de iniciar la simulación.");
            return;
        }

        if (!abejas.isEmpty()) {
            ColorConsole.printInfo("La simulación ya está en ejecución.");
            return;
        }

        // Verificar que el servidor esté realmente disponible
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PUERTO_SERVIDOR), 1000);
        } catch (IOException e) {
            ColorConsole.printInfo("No se puede conectar al servidor. Asegúrese de que esté en ejecución.");
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

        ColorConsole.printInfo("Simulación iniciada con éxito.");
    }

    private static void detenerSimulacion() {
        if (abejas.isEmpty()) {
            ColorConsole.printInfo("No hay simulación en ejecución.");
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

        ColorConsole.printInfo("Simulación detenida.");
    }

    private static void mostrarEstadoAbejas() {
        if (abejas.isEmpty()) {
            ColorConsole.printInfo("No hay simulación en ejecución.");
            return;
        }

        ColorConsole.printInfo("\n===== ESTADO DE LA COLMENA =====");

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

        ColorConsole.printInfo("Zánganos: " + zanganosCon + "/" + zanganos.size() + " conectados");
        ColorConsole.printInfo("Limpiadoras: " + limpiadorasCon + "/" + limpiadoras.size() + " conectadas");
        ColorConsole.printInfo("Recolectoras: " + recolectorasCon + "/" + recolectoras.size() + " conectadas");
        ColorConsole.printInfo("Nodrizas: " + nodrizas.size() + " activas");
        ColorConsole.printInfo("Reina: " + (reina != null ? "Activa" : "Inactiva"));
        ColorConsole.printInfo("================================");
    }
}