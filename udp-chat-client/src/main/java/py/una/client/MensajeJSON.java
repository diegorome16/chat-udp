package py.una.client;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import py.una.server.Mensaje;

import java.net.InetAddress;

public class MensajeJSON {

    public static Mensaje stringObjeto(String datos) throws ParseException {
        Mensaje mensaje = new Mensaje();

        JSONParser parser = new JSONParser();

        Object obj = parser.parse(datos.trim());
        JSONObject jsonObject = (JSONObject) obj;

        InetAddress ip = (InetAddress) jsonObject.get("ip");
        mensaje.setIpDestino(ip);
        mensaje.setPort((int)jsonObject.get("port"));
        mensaje.setUsuarioDestino((String)jsonObject.get("usuario"));
        mensaje.setMesaje((String) jsonObject.get("mensaje"));

        return mensaje;
    }

    public static String objetoString(Mensaje m) {

        JSONObject obj = new JSONObject();
        obj.put("ip", m.getIpDestino().toString());
        obj.put("port", m.getPort());
        obj.put("usuario", m.getUsuarioDestino());
        obj.put("mensaje", m.getMesaje());

        return obj.toJSONString();
    }
}