import {Namespace} from "socket.io";
import {LogEntry, LogEntryType, State} from "index";

export const broadcastInfo = (sockets: Namespace, message: String) => {
    broadcast(sockets, "INFO", message)
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
