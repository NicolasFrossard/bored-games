<template>
  <div>
    <el-row class="spaced" v-for="player in gameState.players" v-bind:key="player.socketId">
      <el-col>
        <span class="player-name">{{player.name}}</span>
      </el-col>
      <el-col>
        <template v-for="card in player.cardsInHand">
          <el-button v-if="gameState.cardsPlayed.includes(card)" class="large" plain size="medium" @click="playCard(card)">
            ✓
          </el-button>
          <el-button v-else-if="player.socketId === mySocketId" class="large own-non-played-card" plain size="medium" @click="playCard(card)">
            {{card}}
          </el-button>
          <el-button v-else class="large other-non-played-card" plain size="medium">
            ?
          </el-button>
        </template>
      </el-col>
    </el-row>
  </div>
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
  .el-button.large {
    padding: 20px 10px;
    font-size: 28px;
    border-radius: 8px;
    font-family: fantasy;
    font-weight: bold;
  }
  .el-button.own-non-played-card {
    background-color: aliceblue;
  }
  .el-button.other-non-played-card {
    background-color: #dcdfe6;
  }
  .el-row.spaced {
    margin-top: 20px;
  }
  .player-name {
    font-size: large;
  }
</style>