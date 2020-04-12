<template>
  <el-row>
    <el-col :span="14">
      <el-row class="spaced" v-for="player in gameState.players" v-bind:key="player.socketId">
        <el-col>
          <span class="player-name">{{player.name}}</span>
        </el-col>
        <el-col>
          <template v-for="card in player.cardsInHand">
            <el-button v-if="gameState.cardsPlayed.includes(card)" class="card large" plain @click="playCard(card)">
              ✓
            </el-button>
            <el-button v-else-if="player.socketId === mySocketId" class="card large own-non-played-card" plain @click="playCard(card)">
              {{card}}
            </el-button>
            <el-button v-else class="card large other-non-played-card" plain>
              ?
            </el-button>
          </template>
        </el-col>
      </el-row>
    </el-col>
    <el-col :span="10">
      <el-button v-if="gameState.cardsPlayed.length > 0" class="card last-played" plain>
        {{gameState.cardsPlayed[gameState.cardsPlayed.length-1]}}
      </el-button>
      <el-button v-else class="card last-played" plain disabled>
        ∅
      </el-button>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'GameLog',
  props: {
    gameState: {
      type: Object,
      required: true,
    },
    mySocketId: {
      type: String,
      required: true,
    }
  },
  methods: {
    playCard: function (card) {
      this.$emit('playCard', card);
    }
  }
}
</script>

<style scoped>
  .el-button.card {
    border-radius: 8px;
    font-family: fantasy;
    font-weight: bold;
  }
  .el-button.large {
     padding: 20px 10px;
     font-size: 28px;
  }
  .el-button.last-played {
    margin-top: 100px;
    padding: 40px 20px;
    font-size: 56px;
  }
  .el-button.own-non-played-card {
    background-color: aliceblue;
  }
  .el-button.other-non-played-card {
    background-color: #dcdfe6;
  }
  .el-button.last-played-card {
    background-color: #dcdfe6;
  }
  .el-row.spaced {
    margin-top: 20px;
  }
  .player-name {
    font-size: large;
  }
</style>