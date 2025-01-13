
<template>
      <div class="joinGame">
      <form class="joinGameForm" @submit.prevent="start(lobbyID, displayName)">
        <label for="lobbyID">Game Code</label><br>
        <input type="text" id="lobbyID" v-model="lobbyID" name="lobbyID"><br>
        <label for="displayName">Display Name:</label><br>
        <input type="text" id="displayName" v-model="displayName" name="displayName"><br><br>
        <button type="submit">Join Lobby</button>
      </form> 
    </div>
  </template>
  
  
<script>
  import { Stomp } from '@stomp/stompjs';
  import { mapActions } from 'vuex';
  import { mapState } from 'vuex';
  export default {
    name: 'JoinHeartsComponent',
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
      ...mapActions(['storeIsLobbyCreated', 'storeStompClient', 'storePlayerID', 'storeUsername', 'storeOtherPlayer', 'storeLobbyID']),

      start(lobbyID, displayName) {
        if (this.connected || this.connecting) return; 
        this.connecting = true;
        this.lobbyID = lobbyID

        const socketUrl = 'ws://localhost:8080/gs-guide-websocket';
        this.stompClient = Stomp.over(() => new WebSocket(socketUrl));
        
        this.stompClient.connect({}, frame => {
          console.log('Websocket Connected: ', frame);
          this.connected = true;
          this.connecting = false;

          // Set store values
          let playerID = crypto.randomUUID().toString();
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
        let subscription = '/topic/joinLobby/' + this.$store.getters.playerID
        this.stompClient.subscribe(subscription, message => {
          let otherNames = JSON.parse(message.body);
          console.log('Joined lobby successfully - Other player names: ', otherNames)

          otherNames.forEach(playerName => {
            console.log("storing username: ", playerName)
            this.storeOtherPlayer(playerName)
          });

          this.storeIsLobbyCreated(true);
          this.$router.push('/heartsLobby');
        });
      },
      publishJoinLobby() {
        if (this.connected) {
          this.stompClient.publish({
            destination: "/app/joinLobby",
            body: JSON.stringify({'playerID': this.$store.getters.playerID, 'roomID': this.lobbyID, 'cardIDs':"", 'name': this.$store.getters.username})
          })
        }
      },
    }
  }
</script>
  
<style scoped>
  .joinGame {
    display: flex;
    justify-content: center; /* Centers horizontally */
    align-items: center;     /* Centers vertically */
    height: 100vh;           /* Full viewport height */
    margin: 0;               /* Removes default body margin */
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
</style>
  