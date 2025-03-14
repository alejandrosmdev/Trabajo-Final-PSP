public class Soldado extends BeeClient{
    public Soldado(String host, int port) {
        super(host, port, "SOLDADO","");
    }

    private String pass;

    @Override
    public void startBee() {
        new Thread(() -> {
            try {
                while (true) {
                    // Solicitar orden (se añade a la cola en el servidor)
                    System.out.println("Soldado: solicitando contraseña");
                    sendMessage("SOLDADO_SOLICITA_CONTRASENA");
                    // Espera a recibir la orden enviada por la Reina (tarea en el servidor)
                    String msg = readMessage();
                    if(msg != null && msg.startsWith("CONTRASENA:")) {
                        String contrasena = msg.substring("CONTRASENA:".length());
                        System.out.println("SOLDADO: recibí la password de la reina " + contrasena);
                        this.pass = contrasena;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        Soldado bee = new Soldado("localhost", 5000);
        bee.startBee();
    }
}
