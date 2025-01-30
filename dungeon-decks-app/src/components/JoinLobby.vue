
<template>
  <div class="joinGame">
    <form class="joinGameForm" @submit.prevent="join(lobbyID, displayName)">
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
    <BubbleBackground />
  </div>
</template>
  
<script>
  import { Stomp } from '@stomp/stompjs';
  import { mapActions } from 'vuex';
  import { mapState } from 'vuex';
  import BubbleBackground from './animations/BubbleBackground.vue';
  import { v4 as uuidv4 } from 'uuid';
  import '@/assets/styles/global.css';

  export default {
    name: 'JoinLobby',
    components: {
      BubbleBackground,
    },
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
          ? 'ws://localhost:8080/dungeon-decks-websocket'
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
.joinGameForm {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: inline-block;
  padding: 30px 50px;
  background: linear-gradient(135deg, #1b479a ,#6a1b9a, #ff1744);
  border-radius: 15px;
  border: 3px solid #ffffff;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
  text-align: center;
  width: 400px;
  transition: all 0.3s ease-in-out;
  z-index: 100;
  align-items: center;
}

.display-name-section , .game-code-section {
  padding: 10% 10%;
  transform: translateX(-05%);
}

.game-code-section {
  margin-bottom: 10%;
}
  
.joinGameForm label {
  font-family: 'FancyFont', sans-serif;
  font-size: 2.2em;
  margin-bottom: 10px;
  display: block;
  font-weight: bold;
  color: black;
  text-shadow: 0px 0px 2px #f6f8f8, 0px 0px 2px #ffffff;
  transform: translateX(5%);
}
  
.joinGameForm input {
  width: 100%;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #fff;
  font-size: 1em;
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
  text-align: center;
  transition: 0.3s ease;
  align-items: center;

}
  
.joinGameForm input::placeholder {
  color: #ccc;
}
  
.joinGameForm input:focus {
  outline: none;
  background: rgba(255, 255, 255, 0.5);
}
  
.joinGameForm button {
  font-family: 'FancyFont', sans-serif;
  font-weight: bold;
  font-size: 2em;
  width: 100%;
  padding: 20px;
  border-radius: 8px;
  background-color: rgba(201, 84, 201, 0.507);
  color: black;
  border: none;
  cursor: pointer;
  transition: 0.3s ease, transform 0.2s ease;
  text-shadow: 0px 0px 1px #ffffff, 0px 0px 1px #ffffff;
  align-items: center;
}
  
.joinGameForm button:hover {
  background-color: rgba(35, 68, 214, 0.507);
  transform: scale(1.05);
  animation: neonGlowingBorder 2s infinite alternate;
}
  
.joinGameForm button:active {
  background-color: rgba(120, 101, 233, 0.507);
}

</style>
  