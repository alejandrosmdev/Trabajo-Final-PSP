public class Limpiadora extends BeeClient {
    public Limpiadora(String host, int port) {
        super(host, port, "LIMPIADORA", "");
    }

    @Override
    public void startBee() {
        new Thread(() -> {
            try {
                while (true) {
                    // Solicitar orden (se añade a la cola en el servidor)
                    System.out.println("Limpiadora: solicitando orden");
                    sendMessage("LIMPIADORA_ESPERANDO");
                    // Espera a recibir la orden enviada por la Reina (tarea en el servidor)
                    String msg = readMessage();
                    if(msg != null && msg.startsWith("LIMPIAR:")) {
                        String zone = msg.substring("LIMPIAR:".length());
                        System.out.println("Limpiadora: recibí orden de limpiar " + zone);
                        // Simula el tiempo de limpieza (3 a 10 seg)
                        int time = 3000 + new java.util.Random().nextInt(7000);
                        Thread.sleep(time);
                        System.out.println("Limpiadora: terminé de limpiar " + zone + " en " + time/1000.0 + " seg");
                        // Aquí podrías notificar finalización si se requiere más lógica
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        Limpiadora bee = new Limpiadora("localhost", 5000);
        bee.startBee();
    }
}
