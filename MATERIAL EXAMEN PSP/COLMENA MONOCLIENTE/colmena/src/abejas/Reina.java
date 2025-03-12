package abejas;

import util.ColorConsole;

import java.util.Random;


public class Reina extends Thread {
    private Random random = new Random();
    private boolean running = true;
    private boolean ocupada = false; // Estado para indicar si la reina está ocupada

    @Override
    public void run() {
        ColorConsole.printReina("Reina: Iniciada y lista para asignar tareas");

        while (running) {
            try {
                // La reina realiza sus tareas
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                if (!running) {
                    break;
                }
            }
        }

        ColorConsole.printReina("Reina: Terminando");
    }

    // Método sincronizado para asignar zona, que marca a la reina como ocupada
    public synchronized String asignarZona() {
        ocupada = true;
        ColorConsole.printReina("Reina: Asignando zona...");

        // Simular tiempo que tarda en asignar una zona (entre 1 y 3 segundos)
        try {
            int tiempoAsignacion = random.nextInt(3) + 1;
            Thread.sleep(tiempoAsignacion * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String zona = "Zona " + (random.nextInt(10) + 1);
        ColorConsole.printReina("Reina: Zona asignada: " + zona);
        ocupada = false; // Ya no está ocupada
        return zona;
    }

    // Método para verificar si la reina está ocupada
    public synchronized boolean estaOcupada() {
        return ocupada;
    }

    public void detener() {
        running = false;
        interrupt();
    }
}