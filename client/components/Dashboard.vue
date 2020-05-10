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
          <img src="static/gameOver/pathetic.jpg" width="50%" v-if="gameOverRoundAchieved <= 3">
          <img src="static/gameOver/not-bad.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 4">
          <img src="static/gameOver/good-job.jpg" width="33%" v-else-if="gameOverRoundAchieved <= 5">
          <img src="static/gameOver/amazing.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 6">
          <img src="static/gameOver/awesome.jpg" width="50%" v-else-if="gameOverRoundAchieved <= 7">
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
    // TODO wait until socket is connected instead of hardcoded 1 second
    const initSocketCall = this.initSocket;
    setTimeout(function () {
      initSocketCall();
    }, 1000);
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
      const dashboard = this;

      this.$socket.onmessage = function (event) {
        const parsed = JSON.parse(event.data);
        switch (parsed.type) {
          case 'EVENT_INFO':
            const gameLogInfo = parsed.event;
            dashboard.logEntries.unshift({type: "INFO", text: gameLogInfo});
            dashboard.highlightLatestLogEntry();
            break;

          case 'EVENT_WARNING':
            const gameLogWarning = parsed.event;
            dashboard.logEntries.unshift({type: "WARN", text: gameLogWarning});
            dashboard.$message.warning(gameLogWarning);
            dashboard.highlightLatestLogEntry();
            break;

          case 'EVENT_ERROR':
            const gameLogError = parsed.event;
            dashboard.logEntries.unshift({type: "ERROR", text: gameLogError});
            dashboard.$message.error(gameLogError);
            dashboard.highlightLatestLogEntry();
            break;

          case 'EVENT_GAME_STATE':
            const gameState = parsed.event;
            dashboard.gameState = gameState;
            break;

          case 'serverWarning':
            const message = parsed.event;
            dashboard.$message.warning(message);
            break;

          case 'EVENT_CONNECTION_SUCCESS':
            const socketId = parsed.event
            dashboard.connectionEstablished = true;
            dashboard.mySocketId = socketId;
            break;

          case 'EVENT_GAME_LOST':
            const round = parsed.event
            dashboard.playSoundLostGame();
            dashboard.gameOverRoundAchieved = round;
            dashboard.gameOverDialogVisible = true;
            break;

          case 'EVENT_ERROR_MADE':
            const cards = parsed.event
            dashboard.playSoundErrorMade();
            dashboard.flipCards(cards);
            break;

          case 'EVENT_CARD_WELL_PLAYED':
            const card = parsed.event
            dashboard.playSoundCardPlayed();
            dashboard.highlightLastCardPlayed();
            dashboard.flipCards([card]);
            break;

          case 'EVENT_NEW_ROUND':
            const newRound = parsed.event
            dashboard.$message.info(`Starting round ${newRound}`);
            dashboard.playSoundNewRound();
            dashboard.triggerCountdown();
            break;

          case 'EVENT_GAME_STARTED':
            dashboard.playStartingGame();
            dashboard.triggerCountdown();
            break;

          default:
            console.error(`Got unknown event type: ${JSON.stringify(parsed)}`);
        }
      }
    },
    wsSend: function(eventType, event) {
      this.$socket.send(JSON.stringify({type: eventType, event: event}));
    },
    connect: function () {
      this.wsSend('EVENT_CONNECT_WITH_PLAYER_NAME', {playerName: this.playerName})
    },
    startTheGame: function () {
      this.wsSend('EVENT_START_THE_GAME', {})
    },
    stopTheGame: function () {
      this.wsSend('EVENT_STOP_THE_GAME', {})
    },
    playCard: function (card) {
      this.wsSend('EVENT_PLAY_CARD', {card: card, round: this.gameState.round})
    },
    deletePlayer: function (playerName) {
      this.wsSend('EVENT_DELETE_PLAYER', {playerName: playerName})
    },
    getGameState: function () {
      this.wsSend('EVENT_GET_GAME_STATE', {})
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