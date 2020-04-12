declare module Game {
    interface State {
        players: Player[];
    }

    interface Player {
        socketId: String;
        name: String;
        connected: boolean;
    }

    interface LogEntry {
        type: String;
        text: String;
        date: String;
    }
}