
<template>
  <div class="heartsLobby">
    <div v-if="isLobbyCreated" class="lobbyPlayerList" >
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
import BubbleBackground from './animations/BubbleBackground.vue';
import { v4 as uuidv4 } from 'uuid';
import '@/assets/styles/global.css';

export default {
  name: 'LobbyComponent',
  computed: {
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'lobbyID', 'playerID', 'playerIndex']),
  },
  components: {
    BubbleBackground,
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
    onNewLobby(displayName) {
      this.displayName = displayName
      this.storeUsername(displayName)

      if (this.stompClient && this.stompClient.connected) return;
      this.connecting = true;

      const socketUrl = process.env.NODE_ENV === 'development'
          ? 'ws://localhost:8080/dungeon-decks-websocket'
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
      if (this.connected) {
        console.log('Creating Lobby with playerID: ', playerID);
        let dest = "/app/" + this.$route.query.game + "/newLobby";
        this.stompClient.publish({
          destination: dest,
          body: JSON.stringify({'playerID': this.$store.getters.playerID, 'username': this.displayName})
        })
      }
    }
  }
}
</script>

<style scoped>
.lobbyPlayerList {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: inline-block;
  padding: 30px 40px;
  text-align: center;
  background: linear-gradient(to bottom right, #b1031a, #a30c8a, #2762b0);
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  width: 100%;
  height: 100%;
  max-width: 35vw;
  max-height: 80vh;
  height: auto;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
  z-index: 100;
}

.lobbyPlayerList:hover {
  transform: translate(-50%, -51%);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.6);
  background: linear-gradient(to bottom right, #b1031a, #8a0cb1, #2762b0);
}

h1, h3 {
  color: black;
  font-family: 'GatsbyFont';
  animation: glowingBorder 2s infinite alternate;
  text-shadow: 0px 0px 4px rgb(60, 137, 224), 0px 0px 3px rgb(0, 183, 255);;
}

h1 {
  font-size: 3.2em;
  font-weight: bold;
  margin-bottom: 15px;
}

h2 {
  font-size: 3.2em;
  font-weight: bold;
  margin-bottom: 20px;
  color: black;
  font-family: 'FancyFont';
  text-shadow: 0 0 5px rgb(60, 137, 224), 0 0 10px rgb(0, 183, 255);
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
  animation: neonGlowingBorder 2s infinite alternate;
  font-size: 2em;
  color: black;
  padding: 10px;
  margin: 8px 0;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 10px;
  transition: background-color 0.3s, transform 0.3s;
  text-shadow: 0px 0px 4px #44c9e0, 0px 0px 3px #ffffff;
}

ul li:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

ul li:first-child {
  background: rgba(255, 255, 255, 0.1);
}

</style>
