import * as express from "express";
import {Socket} from "socket.io";
import {broadcastInfo} from "./gameLogUtil"
import {GameState, Player} from "index";

let gameState: GameState = {
    players: []
};

const app = express();
app.set("port", process.env.PORT || 3000);

let http = require("http").Server(app);
// set up socket.io and bind it to our
// http server.
let io = require("socket.io")(http);

app.use(express.static("dist"));

io.on('connection', (socket: Socket) => {
    console.log('a user connected:', socket.id);

    socket.on('disconnect', function() {
        console.log('user disconnected: ', socket.id);
        //delete gameState.players[socket.id]
    });

    socket.on("message", function(message: String) {
        console.log(`From the socket ${socket.id} I received ${message}`);
    });

    socket.on("connectWithPlayerName", function(playerName: String) {
        console.log(`connect: socketId=${socket.id} and name=${playerName}. Right now we have ${gameState.players.length} players`);
        const newPlayer: Player = {
            socket: socket,
            name: playerName,
        };
        gameState.players.push(newPlayer);
        socket.emit('connectionSuccessful');
        broadcastInfo(io.sockets, `New player connected: ${playerName}`);
    });
});

// setInterval(() => {
//     io.sockets.emit('state', gameState);
// }, 1000 / 60);

const server = http.listen(3000, function() {
    console.log("listening on *:3000");
});
