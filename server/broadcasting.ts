import {Namespace, Socket} from "socket.io";
import {LogEntry, LogEntryType, State} from "index";

export const broadcastInfo = (sockets: Namespace, message: String) => {
    broadcast(sockets, "INFO", message);
};

export const broadcastWarning = (sockets: Namespace, message: String) => {
    broadcast(sockets, "WARN", message);
};

export const sendWarning = (socket: Socket, message: String) => {
    socket.emit('serverWarning', message);
};

export const broadcastError = (sockets: Namespace, message: String) => {
    broadcast(sockets, "ERROR", message);
};

export const broadcastCardWellPlayed = (sockets: Namespace, card: number) => {
    sockets.emit("cardWellPlayed", card);
};

export const broadcastNewRound = (sockets: Namespace, round: number) => {
    sockets.emit("newRound", round);
};

export const broadcastErrorMade = (sockets: Namespace, cardsForcedToPlay: number[]) => {
    broadcastWarning(sockets, `Dang! The following cards, still held by players, were played: ${cardsForcedToPlay}`);
    sockets.emit("errorMade", cardsForcedToPlay);
};

export const broadcastGameLost = (sockets: Namespace, round: number) => {
    sockets.emit("gameLost", round);
};

export const broadcastNewGameStarted = (sockets: Namespace) => {
    sockets.emit("newGameStarted");
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
    console.log(`Broadcasting state ${gameState}`);
    sockets.emit('gameState', gameState);
};

export const sendGameState = (socket: Socket, gameState: State) => {
    socket.emit('gameState', gameState);
};