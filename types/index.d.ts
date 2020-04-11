import {Socket} from "socket.io";

interface GameState {
    players: Player[];
}

interface Player {
    socket: Socket;
    name: String;
}
