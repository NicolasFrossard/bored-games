<template>
  <el-table id="game-log-table" :data="entries" height="600px" style="width: 100%" cell-class-name="bored-game-sidebar-cell-style">
    <el-table-column prop="icon" width="40">
      <template slot-scope="scope">
        <i v-if="scope.row.type === 'INFO'" class="el-icon-info info-game-log"></i>
        <i v-else-if="scope.row.type === 'WARN'" class="el-icon-warning warning-game-log"></i>
        <i v-else-if="scope.row.type === 'ERROR'" class="el-icon-error error-game-log"></i>
      </template>
    </el-table-column>
    <el-table-column prop="text" label="What happened?">
      <template slot-scope="scope">
        <span>{{scope.row.text}}</span>
      </template>
    </el-table-column>
    <el-table-column prop="date" label="When?" width="120"></el-table-column>
  </el-table>
</template>

<script>
export default {
  name: 'GameLog',
  props: {
    entries: {
      type: Array,
      required: true,
    }
  }
}
</script>

<style>
  i.info-game-log {
    color: dodgerblue;
  }
  i.warning-game-log {
    color: orange;
  }
  i.error-game-log {
    color: red;
  }
</style>