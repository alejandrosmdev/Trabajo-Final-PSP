public class ClientLauncher {
    public static void main(String[] args) {


        // Iniciar varias Abejas Limpiadoras
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Limpiadora limpiadora = new Limpiadora("localhost", 5000);
                limpiadora.startBee();
            }).start();
        }

        // Iniciar varios Zánganos
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Zangano zangano = new Zangano("localhost", 5000);
                zangano.startBee();
            }).start();
        }

        // Iniciar varias Abejas Recolectoras
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                Recolectora recolectora = new Recolectora("localhost", 5000);
                recolectora.startBee();
            }).start();
        }
    }
}
