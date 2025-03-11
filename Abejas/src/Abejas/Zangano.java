package Abejas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class Zangano extends Thread {
    @Override
    public void run() {
        boolean seguirBuscando = true;
        while(seguirBuscando){
            try {
                System.out.print("-".repeat(80) + "\n");
                int tiempoParaLaTarea = new Random().nextInt(5, 11);

                System.out.println("Buscando reinas fuera de la colmena.Tiempo de espera estimado: " + tiempoParaLaTarea + "s");

                Thread.sleep(tiempoParaLaTarea * 1000);

                int reinaEncontrada = new Random().nextInt(1, 11);

                if(reinaEncontrada == 10){
                    System.out.println("Reina encontrada");
                    break;
                } else {
                    System.out.println("No se ha encontrado ninguna reina");
                }

                System.out.println("Buscando una nodriza");

                Socket socket = new Socket("127.0.0.1", 3001);

                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                dataOutputStream.writeUTF("Zangano");

                int nodriza = dataInputStream.read();

                System.out.println("Nodriza encontrada: " + nodriza);

                socket = new Socket("127.0.0.1", 3001 + nodriza);

                inputStream = socket.getInputStream();
                dataInputStream = new DataInputStream(inputStream);

                System.out.println("Esperando el alimento");
                Boolean haComido = dataInputStream.readBoolean();
                System.out.println("Comida ingerida");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
