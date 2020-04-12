import {Namespace} from "socket.io";
import {GameLogEntry} from "index";

export const broadcastInfo = (sockets: Namespace, message: String) => {
    const newGameLogEntry: GameLogEntry = {
      type: "INFO",
      text: message,
      date: new Date().toLocaleTimeString(),
    };
    sockets.emit('message', newGameLogEntry);
};
