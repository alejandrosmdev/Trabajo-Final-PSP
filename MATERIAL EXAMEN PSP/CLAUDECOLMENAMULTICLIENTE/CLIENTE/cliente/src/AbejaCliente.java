public abstract class AbejaCliente extends Thread {
    protected final int id;
    protected final ClienteColmena cliente;
    protected boolean running = true;

    public AbejaCliente(int id, ClienteColmena cliente) {
        this.id = id;
        this.cliente = cliente;
    }

    public void detener() {
        running = false;
        this.interrupt();
    }
}

