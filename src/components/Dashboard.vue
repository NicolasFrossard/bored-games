<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <canvas id="myCanvas" width="680" height="520"></canvas>
    <button @click="sendMsg">Please do not click here</button>

    <el-row>
      <el-button plain>Plain</el-button>
      <el-button type="primary" plain>Primary</el-button>
      <el-button type="success" plain>Success</el-button>
      <el-button type="info" plain>Info</el-button>
      <el-button type="warning" plain>Warning</el-button>
      <el-button type="danger" plain>Danger</el-button>
    </el-row>

    <el-table
            :data="tableData"
            height="250"
            style="width: 100%">
      <el-table-column
              prop="date"
              label="Date"
              width="180">
      </el-table-column>
      <el-table-column
              prop="name"
              label="Name"
              width="180">
      </el-table-column>
      <el-table-column
              prop="address"
              label="Address">
      </el-table-column>
    </el-table>
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
      msg: 'Welcome to Bored Games',
      tableData: [{
        date: '2016-05-03',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-02',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-04',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-01',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-08',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-06',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }, {
        date: '2016-05-07',
        name: 'Tom',
        address: 'No. 189, Grove St, Los Angeles'
      }]
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
