import java.net.*;
import java.io.*;

public class ClientHandler extends Thread {
    Socket socket;
    int clientId;
    String beeType;
    String password;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket socket, int clientId) {
        this.socket = socket;
        this.clientId = clientId;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // Registro inicial: se espera un mensaje del tipo "REGISTER:<TIPO>"
            String line = in.readLine();
            if(line != null && line.startsWith("REGISTER:")) {
                beeType = line.substring("REGISTER:".length()).trim().toUpperCase();
                System.out.println("Cliente " + clientId + " registrado como " + beeType);
            }
//            if(line != null && line.startsWith("PASSWORD:")) {
//                password = line.substring("PASSWORD:".length()).trim();
//                System.out.println(beeType+" " + clientId + " con contrasena: "+ password);
//            }
            // Bucle para procesar mensajes entrantes
            while((line = in.readLine()) != null) {
                this.password=line;
                System.out.println("Recibido de " + clientId + " (" + beeType + "): "+ line);
                if(line.startsWith("SOLDADO_SOLICITA_CONTRASENA") && beeType.equals("SOLDADO")) {
                    // Se añade a la cola para que la tarea de la Reina la atienda
                    MultiClientServer.orderQueue.put(this);
                }
                else if(line.startsWith("ENTRAR_COLMENA") && beeType.equals("ZANGANO")) {
                    // Se añade a la cola para que la tarea de la Nodriza la atienda
                    MultiClientServer.passwordQueue.put(this);
                }else if(line.startsWith("ENTRAR_COLMENA") && beeType.equals("RECOLECTORA")) {
                    // Se añade a la cola para que la tarea de la Nodriza la atienda
                    MultiClientServer.passwordQueue.put(this);
                }
                // Si una limpiadora solicita orden
                else if(line.startsWith("LIMPIADORA_ESPERANDO") && beeType.equals("LIMPIADORA")) {
                    // Se añade a la cola para que la tarea de la Reina la atienda
                    MultiClientServer.orderQueue.put(this);
                }
                // Si un zángano solicita alimento
                else if(line.startsWith("BUSCAR_COMIDA") && beeType.equals("ZANGANO")) {
                    // Se añade a la cola para que la tarea de la Nodriza la atienda
                    MultiClientServer.foodQueue.put(this);
                }

                // Otras comunicaciones o mensajes pueden ser agregados aquí según se requiera.
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch(IOException e) { }
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }
}
