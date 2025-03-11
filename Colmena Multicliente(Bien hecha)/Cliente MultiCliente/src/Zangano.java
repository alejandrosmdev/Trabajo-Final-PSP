public class Zangano extends BeeClient {
    public Zangano(String host, int port) {
        super(host, port, "ZANGANO");
    }

    @Override
    public void startBee() {
        new Thread(() -> {
            try {
                while (true) {
                    // Simula la búsqueda de una nueva reina (5 a 10 seg)
                    int searchTime = 5000 + new java.util.Random().nextInt(5000);
                    System.out.println("Zángano: buscando reina durante " + searchTime/1000.0 + " segundos");
                    Thread.sleep(searchTime);
                    // Con probabilidad 1/10 encuentra una nueva reina y se desconecta
                    if(new java.util.Random().nextInt(10) == 0) {
                        System.out.println("Zángano: ¡encontré una nueva reina! Saliendo de la colmena.");
                        sendMessage("FOUND_REINA");
                        break;
                    } else {
                        System.out.println("Zángano: no encontré reina. Solicitando comidica.");
                        sendMessage("BUSCAR_COMIDA");
                        String msg = readMessage();
                        if(msg != null && msg.startsWith("COMIDA_ENVIADA")) {
                            System.out.println("Zángano: recibí comidica, de puta madre CO. Reiniciando búsqueda.");
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        Zangano zangano = new Zangano("localhost", 5000);
        zangano.startBee();
    }
}
