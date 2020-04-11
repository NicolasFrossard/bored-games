<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <canvas id="myCanvas" width="680" height="520"></canvas>
    <button @click="sendMsg">Please do not click here</button>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  mounted() {
    this.initSocket();
  },
  data () {
    return {
      msg: 'Welcome to Bored Games'
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
