public class NodrizaServidor implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                // Espera a que un zángano solicite alimento
                ClientHandler drone = MultiClientServer.foodQueue.take();
                System.out.println("Nodriza (Servidor): procesando solicitud de alimento del zángano " + drone.clientId);
                // Simula el tiempo de alimentación entre 3 y 5 segundos
                int feedingTime = 3000 + new java.util.Random().nextInt(2000);
                Thread.sleep(feedingTime);
                System.out.println("Nodriza (Servidor): alimento procesado para zángano " + drone.clientId + " en " + feedingTime/1000.0 + " seg");
                // Envía la confirmación de alimento al zángano
                drone.sendMessage("FOOD_PROVIDED");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
