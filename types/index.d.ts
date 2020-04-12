export interface State {
    status: GameStatus;
    players: Player[];
}

export interface TheMindGameState extends State {
    round: number;
    cardsPlayed: number[];
}

export interface Player {
    socketId: String;
    name: String;
    connected: boolean;
    cardsInHand: number[];
}

export interface LogEntry {
    type: LogEntryType;
    text: String;
    date: String;
}

export type LogEntryType = "INFO" | "WARN" | "ERROR"

export type GameStatus = "TO_BE_STARTED" | "STARTED"