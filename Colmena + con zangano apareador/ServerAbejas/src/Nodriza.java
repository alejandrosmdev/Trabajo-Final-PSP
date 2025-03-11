import java.util.Random;

public class Nodriza extends Thread {
    private int id_nodriza;
    private Random random = new Random();
    private boolean running = true;
    private boolean ocupada = false;

    public Nodriza(int id_nodriza) {
        this.id_nodriza = id_nodriza;
    }

    @Override
    public void run() {
        System.out.println("Nodriza-" + id_nodriza + ": lista.");

        while (running) {
            try {
                synchronized (this) {
                    while (!ocupada && running) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }
    }

    public synchronized int alimentar(String zangano) {
        ocupada = true;
        int tiempoAlimentacion = random.nextInt(3) + 3;
        System.out.println("Nodriza-" + id_nodriza + ": Alimentando a " + zangano + " durante " + tiempoAlimentacion + " segundos.");

        try {
            Thread.sleep(tiempoAlimentacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Nodriza-" + id_nodriza + ": " + zangano + " alimentado.");
        ocupada = false;
        notify();

        return tiempoAlimentacion;
    }

    public int getid_nodriza() {
        return id_nodriza;
    }

    public synchronized boolean estaOcupada() {
        return ocupada;
    }

    public void detener() {
        running = false;
        synchronized (this) {
            notify(); // Despertar si está esperando
        }
        interrupt();
    }
}

