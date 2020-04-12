export interface State {
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
