public class ReinaServidor implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                // Espera a que una limpiadora solicite una orden
                ClientHandler cleaner = MultiClientServer.orderQueue.take();
                // Genera una zona aleatoria para limpiar
                String zone = "Zona-" + (new java.util.Random().nextInt(100));
                System.out.println("Reina (Servidor): asignando " + zone + " a limpiadora " + cleaner.clientId);
                // Envía la orden directamente al cliente limpiador
                cleaner.sendMessage("CLEAN:" + zone);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
