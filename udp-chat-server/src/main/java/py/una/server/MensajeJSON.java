package py.una.server;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetAddress;

public class MensajeJSON {

    public static Mensaje stringObjeto(String datos) throws ParseException {
        Mensaje mensaje = new Mensaje();

        JSONParser parser = new JSONParser();

        Object obj = parser.parse(datos.trim());
        JSONObject jsonObject = (JSONObject) obj;

        InetAddress ip = null;
        mensaje.setIpDestino(ip);
        mensaje.setPort(Integer.parseInt(jsonObject.get("port").toString()));
        mensaje.setUsuarioDestino((String)jsonObject.get("usuario"));
        mensaje.setMesaje((String) jsonObject.get("mensaje"));

        return mensaje;
    }
}