package py.una.client;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;


public class UDPClientHilo extends Thread {

    private final DatagramSocket socket;
    private final String usuario;
    private final String server; // por compatibilidad
    

    private volatile boolean running = true;

    public UDPClientHilo(String usuario, DatagramSocket sockeClient, String server) {
        super("UDPCliente: " + usuario);
        this.socket = sockeClient;
        this.usuario = usuario;
        this.server = server;
    }

    /** Apaga el loop y cierra el socket para desbloquear receive(). */
    public void shutdown() {
        running = false;
        try { socket.close(); } catch (Exception ignored) {}
    }

    @Override
    public void run() {

        // Buffer de recepción (reutilizable)
        byte[] receiveData = new byte[1024];

        while (running) {
            try {
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveData, receiveData.length);

                // Receive BLOQUEANTE
                socket.receive(receivePacket);

                // Procesar en un hilo liviano para no bloquear la escucha
                new Thread(() -> {
                    try {
                        // Decodificar SOLO bytes útiles y en UTF-8
                        String datoRecibido = new String(
                                receivePacket.getData(),
                                0,
                                receivePacket.getLength(),
                                StandardCharsets.UTF_8
                        ).trim();

                        if (datoRecibido.isEmpty()) return;

                        String[] partes = datoRecibido.split("\\|", 2); // solo en el primer '|'
                        if (partes.length == 2) {
                            System.out.println(">>> " + partes[1].trim() + ": " + partes[0].trim());
                        } else {
                            // Texto plano (p.ej. [ERROR] ... o /who)
                            System.out.println(datoRecibido);
                        }

                    } catch (Exception e) {
                        System.out.println("[ERROR] Al procesar paquete: " + e.getMessage());
                    }
                }, "udp-client-handler-" + System.nanoTime()).start();

            } catch (SocketException se) {
                // Si fue cierre intencional, salimos silenciosamente
                if (!running) break;
                System.out.println("[ERROR] Escucha UDP: " + (se.getMessage() == null ? se : se.getMessage()));
                break;
            } catch (Exception e) {
                System.out.println("[ERROR] Escucha UDP: " + (e.getMessage() == null ? e : e.getMessage()));
                break;
            }
        }
    }
}
