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
        <div>
          <game-admin v-if="gameState" :game-state="gameState" @onStartTheGame="startTheGame" @onStopTheGame="stopTheGame"></game-admin>
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
        this.logEntries.unshift(gameLogInfo)
        if(gameLogInfo.type === 'ERROR') {
          this.$message.error(gameLogInfo.text);
        } else if(gameLogInfo.type === 'WARN') {
            console.log("one waring")
          this.$message.warning(gameLogInfo.text);
        }
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
        this.gameOverRoundAchieved = round;
        this.gameOverDialogVisible = true;
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
      this.$socket.emit('playCard', card)
    },
    deletePlayer: function (playerName) {
      this.$socket.emit('deletePlayer', playerName)
    },
    getGameState: function () {
      this.$socket.emit('getGameState')
    },
    openGooseVideo: function () {
      window.open('https://youtu.be/AbE9VIQy5zQ?t=15')
    },
  }
}
</script>

<style>
  div.dashboard {
    margin-top: 15px;
  }
  .bored-game-sidebar-cell-style {
    padding-top: 5px !important;
    padding-bottom: 5px !important;
  }
</style>