package py.una.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        UDPMultiServer tms = new UDPMultiServer();

        tms.hilosClientes = new ArrayList<UDPServerHilo>();
        tms.usuarios = new HashMap<>();

        tms.ejecutarServer();
    }
}