interface GameState {
    players: Player[];
}

interface Player {
    socketId: String;
    name: String;
    connected: boolean;
}

interface GameLogEntry {
    type: String;
    text: String;
    date: String;
}