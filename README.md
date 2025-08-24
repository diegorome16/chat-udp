# Chat UDP

## Descripción
Este proyecto implementa un chat basado en **UDP** en Java, compuesto por un **servidor** y múltiples **clientes**.  
- El **servidor** escucha conexiones de clientes y mantiene un registro de usuarios con sus respectivos puertos.  
- Los **clientes** se conectan indicando un nombre de usuario y pueden enviar mensajes a otros clientes usando el formato:
>destinatario: mensaje
- El servidor recibe los mensajes y los reenvía al destinatario correspondiente, permitiendo la comunicación en tiempo real.

---

## Requisitos
- Java 17 (JDK)
- Apache Maven
- Sistema operativo: Windows / Linux / macOS

---

## Instalación y compilación
1. **Clonar el repositorio**
```bash
git clone <URL_DEL_REPOSITORIO>
cd chat-udp
```

2. **Compilar el proyecto con Maven**
```bash
mvn clean package
```
- Esto generará los archivos .jar dentro de las carpetas udp-chat-server/target y udp-chat-client/target.
<img width="943" height="867" alt="image" src="https://github.com/user-attachments/assets/d98393f4-922a-4f7d-83c0-7546c6492803" />

---

3. **Ejecución**

***Servidor***

En una terminal:
```bash
cd udp-chat-server/target
java -jar udp-chat-server-1.0-SNAPSHOT-jar-with-dependencies.jar
```

***Cliente***

En otra terminal (pueden abrir varias para distintos clientes):
```bash
cd udp-chat-client/target
java -jar udp-chat-client-1.0-SNAPSHOT-jar-with-dependencies.jar
```

4. **Opciones del cliente**  

Al iniciar, el cliente muestra dos opciones:  
- `/cliente-chat` → iniciar el chat interactivo.  
- `/SimuladorCliente-envia-mensajes` → ejecutar simulaciones automáticas de envío de mensajes.
<img width="752" height="274" alt="image" src="https://github.com/user-attachments/assets/f32cd679-1204-4291-83da-dae2309f4e91" />







