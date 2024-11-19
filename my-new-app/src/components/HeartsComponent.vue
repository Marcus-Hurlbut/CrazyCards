<template>
  <div class="startHearts">
    <h1>{{ msg }}</h1>
    
    <!-- This part is shown before the game starts -->
    <div v-if="!gameStarted" class="startHeartsContent">
      <h3>Welcome to Hearts</h3>
      <p>
        Press start to begin<br>
      </p>
      <button @click="start">Start</button>
    </div>

    <!-- This part is shown after the user clicks Start -->
    <div v-if="gameStarted" class="gameArea">
      <!-- Player 2 (top) -->
      <div class="player player-top">
        <h3>Player 2</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />
        </div>
      </div>

      <!-- Player 3 (right) -->
      <div class="player player-right">
        <h3>Player 3</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />
        </div>
      </div>

      <!-- Player 4 (left) -->
      <div class="player player-left">
        <h3>Player 4</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />
        </div>
      </div>

      <!-- Your Cards (bottom center) -->
      <div class="player player-center">
        <h3>Your Cards</h3>
        <div class="your-cards">
          <div v-for="(card, index) in playerCards" :key="index" class="card">
            <img 
              :src="require(`@/assets/card-images/PNG-cards/${card.imgPath}`)" 
              :alt="'Card ' + (index + 1)"
              @click="playTurn(card.id)"
            />
          </div>
        </div>
      </div>

      <div class="void-cards">
        <h3>Cards in play</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_light.png" alt="back_dark" />
        </div>
      </div>
    </div>
  </div>

  <button @click="getHand(0)" class="small-button">Your Hand</button>
  <button @click="getHand(1)" class="small-button">Player 2 Hand</button>
  <button @click="getHand(2)" class="small-button">Player 3 Hand</button>
  <button @click="getHand(3)" class="small-button">Player 4 Hand</button>

</template>
  
<script>
// import axios from 'axios';
// import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

export default {
  name: 'HeartsComponent',
  props: {
    msg: String
  },
  data() {
    return {
      gameStarted: false, // Controls whether the game area is shown
      playerCards: {},
      stompClient: null,
      connected: false,
      gameMessages: [], // Stores incoming messages from WebSocket
      connecting: false,
      currentPlayerID: 0
    };
  },
  methods: {
    start() {
      this.gameStarted = true; // Set to true to display the game area
      this.connectWebSocket(); // Connect to the WebSocket
    },
    // async startHeartsAPI() {
    //   try {
    //     const playerID = 0; // Get playerID dynamically if needed
    //     const response = await axios.get(`http://localhost:8080/startHearts?playerID=${playerID}`);
    //     console.log('response.data', response.data.cards);
    //     this.playerCards = response.data.cards;
    //   } catch (error) {
    //     console.error("There was an error calling the API:", error);
    //   }
    // },
    connectWebSocket() {
      if (this.connected || this.connecting) return; // NEW: Avoid multiple connections // NEW
      this.connecting = true; // NEW: Set connecting flag to true // NEW

      const socketUrl = 'ws://localhost:8080/gs-guide-websocket';
      
      // Provide WebSocket factory function to Stomp.over
      this.stompClient = Stomp.over(() => new WebSocket(socketUrl));

      this.stompClient.connect({}, frame => {
        console.log('Websocket Connected: ', frame);
        this.connected = true; // Set connected flag to true once connected
        this.connecting = false;

        // Subscribe to starting game result
        this.stompClient.subscribe('/topic/startGame', message => {
          console.log('Game started: ', message);
        });


        // Subscribe to playing turn result
        this.stompClient.subscribe('/topic/playTurn', message => {
          console.log('Message received: ', message);
          this.handleGameUpdate(message.body);
        });

        this.stompClient.subscribe('/topic/getHand', message => {
          let body = JSON.parse(message.body);
          let hand = JSON.parse(body.content);
          console.log('hand: ', hand);
          // this.playerCards = str.match(/[\w]+\.png/g);
          this.playerCards = {}

          for (const [id, card] of Object.entries(hand)) {
            console.log(`Assigning imgPath for card ID ${id}:`, card.imgPath);
            this.playerCards[id] = {
              id: id,         // Card ID
              suit: card.suit, // Card suit
              value: card.value, // Card value
              imgPath: card.imgPath, // Image path
            };
          }

          console.log('Starting cards received: ', this.playerCards);
        });

        this.startGame(); // NEW: Start the game after successful connection // NEW
      }, error => {
        console.error("WebSocket connection error:", error);
        this.connecting = false; // NEW: Reset connecting flag on error // NEW
      });
    },
    startGame() {
      if (this.connected) {
        const playerID = 0;
        console.log('Starting game..');

        this.stompClient.publish({
          destination: "/app/startGame",
          body: JSON.stringify({'content': playerID})
        })
      } else {
        console.log("WebSocket is not connected, unable to send message: /app/startGame");
      }
    },
    getHand(playerID) {
      this.currentPlayerID = playerID
      this.stompClient.publish({
        destination: "/app/getHand",
        body: JSON.stringify({'content': this.currentPlayerID})
      })
    },
    playTurn(card) {
      if (this.connected) {
        // Sending a player's action through the WebSocket
        this.stompClient.publish({
          destination: "/app/playTurn",
          body: JSON.stringify({'playerID': this.currentPlayerID, 'cardID': card})
        });
      } else {
        console.log("WebSocket is not connected, attempting to connect..."); // NEW
        this.connectWebSocket(); // NEW: Attempt to connect // NEW
        // Wait until connected before retrying playTurn
        this.$watch('connected', (newVal) => { // NEW
          if (newVal) { // NEW
            this.playTurn(card); // NEW
          } // NEW
        }); // NEW
      }
    },
    handleGameUpdate(message) {
      // You can add more logic here to handle different types of game updates
      this.gameMessages.push(message);
    }
  }
};
</script>

