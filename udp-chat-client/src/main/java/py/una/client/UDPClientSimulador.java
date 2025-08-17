package py.una.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientSimulador {

    String direccionServidor;
    int puertoServidor;

    public UDPClientSimulador(String direccionServidor, int puertoServidor){
        this.direccionServidor = direccionServidor;
        this.puertoServidor = puertoServidor;
        this.crearClientes();
    }

    public void crearClientes() {
        // Crear 5 clientes que envían mensajes en paralelo
        for (int i = 1; i <= 5; i++) {
            int clienteId = i;
            new Thread(() -> {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress ipServidor = InetAddress.getByName(direccionServidor);

                    String mensaje = "Hola mi amigo |" + clienteId;
                    byte[] buffer = mensaje.getBytes();

                    DatagramPacket packet = new DatagramPacket(
                            buffer, buffer.length, ipServidor, puertoServidor
                    );

                    socket.send(packet);
                    System.out.println("Cliente-" + clienteId + " envió: " + mensaje);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
