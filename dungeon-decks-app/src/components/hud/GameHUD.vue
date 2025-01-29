<template>
  <div class="game-hud">
    <h3 class="player-name">{{ username }}</h3>
      <div class="grid-container">
        <div class="grid-item">Hand Sort</div>
        <div class="grid-item">
          <div class="radio-container">
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('Default')"> Default
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('CHSD')"> C/H/S/D
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('SDCH')"> S/D/C/H
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('DCHS')"> D/C/H/S
            </label>
          </div>
        </div>
        <div class="grid-item">
          <strong>Score</strong>
        </div>
        <div class="grid-item">
          <span class="score">{{ score || 0 }}</span>
        </div>
        <div class="grid-item">
          <strong>Round Number</strong>
        </div>
        <div class="grid-item">
          <span class="round-number">0</span>
        </div>
        <div class="grid-item">
          <!-- Pass Button with dynamic class -->
          <button
            class="pass-button"
            :class="{ 'gold-border': selected.length === 3 && passPhase }"
            @click="handlePass"
          >
            Pass Cards
          </button>
        </div>
        <div class="grid-item">
          <!-- Play Button with dynamic class -->
          <button
            class="play-button"
            :class="{ 'gold-border': selected.length === 1 && !passPhase }"
            @click="handlePlay"
          >
          Play Card
        </button>
      </div>
    </div>
  </div>
</template>
  
  
<script>
import { mapActions } from 'vuex';

export default {
  name: "GameHUD",
  data() {
    return {
      selectedOption: 'Default',
      nameOrder: {
        'ACE': 11,
        'KING': 10,
        'QUEEN': 9,
        'JACK': 8,
        'TEN': 17,
        'NINE': 6,
        'EIGHT': 6,
        'SEVEN': 5,
        'SIX': 4,
        'FIVE': 3,
        'FOUR': 2,
        'THREE': 1,
        'TWO': 0
      },
    }
  },
  props: {
    username: String,
    score: Number,
    hand: Object,
    selected: Object,
    passPhase: Boolean,
    handlePass: Function,
    handlePlay: Function,
  },
  methods: {
    ...mapActions(['storeHand']),

    handleRadioChange(sortType) {
      if (sortType == 'Default') {
        this.defaultSort();
      } else if (sortType == 'SDCH') {
        this.SDCHSort();
      } else if (sortType == 'CHSD') {
        this.CHSDSort();
      } else if (sortType == 'DCHS') {
        this.DCHSSort();
      }
    },
    handleSort(suitOrder) {
      let sortedCards = {};
      for (const [id, card] of Object.entries(this.hand)) {
        sortedCards[id] = {
          id: id,
          name: card.name,
          suit: card.suit,
          value: card.value,
          imgPath: card.imgPath,
          order: suitOrder[card.suit] + this.nameOrder[card.name]
        };
      }
      return sortedCards;
    },
    defaultSort() {
        let sortedCards = {}
        for (const [id, card] of Object.entries(this.hand)) {
          sortedCards[id] = {
            id: id,
            name: card.name,
            suit: card.suit,
            value: card.value,
            imgPath: card.imgPath,
            order: Number(id)
          };
        }
        this.storeHand(sortedCards);
    },
    SDCHSort() {
      const suitOrder = {
        'SPADE': 0,
        'DIAMOND': 12,
        'CLUB': 25,
        'HEART': 38
      }
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    CHSDSort() {
      const suitOrder = {
        'CLUB': 0,
        'HEART': 12,
        'SPADE': 25,
        'DIAMOND': 38
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    DCHSSort() {
      const suitOrder = {
        'DIAMOND': 0,
        'CLUB': 12,
        'HEART': 25,
        'SPADE': 38
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    }
  }
}
</script>
  
<style scoped>
@font-face {
  font-family: 'GatsbyFont';
  src: url('@/assets/ttf/HeraldSquareTwoNF.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.game-hud {
  position: fixed;
  bottom: 1%;
  right: 1%;
  width: 20%;
  background-color: rgba(0, 0, 0, 0.7);
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.5);
  color: white;
  font-family: Arial, sans-serif;
  z-index: 1000;
}

h3.player-name {
  text-align: center;
  color: rgb(0, 0, 0);
  text-shadow: 0px 0px 2px #f6f8f8, 0px 0px 2px #ffffff;
  font-family: 'GatsbyFont', sans-serif;
  font-size: 2.7em;
  max-width: 25vw;
  margin: 0 0 10px;
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Two columns in grid HUD */
  gap: 10px;
  text-align: center;
  font-size: 1em;
}

.grid-item {
  padding: 8px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 5px;
}

.radio-container {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Two columns for radio buttons */
  gap: 10px;
}

.radio-label {
  display: block;
  padding: 5px;
  font-size: 0.9em;
}

input[type="radio"] {
  margin-right: 5px;
  vertical-align: middle;
}

.pass-button, .play-button {
  width: 100%;
  padding: 10px;
  font-size: 1.2em;
  color: white;
  background: linear-gradient(to top left, #3742d3, red);
  font-family: Impact, Haettenschweiler, 'Arial Narrow Bold', sans-serif;
  border: 3px solid black;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pass-button:hover, .play-button:hover {
  background: linear-gradient(to bottom left, #3742d3, red);
  transform: translateY(-2%);
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
}

/* Gold border for selected buttons */
.gold-border {
  border: 3px solid gold;
}

.score, .round-number {
  font-weight: bold;
  font-size: 1.5em;
}
</style>
  