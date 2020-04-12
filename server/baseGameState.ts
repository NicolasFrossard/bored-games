import {Socket} from "socket.io";

export class BaseGameState implements GameState {
    players: Player[];
    constructor(players: Player[]) {
        this.players = players;
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
}
