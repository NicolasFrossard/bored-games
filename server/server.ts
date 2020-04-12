import * as express from "express";
import {Socket} from "socket.io";
import {broadcast, broadcastState} from "./broacasting"
import {BaseGameState} from "./baseGameState";

let gameState = new BaseGameState([]);

const app = express();
app.set("port", process.env.PORT || 3000);

let http = require("http").Server(app);
let io = require("socket.io")(http);

app.use(express.static("dist"));

io.on('connection', (socket: Socket) => {
    console.log('Socket connected:', socket.id);

    socket.on('disconnect', function() {
        console.log('Socket disconnected: ', socket.id);
        let playerName = gameState.disconnectPlayer(socket);
        if(playerName) {
            broadcast(io.sockets, "ERROR", `Player disconnected: ${playerName}`);
            broadcastState(io.sockets, gameState);
        }
    });

    socket.on("connectWithPlayerName", function(playerName: String) {
        gameState.addNewPlayer(socket, playerName);
        socket.emit('connectionSuccessful');
        broadcast(io.sockets, "INFO", `New player connected: ${playerName}`);
        broadcastState(io.sockets, gameState);
    });
});

// setInterval(() => {
//     io.sockets.emit('state', gameState);
// }, 1000 / 60);

const server = http.listen(3000, function() {
    console.log("listening on *:3000");
});
