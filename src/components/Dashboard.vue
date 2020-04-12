<template>
  <el-row class="dashboard">
    <el-row gutter="2" v-if="connectionEstablished">
      <el-col :span="16">
        <div>
          <game-admin :game-state="gameState" @onStartTheGame="startTheGame"></game-admin>
        </div>
      </el-col>
      <el-col :span="8">
        <div>
          <player-board v-if="gameState" :players="gameState.players"></player-board>
          <game-log :entries="logEntries"></game-log>
        </div>
      </el-col>
    </el-row>
    <el-row v-else>
      <el-col :span="4" :offset="10">
        <el-input size="large" maxlength="8" placeholder="Who are you?" v-model="playerName">
          <el-button slot="append" :disabled="playerName.length === 0" @click="connect">Connect</el-button>
        </el-input>
      </el-col>
    </el-row>
  </el-row>
</template>

<script>
import GameLog from './GameLog'
import PlayerBoard from './PlayerBoard'
import GameAdmin from './GameAdmin'

export default {
  components: {
    GameLog,
    PlayerBoard,
    GameAdmin
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
    }
  },
  methods: {
    initSocket: function () {
      this.sockets.subscribe('gameLog', (gameLogInfo) => {
        this.logEntries.unshift(gameLogInfo)
        if(gameLogInfo.type === 'ERROR') {
          this.$message.error(gameLogInfo.text);
        } else if(gameLogInfo.type === 'WARN') {
          this.$message.warning(gameLogInfo.text);
        }
      });
      this.sockets.subscribe('gameState', (gameState) => {
        this.gameState = gameState;
      });
      this.sockets.subscribe('connectionSuccessful', () => {
        this.connectionEstablished = true;
      });
    },
    connect: function () {
      this.$socket.emit('connectWithPlayerName', this.playerName)
    },
    startTheGame: function () {
      this.$socket.emit('startTheGame')
    },
  }
}
</script>

<style>
  div.dashboard {
    margin-top: 10px;
  }
</style>