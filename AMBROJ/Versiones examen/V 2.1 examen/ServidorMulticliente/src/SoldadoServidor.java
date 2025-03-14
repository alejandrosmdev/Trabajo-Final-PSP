public class SoldadoServidor implements Runnable{

    String password;
    @Override
    public void run() {
        while (true){
            try {
                // Espera a que una abeja solicite entrar
                ClientHandler abejas = MultiClientServer.passwordQueue.take();
                password = "9856";
                boolean acceso=false;
                if (abejas.beeType=="RECOLECTORA") {

                    System.out.println("Comprobando contrasena....");

                    if (abejas.password== this.password){
                        acceso=true;
                        System.out.println("Contrasena correcta, acceso concedido a "+ abejas.beeType+ " " + abejas.clientId);
                        abejas.sendMessage("ACCESO_CONCEDIDO");
                        acceso=false;
                    }else{
                        System.out.println("Contrasena incorrecta");
                        abejas.sendMessage("ACCESO_DENEGADO");
                    }
                }else if (abejas.beeType=="ZANGANO") {

                    System.out.println("Comprobando contrasena....");

                    if (abejas.password== this.password){
                        acceso=true;
                        System.out.println("Contrasena correcta, acceso concedido a "+ abejas.beeType+ " " + abejas.clientId);
                        abejas.sendMessage("ACCESO_CONCEDIDO");
                        acceso=false;
                    }else{
                        System.out.println("Contrasena incorrecta");
                        abejas.sendMessage("ACCESO_DENEGADO");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
