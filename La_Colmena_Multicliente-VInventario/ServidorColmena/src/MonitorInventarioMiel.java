public class MonitorInventarioMiel extends Thread {
    private InventarioMiel inventario;
    private boolean running = true;
    private final int INTERVALO_VERIFICACION = 10000; // 10 segundos

    public MonitorInventarioMiel() {
        this.inventario = InventarioMiel.getInstancia();
    }

    @Override
    public void run() {
        System.out.println("Monitor de Inventario de Miel: Iniciado");

        while (running) {
            try {
                // Mostrar informe periódico
                System.out.println("\n" + inventario.obtenerInforme() + "\n");

                // Verificar si es necesario generar alertas
                verificarNivelMiel();

                Thread.sleep(INTERVALO_VERIFICACION);
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }

        System.out.println("Monitor de Inventario de Miel: Terminado");
    }

    private void verificarNivelMiel() {
        // Verificar si la miel está en nivel crítico
        if (inventario.getCantidadMiel() < 5) {
            System.out.println("¡ALERTA CRÍTICA! Nivel de miel extremadamente bajo. ¡Se necesitan más recolectoras!");
        }
    }

    public void detener() {
        running = false;
        interrupt();
    }
}