package py.una.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPMultiServer {

    List<UDPServerHilo> hilosClientes;
    List<String> usuarios;

    public void ejecutarServer() throws IOException {

        int puerto = 4444;
        try {
            //1) Creamos el socket Servidor de Datagramas (UDP)
            DatagramSocket serverSocket = new DatagramSocket(puerto);
            System.out.println("Servidor chat - UDP ");

            //2) buffer de datos a recibir
            byte[] receiveData = new byte[1024];

            while (true) {
                receiveData = new byte[1024];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveData, receiveData.length);


                System.out.println("Esperando a algun cliente... ");
                // 4) Receive LLAMADA BLOQUEANTE
                serverSocket.receive(receivePacket);
                System.out.println("________________________________________________");
                System.out.println("Aceptamos un paquete");
                // Creamos un hilo para procesar este paquete
                UDPServerHilo hilo = new UDPServerHilo(receivePacket, serverSocket);
                hilosClientes.add(hilo);
                hilo.start(); // Ejecuta el hilo
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo abrir el puerto: " + puerto);
            System.exit(1); //detiene por completo la ejecucion
        }
    }
}
