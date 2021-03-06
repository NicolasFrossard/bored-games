import {Socket} from "socket.io";
import {GameStatus, Player, State} from "index";

export class BaseGameState implements State {
    status: GameStatus;
    players: Player[];

    constructor(players: Player[]) {
        this.status = "TO_BE_STARTED";
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

    getPlayerWithSocketId(socketId: String) : Player | undefined {
        return this.players.find(player => player.socketId === socketId);
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
            cardsInHand: [],
            isAdmin: this.players.length === 0, // the very first player is admin
        };
        this.players.push(newPlayer);
    }

    deletePlayer(playerName: String) {
        let newPlayers = [];
        for (let i = 0; i < this.players.length; i++) {
            const player = this.players[i];
            if(player.name !== playerName) {
                newPlayers.push(player);
            }
        }
        this.players = newPlayers;
    }

    setIsConnected(socket: Socket, isConnected: boolean) : String {
        for (let i = 0; i < this.players.length; i++) {
            const player = this.players[i];
            if(player.socketId === socket.id) {
                this.players[i].connected = isConnected;
                return this.players[i].name;
            }
        }
        return undefined;
    }

    startTheGame() : void {
        this.status = "STARTED";
    }

    stopTheGame() : void {
        this.status = "TO_BE_STARTED";
    }

    isPlayerAdmin(socket: Socket) : boolean {
        const player = this.getPlayerWithSocketId(socket.id);
        return player?.isAdmin;
    }
}
