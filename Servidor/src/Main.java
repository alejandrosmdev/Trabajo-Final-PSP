import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Iniciar los servidores en hilos separados
        new Thread(() -> {
            ServidorReina.main(args);
        }).start();

        new Thread(() -> {
            ServidorNodriza.main(args);
        }).start();

        System.out.println("Servidores iniciados correctamente");
    }}