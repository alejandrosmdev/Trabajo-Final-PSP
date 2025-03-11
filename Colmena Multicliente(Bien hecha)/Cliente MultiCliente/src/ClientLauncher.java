public class ClientLauncher {
    public static void main(String[] args) {


        int port = 5000;

        // Iniciar varias Abejas Limpiadoras
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Limpiadora limpiadora = new Limpiadora("localhost", port);
                limpiadora.startBee();
            }).start();
        }

        // Iniciar varios Zánganos
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Zangano zangano = new Zangano("localhost", port);
                zangano.startBee();
            }).start();
        }

        // Iniciar varias Abejas Recolectoras
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                Recolectora recolectora = new Recolectora("localhost", port);
                recolectora.startBee();
            }).start();
        }
    }
}
