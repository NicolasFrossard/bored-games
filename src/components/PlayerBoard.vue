<template>
  <el-table :data="players" style="width: 100%" cell-class-name="bored-game-sidebar-cell-style">
    <el-table-column prop="connected" width="40">
      <template slot-scope="scope">
        <i v-if="scope.row.connected" class="el-icon-circle-check connected"></i>
        <i v-else class="el-icon-circle-close notConnected"></i>
      </template>
    </el-table-column>
    <el-table-column prop="name" label="Players">
      <template slot-scope="scope">
        {{scope.row.name}}
      </template>
    </el-table-column>
    <el-table-column>
      <template slot-scope="scope">
        <span v-if="scope.row.isAdmin">👑</span>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  name: 'PlayerBoard',
  props: {
    players: {
      type: Array,
      required: true,
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
