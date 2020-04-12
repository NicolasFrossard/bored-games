<template>
  <el-table :data="entries" height="800px" style="width: 100%">
    <el-table-column prop="icon" width="40">
      <template slot-scope="scope">
        <i v-if="scope.row.type === 'INFO'" class="el-icon-info info-game-log"></i>
      </template>
    </el-table-column>
    <el-table-column prop="text" label="What?"></el-table-column>
    <el-table-column prop="date" label="When?" width="180"></el-table-column>
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
</style>