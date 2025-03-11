import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    private static ArrayList<Nodriza> nodrizas = new ArrayList<>();

    private static Socket socketCliente;

    public static void main(String[] args) {
        try {
            despertarNodrizas();

            ServerSocket socketServidor = new ServerSocket(3001);

            while(true) {
                System.out.println("-".repeat(20));

                System.out.println("Esperando una abeja");
                socketCliente = socketServidor.accept();
                System.out.println("Atendiendo a la abeja");

                InputStream inputStream = socketCliente.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String tipoAbeja = dataInputStream.readUTF();
                System.out.println("Tipo de abeja: " + tipoAbeja);

                switch (tipoAbeja) {
                    case "Zangano":
                        atenderZangano();
                        break;
                    case "Recolectora":
                        atenderRecolectora();
                        break;
                }

                System.out.println("Abeja atendida");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void atenderZangano(){
        try {
            OutputStream outputStream = socketCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Atendiendo Zangano - Buscando nodriza");
            Nodriza nodriza = buscarNodrizas();
            System.out.println("Atendiendo Zangano - Nodriza encontrada");
            System.out.println("Atendiendo Zangano - Nodriza: " + nodriza.getIdNodriza());

            dataOutputStream.write(nodriza.getIdNodriza());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void atenderRecolectora(){
        try {
            OutputStream outputStream = socketCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Atendiendo Recolectora - Dejando la miel");
            dataOutputStream.writeBoolean(true);
            System.out.println("Atendiendo Recolectora - Miel dejada");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void despertarNodrizas(){
        Nodriza nodriza1 = new Nodriza(1);
        Nodriza nodriza2 = new Nodriza(2);
        Nodriza nodriza3 = new Nodriza(3);
        Nodriza nodriza4 = new Nodriza(4);
        Nodriza nodriza5 = new Nodriza(5);

        nodrizas.add(nodriza1);
        nodrizas.add(nodriza2);
        nodrizas.add(nodriza3);
        nodrizas.add(nodriza4);
        nodrizas.add(nodriza5);

        for(Nodriza nodriza : nodrizas){
            nodriza.start();
        }
    }

    private static Nodriza buscarNodrizas(){
        while(true) {
            for (Nodriza nodriza : nodrizas) {
                if (nodriza.isDisponible()) {
                    nodriza.setDisponible(false);
                    return nodriza;
                }
            }
        }
    }
}
