
<template>
  <div class="lobbyForm">
    <div v-if="isLobbyCreated && this.$route.query.game != 'spiderSolitaire'">
      <h1>Waiting for other players...</h1>
      <h3>Game Code</h3>
      <h2>{{ lobbyID }}</h2>
      <ul>
        <li> {{ displayName || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[0] || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[1] || 'Waiting..'}}</li>
        <li> {{ otherPlayerNames[2] || 'Waiting..'}}</li>
      </ul>
    </div>
  </div>
  <BubbleBackground />
</template>

<script>
import { Stomp } from '@stomp/stompjs';
import { mapState } from 'vuex';
import { mapActions } from 'vuex';
import { v4 as uuidv4 } from 'uuid';
import '@/assets/styles/global.css';


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
      this.onNewLobby(this.displayName)
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
    onNewLobby(displayName) {
      this.displayName = displayName
      this.storeUsername(displayName)

      if (this.stompClient && this.stompClient.connected) return;
      this.connecting = true;

      const socketUrl = process.env.NODE_ENV === 'development'
          ? 'ws://192.168.86.37:8080/dungeon-decks-websocket'
          : 'wss://dungeondecks.net/dungeon-decks-websocket'

      this.stompClient = Stomp.over(() => new WebSocket(socketUrl));

      this.stompClient.connect({}, frame => {
        console.log('Websocket Connected: ', frame);
        this.connected = true;
        this.connecting = false;

        let playerID = uuidv4().toString();
        this.storePlayerID(playerID);

        this.subscribeNewLobby(playerID);
        this.storeIsLobbyCreated(true);
        this.publishNewLobby(playerID);
      },
      error => {
        console.error("WebSocket connection error:", error);
        this.connecting = false;
      });

      this.storeStompClient(this.stompClient);
    },
    subscribeNotifyGameStart() {
      let subscription = '/topic/' + this.$route.query.game + '/notifyGameStart/'+ this.lobbyID.toString();
      this.stompClient.subscribe(subscription, message => {
        console.log('Game Started');
        let body = message.body
        let gameID = JSON.parse(body)

        this.storeGameID(gameID)
        this.startGame(this.$route.query.game);
      })
    },
    startGame(gameName) {
      console.log('Game name', gameName);
      if (gameName == 'hearts') {
        this.$router.push('/hearts');
      } 
      else if (gameName == 'spades') {
        this.$router.push('/spades');
      }
      else if (gameName == 'spiderSolitaire') {
        this.$router.push('/spiderSolitaire');
      }
    },
    subscribeNotifyPlayerJoined() {
      let subscription = '/topic/' + this.$route.query.game + '/notifyPlayerJoined/'+ this.lobbyID.toString();
      this.stompClient.subscribe(subscription, message => {
        let body = message.body
        let player = JSON.parse(body);

        if (player.username != this.displayName && !this.otherPlayerNames.includes(player.username)) {
          this.storeOtherPlayer(player.username)
        }
        this.otherPlayerNames = this.$store.getters.otherPlayers;
        console.log('Another Player Joined Lobby: ' + player.username)
      })
    },
    subscribeNewLobby(playerID) {
      let subscription = '/topic/' + this.$route.query.game  + '/newLobby/' + playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let lobbyID = JSON.parse(message.body);
        console.log('Lobby started: ', lobbyID);
        this.storeLobbyID(lobbyID);

        this.subscribeNotifyPlayerJoined();
        this.subscribeNotifyGameStart();
      });
    },
    publishNewLobby(playerID) {
      console.log('Creating Lobby with playerID: ', playerID);
      if (this.connected && this.$route.query.suits == null) {
        let dest = "/app/" + this.$route.query.game + "/newLobby";
        this.stompClient.publish({
          destination: dest,
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'username': this.displayName})
        })
      } else if (this.connected) {
        let dest = "/app/" + this.$route.query.game + "/newLobby";
        this.stompClient.publish({
          destination: dest,
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'username': this.displayName, 'lobbyID':'', 'count': this.$route.query.suits})
        })
      }
    }
  }
}
</script>

<style scoped>
.lobbyForm {
  padding: 3vh 5vw;
  height: auto;
}

.lobbyForm:hover {
  transform: translate(-50%, -51%);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.6);
}

h1, h3 {
  color: black;
  font-family: 'GatsbyFont';
  animation: glowingBorder 2s infinite alternate;
  text-shadow: 0px 0px 8px rgb(253, 253, 253), 0px 0px 3px rgb(0, 68, 255);
}

h1, h2 {
  font-size: 3.2em;
  font-weight: bold;
  margin-bottom: 15px;
}

h2 {
  color: rgb(3, 12, 53);
  font-family: 'FancyFont';
  text-shadow: 0px 0px 4px rgb(253, 253, 253), 0px 0px 3px rgb(0, 68, 255);
}

h3 {
  font-size: 2em;
  font-weight: bold;
  margin-bottom: -25px;
}

ul {
  list-style-type: none;
  margin: 0;
  padding: 10px;
}

ul li {
  font-family: 'FancyFont';
  animation: purpleGlowingBorder 2s infinite alternate;
  font-size: 2em;
  color: black;
  padding: 10px;
  margin: 8px 0;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 10px;
  transition: background-color 0.3s, transform 0.3s;
  text-shadow: 0px 0px 2px #ffffff, 0px 0px 5px #ffffff;
}

ul li:hover {
  background: rgba(255, 255, 255, 0.075);
  transform: translateX(5px);
}

ul li:first-child {
  background: rgba(255, 255, 255, 0.1);
}

@media (max-width: 767px) {
  h1, h3 {
    font-size: 1.4em;
  }
  h2 {
    font-size: 2.3em;
  }
  ul li {
    font-size: 1em;
  }
}
</style>
