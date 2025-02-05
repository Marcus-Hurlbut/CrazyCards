
<template>
  <div class="joinGame">
    <form class="lobbyForm" @submit.prevent="join(lobbyID, displayName)">
      <div class="game-code-section">
        <label for="lobbyID">Enter Game Code</label>
        <input type="text" id="lobbyID" v-model="lobbyID" name="lobbyID">
      </div>
      <div class="display-name-section">
        <label for="displayName">Enter Display Name</label>
        <input type="text" id="displayName" v-model="displayName" name="displayName">
      </div>
      <button type="submit">Join Lobby</button>
    </form>
  </div>
</template>
  
<script>
  import { Stomp } from '@stomp/stompjs';
  import { mapActions } from 'vuex';
  import { mapState } from 'vuex';
  import { v4 as uuidv4 } from 'uuid';
  import '@/assets/styles/global.css';
  import '@/components/lobby/css/styles.css';

  export default {
    name: 'JoinLobby',
    computed: {
      ...mapState(['playerID', 'username'])
    },
    data() {
      return {
        lobbyID: "",
        displayName: "",
        stompClient: null,
        connected: false,
        connecting: false,
      }
    },
    methods: {
      ...mapActions(['storeIsLobbyCreated', 'storeStompClient', 'storePlayerID', 'storeUsername', 'storeOtherPlayer', 'storeLobbyID', 'storePlayerIndex']),

      join(lobbyID, displayName) {
        if (this.connected || this.connecting) return; 
        this.connecting = true;
        this.lobbyID = lobbyID

        const socketUrl = process.env.NODE_ENV === 'development'
          ? 'ws://192.168.86.37:8080/dungeon-decks-websocket'
          : 'wss://dungeondecks.net/dungeon-decks-websocket'
        this.stompClient = Stomp.over(() => new WebSocket(socketUrl));
        
        this.stompClient.connect({}, frame => {
          console.log('Websocket Connected: ', frame);
          this.connected = true;
          this.connecting = false;

          // Set store values
          let playerID =  uuidv4().toString();
          this.storePlayerID(playerID);
          this.storeStompClient(this.stompClient);
          this.storeUsername(displayName);
          this.storeLobbyID(lobbyID);

          this.subscribeJoinLobby();
          this.publishJoinLobby();
        }, 
        error => {
          console.error("WebSocket connection error:", error);
          this.connecting = false;
        });
      },
      subscribeJoinLobby() {
        let subscription = '/topic/' + this.$route.query.game + '/joinLobby/' + this.$store.getters.playerID
        this.stompClient.subscribe(subscription, message => {
          let otherNames = JSON.parse(message.body);
          console.log('Joined lobby successfully - Other player names: ', otherNames)
          let index = 0;

          otherNames.forEach(playerName => {
            console.log("storing username: ", playerName)
            this.storeOtherPlayer(playerName)
            index += 1;
          });

          this.storePlayerIndex(index);
          this.storeIsLobbyCreated(true);
          this.$router.push({
            path: '/lobby',
            query: {
              ...this.$route.query
            }
          });
        });
      },
      publishJoinLobby() {
        if (this.connected) {
          let dest = "/app/" + this.$route.query.game + "/joinLobby"
          this.stompClient.publish({
            destination: dest,
            body: JSON.stringify({'playerID': this.$store.getters.playerID, 'lobbyID': this.lobbyID, 'username': this.$store.getters.username})
          })
        }
      },
    }
  }
</script>
  
<style scoped>
.display-name-section , .game-code-section {
  padding: 10% 10%;
  transform: translateX(-5%);
}

.game-code-section {
  margin-bottom: 10%;
}
  
.lobbyForm label {
  transform: translateX(5%);
}

@media (max-width: 767px) {
  .lobbyForm label {
    margin-bottom: 0vh;
    transform: translateX(12%);
  }

  .lobbyForm input {
    transform: translateX(12%);
  }

  .display-name-section , .game-code-section {
    transform: translateX(-12%);
  }
}
</style>
  