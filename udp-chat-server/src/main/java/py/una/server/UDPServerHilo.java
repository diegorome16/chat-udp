package py.una.server;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;



public class UDPServerHilo extends Thread {

    private static final int MAX_PACKET_BYTES = 1024; // igual que el cliente
    private static final int MAX_MSG_LEN      = 280;

    private final DatagramPacket packet;
    private final DatagramSocket socket;
    private final HashMap<String, Integer> usuarios; // usuario -> puerto

    public UDPServerHilo(DatagramPacket packet, DatagramSocket socket, HashMap<String, Integer> usuarios) {
        super("UDPServerHilo");
        this.socket   = socket;
        this.packet   = packet;
        this.usuarios = usuarios;

        // Debug opcional
        for (Map.Entry<String, Integer> entry : usuarios.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
        }
    }

    @Override
    public void run() {
        try {
            // Decodificar SOLO los bytes útiles y en UTF-8 (evita basura del buffer)
            String datoRecibido = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("DatoRecibido: " + datoRecibido);

            // Datos del emisor (IP/puerto de donde vino el paquete)
            InetAddress ipEmisor = packet.getAddress();
            int puertoEmisor     = packet.getPort();
            System.out.println("De : " + ipEmisor + ":" + puertoEmisor);

            if (datoRecibido.isEmpty()) {
                responderError(packet, "[ERROR] Paquete vacío.");
                return;
            }

            // ===== NUEVO: manejo de /who =====
            if (datoRecibido.startsWith("who:")) {
                StringBuilder lista = new StringBuilder("Usuarios conectados: ");
                if (usuarios.isEmpty()) {
                    lista.append("(ninguno)");
                } else {
                    boolean first = true;
                    for (String u : usuarios.keySet()) {
                        if (!first) lista.append(", ");
                        lista.append(u);
                        first = false;
                    }
                }
                byte[] data = lista.toString().getBytes(StandardCharsets.UTF_8);
                DatagramPacket resp = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
                socket.send(resp);
                return; // no seguimos con el flujo normal
            }
            // ===== FIN NUEVO =====

            // ===== NUEVO: manejo de logout =====
            if (datoRecibido.startsWith("logout:")) {
                String usuarioLogout = datoRecibido.substring("logout:".length()).trim();
                usuarios.remove(usuarioLogout);
                System.out.println("Usuario desconectado: " + usuarioLogout);
                return;
            }
            // ===== FIN NUEVO =====

            // Intentar parsear el JSON a Mensaje
            Mensaje mensaje;
            try {
                mensaje = MensajeJSON.stringObjeto(datoRecibido);
            } catch (Exception ex) {
                responderError(packet, "[ERROR] JSON inválido.");
                return;
            }
            if (mensaje == null || mensaje.getUsuarioDestino() == null || mensaje.getMesaje() == null) {
                responderError(packet, "[ERROR] Faltan campos requeridos (usuario/mensaje).");
                return;
            }

            System.out.println("Para: " + mensaje.getUsuarioDestino() + "\nMensaje: " + mensaje.getMesaje());

            // El texto que envía el cliente viene como "DESTINATARIO: cuerpo"
            String texto = mensaje.getMesaje().trim();
            int idx = texto.indexOf(':'); // usamos el PRIMER ':' por si el cuerpo trae más
            if (idx <= 0 || idx == texto.length() - 1) {
                responderError(packet, "[ERROR] Formato inválido. Usa: DESTINATARIO: mensaje");
                return;
            }

            String destinatario = texto.substring(0, idx).trim();
            String cuerpo       = texto.substring(idx + 1).trim();

            if (destinatario.isEmpty() || cuerpo.isEmpty()) {
                responderError(packet, "[ERROR] Faltan destinatario o mensaje.");
                return;
            }
            if (cuerpo.length() > MAX_MSG_LEN) {
                responderError(packet, "[ERROR] Mensaje demasiado largo (máx " + MAX_MSG_LEN + ").");
                return;
            }

            // Puerto del destinatario (el mapa se llena con 'login:<usuario>' en otra parte del server)
            Integer puertoDestino = usuarios.get(destinatario);
            if (puertoDestino == null) {
                responderError(packet, "[ERROR] Usuario destino no conectado: " + destinatario);
                return;
            }

            // Formato que espera el cliente receptor: "mensaje|remitente"
            String remitenteUsuario = mensaje.getUsuarioDestino(); // en este modelo, 'usuarioDestino' es el remitente
            String payloadSalida    = cuerpo + "|" + remitenteUsuario;
            System.out.println(payloadSalida);

            byte[] sendData = payloadSalida.getBytes(StandardCharsets.UTF_8);
            if (sendData.length > MAX_PACKET_BYTES) {
                responderError(packet, "[ERROR] Respuesta demasiado grande para UDP.");
                return;
            }

            // Enviamos al MISMO host que envió el paquete (localhost en la práctica) pero al PUERTO del destinatario
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipEmisor, puertoDestino);
            socket.send(sendPacket);

        } catch (Exception e) {
            e.printStackTrace(); // log y continuar; no cerramos socket aquí
        }
    }

    /** Envía un texto de error al remitente del paquete original. */
    private void responderError(DatagramPacket remitente, String msg) {
        try {
            byte[] data = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket p = new DatagramPacket(data, data.length, remitente.getAddress(), remitente.getPort());
            socket.send(p);
        } catch (Exception ignored) {}
    }
}