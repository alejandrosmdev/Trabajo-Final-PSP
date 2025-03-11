package Abejas.TiposObreras;

import Abejas.Obrera;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class Recolectora extends Obrera {
    @Override
    public void run() {
        while(true){
            try {
                System.out.print("-".repeat(80) + "\n");

                int tiempoParaLaTarea = new Random().nextInt(4, 8);

                System.out.println("Preparando miel. Tiempo de espera estimado: " + tiempoParaLaTarea + "s");

                Thread.sleep(tiempoParaLaTarea * 1000);

                System.out.println("Miel preparada");

                System.out.println("Entrando a la colmena");

                Socket socket = new Socket("127.0.0.1", 3001);

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                dataOutputStream.writeUTF("Recolectora");

                System.out.println("Dejando la miel en la colmena");
                boolean mielDejada = dataInputStream.readBoolean();
                System.out.println("Miel dejada");

                int tiempoDeDescanso = new Random().nextInt(2, 5);

                System.out.println("Descansando. TIempo de espera estimado: " + tiempoDeDescanso + "s");
                Thread.sleep(tiempoDeDescanso * 1000);
                System.out.println("Descanso terminado");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
