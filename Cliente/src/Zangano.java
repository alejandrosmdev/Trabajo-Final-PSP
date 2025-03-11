import java.io.*;
import java.net.*;
import java.util.Random;

public class Zangano extends Thread {
    private static final String HOST = "localhost";
    private static final int PUERTO_NODRIZA_BASE = 5001;
    private static Random random = new Random();
    private int id;

    public Zangano(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Buscar reina
                int tiempoBusqueda = random.nextInt(6) + 5; // 5-10 segundos
                System.out.println("Zángano " + id + ": Buscando reina durante " + tiempoBusqueda + " segundos");
                Thread.sleep(tiempoBusqueda * 1000);

                // Probabilidad 1/10 de encontrar reina
                if (random.nextInt(10) == 0) {
                    System.out.println("Zángano " + id + ": ¡Encontré una reina! Me voy a fundar una nueva colmena");
                    break; // Terminar ejecución
                }

                System.out.println("Zángano " + id + ": No encontré reina, volviendo a la colmena para alimentarme");

                // Pedir alimento a nodriza
                boolean alimentado = false;
                int intentos = 0;

                while (!alimentado && intentos < 5) { // Máximo 5 intentos
                    // Intentar con una nodriza aleatoria
                    int idNodriza = random.nextInt(3) + 1; // 1-3
                    alimentado = pedirAlimento(idNodriza);

                    if (!alimentado) {
                        // Esperar un poco antes de intentar con otra nodriza
                        intentos++;
                        Thread.sleep(1000);
                    }
                }

                if (!alimentado) {
                    System.out.println("Zángano " + id + ": No conseguí alimento después de varios intentos. Esperando más tiempo...");
                    Thread.sleep(5000); // Esperar más tiempo antes de volver a intentar
                }

            } catch (InterruptedException | IOException e) {
                System.out.println("Error en zángano " + id + ": " + e.getMessage());
            }
        }
    }

    private boolean pedirAlimento(int idNodriza) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket(HOST, PUERTO_NODRIZA_BASE + idNodriza);
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Enviar ID del zángano junto con la solicitud
            salida.writeUTF("ALIMENTAR:" + id);
            String respuesta = entrada.readUTF();

            if (respuesta.equals("OCUPADA")) {
                System.out.println("Zángano " + id + ": Nodriza " + idNodriza + " está ocupada, intentaré con otra");
                return false;
            } else if (respuesta.equals("SIN_MIEL")) {
                System.out.println("Zángano " + id + ": No hay suficiente miel disponible en la colmena");
                return false;
            } else if (respuesta.startsWith("ALIMENTADO:")) {
                int tiempoAlimentacion = Integer.parseInt(respuesta.substring(11));
                System.out.println("Zángano " + id + ": Siendo alimentado por nodriza " + idNodriza +
                        " durante " + tiempoAlimentacion + " segundos");
                return true;
            }

            return false;
        } catch (IOException e) {
            System.out.println("Zángano " + id + ": Error conectando con nodriza " + idNodriza + ": " + e.getMessage());
            return false;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}