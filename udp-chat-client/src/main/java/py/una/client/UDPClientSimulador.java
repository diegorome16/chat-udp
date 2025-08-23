package py.una.client;

import py.una.server.Mensaje;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientSimulador {

    String direccionServidor;
    String usuario;

    public UDPClientSimulador(String direccionServidor, String usuario){
        this.direccionServidor = direccionServidor;
        this.usuario = usuario;
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

                    InetAddress address = InetAddress.getByName(direccionServidor);

                    String msn = usuario + ":hola";
                    //de momento le pongo puerto cero, pero debo cambiar la logica
                    Mensaje mensaje = new Mensaje(address, 0, "simulador", msn);

                    String datoPaquete = MensajeJSON.objetoString(mensaje);

                    byte[] buffer = datoPaquete.getBytes();

                    DatagramPacket packet = new DatagramPacket(
                            buffer, buffer.length, ipServidor, 4444
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
