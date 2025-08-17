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
            System.out.println("1  /clienteNormal");
            System.out.println("2  /SimuladorCliente");
            System.out.println();
            String line = sc.nextLine().trim();


            if (line.equals("2")) {
                Scanner scanner = new Scanner(System.in);

                // Pedir ppuerto del cliente al que le vamos a mandar varios mensajes de varios clientes
                System.out.print("Ingrese el puerto del cliente: ");
                puertoCliente = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer

                UDPClientSimulador clienteSimulador = new UDPClientSimulador(direccionServidor, puertoCliente);
            } else {

                Scanner scanner = new Scanner(System.in);

                // Pedir nombre de usuario
                System.out.print("Ingrese su nombre de usuario: ");
                String username = scanner.nextLine();

                // Pedir puerto del cliente (solo necesario si escuchará mensajes)
                System.out.print("Ingrese el puerto en el que desea escuchar: ");
                puertoCliente = scanner.nextInt();
                scanner.nextLine(); // limpiar buffer

                DatagramSocket clientSocket = new DatagramSocket(puertoCliente);

                UDPClientHilo clienteEscucha = new UDPClientHilo(username, clientSocket, direccionServidor);
                clienteEscucha.start();

                BufferedReader inFromUser =
                        new BufferedReader(new InputStreamReader(System.in));

                InetAddress IPAddress = InetAddress.getByName(direccionServidor);
                System.out.println("Intentando conectar a = " + IPAddress + ":" + puertoServidor + " via UDP...");

                while (true) {

                    byte[] sendData = new byte[1024];

                    InetAddress address = InetAddress.getByName(direccionServidor);

                    System.out.print("Inserte los datos para enviar un mensaje\nport: ");
                    String port = inFromUser.readLine();
                    int puerto = Integer.parseInt(port);

                    System.out.print("mensaje: ");
                    String msn = inFromUser.readLine();

                    Mensaje mensaje = new Mensaje(address, puerto, username, msn);

                    String datoPaquete = MensajeJSON.objetoString(mensaje);
                    sendData = datoPaquete.getBytes();

                    System.out.println("Enviar " + datoPaquete + " al servidor. (" + sendData.length + " bytes)");
                    DatagramPacket sendPacket =
                            new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);

                    clientSocket.send(sendPacket);
                /*    String[] parts = line.split("\\s+", 3);
                    if (parts.length >= 3) {
                        String destino = parts[1];
                        String texto = parts[2];
                        enviarMsg(destino, texto);
                    } else {
                        System.out.println("Uso: /msg <destino> <texto>");
                    }*/
                } /*else if (line.equals("/who")) {
                    enviarWho();
                } else if (line.equals("/exit")) {
                    close();
                } else {
                    System.out.println("Comando no reconocido");
                }*/
                //}
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
