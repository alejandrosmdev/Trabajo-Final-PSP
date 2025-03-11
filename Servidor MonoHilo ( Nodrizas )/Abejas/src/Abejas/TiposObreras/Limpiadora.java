package Abejas.TiposObreras;

import Abejas.Obrera;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Limpiadora extends Obrera {

    @Override
    public void run() {
        while(true){
            try {
                System.out.print("-".repeat(80) + "\n");
                System.out.println("Buscando a la reina de la colmena");

                Socket socket = new Socket("127.0.0.1", 3000);

                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                dataOutputStream.writeUTF("Limpiadora");

                System.out.println("Preguntando por la nueva zona");
                int nuevaZona = dataInputStream.read();
                System.out.println("Nueva zona: " + nuevaZona);

                int tiempoParaLaTarea = new Random().nextInt(3, 11);

                System.out.println("Empezando la limpieza de la zona " +  nuevaZona + ". Tiempo de espera estimado: " + tiempoParaLaTarea + "s");

                Thread.sleep(tiempoParaLaTarea * 1000);

                System.out.println("Nueva zona limpiada");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
