<template>
  <el-row>
      <el-tag type="info">
        <div>{{gameState.players.length}} player{{gameState.players.length > 1 ? 's' : ''}}</div>
      </el-tag>
    <template v-if="gameState.status === 'STARTED'">
      <el-tag type="success">Game has started</el-tag>
      <el-tag v-if="gameState.status === 'STARTED'">Round {{gameState.round}}</el-tag>
      <el-button type="danger" size="small" @click="dialogVisible = true" plain>Restart the game</el-button>
      <el-dialog title="Restart the game" :visible.sync="dialogVisible" width="30%">
        <span>Are you sure you want to restart the game?</span>
          <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="onStopTheGame">Confirm</el-button>
        </span>
      </el-dialog>
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
  data () {
    return {
      dialogVisible: false
    }
  },
  methods: {
    onStopTheGame: function () {
      this.dialogVisible = false;
      this.$emit('onStopTheGame')
    },
    onStartTheGame: function () {
      this.$emit('onStartTheGame')
    }
  }
}
</script>
