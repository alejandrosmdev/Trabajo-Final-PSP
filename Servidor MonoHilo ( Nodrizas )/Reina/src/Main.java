import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Main {

    private static Socket socketCliente;

    public static void main(String[] args) {
        try {
            ServerSocket socketServidor = new ServerSocket(3000);

            while(true) {
                System.out.println("-".repeat(20));

                System.out.println("Esperando una abeja");
                socketCliente = socketServidor.accept();
                System.out.println("Atendiendo a la abeja");

                InputStream inputStream = socketCliente.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String tipoAbeja = dataInputStream.readUTF();
                System.out.println("Tipo de abeja: " + tipoAbeja);

                switch (tipoAbeja){
                    case "Limpiadora":
                        atenderLimpiadora();
                        break;
                }

                System.out.println("Abeja atendida");

//                System.out.println("Esperando");
//                Thread.sleep(3000);
//                System.out.println("Espera terminada");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void atenderLimpiadora(){
        try {
            OutputStream outputStream = socketCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Atendiendo Limpiadora - Mirando una nueva zona para limpiar");
            int nuevaZona = new Random().nextInt(1, 11);
            System.out.println("Atendiendo Limpiadora - Nueva zona encontrada");
            System.out.println("Atendiendo Limpiadora - Nueva zona: " + nuevaZona);

            dataOutputStream.write(nuevaZona);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}