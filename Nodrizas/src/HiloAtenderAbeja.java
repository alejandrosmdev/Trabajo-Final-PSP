import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class HiloAtenderAbeja extends Thread {
    private Socket socketCliente;

    public HiloAtenderAbeja(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socketCliente.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            String tipoAbeja = dataInputStream.readUTF();
            System.out.println("Tipo de abeja: " + tipoAbeja);

            switch (tipoAbeja) {
                case "Limpiadora":
                    atenderLimpiadora();
                    break;
                case "Zangano":
                    atenderZangano();
                    break;
                case "Recolectora":
                    atenderRecolectora();
                    break;
            }

            System.out.println("Abeja atendida");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void atenderLimpiadora(){
        try {
            System.out.println("Atendiendo Limpiadora - Esperando para hablar con la reina");
            System.out.println("Atendiendo Limpiadora - Reina encontrada");

//            OutputStream outputStream = socketCliente.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//
//            System.out.println("Atendiendo Limpiadora - Mirando una nueva zona para limpiar");
//            int nuevaZona = new Random().nextInt(1, 11);
//            System.out.println("Atendiendo Limpiadora - Nueva zona encontrada");
//            System.out.println("Atendiendo Limpiadora - Nueva zona: " + nuevaZona);
//
//            dataOutputStream.write(nuevaZona);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void atenderZangano(){
        try {
            OutputStream outputStream = socketCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Atendiendo Zangano - Buscando nodriza");
            Nodriza nodriza = Main.buscarNodrizas();
            System.out.println("Atendiendo Zangano - Nodriza encontrada");
            System.out.println("Atendiendo Zangano - Nodriza: " + nodriza.getIdNodriza());

            dataOutputStream.write(nodriza.getIdNodriza());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void atenderRecolectora(){
        try {
            OutputStream outputStream = socketCliente.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Atendiendo Recolectora - Dejando la miel");
            Main.gestionarMiel(+1);
            dataOutputStream.writeBoolean(true);
            System.out.println("Atendiendo Recolectora - Miel dejada");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
