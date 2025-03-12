package abejas;

import util.ColorConsole;

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
        ColorConsole.printNodriza("Nodriza-" + identificador + ": Iniciada y lista para alimentar zánganos");

        while (running) {
            try {
                // La nodriza espera a que le soliciten alimento
                synchronized (this) {
                    while (!ocupada && running) {
                        wait();
                    }

                    // Si fue despertada para terminar, salir del bucle
                    if (!running) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }

        ColorConsole.printNodriza("Nodriza-" + identificador + ": Terminando");
    }

    public synchronized int alimentar(String zangano) {
        if (ocupada) {
            // Si ya está ocupada, devolver -1 para indicar que no puede alimentar
            return -1;
        }

        ocupada = true;
        int tiempoAlimentacion = random.nextInt(3) + 3; // 3 a 5 segundos
        ColorConsole.printNodriza("Nodriza-" + identificador + ": Alimentando a " + zangano + " durante " + tiempoAlimentacion + " segundos.");

        try {
            Thread.sleep(tiempoAlimentacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ColorConsole.printNodriza("Nodriza-" + identificador + ": " + zangano + " alimentado.");
        ocupada = false;
        notify(); // Notificar que ha terminado de alimentar

        return tiempoAlimentacion;
    }

    public synchronized boolean estaOcupada() {
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