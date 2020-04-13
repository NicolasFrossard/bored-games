import {Namespace, Socket} from "socket.io";
import {LogEntry, LogEntryType, State} from "index";

export const broadcastInfo = (sockets: Namespace, message: String) => {
    broadcast(sockets, "INFO", message)
};

export const broadcastWarning = (sockets: Namespace, message: String) => {
    broadcast(sockets, "WARN", message)
};

export const sendWarning = (socket: Socket, message: String) => {
    socket.emit('serverWarning', message);
};

export const broadcastError = (sockets: Namespace, message: String) => {
    broadcast(sockets, "ERROR", message)
};

export const broadcast = (sockets: Namespace, type: LogEntryType, message: String) => {
    const newGameLogEntry: LogEntry = {
        type: type,
        text: message,
        date: new Date().toLocaleTimeString(),
    };
    sockets.emit('gameLog', newGameLogEntry);
};

export const broadcastState = (sockets: Namespace, gameState: State) => {
    sockets.emit('gameState', gameState);
};
