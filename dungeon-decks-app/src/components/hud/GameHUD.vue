<template>
  <div class="game-hud">
    <h3 class="player-name">{{ username }}</h3>
      <div class="grid-container">
        <!-- <div class="grid-item">Hand Sort</div> -->
        <div class="grid-item"> Hand Sort
          <div class="radio-container">
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('Default')"><br>Default
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('CHSD')"><br>C/H/S/D
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('SDCH')"><br> S/D/C/H
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('DCHS')"><br> D/C/H/S
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('HSDC')"><br> H/S/D/C
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('CDSH')"><br> C/D/S/H
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('SHCD')"><br> S/H/C/D
            </label>
            <label class="radio-label">
              <input type="radio" name="sort-action"  v-model="selectedOption" @change="handleRadioChange('DHCS')"><br> D/H/C/S
            </label>
          </div>
        </div>

        <div class="grid-item">
          <strong>Score</strong><br>
          <span class="score">{{ score || 0 }} pts</span>
        </div>
        <div class="grid-item">
          <strong>Round </strong><br>
          <span class="round-number">0</span>
        </div>
        <div class="grid-item">
          <strong>Card Color</strong><br>
          <div>
            <img 
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_black.png"
              @click="changePlayerCardColor('card_back_black.png')"
            >
            <img
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_blue.png"
              @click="changePlayerCardColor('card_back_blue.png')"
            >
            <img
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_red.png"
              @click="changePlayerCardColor('card_back_red.png')"
            >
            <img
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_green.png"
              @click="changePlayerCardColor('card_back_green.png')"
            >
            <img
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_orange.png"
              @click="changePlayerCardColor('card_back_orange.png')"
            >
            <img
              class="card_color_select"
              src="@/assets/player-card-backs/card_back_purple.png"
              @click="changePlayerCardColor('card_back_purple.png')"
            >
          </div>
        </div>
        <div class="grid-item">
          <button
            class="pass-button"
            :class="{ 'gold-border': selected.length === 3 && passPhase }"
            @click="handlePass"
          >
            PASS
          </button>
        </div>
        <div class="grid-item">
          <button
            class="play-button"
            :class="{ 'gold-border': selected.length === 1 && !passPhase }"
            @click="handlePlay"
          >
          PLAY
          </button>
        </div>
    </div>
  </div>
</template>
  
  
<script>
import { mapActions } from 'vuex';
import { mapState } from 'vuex';

export default {
  name: "GameHUD",
  data() {
    return {
      selectedOption: 'Default',
      nameOrder: {
        'ACE': 12,
        'KING': 11,
        'QUEEN': 10,
        'JACK': 9,
        'TEN': 8,
        'NINE': 7,
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
  computed: {
    ...mapState(['hand'])
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
    ...mapActions(['storeHand', 'storePlayerCardFile']),

    changePlayerCardColor(filename) {
      this.storePlayerCardFile(filename);
    },
    handleRadioChange(sortType) {
      if (sortType == 'Default') {
        this.defaultSort();
      } else if (sortType == 'SDCH') {
        this.SDCHSort();
      } else if (sortType == 'CHSD') {
        this.CHSDSort();
      } else if (sortType == 'DCHS') {
        this.DCHSSort();
      } else if (sortType == 'HSDC') {
        this.HSDCSort();
      } else if (sortType == 'CDSH') {
        this.CDSHSort();
      } else if (sortType == 'SHCD') {
        this.SHCDSort();
      } else if (sortType == 'DHCS') {
        this.DHCSSort();
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
        'DIAMOND': 13,
        'CLUB': 26,
        'HEART': 39
      }
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    CHSDSort() {
      const suitOrder = {
        'CLUB': 0,
        'HEART': 13,
        'SPADE': 26,
        'DIAMOND': 39
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    DCHSSort() {
      const suitOrder = {
        'DIAMOND': 0,
        'CLUB': 13,
        'HEART': 26,
        'SPADE': 39
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    HSDCSort() {
      const suitOrder = {
        'HEART': 0,
        'SPADE': 13,
        'DIAMOND': 26,
        'CLUB': 39
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    CDSHSort() {
      const suitOrder = {
        'CLUB': 0,
        'DIAMOND': 13,
        'SPADE': 26,
        'HEART': 39
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    SHCDSort() {
      const suitOrder = {
        'SPADE': 0,
        'HEART': 13,
        'CLUB': 26,
        'DIAMOND': 39
      };
      let sortedCards = this.handleSort(suitOrder);
      this.storeHand(sortedCards);
    },
    DHCSSort() {
      const suitOrder = {
        'DIAMOND': 0,
        'HEART': 13,
        'CLUB': 26,
        'SPADE': 39
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
  max-height: 60vh;
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
  grid-template-columns: 1fr 1fr 2fr;
  grid-template-rows: 2fr 1fr 1fr ;
  gap: 10px;
  text-align: center;
  font-size: 1em;
}

.grid-item {
  padding: 2px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 5px;
}

.radio-container {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr;
  gap: 10px;
}

.grid-item:nth-child(1) {
  grid-column: span 3; /* Span first grid item span across all columns */
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

.gold-border {
  border: 3px solid gold;
}

.score, .round-number {
  font-weight: bold;
  font-size: 1.5em;
}

.card_color_select {
  max-width: 20px;
  cursor:context-menu;
}

.card_color_select:hover {
  border: 1px solid gold;
}
</style>
  