package py.una.server;

import java.net.InetAddress;

public class Mensaje {

    private InetAddress ipDestino;
    private int port;
    private String usuarioDestino;
    private String mesaje;

    public Mensaje(){}

    public Mensaje(InetAddress ipDestino, int port, String usuarioDestino, String mesaje){
        this.ipDestino = ipDestino;
        this.port = port;
        this.usuarioDestino = usuarioDestino;
        this.mesaje = mesaje;
    }

    public InetAddress getIpDestino() {
        return ipDestino;
    }

    public void setIpDestino(InetAddress ipDestino) {
        this.ipDestino = ipDestino;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(String usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }

    public String getMesaje() {
        return mesaje;
    }

    public void setMesaje(String mesaje) {
        this.mesaje = mesaje;
    }
}
