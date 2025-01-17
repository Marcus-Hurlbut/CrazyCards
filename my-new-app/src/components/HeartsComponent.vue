<template>
  <div class="startHearts">
    <div class ="playerIDDisplay">
      <!-- Uncomment to show player's UUID -->
      <!-- <p>{{ playerID }}</p>  -->
    </div>
    <div v-if="gameStarted" class="gameArea">

      <Scoreboard :username="this.$store.getters.username" :otherPlayerNames="otherPlayerNames" :usernameToScore="usernameToScore" />
      <InvalidCardPrompt :invalidTurn="invalidTurn" :playersTurn="playersTurn" :passPhase="passPhase" />
      <PassingPhasePrompt :passPhase="passPhase" :playerPassedCards="playerPassedCards" />
      <YourTurnPrompt :playersTurn="playersTurn" :invalidTurn="invalidTurn" />
      <WinnerPrompt :trickWinnerName="trickWinnerName" :winnerName="winnerName" />

      <div v-if="winnerName != null" class="winnerPrompt">
        <h3>Game Over</h3>
        <p>{{ winnerName }} won the game!</p>
      </div>

      <!-- Player 2 (left) -->
      <div class="player player-left">
        <h3>{{ otherPlayerNames[0] }}</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark"/>

          <div v-if="this.voidCardsInPlay[otherPlayerNames[0]] != null" class="card stacked-void-cards" :style="getCardPosition(1, this.voidCardsInPlay[otherPlayerNames[0]])">
            <img
              :src="require(`./card-images/PNG-cards/${voidCardsInPlay[otherPlayerNames[0]].imgPath}`)" 
              :alt="'Card 4'"
              />
          </div>
        </div>
      </div>

      <!-- Player 3 (top) -->
      <div class="player player-top">
        <h3>{{ otherPlayerNames[1] }}</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark" />

          <div v-if="this.voidCardsInPlay[otherPlayerNames[1]] != null" class="card stacked-void-cards" :style="getCardPosition(1, this.voidCardsInPlay[otherPlayerNames[1]])">
            <img
              :src="require(`./card-images/PNG-cards/${voidCardsInPlay[otherPlayerNames[1]].imgPath}`)" 
              :alt="'Card 2'"
              />
          </div>
        </div>
      </div>

      <!-- Player 4 (right) -->
      <div class="player player-right">
        <h3>{{ otherPlayerNames[2] }}</h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_dark.png" alt="back_dark"/>

          <div v-if="this.voidCardsInPlay[this.otherPlayerNames[2]] != null" class="card stacked-void-cards" :style="getCardPosition(1, this.voidCardsInPlay[otherPlayerNames[2]])">
            <img
              :src="require(`./card-images/PNG-cards/${voidCardsInPlay[otherPlayerNames[2]].imgPath}`)" 
              :alt="'Card 3'"
              />
          </div>
        </div>
      </div>

      <!-- Your Cards (bottom) -->
      <div class="player player-center">
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
        <h3> {{ this.$store.getters.username  }} </h3>
        <div class="card face-down">
          <img src="./card-images/PNG-cards/back_light.png" alt="back_dark" />

          <div v-if="this.voidCardsInPlay[this.$store.getters.username] != null" class="card stacked-void-cards" :style="getCardPosition(1, this.voidCardsInPlay[otherPlayerNames[2]])">
            <img
              :src="require(`./card-images/PNG-cards/${voidCardsInPlay[this.$store.getters.username].imgPath}`)" 
              :alt="'Card 4'"
              />
          </div>
          <!-- <div v-for="(card, index) in voidCardsInPlay" :key="index" class="card stacked-void-cards" :style="getCardPosition(index, card)">
            <img 
              :src="require(`@/assets/card-images/PNG-cards/${card.imgPath}`)" 
              :alt="'Card ' + (index + 1)"
            />
          </div> -->
        </div>
      </div>

    </div>
  </div>
</template>
  
<script>
import { mapActions } from 'vuex';
import { mapState } from 'vuex';
import WinnerPrompt from './prompts/WinnerPrompt.vue';
import PassingPhasePrompt from './prompts/PassingPhasePrompt.vue';
import InvalidCardPrompt from './prompts/InvalidCardPrompt.vue';
import Scoreboard from './hud/Scoreboard.vue';
import YourTurnPrompt from './prompts/YourTurnPrompt.vue';

