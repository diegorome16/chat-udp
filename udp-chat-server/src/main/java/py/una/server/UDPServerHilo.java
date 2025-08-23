package py.una.server;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class UDPServerHilo extends Thread {

    private DatagramPacket packet;
    private DatagramSocket socket;
    private HashMap<String, Integer> usuarios;

    public UDPServerHilo (DatagramPacket packet, DatagramSocket socket, HashMap<String, Integer> usuarios) {
        super("UDPServerHilo");
        this.socket = socket;
        this.packet = packet;
        this.usuarios = usuarios;
        // Forma 1: usando entrySet
        for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
        }
    }

    public void run (){

        try {
            //buffer de datos a enviar
            byte[] sendData = new byte[1024];

            // Datos recibidos e Identificamos quien nos envio
            String datoRecibido = new String(packet.getData());
            datoRecibido = datoRecibido.trim();
            System.out.println("DatoRecibido: " + datoRecibido );

            //estos son los datos del cliente emisor
            InetAddress IPAddress = packet.getAddress();
            int port = packet.getPort();

            System.out.println("De : " + IPAddress + ":" + port);

            Mensaje mensaje = MensajeJSON.stringObjeto(datoRecibido);
            System.out.println("Para: " + mensaje.getUsuarioDestino() + "\nMensaje: " + mensaje.getMesaje());

            //datos del cliente receptor
            String [] partes = mensaje.getMesaje().split(":");
            String clienteReceptor = partes[0].trim();
            // Forma 1: usando entrySet
            for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
                System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
            }
            System.out.println(clienteReceptor);
            Integer puerto = usuarios.get(clienteReceptor);
            System.out.println(puerto);
            int puertoDestino = puerto;
            String msn = partes[1];


            // Enviamos la respuesta inmediatamente al cliente si hay
            // Es no bloqueante
            String mensajeYusuario = msn + "|" + mensaje.getUsuarioDestino();
            System.out.println(mensajeYusuario);
            sendData = mensajeYusuario.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, puertoDestino);

            socket.send(sendPacket);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
