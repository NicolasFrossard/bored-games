<template>
  <el-table :data="entries" height="800px" style="width: 100%">
    <el-table-column prop="message" label="What?"></el-table-column>
    <el-table-column prop="timestamp" label="When?" width="180"></el-table-column>
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
