import java.net.*;
import java.io.*;

public abstract class BeeClient {
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected String beeType;
    protected String contrasena;

    public BeeClient(String host, int port, String beeType, String pass) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.beeType = beeType;
            this.contrasena= pass;
            register();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void register() {
        out.println("REGISTER:" + beeType);
        out.println(contrasena);
    }

    protected void sendMessage(String msg) {
        out.println(msg);
    }

    protected String readMessage() throws IOException {
        return in.readLine();
    }

    public abstract void startBee();
}
