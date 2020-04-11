<template>
  <div class="hello">
    <el-row gutter="2">
      <el-col :span="14">
        <div class="grid-content bg-purple-dark">a</div>
      </el-col>
      <el-col :span="10">
        <div class="grid-content bg-purple-dark">
          <game-log></game-log>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import GameLog from './GameLog'

export default {
  components: {GameLog},
  name: 'Dashboard',
  mounted() {
    this.initSocket();
  },
  data () {
    return {

    }
  },
  methods: {
    sendMsg: function () {
      this.$socket.emit('message', "Helloooo")
    },
    initSocket: function () {
      this.sockets.subscribe('message', (data) => {
        console.log("Received this: " + data);
      });
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
#myCanvas {
  background-color: burlywood;
}
</style>
