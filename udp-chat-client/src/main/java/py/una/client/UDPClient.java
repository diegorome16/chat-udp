package py.una.client;

import py.una.server.Mensaje;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String a[]) throws Exception {
        // Datos necesario
        String direccionServidor = "127.0.0.1";
        int puertoServidor = 4444;
        int puertoCliente = 5000;

        if (a.length > 0) {
            puertoCliente = Integer.parseInt(a[0]);
        }

        try {

            Scanner sc = new Scanner(System.in);
            System.out.println("Comandos:");
            System.out.println("1  /cliente-chat");
            System.out.println("2  /SimuladorCliente-envia-mensajes");
            System.out.println();
            String line = sc.nextLine().trim();


            if (line.equals("2")) {
                Scanner scanner = new Scanner(System.in);

                // Pedir ppuerto del cliente al que le vamos a mandar varios mensajes de varios clientes
                System.out.print("Ingrese el nombre del cliente: ");
                String cliente = scanner.nextLine();

                UDPClientSimulador clienteSimulador = new UDPClientSimulador(direccionServidor, cliente);
            } else {

                Scanner scanner = new Scanner(System.in);

                // Pedir nombre de usuario
                System.out.print("Ingrese su nombre de usuario: ");
                String username = scanner.nextLine();

                DatagramSocket clientSocket = new DatagramSocket();

                UDPClientHilo clienteEscucha = new UDPClientHilo(username, clientSocket, direccionServidor);
                clienteEscucha.start();

                //enviaremos un primer mensaje para el login, enviando nuestro usuario, asi el servidor
                // nos registra
                InetAddress IPAddress = InetAddress.getByName(direccionServidor);
                byte[] sendLogin = new byte[1024];
                String login = "login:" + username;
                sendLogin = login.getBytes();

                DatagramPacket sendPacketLogin =
                        new DatagramPacket(sendLogin, sendLogin.length, IPAddress, puertoServidor);

                clientSocket.send(sendPacketLogin);

                BufferedReader inFromUser =
                        new BufferedReader(new InputStreamReader(System.in));

                System.out.println("-----------------------------------------------------------");
                System.out.print("\nInserte los datos para enviar un mensaje\nusuario: mensaje\n");
                System.out.println("-----------------------------------------------------------");
                while (true) {

                    byte[] sendData = new byte[1024];

                    InetAddress address = InetAddress.getByName(direccionServidor);

                    String msn = inFromUser.readLine();

                    //de momento le pongo puerto cero, pero debo cambiar la logica
                    Mensaje mensaje = new Mensaje(address, 0, username, msn);

                    String datoPaquete = MensajeJSON.objetoString(mensaje);
                    sendData = datoPaquete.getBytes();

                    //System.out.println("Enviar " + datoPaquete + " al servidor. (" + sendData.length + " bytes)");
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);

                    clientSocket.send(sendPacket);
                }
            }
        } catch (
                UnknownHostException ex) {
            System.err.println(ex);
        } catch (
                IOException ex) {
            System.err.println(ex);
        }
    }
}
