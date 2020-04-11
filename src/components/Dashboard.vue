<template>
  <el-row class="dashboard">
    <el-row gutter="2" v-if="connectionEstablished">
      <el-col :span="14">
        <div class="grid-content bg-purple-dark">a</div>
      </el-col>
      <el-col :span="10">
        <el-button @click="sendMsg" type="primary">Send a message</el-button>
        <div class="grid-content bg-purple-dark">
          <game-log :entries="logEntries"></game-log>
        </div>
      </el-col>
    </el-row>
    <el-row v-else>
      <el-col :span="4" :offset="10">
        <el-row>
          <el-input size="large" placeholder="Who are you?" v-model="playerName">
            <el-button slot="append" :disabled="playerName.length === 0" @click="connect">Connect</el-button>
          </el-input>
        </el-row>
      </el-col>
    </el-row>
  </el-row>
</template>

<script>
import GameLog from './GameLog'

export default {
  components: {GameLog},
  name: 'Dashboard',
  mounted() {
    this.initSocket();
    this.addLogEntry("Started")
  },
  data () {
    return {
      logEntries: [],
      playerName: '',
      connectionEstablished: false,
    }
  },
  methods: {
    sendMsg: function () {
      this.$socket.emit('message', "Helloooo")
    },
    initSocket: function () {
      this.sockets.subscribe('message', (message) => {
        this.addLogEntry(message)
      });
      this.sockets.subscribe('connectionSuccessful', () => {
        this.connectionEstablished = true;
      });
    },
    addLogEntry: function (message) {
      this.logEntries.unshift({
        timestamp: new Date().toLocaleTimeString(),
        message: message
      });
    },
    connect: function () {
      this.$socket.emit('connectWithPlayerName', this.playerName)
    },
  }
}
</script>

<style>
  div.dashboard {
    margin-top: 10px;
  }
</style>