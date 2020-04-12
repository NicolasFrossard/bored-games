import {Namespace} from "socket.io";

export const broadcast = (sockets: Namespace, type: String, message: String) => {
    const newGameLogEntry: Game.LogEntry = {
        type: type,
        text: message,
        date: new Date().toLocaleTimeString(),
    };
    sockets.emit('gameLog', newGameLogEntry);
};

export const broadcastState = (sockets: Namespace, gameState: Game.State) => {
    sockets.emit('gameState', gameState);
};
