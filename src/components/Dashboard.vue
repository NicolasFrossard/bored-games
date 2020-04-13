<template>
  <el-row class="dashboard">
    <el-row v-if="connectionEstablished">
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
          this.$message.warning(gameLogInfo.text);
        }
      });
      this.sockets.subscribe('serverWarning', (message) => {
        this.$message.warning(message);
      });
      this.sockets.subscribe('gameState', (gameState) => {
        console.log('Got game state')
        this.gameState = gameState;
      });
      this.sockets.subscribe('connectionSuccessful', (socketId) => {
        this.connectionEstablished = true;
        this.mySocketId = socketId;
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