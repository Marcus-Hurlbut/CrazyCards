
<template>
  <div class="heartsLobby">
    <div v-if="isLobbyCreated" class="playerList" >
      <h1>Waiting for other players...</h1>
      <h2>Game Code: {{ lobbyID }}</h2>
      <ul>
        <li> {{ displayName || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[0] || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[1] || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[2] || 'Waiting..'}}</li>
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
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'lobbyID', 'playerID', 'playerIndex']),
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
    else {
      this.displayName = this.$store.getters.username;
      this.start(this.displayName)
    }
  },
  props: {

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
          // this.otherPlayerNames.push(player.username)
          this.storeOtherPlayer(player.username)
        }
        this.otherPlayerNames = this.$store.getters.otherPlayers;
        console.log('Another Player Joined Lobby: ' + player.username)
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
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
  /* background: linear-gradient(to bottom right, #D50032, #9C1B80); */
}

.playerList {
  display: inline-block;
  padding: 30px 40px;
  text-align: center;
  background: linear-gradient(to bottom right, #C2185B, #9C27B0);
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  width: 100%;
  max-width: 450px;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
}

.playerList:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.6);
}

h1, h2 {
  color: white;
  font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
}

h1 {
  font-size: 2.5em;
  font-weight: bold;
  margin-bottom: 15px;
}

h2 {
  font-size: 1.5em;
  font-weight: bold;
  margin-bottom: 20px;
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
}

ul li {
  font-size: 1.2em;
  color: white;
  padding: 10px;
  margin: 8px 0;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 10px;
  transition: background-color 0.3s, transform 0.3s;
}

ul li:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

ul li:first-child {
  background: rgba(255, 255, 255, 0.1);
}

</style>
