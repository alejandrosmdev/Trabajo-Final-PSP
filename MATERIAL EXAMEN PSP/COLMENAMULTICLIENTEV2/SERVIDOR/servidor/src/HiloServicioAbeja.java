import java.io.*;
import java.net.*;
import java.util.*;

public class HiloServicioAbeja extends Thread {
    private Socket clienteSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Random random = new Random();
    private String tipoCliente = "Cliente desconocido"; // Valor por defecto
    private ServidorColmena servidor;
    private boolean running = true;

    public HiloServicioAbeja(Socket clienteSocket, ServidorColmena servidor) {
        this.clienteSocket = clienteSocket;
        this.servidor = servidor;
        try {
            this.in = new DataInputStream(clienteSocket.getInputStream());
            this.out = new DataOutputStream(clienteSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Error al crear flujos de datos: " + e.getMessage());
            running = false; // No continuar si no se pueden crear los flujos
        }
    }

    @Override
    public void run() {
        try {
            // Verificar si los flujos se crearon correctamente
            if (in == null || out == null) {
                cerrarConexion();
                return;
            }

            // Establecer un timeout para la lectura
            clienteSocket.setSoTimeout(10000); // 10 segundos de timeout

            try {
                // Recibir identificación del cliente
                tipoCliente = in.readUTF();
                System.out.println("Cliente conectado: " + tipoCliente);

                // Enviar confirmación de conexión
                out.writeUTF("Conexión establecida con el servidor de la colmena");

                // Restablecer el timeout a infinito para operaciones normales
                clienteSocket.setSoTimeout(0);
            } catch (SocketTimeoutException ste) {
                // Simplemente cerrar la conexión sin mostrar error
                cerrarConexion();
                return;
            } catch (EOFException | SocketException e) {
                // Cliente desconectado durante la identificación, cerrar silenciosamente
                cerrarConexion();
                return;
            } catch (IOException ioe) {
                // Si el mensaje de error es null, no mostrar nada
                if (ioe.getMessage() != null && !ioe.getMessage().equals("null")) {
                    System.out.println("Error de E/S con cliente: " + ioe.getMessage());
                }
                cerrarConexion();
                return;
            }

            // Bucle principal de procesamiento de solicitudes
            while (running) {
                try {
                    String solicitud = in.readUTF();
                    procesarSolicitud(solicitud);
                } catch (SocketException se) {
                    // Cliente desconectado, cerrar silenciosamente si es una desconexión normal
                    System.out.println(tipoCliente + " se ha desconectado");
                    break;
                } catch (EOFException eof) {
                    // Fin de flujo, cliente cerró la conexión
                    System.out.println(tipoCliente + " ha cerrado la conexión");
                    break;
                } catch (IOException e) {
                    // Solo mostrar errores que no sean desconexiones normales
                    if (e.getMessage() != null && !e.getMessage().equals("null") &&
                            !e.getMessage().contains("reset") && !e.getMessage().contains("closed")) {
                        System.out.println("Error en la comunicación con " + tipoCliente + ": " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (SocketException se) {
            // La conexión se cerró mientras configurábamos el timeout, no mostrar error
        } catch (Exception e) {
            // Capturar cualquier otra excepción no prevista, pero no mostrar si es null
            if (e.getMessage() != null && !e.getMessage().equals("null")) {
                System.out.println("Error inesperado en el hilo de servicio para " + tipoCliente + ": " + e.getMessage());
            }
        } finally {
            cerrarConexion();
        }
    }

    private void procesarSolicitud(String solicitud) throws IOException {
        if (solicitud == null) {
            return;
        }

        System.out.println("Solicitud recibida de " + tipoCliente + ": " + solicitud);

        switch (solicitud) {
            case "SOLICITAR_ZONA":
                // Asignar zonas a las limpiadoras
                if (tipoCliente.startsWith("Limpiadora")) {
                    // Verificar si la reina está ocupada
                    while (servidor.reinaEstaOcupada()) {
                        try {
                            System.out.println("Reina ocupada, " + tipoCliente + " espera...");
                            Thread.sleep(500); // Esperar un poco antes de volver a verificar
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    String zona = servidor.asignarZona();
                    out.writeUTF(zona);
                    System.out.println("Servidor: Asigna " + zona + " a " + tipoCliente);
                } else {
                    out.writeUTF("Solo las limpiadoras pueden solicitar zonas");
                }
                break;

            case "SOLICITAR_ALIMENTO":
                // Buscar una nodriza libre para alimentar al zángano
                if (tipoCliente.startsWith("Zangano")) {
                    // Intentar encontrar una nodriza libre
                    int idNodriza = servidor.obtenerNodrizaLibre();

                    if (idNodriza > 0) {
                        // Hay una nodriza libre
                        int tiempoAlimentacion = servidor.alimentarZangano(idNodriza, tipoCliente);
                        out.writeUTF("Alimentación completada por Nodriza-" + idNodriza + " en " + tiempoAlimentacion + " segundos.");
                    } else {
                        // No hay nodrizas libres
                        out.writeUTF("No hay nodrizas disponibles en este momento. Intenta más tarde.");
                    }
                } else {
                    out.writeUTF("Solo los zánganos pueden solicitar alimento");
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
                running = false;
                break;

            default:
                System.out.println("Solicitud no reconocida de " + tipoCliente + ": " + solicitud);
                out.writeUTF("Solicitud no reconocida");
                break;
        }
    }

    public void cerrarConexion() {
        running = false;
        try {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignorar errores al cerrar
                }
                in = null;
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignorar errores al cerrar
                }
                out = null;
            }

            if (clienteSocket != null && !clienteSocket.isClosed()) {
                try {
                    clienteSocket.close();
                    // Solo mostrar mensaje de cierre si conocemos el tipo de cliente
                    if (!tipoCliente.equals("Cliente desconocido")) {
                        System.out.println("Conexión cerrada con " + tipoCliente);
                    }
                } catch (IOException e) {
                    // Ignorar errores al cerrar
                }
                clienteSocket = null;
            }
        } finally {
            servidor.eliminarHiloServicio(this);
        }
    }
}