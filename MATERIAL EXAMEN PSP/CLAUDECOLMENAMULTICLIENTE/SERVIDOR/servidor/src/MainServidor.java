public class MainServidor {
    public static void main(String[] args) {
        // Número de nodrizas en la colmena
        int numNodrizas = 3;

        // Crear e iniciar el servidor
        ServidorColmena servidor = new ServidorColmena(numNodrizas);
        servidor.iniciar();

        System.out.println("\u001B[35m[SERVIDOR] Servidor de la colmena iniciado con " +
                numNodrizas + " nodrizas\u001B[0m");
        System.out.println("\u001B[35m[SERVIDOR] Esperando conexiones de clientes...\u001B[0m");
    }
}

