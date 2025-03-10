import java.util.Random;

public class Limpiadora extends Thread {
    private int id;
    private AbejaReina reina;
    private Random random = new Random();

    public Limpiadora(int id, AbejaReina reina) {
        super("Limpiadora-" + id);
        this.id = id;
        this.reina = reina;
    }

    @Override
    public void run() {
        System.out.println("Limpiadora " + id + " ha comenzado a trabajar.");

        while (true) {
            try {
                // Solicitar una zona para limpiar a la reina
                System.out.println("Limpiadora " + id + ": Señá Majestá!!! ¿¿¿Qué mas quié que limpie pues???");
                String zona = reina.solicitarOrden(id);

                System.out.println("Limpiadora " + id + ": Pos nada... aquí a limpiar " + zona + " un ratico!!!");

                // Tiempo aleatorio para limpiar (3-10 segundos)
                int tiempoLimpieza = 3000 + random.nextInt(7001);
                Thread.sleep(tiempoLimpieza);

                System.out.println("Limpiadora " + id + " ha terminado de limpiar " + zona +
                        " (Tiempo: " + (tiempoLimpieza / 1000.0) + " segundos)");

            } catch (InterruptedException e) {
                System.out.println("Limpiadora " + id + " ha sido interrumpida.");
                break;
            }
        }
    }
}

