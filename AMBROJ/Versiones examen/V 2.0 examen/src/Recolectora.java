import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Recolectora extends Thread{
    private static final Random random = new Random();

    private static final String HOST = "localhost";
    private static final int PUERTO_SOLDADO = 5003;
    private boolean running = true;

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            System.out.println("Recolectora: Entrando a la colmena!");

            boolean permiso=solicitar_acceso();
            if (permiso) {
                hacerMiel();
            }else{
                break;
            }
        }
    }

    private void hacerMiel(){
        int tiempoProduccion = random.nextInt(5) + 4; // 4 a 8 segundos
        System.out.println("Recolectora: Produciendo miel durante " + tiempoProduccion + " segundos.");
        try {
            Thread.sleep(tiempoProduccion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Recolectora: Miel producida.");

        int tiempoDescanso = random.nextInt(4) + 2; // 2 a 5 segundos
        System.out.println("Recolectora: Descansando durante " + tiempoDescanso + " segundos.");
        try {
            Thread.sleep(tiempoDescanso * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Saliendo de la colmena!!");
    }

    private boolean solicitar_acceso(){
        Socket socket = null;

        try {
            socket = new Socket(HOST, PUERTO_SOLDADO);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("SOLICITAR_ENTRAR , pass: 9856");
            String respuesta = in.readLine();
            System.out.println("Recolectora: " + respuesta);

            in.close();
            out.close();
            socket.close();

            if (respuesta=="PERMISO_CONCEDIDO"){
                return true;
            }else {

                System.out.println("No me dejaron entrar, me voy");
                return false;
            }


        } catch (IOException e) {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    // Ignorar
                }
            }
        }
        return false;
    }

    public void detener() {
        running = false;
    }
}