import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServidorNodriza {
    private static final int PUERTO_ZANGANOS = 5001;
    private static final int PUERTO_RECOLECTORAS = 6001;
    private static Random random = new Random();
    private static final int NUM_NODRIZAS = 3;
    private static InventarioMiel inventario = new InventarioMiel();

    public static void main(String[] args) {
        try {
            // Crear e iniciar las nodrizas
            for (int i = 1; i <= NUM_NODRIZAS; i++) {
                Nodriza nodriza = new Nodriza(i);
                nodriza.start();
            }

            System.out.println("Servidor Nodriza iniciado con " + NUM_NODRIZAS + " nodrizas");

            // Esperar indefinidamente
            Thread.currentThread().join();

        } catch (InterruptedException e) {
            System.out.println("Error en el servidor Nodriza: " + e.getMessage());
        }
    }

    // Clase para gestionar el inventario de miel con bloqueos
    static class InventarioMiel {
        private int cantidadMiel = 0;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition mielDisponible = lock.newCondition();
        private final int UMBRAL_BAJO = 5; // Umbral para avisar de nivel bajo
        private boolean nivelBajo = true;

        public void agregarMiel(int cantidad) {
            lock.lock();
            try {
                cantidadMiel += cantidad;
                System.out.println("Inventario: Se han añadido " + cantidad + " unidades de miel. Total: " + cantidadMiel);

                // Notificar que hay miel disponible
                mielDisponible.signalAll();

                // Verificar si se ha superado el umbral bajo
                verificarUmbral();
            } finally {
                lock.unlock();
            }
        }

        // Método para consumir miel del inventario
        public boolean consumirMiel(int cantidad) {
            lock.lock();
            try {
                // Verificar si hay suficiente miel
                if (this.cantidadMiel < cantidad) {
                    System.out.println("Inventario: No hay suficiente miel para consumir " + cantidad + " unidades.");
                    return false;
                }

                // Consumir la miel
                this.cantidadMiel -= cantidad;

                System.out.println("Inventario: Se han consumido " + cantidad + " unidades de miel. Quedan: " + this.cantidadMiel);

                verificarUmbral();
                return true;
            } finally {
                lock.unlock();
            }
        }

        // Método para esperar hasta que haya miel disponible
        public boolean esperarMielDisponible(long tiempoEspera) throws InterruptedException {
            lock.lock();
            try {
                // Si ya hay miel, retornar inmediatamente
                if (cantidadMiel > 0) {
                    return true;
                }

                // Esperar hasta que haya miel o se agote el tiempo
                return mielDisponible.await(tiempoEspera, java.util.concurrent.TimeUnit.MILLISECONDS);
            } finally {
                lock.unlock();
            }
        }

        // Verificar si el nivel de miel está por debajo del umbral
        private void verificarUmbral() {
            if (cantidadMiel < UMBRAL_BAJO && !nivelBajo) {
                nivelBajo = true;
                System.out.println("¡ALERTA! Nivel de miel bajo: " + cantidadMiel + " unidades");
            } else if (cantidadMiel >= UMBRAL_BAJO && nivelBajo) {
                nivelBajo = false;
                System.out.println("Nivel de miel normalizado: " + cantidadMiel + " unidades");
            }
        }

        // Obtener la cantidad actual de miel
        public int getCantidadMiel() {
            lock.lock();
            try {
                return cantidadMiel;
            } finally {
                lock.unlock();
            }
        }
    }

    static class Nodriza extends Thread {
        private int id;
        private boolean ocupada = false;
        private final ReentrantLock lock = new ReentrantLock();

        public Nodriza(int id) {
            this.id = id;
            setName("Nodriza-" + id);
        }

        @Override
        public void run() {
            System.out.println("Nodriza " + id + " iniciada");

            // Iniciar un hilo para atender zánganos
            new Thread(() -> atenderZanganos()).start();

            // Iniciar un hilo para recibir miel
            new Thread(() -> recibirMiel()).start();
        }

        private void atenderZanganos() {
            try (ServerSocket servidorZanganos = new ServerSocket(PUERTO_ZANGANOS + id)) {
                System.out.println("Nodriza " + id + " esperando zánganos en puerto " + (PUERTO_ZANGANOS + id));

                while (true) {
                    Socket socket = servidorZanganos.accept();

                    // Verificar si la nodriza está ocupada
                    lock.lock();
                    try {
                        if (ocupada) {
                            // Si está ocupada, enviar mensaje de ocupada
                            try (DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {
                                salida.writeUTF("OCUPADA");
                            }
                            socket.close();
                            continue;
                        }

                        // Marcar como ocupada
                        ocupada = true;
                    } finally {
                        lock.unlock();
                    }

                    // Atender al zángano
                    try (
                            DataInputStream entrada = new DataInputStream(socket.getInputStream());
                            DataOutputStream salida = new DataOutputStream(socket.getOutputStream())
                    ) {
                        String mensaje = entrada.readUTF();

                        // Extraer ID del zángano del mensaje
                        if (mensaje.startsWith("ALIMENTAR:")) {
                            int idZangano = Integer.parseInt(mensaje.substring(10));

                            // Intentar esperar hasta que haya miel disponible (máximo 5 segundos)
                            boolean hayMiel = inventario.esperarMielDisponible(5000);

                            if (hayMiel && inventario.consumirMiel(1)) {
                                int tiempoAlimentacion = random.nextInt(3) + 3; // 3-5 segundos

                                System.out.println("Nodriza " + id + ": Alimentando al zángano " + idZangano +
                                        " durante " + tiempoAlimentacion + " segundos. Miel restante: " +
                                        inventario.getCantidadMiel());

                                Thread.sleep(tiempoAlimentacion * 1000);
                                salida.writeUTF("ALIMENTADO:" + tiempoAlimentacion);
                            } else {
                                System.out.println("Nodriza " + id + ": No hay suficiente miel para alimentar al zángano " + idZangano);
                                salida.writeUTF("SIN_MIEL");
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error atendiendo zángano por nodriza " + id + ": " + e.getMessage());
                    } finally {
                        // Marcar como libre nuevamente
                        lock.lock();
                        try {
                            ocupada = false;
                        } finally {
                            lock.unlock();
                        }
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en servidor zánganos de nodriza " + id + ": " + e.getMessage());
            }
        }

        private void recibirMiel() {
            try (ServerSocket servidorMiel = new ServerSocket(PUERTO_RECOLECTORAS + id)) {
                System.out.println("Nodriza " + id + " esperando miel en puerto " + (PUERTO_RECOLECTORAS + id));

                while (true) {
                    Socket socket = servidorMiel.accept();

                    // Verificar si la nodriza está ocupada
                    lock.lock();
                    try {
                        if (ocupada) {
                            // Si está ocupada, enviar mensaje de ocupada
                            try (DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {
                                salida.writeUTF("OCUPADA");
                            }
                            socket.close();
                            continue;
                        }

                        // Marcar como ocupada
                        ocupada = true;
                    } finally {
                        lock.unlock();
                    }

                    // Recibir miel
                    try (DataInputStream entrada = new DataInputStream(socket.getInputStream())) {
                        String mensaje = entrada.readUTF();

                        // Extraer ID de la recolectora y cantidad de miel del mensaje
                        if (mensaje.startsWith("MIEL:")) {
                            String[] partes = mensaje.substring(5).split(":");
                            int idRecolectora = Integer.parseInt(partes[0]);
                            int cantidadMiel = Integer.parseInt(partes[1]);

                            int tiempoRecepcion = random.nextInt(2) + 1; // 1-2 segundos
                            System.out.println("Nodriza " + id + ": Recibiendo " + cantidadMiel + " unidades de miel de la recolectora " +
                                    idRecolectora + " durante " + tiempoRecepcion + " segundos");

                            Thread.sleep(tiempoRecepcion * 1000);

                            // Añadir la miel al inventario
                            inventario.agregarMiel(cantidadMiel);
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error recibiendo miel por nodriza " + id + ": " + e.getMessage());
                    } finally {
                        // Marcar como libre nuevamente
                        lock.lock();
                        try {
                            ocupada = false;
                        } finally {
                            lock.unlock();
                        }
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en servidor miel de nodriza " + id + ": " + e.getMessage());
            }
        }
    }
}
