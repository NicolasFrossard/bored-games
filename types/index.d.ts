export interface State {
    status: GameStatus;
    round: number;
    players: Player[];
}

export interface Player {
    socketId: String;
    name: String;
    connected: boolean;
}

export interface LogEntry {
    type: LogEntryType;
    text: String;
    date: String;
}

export type LogEntryType = "INFO" | "WARN" | "ERROR"

export type GameStatus = "TO_BE_STARTED" | "STARTED"