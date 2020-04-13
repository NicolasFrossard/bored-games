import * as express from "express";
import {Socket} from "socket.io";
import {
    broadcastCardWellPlayed,
    broadcastError, broadcastErrorMade,
    broadcastGameLost,
    broadcastInfo, broadcastNewGameStarted, broadcastNewRound,
    broadcastState,
    broadcastWarning,
    sendGameState,
    sendWarning
} from "./broadcasting"
import {TheMindGameState} from "./theMindGameState";

let gameState = new TheMindGameState([]);

const app = express();
app.set("port", process.env.PORT || 3000);

let http = require("http").Server(app);
let io = require("socket.io")(http);

app.use(express.static("dist"));

io.on('connection', (socket: Socket) => {
    console.log('Socket connected:', socket.id);

    socket.on('getGameState', function () {
        sendGameState(socket, gameState);
    });

    socket.on('disconnect', function () {
        console.log('Socket disconnected: ', socket.id);
        let playerName = gameState.setIsConnected(socket, false);
        if (playerName) {
            broadcastError(io.sockets, `Player disconnected: ${playerName}`);
            broadcastState(io.sockets, gameState);
        }
    });

    socket.on('reconnect', function () {
        console.log('Socket reconnecting: ', socket.id);
        gameState.setIsConnected(socket, true);
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
            sendWarning(socket, `Rejected player ${playerName} as the game started already. Sorry, dude.`);
        }
    });

    socket.on('startTheGame', function () {
        if(!gameState.isPlayerAdmin(socket)) {
            sendWarning(socket, "Only the admin can do that. Please don't be such a goose");
            return;
        }

        console.log('Starting the game');
        gameState.startTheGame();
        gameState.moveToNextRound();
        broadcastInfo(io.sockets, 'Game has started!');
        broadcastInfo(io.sockets, 'Starting first round');
        broadcastNewGameStarted(io.sockets);
        broadcastState(io.sockets, gameState);
    });

    socket.on('stopTheGame', function () {
        if(!gameState.isPlayerAdmin(socket)) {
            sendWarning(socket, "Only the admin can do that. Please don't be such a goose");
            return;
        }

        console.log('Stopping the game');
        gameState.stopTheGame();
        broadcastInfo(io.sockets, 'Game was stopped!');
        broadcastState(io.sockets, gameState);
    });

    socket.on('playCard', function (card: number, round: number) {
        console.log(`Player ${socket.id} is playing the card ${card} in round ${round}`);

        if(gameState.round !== round) {
            console.warn(`Got a card for round ${round} but it's round ${gameState.round}. Aborting`);
            return;
        }

        if(this.cardsPlayed.includes(card)) {
            console.error(`The card ${card} was already played. This should not be possible. Aborting`);
            return;
        }

        const cardsInPlayerHandsThatAreBelow = gameState.playCard(card);

        const player = gameState.getPlayerWithSocketId(socket.id);
        broadcastInfo(io.sockets, `${player?.name} has played the card ${card}`);

        if(cardsInPlayerHandsThatAreBelow.length > 0) {
            console.log(`An error was made. Cards below are ${cardsInPlayerHandsThatAreBelow}`);
            broadcastErrorMade(io.sockets, cardsInPlayerHandsThatAreBelow);
            gameState.lives--;

            if(gameState.lives > 0) {
                broadcastInfo(io.sockets, `Losing one life. Lives remaining: ${gameState.lives}`);
            } else {
                broadcastGameLost(io.sockets, gameState.round);
                gameState.stopTheGame();
            }
        } else {
            broadcastCardWellPlayed(io.sockets, card);
        }

        if(gameState.isCurrentRoundFinished() && gameState.lives > 0) {
            broadcastInfo(io.sockets, `Round ${gameState.round} is over, congratulations!`);
            gameState.moveToNextRound();
            broadcastNewRound(io.sockets, gameState.round);

            if(gameState.regainOneLife()) {
                broadcastInfo(io.sockets, "You regained one ❤");
            }
        }

        broadcastState(io.sockets, gameState);
    });

    socket.on('deletePlayer', function (playerName: string) {
        if (!gameState.isPlayerAdmin(socket)) {
            sendWarning(socket, "Only the admin can do that. Please don't be such a goose");
            return;
        }
        gameState.deletePlayer(playerName);
        broadcastInfo(io.sockets, `Player ${playerName} has been deleted`);
        broadcastState(io.sockets, gameState);
    });
});

const server = http.listen(3000, function () {
    console.log("listening on *:3000");
});
