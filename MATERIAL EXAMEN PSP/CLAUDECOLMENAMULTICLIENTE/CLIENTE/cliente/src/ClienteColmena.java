import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteColmena {
    private static final String HOST = "127.0.0.1";
    private static final int PUERTO = 5000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean conectado = false;

    private List<Thread> hilosAbejas = new ArrayList<>();

    public boolean conectar() {
        try {
            socket = new Socket(HOST, PUERTO);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // Recibir mensaje de bienvenida
            String bienvenida = in.readUTF();
            System.out.println("\u001B[35m[CLIENTE] " + bienvenida + "\u001B[0m");

            conectado = true;
            return true;
        } catch (IOException e) {
            System.out.println("\u001B[31m[ERROR] No se pudo conectar al servidor: " +
                    e.getMessage() + "\u001B[0m");
            return false;
        }
    }

    public void desconectar() {
        if (conectado) {
            try {
                // Enviar señal de desconexión
                out.writeInt(0);

                // Detener todos los hilos de abejas
                for (Thread hilo : hilosAbejas) {
                    if (hilo instanceof AbejaCliente) {
                        ((AbejaCliente) hilo).detener();
                    }
                }

                // Cerrar conexiones
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();

                conectado = false;
                System.out.println("\u001B[35m[CLIENTE] Desconectado del servidor\u001B[0m");
            } catch (IOException e) {
                System.out.println("\u001B[31m[ERROR] Error al desconectar: " +
                        e.getMessage() + "\u001B[0m");
            }
        }
    }

    public void iniciarSimulacion(int numLimpiadoras, int numZanganos, int numRecolectoras) {
        if (!conectado) {
            System.out.println("\u001B[31m[ERROR] No estás conectado al servidor\u001B[0m");
            return;
        }

        // Crear y iniciar las abejas limpiadoras
        for (int i = 0; i < numLimpiadoras; i++) {
            AbejaLimpiadora limpiadora = new AbejaLimpiadora(i, this);
            hilosAbejas.add(limpiadora);
            limpiadora.start();
        }

        // Crear y iniciar los zánganos
        for (int i = 0; i < numZanganos; i++) {
            AbejaZangano zangano = new AbejaZangano(i, this);
            hilosAbejas.add(zangano);
            zangano.start();
        }

        // Crear y iniciar las abejas recolectoras
        for (int i = 0; i < numRecolectoras; i++) {
            AbejaRecolectora recolectora = new AbejaRecolectora(i, this);
            hilosAbejas.add(recolectora);
            recolectora.start();
        }

        System.out.println("\u001B[35m[CLIENTE] Simulación iniciada con " +
                numLimpiadoras + " limpiadoras, " +
                numZanganos + " zánganos y " +
                numRecolectoras + " recolectoras\u001B[0m");
    }

    public synchronized String solicitarZonaLimpieza() throws IOException {
        // Enviar solicitud de zona a limpiar
        out.writeInt(1);

        // Recibir la zona asignada
        return in.readUTF();
    }

    public synchronized boolean solicitarAlimento() throws IOException {
        // Enviar solicitud de alimento
        out.writeInt(2);

        // Verificar si hay una nodriza disponible
        boolean hayNodriza = in.readBoolean();

        if (hayNodriza) {
            // Recibir el ID de la nodriza y el tiempo de alimentación
            int idNodriza = in.readInt();
            int tiempoAlimentacion = in.readInt();

            System.out.println("\u001B[33m[ZÁNGANO] Siendo alimentado por Nodriza-" +
                    idNodriza + " durante " + tiempoAlimentacion + " segundos\u001B[0m");

            // Simular el tiempo de alimentación
            try {
                Thread.sleep(tiempoAlimentacion * 1000);
            } catch (InterruptedException e) {
                System.out.println("\u001B[31m[ERROR] Zángano interrumpido durante alimentación: " +
                        e.getMessage() + "\u001B[0m");
            }
        }

        return hayNodriza;
    }

    public synchronized void notificarRecoleccion(int tiempoRecoleccion, int tiempoDescanso) throws IOException {
        // Enviar notificación de recolección
        out.writeInt(3);
        out.writeInt(tiempoRecoleccion);
        out.writeInt(tiempoDescanso);

        // Recibir confirmación
        in.readBoolean();
    }

    public boolean estaConectado() {
        return conectado;
    }

    public static void main(String[] args) {
        ClienteColmena cliente = new ClienteColmena();
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== SIMULADOR DE COLMENA =====");
            System.out.println("1. Conectar al servidor");
            System.out.println("2. Iniciar simulación");
            System.out.println("3. Desconectar");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = 0;
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("\u001B[31m[ERROR] Opción no válida\u001B[0m");
                continue;
            }

            switch (opcion) {
                case 1:
                    if (!cliente.estaConectado()) {
                        cliente.conectar();
                    } else {
                        System.out.println("\u001B[35m[CLIENTE] Ya estás conectado al servidor\u001B[0m");
                    }
                    break;
                case 2:
                    if (cliente.estaConectado()) {
                        System.out.print("Número de limpiadoras: ");
                        int numLimpiadoras = Integer.parseInt(scanner.nextLine());

                        System.out.print("Número de zánganos: ");
                        int numZanganos = Integer.parseInt(scanner.nextLine());

                        System.out.print("Número de recolectoras: ");
                        int numRecolectoras = Integer.parseInt(scanner.nextLine());

                        cliente.iniciarSimulacion(numLimpiadoras, numZanganos, numRecolectoras);
                    } else {
                        System.out.println("\u001B[31m[ERROR] Debes conectarte primero\u001B[0m");
                    }
                    break;
                case 3:
                    cliente.desconectar();
                    break;
                case 4:
                    cliente.desconectar();
                    salir = true;
                    break;
                default:
                    System.out.println("\u001B[31m[ERROR] Opción no válida\u001B[0m");
                    break;
            }
        }

        scanner.close();
        System.out.println("\u001B[35m[CLIENTE] Programa finalizado\u001B[0m");
    }
}

