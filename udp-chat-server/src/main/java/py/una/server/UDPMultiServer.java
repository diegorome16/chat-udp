package py.una.server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDPMultiServer {

    List<UDPServerHilo> hilosClientes;
    HashMap<String, Integer> usuarios;

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
                System.out.println("DatoRecibido: " + receivePacket );
                // Datos recibidos e Identificamos quien nos envio
                String datoRecibido = new String(receivePacket.getData());
                String [] partes = datoRecibido.split(":");

                if (partes[0].equals("login")) {
                    //estos son los datos del cliente emisor
                    InetAddress IPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();

                    System.out.println("Se logueo " + partes[0] + partes[1]);
                    //añadimos a la lista de clientes
                    usuarios.put(partes[1].trim(),Integer.valueOf(port));
                    // Forma 1: usando entrySet
                    for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
                        System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
                    }

                    System.out.println("Se logueo " + usuarios.get(partes[1].trim()));

                    byte[] sendData = new byte[1024];
                    sendData = "Bienvenido al chat".getBytes();
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData, sendData.length, IPAddress, port);

                    serverSocket.send(sendPacket);
                } else {
                    // Forma 1: usando entrySet
                    for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
                        System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
                    }
                    UDPServerHilo hilo = new UDPServerHilo(receivePacket, serverSocket, usuarios);
                    hilosClientes.add(hilo);
                    hilo.start(); // Ejecuta el hilo
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo abrir el puerto: " + puerto);
            System.exit(1); //detiene por completo la ejecucion
        }
    }
}
