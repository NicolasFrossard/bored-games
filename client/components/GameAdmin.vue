<template>
  <el-row>
    <el-tag type="info">
      <div>{{gameState.players.length}} player{{gameState.players.length > 1 ? 's' : ''}}</div>
    </el-tag>
    <template v-if="gameState.status === 'STARTED'">
      <el-tag type="success">Game has started</el-tag>
      <el-tag v-if="gameState.status === 'STARTED'">Round {{gameState.round}}</el-tag>
      <el-tag>
        <span v-for="(player, index) of gameState.players">
          <span v-if="(index+1) <= gameState.lives">
            ❤️
          </span>
          <span v-else>
            🤍
          </span>
        </span>️
      </el-tag>
      <el-button type="danger" size="small" @click="dialogVisible = true" plain>Stop the current game</el-button>
      <el-dialog title="Restart the game" :visible.sync="dialogVisible" width="30%">
        <span>Are you sure you want to stop the game?</span>
          <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="onStopTheGame">Confirm</el-button>
        </span>
      </el-dialog>
    </template>
    <template v-else>
      <el-tag type="info">Game to be started</el-tag>
      <el-button type="primary" size="small" @click="onStartTheGame">
        Click here to start the game with {{gameState.players.length}} player{{gameState.players.length > 1 ? 's' : ''}}
      </el-button>
    </template>
    <el-button size="small" @click="testSound" icon="el-icon-video-play" plain>
      Sound test
    </el-button>
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
    },
    testSound: function () {
      this.$emit('testSound')
    }
  }
}
</script>
