import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hilo que gestiona la comunicación con cada abeja conectada al servidor.
 */
public class HiloServicioAbeja extends Thread {
    private Socket clienteSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Random random = new Random();
    private String tipoCliente;
    private Reina reina;
    private static List<Nodriza> nodrizas;

    public HiloServicioAbeja(Socket clienteSocket, Reina reina) {
        this.clienteSocket = clienteSocket;
        this.reina = reina;
        try {
            this.in = new DataInputStream(clienteSocket.getInputStream());
            this.out = new DataOutputStream(clienteSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error al crear flujos de datos: " + e.getMessage());
        }
    }

    public static void setNodrizas(List<Nodriza> nodrizasList) {
        nodrizas = nodrizasList;
    }

    @Override
    public void run() {
        try {
            // Recibir identificación del cliente
            tipoCliente = in.readUTF();
            System.out.println("Cliente conectado: " + tipoCliente);

            // Enviar confirmación de conexión
            out.writeUTF("Conexión establecida con el servidor de la colmena");

            boolean continuar = true;
            while (continuar && reina.isRunning()) {
                try {
                    String solicitud = in.readUTF();
                    procesarSolicitud(solicitud);
                } catch (SocketException se) {
                    System.out.println(tipoCliente + " se ha desconectado");
                    continuar = false;
                } catch (IOException e) {
                    System.out.println("Error en la comunicación con " + tipoCliente + ": " + e.getMessage());
                    continuar = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error en el hilo de servicio: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void procesarSolicitud(String solicitud) throws IOException {
        System.out.println("Solicitud recibida de " + tipoCliente + ": " + solicitud);

        switch (solicitud) {
            case "SOLICITAR_ZONA":
                // La Reina asigna zonas a las limpiadoras
                if (tipoCliente.startsWith("Limpiadora")) {
                    String zona = reina.asignarZona();
                    out.writeUTF(zona);
                    System.out.println("Reina: Asigna " + zona + " a " + tipoCliente);
                } else {
                    out.writeUTF("Solo las limpiadoras pueden solicitar zonas");
                }
                break;

            case "SOLICITAR_ALIMENTO":
                // Las nodrizas alimentan a los zánganos
                if (tipoCliente.startsWith("Zangano") && nodrizas != null && !nodrizas.isEmpty()) {
                    // Buscar nodrizas libres
                    List<Nodriza> nodrizasLibres = new ArrayList<>();
                    for (Nodriza nodriza : nodrizas) {
                        if (!nodriza.estaOcupada()) {
                            nodrizasLibres.add(nodriza);
                        }
                    }

                    Nodriza nodrizaSeleccionada;
                    if (!nodrizasLibres.isEmpty()) {
                        // Seleccionar una nodriza libre aleatoriamente
                        nodrizaSeleccionada = nodrizasLibres.get(random.nextInt(nodrizasLibres.size()));
                        System.out.println(tipoCliente + " selecciona aleatoriamente a Nodriza-" +
                                nodrizaSeleccionada.getIdentificador() + " que está libre");
                    } else {
                        // Si no hay nodrizas libres, esperar a que alguna se libere
                        System.out.println(tipoCliente + " espera porque todas las nodrizas están ocupadas");
                        out.writeUTF("Todas las nodrizas están ocupadas. Espera un momento...");

                        // Esperar un tiempo aleatorio antes de intentar de nuevo
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        // Seleccionar una nodriza aleatoria (posiblemente aún ocupada)
                        nodrizaSeleccionada = nodrizas.get(random.nextInt(nodrizas.size()));
                    }

                    int tiempoAlimentacion = nodrizaSeleccionada.alimentar(tipoCliente);
                    out.writeUTF("Alimentación completada por Nodriza-" + nodrizaSeleccionada.getIdentificador() +
                            " en " + tiempoAlimentacion + " segundos.");
                } else {
                    out.writeUTF("No hay nodrizas disponibles o no eres un zángano");
                }
                break;

            case "REPORTAR_MIEL":
                int cantidadMiel = in.readInt();
                System.out.println(tipoCliente + " ha producido " + cantidadMiel + " unidades de miel.");
                out.writeUTF("Miel registrada correctamente");
                break;

            case "DESCONECTAR":
                System.out.println(tipoCliente + " se desconecta del servidor");
                out.writeUTF("Desconexión aceptada");
                cerrarConexion();
                break;

            default:
                System.out.println("Solicitud no reconocida de " + tipoCliente + ": " + solicitud);
                out.writeUTF("Solicitud no reconocida");
                break;
        }
    }

    private void cerrarConexion() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close();
                System.out.println("Conexión cerrada con " + tipoCliente);
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}