export default {
  name: 'HeartsComponent',
  components:  { 
    WinnerPrompt,
    PassingPhasePrompt,
    InvalidCardPrompt,
    Scoreboard,
    YourTurnPrompt
  },
  computed: {
    ...mapState(['isLobbyCreated', 'otherPlayers', 'username', 'stompClient', 'gameID', 'playerID', 'playerIndex']),
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
      playersTurn: false,  // Toggles when player is in turn
      usernameToScore: {},
      validTurn: true,
      invalidTurn: false,
      
      trickWinnerName: null,
      winnerName: null,
    };
  },
  mounted() {
    this.stompClient = this.$store.getters.stompClient;
    this.gameID = this.$store.getters.gameID;
    this.playerID = this.$store.getters.playerID;
    this.otherPlayerNames = this.$store.getters.otherPlayers;
    this.setPlayerOrientationRing();

    this.displayName - this.$store.getters.username;
    this.gameStarted = true;
    this.passPhase = true;
    this.playerPassedCards = false;

    this.subscribePlayTurn();
    this.subscribeGetHand();
    this.subscribePassCards();
    this.subscribeNotifyPassingPhase();
    this.subscribeNotifyVoidCards();
    this.subscribeNotifyPlayersTurn();
    this.subscribeNotifyEndOfTrick();
    this.subscribeNotifyEndOfRound();
    this.subscribeNotifyEndOfGame();

    this.subscribeUpdateScoreboard()

    this.publishGetHand();
  },
  methods: {
    ...mapActions(['storeGameID', 'storeOtherPlayers']),

    setPlayerOrientationRing() {
      let otherPlayer_1 = this.otherPlayerNames[0]
      let otherPlayer_2 = this.otherPlayerNames[1]
      let otherPlayer_3 = this.otherPlayerNames[2]

      switch (this.$store.getters.playerIndex) {
        case 1:
          this.otherPlayerNames[0] = otherPlayer_2;
          this.otherPlayerNames[1] = otherPlayer_3;
          this.otherPlayerNames[2] = otherPlayer_1;
          break;
        case 2:
          this.otherPlayerNames[0] = otherPlayer_3;
          this.otherPlayerNames[1] = otherPlayer_1;
          this.otherPlayerNames[2] = otherPlayer_2;
          break;
        default:
          break;
      }

      this.storeOtherPlayers(this.otherPlayerNames);
      console.log("!!!!",this.otherPlayerNames );
    },

    getUsernameCardPlayed(username) {
      return this.voidCardsInPlay[username].imgPath;
    },

    getUsernameScore(username) {
      return this.usernameToScore[username] || 0;
    },

    announceTrickWinner(winnerName) {
      this.trickWinnerName = winnerName;
      setTimeout(() => {
        this.voidCardsInPlay = {}
        this.trickWinnerName = null;
      }, 4000);
    },

    getCardPosition(index, card) {
      const offset = 10;
      const xOffset = index * offset;
      const yOffset = index * offset;
      console.log('get card position for', card);

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
        this.playersTurn = true;
      });
    },
    subscribePlayTurn() {
      let subscription = '/topic/playTurn/' + this.playerID.toString();
      this.stompClient.subscribe(subscription, message => {
        this.validTurn = JSON.parse(message.body);
        console.log('Card placed was valid: ', this.validTurn);

        if (this.validTurn === true) {
          delete this.playerCards[this.cardIDInPlay];
          this.cardIDInPlay = null;
          this.playersTurn = false;
          this.invalidTurn = false;
        } else {
          console.log("Setting invalid turn to true - playerPassedCards: ", this.playerPassedCards);
          this.invalidTurn = true;
        }
      });
    },
    subscribeNotifyVoidCards() {
      let subscription = '/topic/notifyVoidCards/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let usernameToVoidCards = JSON.parse(message.body);
        console.log("Void cards in play updated: ", usernameToVoidCards);

        let playedByUsername = Object.keys(usernameToVoidCards)[0];
        let cardIDtoVoidCard = usernameToVoidCards[playedByUsername];

        for (const [id, voidCard] of Object.entries(cardIDtoVoidCard)) {
          this.voidCardsInPlay[playedByUsername] = {
            id: id,  
            suit: voidCard.suit,
            value: voidCard.value,
            imgPath: voidCard.imgPath,
            order: this.numOfSubscribtionNotifyVoidCards,
            playedBy: playedByUsername
          };
        }
        this.numOfSubscribtionNotifyVoidCards += 1;
      });
    },
    subscribeNotifyPassingPhase() {
      let subscription = '/topic/notifyPassingPhase/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let inPassPhase = JSON.parse(message.body);
        console.log('Notified of passing phase - In pass phase: ', inPassPhase);
        this.passPhase = Boolean(inPassPhase);

        if (this.passPhase === true) {
          this.playerPassedCards = false;
        }
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
        let name = JSON.parse(message.body);
        console.log(`[topic/notifyEndOfTrick/${this.gameID.toString()}] - trick winner name received: `, name);
        
        // this.voidCardsInPlay = {}
        this.announceTrickWinner(name)
      })
    },
    subscribeNotifyEndOfRound() {
      let subscription = '/topic/notifyEndOfRound/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let isEndOfRound = JSON.parse(message.body);
        console.log(`[topic/notifyEndOfRound/${this.gameID.toString()}] - message received: `, isEndOfRound)
        
        this.publishGetHand();
      })
    },
    subscribeUpdateScoreboard() {
      let subscription = '/topic/updateScoreboard/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        let scoreboardMap = JSON.parse(message.body);
        console.log(`[topic/updateScoreboard/${this.gameID.toString()}] - message received: `, scoreboardMap)
        this.usernameToScore = scoreboardMap;
      })
    },
    subscribeNotifyEndOfGame() {
      let subscription = '/topic/subscribeNotifyEndOfGame/' + this.gameID.toString();
      this.stompClient.subscribe(subscription, message => {
        this.winnerName = JSON.parse(message.body);
        console.log(`[topic/subscribeNotifyEndOfGame/${this.gameID.toString()}] - winner name: `, this.winnerName)
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

      if (this.trickWinnerName == null) {
        this.stompClient.publish({
          destination: "/app/playTurn",
          body: JSON.stringify({'playerID': this.playerID, 'roomID':this.gameID, 'cardIDs': JSON.stringify([card])})
        });
      }
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
  color: white;
  text-align: center;
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center; 
  align-items: center; 
}

.startHeartsContent {
  margin-bottom: 50em;
}

.selected-card {
  border: 2px solid gold;
  border-radius: 5px;
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.8); /* glow effect */
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
  top: 0%;
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
  /* z-index: 0; */
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
  position: absolute;
  top: 30%;
  left: 50%;
  color: white;
  font-size: 20px; 
  font-weight: bold; 
}

.player-left h3, .player-right h3, .player-top h3 {
  color: white;
  z-index: 10;
}


.player-center {
  position: absolute;
  bottom: 0px;
  align-content: center;
  text-align: center;
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
  z-index: 0;
}

/* Other Players top card display & Void Card top card */
.face-down img {
  width: 120px;
  height: 142px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
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


</style>
