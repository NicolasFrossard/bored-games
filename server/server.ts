import * as express from "express";
import {Socket} from "socket.io";
import {broadcastError, broadcastInfo, broadcastState, broadcastWarning} from "./broacasting"
import {TheMindGameState} from "./theMindGameState";

let gameState = new TheMindGameState([]);

const app = express();
app.set("port", process.env.PORT || 3000);

let http = require("http").Server(app);
let io = require("socket.io")(http);

app.use(express.static("dist"));

io.on('connection', (socket: Socket) => {
    console.log('Socket connected:', socket.id);

    socket.on('disconnect', function () {
        console.log('Socket disconnected: ', socket.id);
        let playerName = gameState.disconnectPlayer(socket);
        if (playerName) {
            broadcastError(io.sockets, `Player disconnected: ${playerName}`);
            broadcastState(io.sockets, gameState);
        }
    });

    socket.on("connectWithPlayerName", function (playerName: String) {
        if (gameState.isPlayerNameTaken(playerName)) {
            if (gameState.isPlayerDisconnected(playerName)) {
                gameState.updateDisconnectedPlayer(socket, playerName);
                socket.emit('connectionSuccessful', socket.id);
                broadcastInfo(io.sockets, `Player came back: ${playerName}`);
                broadcastState(io.sockets, gameState);
            }
        } else if(gameState.status === "TO_BE_STARTED") {
            gameState.addNewPlayer(socket, playerName);
            socket.emit('connectionSuccessful', socket.id);
            broadcastInfo(io.sockets, `New player connected: ${playerName}`);
            broadcastState(io.sockets, gameState);
        } else {
            broadcastWarning(io.sockets, `Rejected player ${playerName} as the game started already. Sorry, dude.`);
        }
    });

    socket.on('startTheGame', function () {
        console.log('Starting the game');
        gameState.startTheGame();
        gameState.moveToNextRound();
        broadcastInfo(io.sockets, 'Game has started!');
        broadcastInfo(io.sockets, 'Starting first round');
        broadcastState(io.sockets, gameState);
    });

    socket.on('stopTheGame', function () {
        console.log('Stopping the game');
        gameState.stopTheGame();
        broadcastInfo(io.sockets, 'Game was stopped!');
        broadcastState(io.sockets, gameState);
    });

    socket.on('playCard', function (card: number) {
        console.log(`Player ${socket.id} is playing the card ${card}`);
        gameState.playCard(card);

        const player = gameState.getPlayerWithSocketId(socket.id);
        broadcastInfo(io.sockets, `${player?.name} has played the card ${card}`);

        if(gameState.isCurrentRoundFinished()) {
            broadcastInfo(io.sockets, `Round ${gameState.round} is over, congratulations!`);
            gameState.moveToNextRound();
            broadcastInfo(io.sockets, `Starting round ${gameState.round}`);
        } else {
            console.log('Round is not done yet')
        }

        broadcastState(io.sockets, gameState);
    });
});

// setInterval(() => {
//     io.sockets.emit('state', gameState);
// }, 1000 / 60);

const server = http.listen(3000, function () {
    console.log("listening on *:3000");
});
