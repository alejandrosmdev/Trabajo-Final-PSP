import java.util.Random;

public class Nodriza extends Thread {
    private int identificador;
    private Random random = new Random();
    private boolean running = true;
    private boolean ocupada = false;

    public Nodriza(int identificador) {
        this.identificador = identificador;
    }

    @Override
    public void run() {
        System.out.println("Nodriza-" + identificador + ": Iniciada y lista para alimentar zánganos");

        while (running) {
            try {
                // La nodriza espera a que le soliciten alimento
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
        int tiempoAlimentacion = random.nextInt(3) + 3; // 3 a 5 segundos
        System.out.println("Nodriza-" + identificador + ": Alimentando a " + zangano + " durante " + tiempoAlimentacion + " segundos.");

        try {
            Thread.sleep(tiempoAlimentacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Nodriza-" + identificador + ": " + zangano + " alimentado.");
        ocupada = false;
        notify(); // Notificar que ha terminado de alimentar

        return tiempoAlimentacion;
    }

    public boolean estaOcupada() {
        return ocupada;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void detener() {
        running = false;
        synchronized (this) {
            notify(); // Despertar si está esperando
        }
        interrupt();
    }
}

