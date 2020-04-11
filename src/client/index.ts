// @ts-ignore
const socket : any = io();

socket.on("message", function(data: any) {
    console.log('received from server: ' + data);
});

function sendMsg() {
    socket.emit("message", "HELLO WORLD");
}

const canvas: any = document.getElementById('myCanvas');
const ctx = canvas.getContext('2d');
socket.emit('newPlayer');

const drawPlayer = (player: any) => {
    ctx.beginPath();
    ctx.rect(player.x, player.y, player.width, player.height);
    ctx.fillStyle = '#0095DD';
    ctx.fill();
    ctx.closePath();
};

socket.on('state', (gameState: any) => {
    for (let player in gameState.players) {
        drawPlayer(gameState.players[player])
    }
});

const playerMovement = {
    up: false,
    down: false,
    left: false,
    right: false
};
const keyDownHandler = (e: any) => {
    if (e.keyCode === 39) {
        playerMovement.right = true;
    } else if (e.keyCode === 37) {
        playerMovement.left = true;
    } else if (e.keyCode === 38) {
        playerMovement.up = true;
    } else if (e.keyCode === 40) {
        playerMovement.down = true;
    }
};
const keyUpHandler = (e: any) => {
    if (e.keyCode === 39) {
        playerMovement.right = false;
    } else if (e.keyCode === 37) {
        playerMovement.left = false;
    } else if (e.keyCode === 38) {
        playerMovement.up = false;
    } else if (e.keyCode === 40) {
        playerMovement.down = false;
    }
};

setInterval(() => {
    socket.emit('playerMovement', playerMovement);
}, 1000 / 60);
document.addEventListener('keydown', keyDownHandler, false);
document.addEventListener('keyup', keyUpHandler, false);