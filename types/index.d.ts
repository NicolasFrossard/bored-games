import {Socket} from "socket.io";

interface GameState {
    players: Player[];
}

interface Player {
    socket: Socket;
    name: String;
}

interface GameLogEntry {
    type: String;
    text: String;
    date: String;
}