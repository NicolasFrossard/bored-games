import {Socket} from "socket.io";
import {GameStatus, Player, State} from "index";

export class BaseGameState implements State {
    status: GameStatus;
    round: number;
    players: Player[];
    constructor(players: Player[]) {
        this.status = "TO_BE_STARTED";
        this.round = 1;
        this.players = players;
    }

    isPlayerNameTaken(playerName: String) : boolean {
        return this.getPlayerWithName(playerName) !== undefined;
    }

    isPlayerDisconnected(playerName: String) : boolean {
        const player = this.getPlayerWithName(playerName);
        return player && !player.connected;
    }

    getPlayerWithName(playerName: String) : Player | undefined {
        return this.players.find(player => player.name === playerName);
    }

    updateDisconnectedPlayer(socket: Socket, playerName: String) : void {
        for (let i = 0; i < this.players.length; i++) {
            const player = this.players[i];
            if(player.name === playerName) {
                this.players[i].connected = true;
                this.players[i].socketId = socket.id;
                return;
            }
        }
    }

    addNewPlayer(socket: Socket, playerName: String) : void {
        console.log(`connect: socketId=${socket.id} and name=${playerName}. Right now we have ${this.players.length} players`);
        const newPlayer: Player = {
            socketId: socket.id,
            name: playerName,
            connected: true,
        };
        this.players.push(newPlayer);
    }

    disconnectPlayer(socket: Socket) : String {
        for (let i = 0; i < this.players.length; i++) {
            const player = this.players[i];
            if(player.socketId === socket.id) {
                this.players[i].connected = false;
                return this.players[i].name;
            }
        }
        return undefined;
    }

    startTheGame() : void {
        this.status = "STARTED";
    }
}
