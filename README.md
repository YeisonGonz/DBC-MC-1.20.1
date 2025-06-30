## DBC-MC-1.20.1

Solo disponible para **Fabric 1.20.1**.

Este mod permite crear un puente entre un servidor de Minecraft y un bot de Discord

## Requisitos

Tener un servidor de minecraft ejecutandose en la version 1.20.1, instalar el mod, y en la carpeta **config** del servidor
crear el fichero **discord_bridge.json**

Este fichero es que contiene configuracion para mandar las peticiones a la API

```json
{
  "ip": "192.168.1.20",
  "port": 5000,
  "endpoint":"/send",
  "announcement_endpoint": "/announ",
  "player_join_announcement": "false",
  "player_message_announcement": "true"
}
```
## Post

El mod manda un POST a la API del bot de Discord con la estructura de **username** y **content**
algo de este estilo ```POST -H "Content-Type: application/json" -d '{"username":"Steve", "content":"Test"}' http://192.168.1.1:5000/send```

## Announcements 

Ahora se ha implementado un mini sistema que manda un POST cuando un jugador entra o sale del servidor
se puedes deshabilitar en la configuracion poniendo **player_join_announcement: "false"**.

Ahora en tiempo de ejecucion puedes habilitar o deshabilitar los **announcements** con el comando ```/dbc```

Por ejemplo:
```/dbc message set false``` -> Deshabilitas mandar mensajes a Discord
```/dbc annoiun set true``` -> Habilitas mandar el mensaje de conexion a Discord