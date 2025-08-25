package py.una.client;

import py.una.server.Mensaje;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;




public class UDPClient {

    // Config
    private static final String SERVER_IP        = "127.0.0.1";
    private static final int    SERVER_PORT      = 4444;
    private static final int    MAX_PACKET_BYTES = 1024; // igual que el buffer del server
    private static final int    MAX_MSG_LEN      = 280;  // límite razonable

    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Menú
            System.out.println("Comandos:");
            System.out.println("1  /cliente-chat");
            System.out.println("2  /SimuladorCliente-envia-mensajes");
            System.out.println();
            String option = safeReadLine(in);

            if ("2".equals(option)) {
                // === Simulador ===
                System.out.print("Ingrese el nombre del cliente: ");
                String cliente = safeReadLine(in);
                if (cliente == null || cliente.trim().isEmpty()) {
                    System.out.println("[ERROR] El nombre no puede estar vacío.");
                    return;
                }
                new UDPClientSimulador(SERVER_IP, cliente.trim());
                return;
            }

            // === Cliente interactivo ===
            String username = pedirUsuario(in);
            if (username == null) {
                System.out.println("Saliendo...");
                return;
            }

            DatagramSocket clientSocket = new DatagramSocket();
            UDPClientHilo clienteEscucha = null;

            try {
                // Hilo de escucha
                clienteEscucha = new UDPClientHilo(username, clientSocket, SERVER_IP);
                clienteEscucha.start();

                // Login
                InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
                String login = "login:" + username;
                byte[] loginBytes = login.getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(loginBytes, loginBytes.length, serverAddress, SERVER_PORT));

                // Loop de envío
                printHelp();
                System.out.println("-----------------------------------------------------------");
                System.out.print("\nInserte los datos para enviar un mensaje\nusuario: mensaje\n");
                System.out.println("-----------------------------------------------------------");

                while (true) {
                    System.out.print("> ");
                    String linea = safeReadLine(in);
                    if (linea == null) { // EOF
                        System.out.println("Saliendo...");
                        break;
                    }

                    String t = linea.trim();

                    // ---- Comandos ----
                    if (t.equalsIgnoreCase("/exit")) {
                        // (opcional) logout
                        String logout = "logout:" + username;
                        byte[] out = logout.getBytes(StandardCharsets.UTF_8);
                        clientSocket.send(new DatagramPacket(out, out.length, serverAddress, SERVER_PORT));

                        // Apagar escucha ordenadamente (evita "Socket closed" en loop)
                        if (clienteEscucha != null) {
                            clienteEscucha.shutdown();
                            try { clienteEscucha.join(300); } catch (InterruptedException ignored) {}
                        }

                        System.out.println("Saliendo...");
                        break;
                    }

                    if (t.equalsIgnoreCase("/help")) {
                        printHelp();
                        continue;
                    }

                    if (t.equalsIgnoreCase("/who")) {
                        String whoReq = "who:" + username;
                        byte[] data = whoReq.getBytes(StandardCharsets.UTF_8);
                        clientSocket.send(new DatagramPacket(data, data.length, serverAddress, SERVER_PORT));
                        continue;
                    }

                    if (t.isEmpty()) {
                        System.out.println("[ERROR] Línea vacía. Usa: DESTINATARIO: mensaje");
                        continue;
                    }

                    // ---- Validación DEST: MSG ----
                    int idx = t.indexOf(':');
                    if (idx <= 0 || idx == t.length() - 1) {
                        System.out.println("[ERROR] Formato inválido. Usa: DESTINATARIO: mensaje");
                        System.out.println("Ejemplo: Angel: Hola, ¿cómo estás?");
                        continue;
                    }

                    String destinatario = t.substring(0, idx).trim();
                    String body         = t.substring(idx + 1).trim();

                    if (destinatario.isEmpty() || body.isEmpty()) {
                        System.out.println("[ERROR] Faltan destinatario o mensaje.");
                        continue;
                    }
                    if (body.length() > MAX_MSG_LEN) {
                        System.out.println("[ERROR] Mensaje demasiado largo (máx " + MAX_MSG_LEN + ").");
                        continue;
                    }

                    // Construcción del JSON del proyecto
                    Mensaje mensaje = new Mensaje(
                            InetAddress.getByName(SERVER_IP),
                            0,                       // el server no usa este puerto
                            username,                // remitente
                            destinatario + ": " + body
                    );

                    String json = MensajeJSON.objetoString(mensaje);
                    byte[] sendData = json.getBytes(StandardCharsets.UTF_8);

                    if (sendData.length > MAX_PACKET_BYTES) {
                        System.out.println("[ERROR] El paquete a enviar supera el tamaño permitido (" + MAX_PACKET_BYTES + " bytes).");
                        continue;
                    }

                    clientSocket.send(new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT));
                }

            } finally {
                try { clientSocket.close(); } catch (Exception ignored) {}
            }

        } catch (UnknownHostException ex) {
            System.err.println("[ERROR] Host inválido: " + ex.getMessage());
        } catch (SocketException ex) {
            System.err.println("[ERROR] Socket: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("[ERROR] I/O: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("[ERROR] Inesperado: " + ex.getMessage());
        }
    }

    // ===== helpers =====

    private static String safeReadLine(BufferedReader in) throws IOException {
        return in.readLine();
    }

    private static void printHelp() {
        System.out.println("---------------------------------------------------");
        System.out.println("Comandos: /help, /who, /exit");
        System.out.println("Formato de mensaje: DESTINATARIO: mensaje");
        System.out.println("Ejemplo:   Angel: Hola, ¿cómo estás?");
        System.out.println("----------------------------------------------------");
    }

    private static String pedirUsuario(BufferedReader in) throws IOException {
        System.out.println("=== Cliente UDP-Chat ===");
        printHelp();
        while (true) {
            System.out.print("Ingrese su nombre de usuario: ");
            String u = safeReadLine(in);
            if (u == null) return null; // EOF
            u = u.trim();
            if (u.equalsIgnoreCase("/exit")) return null;
            if (u.isEmpty()) {
                System.out.println("[ERROR] El usuario no puede estar vacío.");
                continue;
            }
            if (u.contains(":")) {
                System.out.println("[ERROR] El usuario no puede contener ':'.");
                continue;
            }
            return u;
        }
    }
}