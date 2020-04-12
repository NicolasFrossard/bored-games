<template>
  <div>
    <el-row class="spaced" v-for="player in players" v-bind:key="player.socketId">
      <el-col>
        <span class="player-name">{{player.name}}</span>
      </el-col>
      <el-col>
        <template v-for="card in player.cardsInHand">
          <el-button class="large" plain size="medium">
            <span v-if="player.socketId === mySocketId">
              {{card}}
            </span>
            <span v-else>
              ?
            </span>
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
    players: {
      type: Array,
      required: true,
    },
    mySocketId: {
      type: String,
      required: true,
    }
  }
}
</script>

<style scoped>
  .el-button.large {
    padding: 20px 10px;
    font-size: 28px;
    border-radius: 8px;
    background-color: #dcdfe6;
    font-family: fantasy;
    font-weight: bold;
  }
  .el-row.spaced {
    margin-top: 20px;
  }
  .player-name {
    font-size: large;
  }
</style>