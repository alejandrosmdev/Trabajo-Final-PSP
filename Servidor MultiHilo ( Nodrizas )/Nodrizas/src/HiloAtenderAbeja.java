import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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


    private void atenderZangano(){
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

    private void atenderRecolectora(){
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

    private Nodriza buscarNodrizas(){
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
