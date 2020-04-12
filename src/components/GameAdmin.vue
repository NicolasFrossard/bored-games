<template>
  <el-row>
      <el-tag type="info">
        <div>{{gameState.players.length}} player{{gameState.players.length > 1 ? 's' : ''}}</div>
      </el-tag>
    <template v-if="gameState.status === 'STARTED'">
      <el-tag type="success">Game has started</el-tag>
      <el-tag v-if="gameState.status === 'STARTED'">Round {{gameState.round}}</el-tag>
    </template>
    <template v-else>
      <el-tag type="info">Game to be started</el-tag>
      <el-button type="primary" size="small" @click="onStartTheGame">Click here to start the game</el-button>
    </template>
  </el-row>
</template>

<script>
export default {
  props: {
    gameState: {
      type: Object,
      required: true,
    }
  },
  methods: {
    onStartTheGame: function () {
      this.$emit('onStartTheGame')
    }
  }
}
</script>
