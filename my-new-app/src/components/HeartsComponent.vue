<template>
  <div class="startHearts">
    <div class ="playerIDDisplay">
      <p>{{ playerID }}</p>
    </div>

    <div v-if="gameStarted" class="gameArea">

      <div v-if="passPhase" class="passingPhasePrompt">
        <h3>Passing Phase</h3>
        <p>Select 3 Cards to Pass</p>
      </div>

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
        <!-- <h3>Your Cards</h3> -->
        <div class="your-cards">
          <div v-for="(card, index) in playerCards" :key="index" class="card">
            <img 
              :src="require(`@/assets/card-images/PNG-cards/${card.imgPath}`)" 
              :alt="'Card ' + (index + 1)"
              @click="passPhase ? toggleCardSelection(card.id) : publishPlayTurn(card.id)"
              :class="{'selected-card': this.selectedCards.includes(card.id)}"
            />
          </div>
        </div>
      </div>

      <div class="void-cards">
        <h3>Card Pile</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_light.png" alt="back_dark" />
        </div>
      </div>
    </div>
  </div>
</template>
  
<script>
// import { Stomp } from '@stomp/stompjs';
import { mapActions } from 'vuex';
import { mapState } from 'vuex';

export default {
  name: 'HeartsComponent',
  computed: {
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'gameID', 'playerID']),
  },
  props: {
    msg: String
  },
  data() {
    return {
      gameID: null,
      gameStarted: false,
      playerCards: {},
      playerPassCards: [],
      selectedCards: [],
      stompClient: null,
      passPhase: true,
      cardIDInPlay: null
    };
  },
  mounted() {
    this.stompClient = this.$store.getters.stompClient;
    this.gameID = this.$store.getters.gameID;
    this.playerID = this.$store.getters.playerID;
    this.gameStarted = true;
    this.passPhase = true

    this.subscribePlayTurn();
    this.subscribeGetHand();
    this.subscribePassCards();
    this.subscribeNotifyPassingPhase()
    this.publishGetHand();
  },
  methods: {
    ...mapActions(['storeGameID']),

    subscribeGetHand() {
      let subscription = '/topic/getHand/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let hand = JSON.parse(message.body);
        console.log(`[topic/getHand/${this.playerID}]: `, hand);
        this.playerCards = {}

        for (const [id, card] of Object.entries(hand)) {
          this.playerCards[id] = {
            id: id,
            suit: card.suit,
            value: card.value,
            imgPath: card.imgPath,
          };
        }
        console.log('Starting cards received: ', this.playerCards);
      });
    },
    subscribePassCards() {
      let subscription = '/topic/passCards/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let passedCards = JSON.parse(message.body);

        if (passedCards === false) {
          console.log("Still waiting on passed cards - subscribing to topic notifyPassCardsReceived");
          this.subscribeNotifyPassCardsReceived();
        } else {
          console.log('Passed Cards received: ', passedCards);

          for (const [id, card] of Object.entries(passedCards)) {
            this.playerCards[id] = {
              id: id,
              suit: card.suit,
              value: card.value,
              imgPath: card.imgPath,
            };
        }
        }
      });
    },
    subscribePlayTurn() {
      this.stompClient.subscribe('/topic/playTurn', message => {
        let validTurn = JSON.parse(message.body);
        console.log('Card Played is valid: ', validTurn);

        if (validTurn === true) {
          delete this.playerCards[this.cardIDInPlay];
          this.cardIDInPlay = null;
        }
      });
    },
    subscribeNotifyPassingPhase() {
      let subscription = '/topic/notifyPassingPhase/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let inPassPhase = JSON.parse(message.body);
        console.log('Notified of passing phase - In pass phase: ', inPassPhase);
        this.passPhase = Boolean(inPassPhase);
      });
    },
    subscribeNotifyPassCardsReceived() {
      let subscription = '/topic/notifyPassCardsReceived/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let passedCards = JSON.parse(message.body);
 
        console.log(`[topic/notifyPassCardsReceived/${this.playerID.toString()}]: `, passedCards);

        for (const [id, card] of Object.entries(passedCards)) {
          console.log(`Assigning imgPath for card ID ${id}:`, card.imgPath);
          this.playerCards[id] = {
            id: id,         
            suit: card.suit,
            value: card.value,
            imgPath: card.imgPath,
          };
        }
      });
    },
    subscribeNotifyPlayerInitiative() {
      let subscription = 'topic/notifyPlayerInitiative/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let playerContent = JSON.parse(message.body);
        console.log(`[topic/notifyPlayerInitiative/${this.playerID.toString()}] - message received: `, playerContent);
      })
    },
    subscribeNotifyEndOfTrick() {
      let subscription = 'topic/notifyEndOfTrick/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isEndOfTrick = JSON.parse(message.body);
        console.log(`[topic/notifyEndOfTrick/${this.playerID.toString()}] - message received: `, isEndOfTrick);
      })
    },
    toggleCardSelection(cardID) {
      if (this.selectedCards.includes(cardID)) {
        // If already selected, remove from selectedCards
        this.selectedCards = this.selectedCards.filter(id => id !== cardID);
        this.playerPassCards = this.playerPassCards.filter(id => id !== cardID);
      } else if (this.selectedCards.length < 3) {
        this.selectedCards.push(cardID);
        this.playerPassCards.push(cardID);
      }

      // Trigger passing logic if 3 cards are selected
      if (this.selectedCards.length === 3) {
        const [card1, card2, card3] = this.selectedCards;
        this.publishPassCards(card1, card2, card3);

        delete this.playerCards[card1];
        delete this.playerCards[card2];
        delete this.playerCards[card3]; 

        this.selectedCards = [];
        this.playerPassCards = [];
      }
    },
    publishPassCards(card1, card2, card3) {
      this.stompClient.publish({
        destination: "/app/passCards",
        body: JSON.stringify({'playerID': this.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card1, card2, card3])})
      });
      this.playerPassCards = []; 
    },
    publishGetHand() {
      this.stompClient.publish({
        destination: "/app/getHand",
        body: JSON.stringify({'playerID': this.playerID, 'roomID': this.gameID})
      })
    },
    publishPlayTurn(card) {
      this.cardIDInPlay = card
      this.stompClient.publish({
        destination: "/app/playTurn",
        body: JSON.stringify({'playerID': this.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card])})
      });
    },
  }
};
</script>

