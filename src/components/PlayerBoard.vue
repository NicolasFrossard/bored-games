<template>
  <el-table :data="gameState.players" style="width: 100%" cell-class-name="bored-game-sidebar-cell-style">
    <el-table-column prop="connected" width="40">
      <template slot-scope="scope">
        <i v-if="scope.row.connected" class="el-icon-circle-check connected"></i>
        <i v-else class="el-icon-circle-close notConnected"></i>
      </template>
    </el-table-column>
    <el-table-column prop="name" label="Players">
      <template slot-scope="scope">
        <span v-if="scope.row.socketId === mySocketId">
          <b>{{scope.row.name}}</b>
        </span>
        <span v-else>
          {{scope.row.name}}
        </span>
      </template>
    </el-table-column>
    <el-table-column>
      <template slot-scope="scope">
        <span v-if="scope.row.isAdmin">👑</span>
      </template>
    </el-table-column>
    <el-table-column v-if="gameState.status !== 'STARTED'">
      <template slot-scope="scope">
        <el-button type="primary" size="small" @click="deletePlayer(scope.row.name)" plain :disabled="scope.row.socketId === mySocketId">
          Delete player
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  name: 'PlayerBoard',
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
    deletePlayer: function (playerName) {
      this.$emit('deletePlayer', playerName)
    }
  }
}
</script>

<style scoped>
  i.connected {
    color: limegreen;
  }
  i.notConnected {
    color: red;
  }
</style>
