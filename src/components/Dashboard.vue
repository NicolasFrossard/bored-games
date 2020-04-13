<template>
  <el-row class="dashboard">
    <el-row v-if="connectionEstablished">
      <el-dialog title="Game over!" :visible.sync="gameOverDialogVisible" width="60%">
        <el-row class="dashboard">
          <span>You lost at round <b>{{gameOverRoundAchieved}}</b>!</span>
        </el-row>
        <el-row class="dashboard">
          <span>You made it to the following rank:</span>
        </el-row>
        <el-row class="dashboard">
          <img src="../assets/gameOver/pathetic.jpg" width="50%" v-if="gameOverRoundAchieved <= 3">
          <img src="../assets/gameOver/not-bad.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 4">
          <img src="../assets/gameOver/good-job.jpg" width="33%" v-else-if="gameOverRoundAchieved <= 5">
          <img src="../assets/gameOver/amazing.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 6">
          <img src="../assets/gameOver/awesome.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 7">
        </el-row>
        <span slot="footer" class="dialog-footer">
          <el-button @click="gameOverDialogVisible = false">I acknowledge my failure as a player</el-button>
        </span>
      </el-dialog>
      <el-col :span="16">
        <div v-if="countdown > 0">
          <span id="countdown">{{countdown}}</span>
        </div>
        <div v-else>
          <game-admin v-if="gameState" :game-state="gameState" @onStartTheGame="startTheGame" @onStopTheGame="stopTheGame" @testSound="playSoundNewRound"></game-admin>
          <the-mind-game v-if="gameState && gameState.status === 'STARTED'" :game-state="gameState" :my-socket-id="mySocketId" @playCard="playCard"></the-mind-game>
        </div>
      </el-col>
      <el-col :span="8">
        <div>
          <player-board v-if="gameState" :game-state="gameState" :my-socket-id="mySocketId" @deletePlayer="deletePlayer"></player-board>
          <game-log :entries="logEntries"></game-log>
        </div>
      </el-col>
    </el-row>
    <el-row v-else-if="gameState">
      <el-row class="dashboard">
        <el-col :span="4" :offset="10">
          <el-input size="large" maxlength="8" placeholder="Who are you?" v-model="playerName">
            <el-button slot="append" :disabled="playerName.length === 0" @click="connect">Connect</el-button>
          </el-input>
        </el-col>
      </el-row>
      <el-row class="dashboard" v-if="gameState.status === 'STARTED'">
        A game has been started already. You can only join by using the name of a disconnected player.
      </el-row>
      <el-row class="dashboard" v-else>
        The game has not been started yet. Come and join!
      </el-row>
      <el-row class="dashboard">
        <el-col :span="8" :offset="8">
          <player-board v-if="gameState && gameState.players.length > 0" :show-delete-player="false" :game-state="gameState" :my-socket-id="mySocketId"></player-board>
        </el-col>
      </el-row>
    </el-row>
    <el-row v-else>
      <div class="dashboard">
        Before connecting to the server, please confirm that you are not a goose:
      </div>
      <div class="dashboard">
        <el-button @click="getGameState" type="primary">I am not a goose</el-button>
      </div>
      <div class="dashboard">
        <el-button @click="openGooseVideo">Actually, I am a goose</el-button>
      </div>
    </el-row>
  </el-row>
</template>

<script>
import GameLog from './GameLog'
import PlayerBoard from './PlayerBoard'
import GameAdmin from './GameAdmin'
import TheMindGame from './TheMindGame'

