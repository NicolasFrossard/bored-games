import {Namespace} from "socket.io";

export const broadcastInfo = (sockets: Namespace, message: String) => {
    broadcast(sockets, message, "INFO")
};

export const broadcastWarning = (sockets: Namespace, message: String) => {
    broadcast(sockets, message, "WARN")
};

export const broadcastError = (sockets: Namespace, message: String) => {
    broadcast(sockets, message, "ERROR")
};

export const broadcast = (sockets: Namespace, message: String, type: String) => {
    const newGameLogEntry: GameLogEntry = {
        type: type,
        text: message,
        date: new Date().toLocaleTimeString(),
    };
    sockets.emit('message', newGameLogEntry);
};

export const broadcastState = (sockets: Namespace, gameState: GameState) => {
    sockets.emit('gameState', gameState);
};
