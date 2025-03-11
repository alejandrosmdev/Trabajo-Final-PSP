import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    private static final Object lockMiel = new Object();
    private static int mielAlmacenada = 10;

    public static Reina reina = new Reina();

    public static ArrayList<Nodriza> nodrizas = new ArrayList<>();

    private static Socket socketCliente;

    public static void main(String[] args) {
        try {
            despertarNodrizas();

            ServerSocket socketServidor = new ServerSocket(3001);

            while(true) {
                System.out.println("-".repeat(20));

                System.out.println("Esperando una abeja");

                socketCliente = socketServidor.accept();
                HiloAtenderAbeja hiloAtender = new HiloAtenderAbeja(socketCliente);
                hiloAtender.start();

                System.out.println("Atendiendo a la abeja");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void despertarNodrizas(){
        Nodriza nodriza1 = new Nodriza(1);
        Nodriza nodriza2 = new Nodriza(2);
        Nodriza nodriza3 = new Nodriza(3);
        Nodriza nodriza4 = new Nodriza(4);
        Nodriza nodriza5 = new Nodriza(5);

        nodrizas.add(nodriza1);
//        nodrizas.add(nodriza2);
//        nodrizas.add(nodriza3);
//        nodrizas.add(nodriza4);
//        nodrizas.add(nodriza5);

        for(Nodriza nodriza : nodrizas){
            nodriza.start();
        }
    }

    public static boolean gestionarMiel(int cantidad){
        synchronized (lockMiel){
            System.out.println("MIEL ANTES DE GESTIONAR: " + mielAlmacenada);
            if(mielAlmacenada + cantidad >= 0){
                mielAlmacenada += cantidad;
                System.out.println("Miel almacenada en la colmena: " + mielAlmacenada);
                lockMiel.notifyAll();
                return true;
            } else {
                lockMiel.notifyAll();
                return false;
            }
        }
    }

    public static synchronized Nodriza buscarNodrizas(){
        while(true) {
            for (Nodriza nodriza : Main.nodrizas) {
                if (nodriza.isDisponible()) {
                    nodriza.setDisponible(false);
                    return nodriza;
                }
            }
        }
    }
}
