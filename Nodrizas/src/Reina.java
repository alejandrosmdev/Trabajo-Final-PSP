import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Reina extends Thread{

    private static Socket socketCliente;

    public Reina(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        OutputStream outputStream = socketCliente.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        System.out.println("Atendiendo Limpiadora - Mirando una nueva zona para limpiar");
        int nuevaZona = new Random().nextInt(1, 11);
        System.out.println("Atendiendo Limpiadora - Nueva zona encontrada");
        System.out.println("Atendiendo Limpiadora - Nueva zona: " + nuevaZona);

        dataOutputStream.write(nuevaZona);
    }
}