<style scoped>
.startHearts {
  background: linear-gradient(to bottom right, #a2c2e3, #76b0e8);  /* Gradient background */
  background-image: url(./squares-background.jpg);
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

.selected-card {
  border: 2px solid gold; /* Highlight with a gold border */
  border-radius: 5px;     /* Optional: Rounded corners */
  transform: scale(1.1);  /* Slightly enlarge the image */
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.8); /* Add a glow effect */
}

.gameArea {
  position: relative;
  width: 100%;
  height: 100%;
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

.playerIDDisplay {
  position: absolute;
  top: 18%;
  left: 1%;
  color: wheat;
  background-color: black;
}

.player-top {
  /* position: absolute; */
  top: 0%;
  /* left: 45%; */
}

.player-top h3 {
  position: absolute;
  top: 30%;
  left: 50%;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.void-cards {
  position: absolute;
  top: 25%;
}

.void-cards h3 {
  position: absolute;
  top: 0%;
  left: 50%;
  color: black;
  font-size: 20px;
  font-weight: bold;
}

.player-right {
  right: 30%;
  bottom: 70%;
}

.player-right h3 {
  position: absolute;
  top: 30%;
  left: 50%;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.player-left {
  left: 30%;
  bottom: 70%;
}

.player-left h3 {
  position: absolute; /* Position it relative to its parent (the .player-left div) */
  top: 30%; /* Adjust the distance from the top */
  left: 50%;
  color: white;
  font-size: 20px; 
  font-weight: bold; /* Make it bold for better visibility */
}

.player-center {
  position: absolute;
  bottom: 0px;
  /* left: 35%; */
  /* transform: translateX(-50%); */
  align-content: center;
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

.card {
  width: 50px;
  height: 70px;
  background-color: #2c3e50; /* Card back color */
  border-radius: 5px;
  margin: 5px;
  display: inline-block;
}

/* Other Players top card display & Void Card top card */
.face-down img {
  width: 120px;
  height: 142px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.your-cards .card {
  background-color: #2980b9; /* Example color for your cards */
  color: white;
  text-align: center;
  font-size: 18px;
  line-height: 70px;
}

.your-cards .card img {
  width: 120px; /* Your Card width */
  height: 142px;  /* Your Card height */
  border-radius: 5px; /* Optional: to make the card corners rounded */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2); /* Optional: add a slight shadow to make cards stand out */
  transition: transform 0.2s ease; /* Optional: add a hover effect */
  cursor: pointer;
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

.passingPhasePrompt {
  position: absolute;
  bottom: 20%;
  background: linear-gradient(to bottom right, red, purple);
  color: white;
  font-size: 24px;
  font-weight: bold;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  text-align: center;
  width: 80%;
  max-width: 500px;
  box-sizing: border-box;
  margin: 0;
}

.passingPhasePrompt h3 {
  margin: 0;
}
</style>
