<template>
  <div class="startHearts">
    <div class ="playerIDDisplay">
      <p>{{ playerID }}</p>
    </div>

    <div v-if="gameStarted" class="gameArea">

      <div v-if="passPhase && !playerPassedCards" class="passingPhasePrompt">
        <h3>Passing Phase!</h3>
        <p>Select 3 Cards to Pass</p>
      </div>

      <div v-if="playersTurn" class="yourTurnPrompt">
        <h3>Your Turn!</h3>
        <p>Select a card from the first suite played if you have one</p>
        <p>Otherwise play any card as a wildcard</p>
      </div>

      <!-- Player 2 (top) -->
      <div class="player player-top">
        <h3>{{ otherPlayerNames[0] }}</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />
        </div>
      </div>

      <!-- Player 3 (right) -->
      <div class="player player-right">
        <h3>{{ otherPlayerNames[1] }}</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />
        </div>
      </div>

      <!-- Player 4 (left) -->
      <div class="player player-left">
        <h3>{{ otherPlayerNames[2] }}</h3>
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
          <div v-for="(card, index) in voidCardsInPlay" :key="index" class="card stacked-void-cards" :style="getCardPosition(index)">
            <img 
              :src="require(`@/assets/card-images/PNG-cards/${card.imgPath}`)" 
              :alt="'Card ' + (index + 1)"
            />
          </div>
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
      selectedCards: [],  // Cards highlighted for passing
      stompClient: null,
      passPhase: true,
      playerPassedCards: false,
      cardIDInPlay: null, // Tracks card ID player attempts to put in play
      otherPlayerNames: null,

      numOfSubscribtionNotifyVoidCards: 0,  // Used for sorting void cards in play
      voidCardsInPlay: {},  // Cards in middle that are in play
      playersTurn: false  // Toggles when player is in turn
    };
  },
  mounted() {
    this.stompClient = this.$store.getters.stompClient;
    this.gameID = this.$store.getters.gameID;
    this.playerID = this.$store.getters.playerID;
    this.otherPlayerNames = this.$store.getters.otherPlayers;
    this.gameStarted = true;
    this.passPhase = true
    this.playerPassedCards = false

    this.subscribePlayTurn();
    this.subscribeGetHand();
    this.subscribePassCards();
    this.subscribeNotifyPassingPhase()
    this.subscribeNotifyVoidCards()
    this.subscribeNotifyPlayersTurn()
    this.subscribeNotifyEndOfTrick()

    this.publishGetHand();
  },
  methods: {
    ...mapActions(['storeGameID']),

    getCardPosition(index) {
      const offset = 10; // Adjust this to control the distance between stacked cards
      const xOffset = index * offset; // Spread cards horizontally based on their index
      const yOffset = index * offset; // Add slight vertical offset for each card
      return {
        transform: `translate(${xOffset}px, ${yOffset}px)`
      };
    },
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
    subscribeNotifyPlayersTurn() {
      let subscription = '/topic/notifyPlayersTurn/' + this.gameID.toString() + '/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isPlayersTurn = JSON.parse(message.body);
        console.log("isPlayersTurn: ", isPlayersTurn);
        // if (isPlayersTurn === true) {
        this.playersTurn = true;
        // }
      });
    },
    subscribePlayTurn() {
      let subscription = '/topic/playTurn/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        let validTurn = JSON.parse(message.body);
        console.log('Card placed was valid: ', validTurn);

        if (validTurn === true) {
          delete this.playerCards[this.cardIDInPlay];
          this.cardIDInPlay = null;
          this.playersTurn = false;
        }
      });
    },
    subscribeNotifyVoidCards() {
      let subscription = '/topic/notifyVoidCards/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let voidCards = JSON.parse(message.body);
        console.log("Void cards in play updated: ", voidCards);

        for (const [id, card] of Object.entries(voidCards)) {
          // Check if the card with this id already exists
          if (!Object.prototype.hasOwnProperty.call(this.voidCardsInPlay, id)) {
            console.log(`Assigning imgPath for card ID ${id}:`, card.imgPath);
            this.voidCardsInPlay[id] = {
              id: id,         
              suit: card.suit,
              value: card.value,
              imgPath: card.imgPath,
              order: this.numOfSubscribtionNotifyVoidCards
            };
          }
        }
        this.voidCardsInPlay = Object.values(this.voidCardsInPlay)
          .sort((a, b) => a.order - b.order);
        this.numOfSubscribtionNotifyVoidCards += 1;
      });
    },
    subscribeNotifyPassingPhase() {
      let subscription = '/topic/notifyPassingPhase/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let inPassPhase = JSON.parse(message.body);
        console.log('Notified of passing phase - In pass phase: ', inPassPhase);
        this.passPhase = Boolean(inPassPhase);
        this.playerPassedCards = false;
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
    subscribeNotifyEndOfTrick() {
      let subscription = '/topic/notifyEndOfTrick/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isEndOfTrick = JSON.parse(message.body);
        console.log(`[topic/notifyEndOfTrick/${this.playerID.toString()}] - message received: `, isEndOfTrick);
        this.voidCardsInPlay = {}
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
      this.playerPassedCards = true;
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

.card.stacked-void-cards {
  position: absolute;
  top: 0; /* Default position at the top of the parent */
  left: 0; /* Default position at the left of the parent */
  transition: transform 0.3s ease-in-out;
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

.yourTurnPrompt {
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

.yourTurnPrompt h3 {
  margin: 0;
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
