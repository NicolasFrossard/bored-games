import * as express from "express";
import * as socketio from "socket.io";
import * as path from "path";
const gameState: any = {
    players: {}
};

const app = express();
app.set("port", process.env.PORT || 3000);

app.get("/index.js", (req: any, res: any) => {
    res.sendFile(path.resolve("./dist/client/index.js"));
});

let http = require("http").Server(app);
// set up socket.io and bind it to our
// http server.
let io = require("socket.io")(http);

app.use(express.static("public"));

io.on('connection', (socket: any) => {
    console.log('a user connected:', socket.id);

    socket.on('disconnect', function() {
        console.log('user disconnected: ', socket.id);
        delete gameState.players[socket.id]
    });

    socket.on("message", function(message: any) {
        console.log(message);
        socket.emit("message", "I got this from you: " + message);
    });

    socket.on('newPlayer', () => {
        console.log('we have a new player: ', socket.id);
        gameState.players[socket.id] = {
            x: Math.floor(Math.random()*600),
            y: Math.floor(Math.random()*480),
            width: 25,
            height: 25
        }
    });

    socket.on('playerMovement', (playerMovement: any) => {
        const player = gameState.players[socket.id];
        const canvasWidth = 680;
        const canvasHeight = 520;

        if (playerMovement.left && player.x > 0) {
            player.x -= 4
        }
        if (playerMovement.right && player.x < canvasWidth - player.width) {
            player.x += 4
        }

        if (playerMovement.up && player.y > 0) {
            player.y -= 4
        }
        if (playerMovement.down && player.y < canvasHeight - player.height) {
            player.y += 4
        }
    })
});

setInterval(() => {
    io.sockets.emit('state', gameState);
}, 1000 / 60);

const server = http.listen(3000, function() {
    console.log("listening on *:3000");
});