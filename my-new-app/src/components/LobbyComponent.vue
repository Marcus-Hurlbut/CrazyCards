
<template>
  <div class="heartsLobby">
    <form v-if="!isLobbyCreated" class="joinGameForm" @submit.prevent="start(displayName)">
      <label for="displayName">Enter Your Display Name:</label><br>
      <input type="text" id="displayName" v-model="displayName" name="displayName"><br><br>
      <button type="submit">Create Lobby</button>
    </form>

    <div v-if="isLobbyCreated" class="playerList">
      <h1>Waiting for other players...</h1>
      <h2>Game Code: {{ lobbyID }}</h2>
      <ul>
        <li>Player 1: {{ displayName || 'Waiting..'}}</li>
        <li>Player 2: {{ otherPlayerNames[0] || 'Waiting..'}}</li>
        <li>Player 3: {{ otherPlayerNames[1] || 'Waiting..'}}</li>
        <li>Player 4: {{ otherPlayerNames[2] || 'Waiting..'}}</li>
      </ul>  
    </div>
  </div>
</template>

<script>
import { Stomp } from '@stomp/stompjs';
import { mapState } from 'vuex';
import { mapActions } from 'vuex';

export default {
  name: 'LobbyComponent',
  computed: {
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'lobbyID', 'playerID']),
  },
  data() {
    return {
      displayName: null,
      stompClient: null,
      connected: false,
      connecting: false,
      otherPlayerNames: []
    }
  },
  mounted() {
    if (this.isLobbyCreated == true) {
      this.onJoinedLobby()
    }
  },
  methods: {
    ...mapActions(['storeIsLobbyCreated', 'storeStompClient' , 'storePlayerID', 'storeUsername', 'storeOtherPlayer', 'storeGameID', 'storeLobbyID']),

    onJoinedLobby() {
      this.stompClient = this.$store.getters.stompClient;
      this.displayName = this.$store.getters.username;
      this.otherPlayerNames = this.$store.getters.otherPlayers;
      this.lobbyID = this.$store.getters.lobbyID;
      this.subscribeNotifyPlayerJoined();
      this.subscribeNotifyGameStart();
    },
    subscribeNotifyGameStart() {
      this.stompClient.subscribe('/topic/notifyGameStart/' + this.lobbyID.toString(), message => {
        console.log('Game Started');
        let body = message.body
        let gameID = JSON.parse(body)

        this.storeGameID(gameID)
        this.$router.push('/heartsGame');
      })
    },
    subscribeNotifyPlayerJoined() {
      this.stompClient.subscribe('/topic/notifyPlayerJoined', message => {
        let body = message.body
        let player = JSON.parse(body);

        if (player.username != this.displayName && !this.otherPlayerNames.includes(player.username)) {
          this.otherPlayerNames.push(player.username)
        }
        console.log('Another Player Joined Lobby: ' + player.username)
        console.log('Other player list updated: ' + this.otherPlayerNames)
      })
    },
    subscribeNewLobby() {
      this.stompClient.subscribe('/topic/newLobby', message => {
        let body = JSON.parse(message.body);
        let lobbyID = JSON.parse(body.content);

        console.log('Lobby started: ', lobbyID);

        this.storeLobbyID(lobbyID);
        this.subscribeNotifyGameStart();
      });
    },
    publishNewLobby() {
      if (this.connected) {
        let playerID = crypto.randomUUID();
        this.storePlayerID(playerID);
        console.log('Creating Lobby with playerID: ', playerID);

        this.stompClient.publish({
          destination: "/app/newLobby",
          body: JSON.stringify({'ID': this.$store.getters.playerID, 'username': this.displayName})
        })
      }
    },
    start(displayName) {
      this.displayName = displayName
      this.storeUsername(displayName)

      if (this.connected || this.connecting) return;
      this.connecting = true;

      const socketUrl = 'ws://localhost:8080/gs-guide-websocket';
      this.stompClient = Stomp.over(() => new WebSocket(socketUrl));

      this.stompClient.connect({}, frame => {
        console.log('Websocket Connected: ', frame);
        this.connected = true;
        this.connecting = false;

        this.subscribeNewLobby();
        this.subscribeNotifyPlayerJoined();
        this.storeIsLobbyCreated(true);
        this.publishNewLobby();
      },
      error => {
        console.error("WebSocket connection error:", error);
        this.connecting = false;
      });

      this.storeStompClient(this.stompClient);
    },
  }
}
</script>

<style scoped>
.heartsLobby {
  display: flex;
  justify-content: center; /* Centers horizontally */
  align-items: center;     /* Centers vertically */
  height: 100vh;           /* Full viewport height */
  margin: 0;               /* Removes default body margin */
}
.playerList {
  display: inline-block;
  padding: 20px 80px;
  text-align: center;
  transform: translateY(-100%);
  background: linear-gradient(to bottom right, red, purple);
  border: 5px solid;
  border-color: black;
}
.joinGameForm {
    display: inline-block;
    padding: 20px 80px;
    text-align: center;
    transform: translateY(-100%);
    background: linear-gradient(to bottom right, red, purple);
    border: 5px solid;
    border-color: black;
}
ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}
</style>