<style scoped>
.startHearts {
  background: linear-gradient(to bottom right, #a2c2e3, #76b0e8);  /* Gradient background */
  background-image: url(./poker-table-green-fabric.jpg);
  background-size: cover; /* Makes the image cover the entire area */
  background-repeat: no-repeat;
  height: 100vh; /* Full viewport height */
  color: white; /* Text color */
  text-align: center;
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center; /* Vertically center content */
  align-items: center; /* Horizontally center content */
}


.startHeartsContent {
  margin-bottom: 50em;
}

.gameArea {
  position: relative;
  width: 80%;
  height: 80%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.player {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
}

.player-top {
  top: 0px;
  transform: translateX(-50%);
  transform: translateY(-100%);
}

.player-right {
  right: 10%;
  bottom: 50%;
  transform: translateY(-50%);
}

.player-left {
  left: 10%;
  bottom: 50%;
  transform: translateY(-50%);
}

.player-center {
  bottom: 0px;
  left: 35%;
  transform: translateX(-50%);
  text-align: center;
  z-index: 10; /* Ensure your cards are on top */
}

.small-button {
  padding: 5px 10px; /* Adjust the padding */
  font-size: 12px; /* Reduce the font size */
  border: 1px solid #ccc; /* Optional: adjust the border */
  border-radius: 5px; /* Add rounded corners */
  background-color: #f5f5f5; /* Light background color */
  color: #333; /* Adjust text color */
  cursor: pointer;
  max-width: 100px;
}

.void-cards {
  transform: translateY(-20%);
}

.card {
  width: 50px;
  height: 70px;
  background-color: #2c3e50; /* Card back color */
  border-radius: 5px;
  margin: 5px;
  display: inline-block;
}

.face-down img {
  width: 160px; /* Adjust the card image size */
  height: 190px;
}

.your-cards .card {
  background-color: #2980b9; /* Example color for your cards */
  color: white;
  text-align: center;
  font-size: 18px;
  line-height: 70px;
}

.your-cards .card img {
  width: 160px; /* Adjust the card image size */
  height: 190px; /* Adjust the card image size */
  border-radius: 5px; /* Optional: to make the card corners rounded */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); /* Optional: add a slight shadow to make cards stand out */
  transition: transform 0.2s ease; /* Optional: add a hover effect */
  cursor: pointer; /* NEW: Change cursor to pointer to indicate clickability */
}

.your-cards .card:hover img {
  transform: scale(1.1); /* Slightly enlarge the card on hover */
}

button {
  padding: 10px 20px;
  background-color: #ff4081; /* Button color */
  color: white;
  border: none;
  cursor: pointer;
  font-size: 18px;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #ff80ab; /* Button hover effect */
}

a {
  color: #42b983;
}
</style>
