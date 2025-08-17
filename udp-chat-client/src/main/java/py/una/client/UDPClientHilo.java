package py.una.client;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPClientHilo extends Thread{

    private DatagramSocket socket;
    private String usuario;
    private String server;

    public UDPClientHilo (String usuario, DatagramSocket sockeClient, String server) {
        super("UDPCliente: " + usuario);
        this.socket = sockeClient;
        this.usuario = usuario;
        this.server = server;
    }

    public void run(){

        byte[] receiveData = new byte[2048];

        while (true) {
            try {
                receiveData = new byte[1024];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveData, receiveData.length);

                // 4) Receive LLAMADA BLOQUEANTE
                socket.receive(receivePacket);

                //agregaremos un hilo para que no bloquee la escucha
                new Thread(() -> {
                    System.out.println("________________________________________________");
                    System.out.println("Aceptamos un paquete");

                    // Datos recibidos e Identificamos quien nos envio
                    String datoRecibido = new String(receivePacket.getData());
                    datoRecibido = datoRecibido.trim();
                    String [] partes = datoRecibido.split("\\|");
                    System.out.println("USUARIO-ORIGEN: " + partes[1] + "\nMensaje: " +  partes[0]);

                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
