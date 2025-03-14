public class Recolectora extends BeeClient {
    public Recolectora(String host, int port) {
        super(host, port, "RECOLECTORA");
    }

    @Override
    public void startBee() {
        new Thread(() -> {
            try {
                while (true) {
                    // Producción de miel (4 a 8 seg)
                    int workTime = 4000 + new java.util.Random().nextInt(4000);
                    System.out.println("Recolectora: haciendo miel durante " + workTime/1000.0 + " seg");
                    Thread.sleep(workTime);
                    System.out.println("Recolectora: terminé de hacer miel");
                    // Descanso (2 a 5 seg)
                    int restTime = 2000 + new java.util.Random().nextInt(3000);
                    System.out.println("Recolectora: descansando durante " + restTime/1000.0 + " seg");
                    Thread.sleep(restTime);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        Recolectora forager = new Recolectora("localhost", 5000);
        forager.startBee();
    }
}
