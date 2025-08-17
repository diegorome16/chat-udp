package py.una.server;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServerHilo extends Thread {

    private DatagramPacket packet;
    private DatagramSocket socket;

    public UDPServerHilo (DatagramPacket packet, DatagramSocket socket) {
        super("UDPServerHilo");
        this.socket = socket;
        this.packet = packet;
    }

    public void run (){

        try {
            //buffer de datos a enviar
            byte[] sendData = new byte[1024];

            // Datos recibidos e Identificamos quien nos envio
            String datoRecibido = new String(packet.getData());
            datoRecibido = datoRecibido.trim();
            System.out.println("DatoRecibido: " + datoRecibido );


            InetAddress IPAddress = packet.getAddress();

            int port = packet.getPort();

            System.out.println("De : " + IPAddress + ":" + port);
            //System.out.println("Persona Recibida : " + p.getCedula() + ", " + p.getNombre() + " " + p.getApellido());

            Mensaje mensaje = MensajeJSON.stringObjeto(datoRecibido);
            System.out.println("Para: " + mensaje.getUsuarioDestino() + "\nMensaje: " + mensaje.getMesaje());
            // Enviamos la respuesta inmediatamente al cliente si hay
            // Es no bloqueante
            String mensajeYusuario = mensaje.getMesaje() + "|" + mensaje.getUsuarioDestino();
            System.out.println(mensajeYusuario);
            sendData = mensajeYusuario.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, mensaje.getPort());

            socket.send(sendPacket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
