import java.util.Random;

public class Recolectora extends BeeClient {

    private String pass;
    public Recolectora(String host, int port, String contrasena) {
        super(host, port, "RECOLECTORA","9856");
        this.pass= contrasena;
    }

    @Override
    public void startBee() {
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Saliendo de la colmena a por polen!!");
                    int outsideTime = 2000 + new Random().nextInt(4000);
                    Thread.sleep(outsideTime);

                    System.out.println("Solicitando entrada a colmena");
                    sendMessage("ENTRAR_COLMENA");
                    String msg = readMessage();
                    if(msg != null && msg.startsWith("ACCESO_CONCEDIDO:")) {
                        System.out.println("Entré en la colmena, a hacer miel!!!");
                        // Producción de miel (4 a 8 seg)
                        int workTime = 4000 + new Random().nextInt(4000);
                        System.out.println("Recolectora: haciendo miel durante " + workTime/1000.0 + " seg");
                        Thread.sleep(workTime);
                        System.out.println("Recolectora: terminé de hacer miel");
                        // Descanso (2 a 5 seg)
                        int restTime = 2000 + new Random().nextInt(3000);
                        System.out.println("Recolectora: descansando durante " + restTime/1000.0 + " seg");
                        Thread.sleep(restTime);
                    }else if (msg != null && msg.startsWith("ACCESO_DENEGADO:")){
                        System.out.println("No tengo acceso....");
                        break;
                    }

                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        Recolectora forager = new Recolectora("localhost", 5000, "9856");
        forager.startBee();
    }
}