export default {
  components: {
    GameLog,
    PlayerBoard,
    GameAdmin,
    TheMindGame
  },
  name: 'Dashboard',
  mounted() {
    this.initSocket();
  },
  data () {
    return {
      countdown: 0,
      logEntries: [],
      playerName: '',
      connectionEstablished: false,
      gameState: undefined,
      mySocketId: '',
      gameOverDialogVisible: false,
      gameOverRoundAchieved: -1,
    }
  },
  methods: {
    initSocket: function () {
      console.log('Initializing socket')
      this.sockets.subscribe('gameLog', (gameLogInfo) => {
        this.logEntries.unshift(gameLogInfo);
        if(gameLogInfo.type === 'ERROR') {
          this.$message.error(gameLogInfo.text);
        } else if(gameLogInfo.type === 'WARN') {
          this.$message.warning(gameLogInfo.text);
        }
        this.highlightLatestLogEntry();
      });
      this.sockets.subscribe('serverWarning', (message) => {
        this.$message.warning(message);
      });
      this.sockets.subscribe('gameState', (gameState) => {
        this.gameState = gameState;
      });
      this.sockets.subscribe('connectionSuccessful', (socketId) => {
        this.connectionEstablished = true;
        this.mySocketId = socketId;
      });
      this.sockets.subscribe('gameLost', (round) => {
        this.playSoundLostGame();
        this.gameOverRoundAchieved = round;
        this.gameOverDialogVisible = true;
      });
      this.sockets.subscribe('errorMade', (cards) => {
        this.playSoundErrorMade();
        this.flipCards(cards);
      });
      this.sockets.subscribe('cardWellPlayed', (card) => {
        this.playSoundCardPlayed();
        this.highlightLastCardPlayed();
        this.flipCards([card]);
      });
      this.sockets.subscribe('newRound', (round) => {
        this.$message.info(`Starting round ${round}`);
        this.playSoundNewRound();
        this.triggerCountdown();
      });
      this.sockets.subscribe('newGameStarted', () => {
        this.playStartingGame();
        this.triggerCountdown();
      });
    },
    connect: function () {
      this.$socket.emit('connectWithPlayerName', this.playerName)
    },
    startTheGame: function () {
      this.$socket.emit('startTheGame')
    },
    stopTheGame: function () {
      this.$socket.emit('stopTheGame')
    },
    playCard: function (card) {
      this.$socket.emit('playCard', card, this.gameState.round)
    },
    deletePlayer: function (playerName) {
      this.$socket.emit('deletePlayer', playerName)
    },
    getGameState: function () {
      this.$socket.emit('getGameState')
    },
    triggerCountdown() {
      this.countdown = 3;
      const reduceCountdown = this.reduceCountdown;
      setTimeout(function () {
        reduceCountdown();
        setTimeout(function () {
          reduceCountdown();
          setTimeout(function () {
            reduceCountdown();
          }, 1000);
        }, 1000);
      }, 1000);
    },
    reduceCountdown() {
      if(this.countdown > 0) {
        this.countdown--;
      }
    },
    openGooseVideo: function () {
      window.open('https://youtu.be/AbE9VIQy5zQ?t=15')
    },
    playStartingGame () {
      this.playSound('http://soundbible.com/mp3/Boxing%20Bell%20Start%20Round-SoundBible.com-1691615580.mp3', 0.05)
    },
    playSoundNewRound () {
      this.playSound('http://soundbible.com/mp3/Ta%20Da-SoundBible.com-1884170640.mp3', 0.03)
    },
    playSoundLostGame () {
      this.playSound('http://soundbible.com/mp3/Sad_Trombone-Joe_Lamb-665429450.mp3', 0.05)
    },
    playSoundErrorMade () {
      this.playSound('http://soundbible.com/mp3/Computer%20Error-SoundBible.com-399240903.mp3', 0.05)
    },
    playSoundCardPlayed () {
      this.playSound('http://soundbible.com/mp3/Button_Press_2-Marianne_Gagnon-1415267358.mp3', 0.2)
    },
    playSound (sound, volume) {
      let audio = new Audio(sound);
      audio.volume = volume;
      audio.play();
    },
    highlightLastCardPlayed() {
      this.animateCSS("#last-card-played", "tada", "fast");
    },
    highlightLatestLogEntry() {
      this.animateCSS("#game-log-table > div.el-table__body-wrapper.is-scrolling-none > table > tbody > tr:nth-child(1)", "slideInLeft", "faster");
    },
    flipCards(cards) {
      cards.forEach(card => this.animateCSS(`#player-card-${card}`, "flip", "faster"));
    },
    animateCSS(element, animationName, option) {
      console.log(`animating element ${element}`)
      const node = document.querySelector(element)
      if(!node) {
        console.log(`Could not find element ${element}`)
        return;
      }

      node.classList.add('animated', animationName, option)
      function handleAnimationEnd() {
        node.classList.remove('animated', animationName)
        node.removeEventListener('animationend', handleAnimationEnd)
      }
      node.addEventListener('animationend', handleAnimationEnd)
    }
  }
}
</script>

<style>
  div.dashboard {
    margin-top: 15px;
  }
  #countdown {
    font-size: 200px;
    font-family: fantasy;
  }
</style>