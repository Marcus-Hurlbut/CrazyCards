<template>
    <div class="spider-solitaire">
        <div class="game-cards">
          <div class="deck">
            <h2>Deals left: {{ dealsLeft }} </h2>
            <Card
            :fileName="'card_back_black.png'"
            @click="publishHitDeck"
            />
          </div>
          <div class="foundations">
            <div v-for="(card, columnIndex) in foundation" :key="columnIndex">
                <div v-if="foundation[columnIndex][0] == null" >
                    <Card
                    :fileName="card.imgPath || 'card_back_red.png'"
                />
                </div>
                <div v-if="foundation[columnIndex][0] != null" >
                    <Card
                    :fileName="card.imgPath || 'card_back_purple.png'"
                />
                </div>
              </div>
            </div>
        
            <div class="tableaus">
                <div v-for="(column, columnIndex) in tableau" :key="columnIndex" class="column">
                    <div v-for="(card, index) in column" :key="index" class="card" :style="getCardPosition(index, columnIndex)">
                        <Card
                        :fileName="card.imgPath"
                        @click="toggleCardSelection(card, card.value, columnIndex)"
                        :isSelected="this.selectedCard == card"
                        />
                    </div>
                </div>
            </div>
        </div>
    </div>
  </template>
  
  <script>
  import Card from '../objects/Card.vue';
  import { mapState } from 'vuex';

  export default {
    name: "SpiderSolitaireComponent",
    data() {
      return {
        gameID: null,
        tableau: [[], [], [], [], [], [], [], [], [], []],
        foundation: [[], [], [], [], [], [], [], []],
        foundationsWon: 0,
        winnerName: null,

        selectedCard: null,
        source: null,
        validTurn: false,
        cardsPlayed: [],
        dealsLeft: 5,
        
        from: null,
        to: null,
      };
    },
    computed: {
        ...mapState(['username', 'stompClient', 'lobbyID', 'playerID']),
    },
    components: {
      Card,
    },
    mounted() {
      this.initializeTableaus();
      this.subscribeRevealTopCards();
      this.subscribePlayTurn();
      this.subscribeHitDeck();
      this.subscribeNotifyNewRevealedCard();
      this.subscribeNotifyCardsTransferredTableau();
      this.subscribeNotifyFoundations();
      this.subscribeNumberDealsLeft();

      this.publishRevealTopCards();
    },
    methods: {
      getCardPosition(index, columnIndex) {
        const offset = 25;
        const yOffset = index * offset;
  
        return {
          transform: `translateY(${yOffset}px)`,
          zIndex: columnIndex,
        };
      },
      toggleCardSelection(card, value, column) {
        if (value == -1 && this.tableau[column].length > 1) {
            return
        }

        if (this.selectedCard == null) {
          this.source = column;
          this.selectedCard = card;
        }
        else if (card == this.selectedCard) {
          this.selectedCard = null;
          this.source = null;
        } 
        else {
            let cards = [];
            this.tableau[this.source].forEach(element => {
                cards.push(element);
            });
            cards.reverse();
            let cardList = []

            for (const card of cards) {
              cardList.push(card)
              if (this.selectedCard.id === card.id) {
                break;
              }
            }
            cardList.reverse();
            this.publishPlayTurn(cardList, column, this.source);
            this.selectedCard = null;
        }
      },
      addCardToTableau(card, index, replaceTop)  {
        if (replaceTop) {
          let length = this.tableau[index].length - 1;
          this.tableau[index][length] = ({
            id: card.id,
            suit: card.suit,
            name: card.name,
            value: card.value,
            imgPath: card.imgPath,
          });
        } else {
          this.tableau[index].push(card)
        }
      },
      subscribeRevealTopCards() {
        let subscription = '/topic/spiderSolitaire/revealTopCards/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            let topCards = JSON.parse(message.body);
            console.log(topCards)

            let index = 0;
            for (const card of topCards) {
              let replaceTop = true;
              this.addCardToTableau(card, index, replaceTop);
              index += 1;
            }
        })
      },
      subscribeNotifyNewRevealedCard() {
        let subscription = '/topic/spiderSolitaire/notifyNewRevealedCard/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            let destinationToCard = JSON.parse(message.body);
            console.log('Revealed Card: ', destinationToCard);

            setTimeout(() => {
            for (const [column, card] of Object.entries(destinationToCard)) {
              let replaceTop = true;
              this.addCardToTableau(card, column, replaceTop);
            }
          }, 250);
        });
      },
      subscribeHitDeck() {
        let subscription = '/topic/spiderSolitaire/hitDeck/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            let cardsAdded = JSON.parse(message.body);
            console.log("Cards dealt from deck: ", cardsAdded);

            let index = 0;
            for (const card of cardsAdded) {
                let objCard = ({id: card.id, suit: card.suit, name: card.name, value: card.value, imgPath: card.imgPath});
                this.addCardToTableau(objCard, index, false);
                index += 1;
            }
        });
      },
      subscribeNotifyCardsTransferredTableau() {
        let subscription = '/topic/spiderSolitaire/notifyCardsTransferredTableau/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            let destinationToCards = JSON.parse(message.body);
            console.log('Cards moved: ', destinationToCards);

            // Replace origin card with placeholder card if empty
            for (let i = 0; i < this.cardsPlayed.length; i++) {
              this.tableau[this.from].pop();
              if (this.tableau[this.from].length == 0) {
                let card = ({id: -1, name: null, suit: null, value: -1, imgPath: 'card_back_red.png'});
                this.addCardToTableau(card, this.from, false);
              }
            }
            this.from = null;

            // Finally add card to its new location
            for (const [column, cards] of Object.entries(destinationToCards)) {
              if (Array.isArray(cards)) {
                cards.forEach(card => {
                  this.tableau[column].push({
                      id: card.id,
                      suit: card.suit,
                      name: card.name,
                      value: card.value,
                      imgPath: card.imgPath,
                  });
                });
              }
            }
        });
      },
      subscribeNotifyFoundations() {
        let subscription = '/topic/spiderSolitaire/notifyFoundations/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            console.log('New Foundation! ', message.body);
            let locationToTopcard = JSON.parse(message.body);
            let card = null
            let column = -1;

            // If top card needs to be revealed, reveal now
            for (const [index, topCard] of Object.entries(locationToTopcard)) {
              column = index;
              if (topCard.id == null) {
                  card = ({id: -1, name: null, suit: null, value: -1, imgPath: 'card_back_red.png'});
              } else {
                  card = ({id: topCard.id, name: topCard.name, suit: topCard.suit, value: topCard.value, imgPath: topCard.imgPath});
              }
            }

            // Remove cards from tableau & add to foundation
            for (let i = 0; i < 13; i++) {
              let card = this.tableau[column].pop();
              console.log('removing card: ', card);
              this.foundation[this.foundationsWon].push(card);
            }

            this.foundationsWon += 1;

            // Add new top card or placeholder
            if (this.tableau[column].length == 0) {
              this.addCardToTableau(card, column, false)
            } else {
              this.addCardToTableau(card, column, true)
            }
        });
      },
      subscribeNumberDealsLeft() {
        let subscription = '/topic/spiderSolitaire/notifyNumberDealsLeft/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            this.dealsLeft = JSON.parse(message.body);
            console.log('Number of deals left: ', this.dealsLeft);
        });
      },
      subscribePlayTurn() {
        let subscription = '/topic/spiderSolitaire/playTurn/'+ this.$store.getters.gameID.toString() + '/' + this.$store.getters.playerID.toString();
        this.stompClient.subscribe(subscription, message => {
            this.validTurn = JSON.parse(message.body);
            console.log('valid Turn: ', this.validTurn);
        });
      },
      publishPlayTurn(cards, to, from) {
        this.from = from;
        this.to = to;
        this.cardsPlayed = cards;
        this.stompClient.publish({
          destination: "/app/spiderSolitaire/playTurn",
          body: JSON.stringify({'playerID': this.$store.getters.playerID.toString(), 'gameID': this.$store.getters.gameID.toString(), 'cards': JSON.stringify(cards), 'to': to, 'from': from })
        })
      },
      publishRevealTopCards() {
        this.stompClient.publish({
          destination: "/app/spiderSolitaire/revealTopCards",
          body: JSON.stringify({'playerID': this.$store.getters.playerID.toString(), 'gameID': this.$store.getters.gameID.toString() })
        })
      },
      publishHitDeck() {
        this.stompClient.publish({
          destination: "/app/spiderSolitaire/hitDeck",
          body: JSON.stringify({'playerID': this.$store.getters.playerID.toString(), 'gameID': this.$store.getters.gameID.toString() })
        })
      },
      initializeTableaus() {
        this.tableau = [[], [], [], [], [], [], [], [], [], []];
  
        for (let i = 0; i < 4; i++) {
          for (let j = 0; j < 6; j++) {
            this.tableau[i].push({
              id: -1,
              name: null,
              suit: null,
              value: -1,
              imgPath: 'card_back_black.png',
            });
          }
        }
  
        for (let i = 4; i < 10; i++) {
          for (let j = 0; j < 5; j++) {
            this.tableau[i].push({
              id: -1,
              name: null,
              suit: null,
              value: -1,
              imgPath: 'card_back_black.png',
            });
          }
        }
  
        console.log(this.tableau);
      },
    },
  };
  </script>
  
  <style scoped>
  .spider-solitaire {
    position: relative;
    transform: translate(5%, 10%);
  }
  .tableaus {
    display: flex;
    gap: 8vw;
    position: relative;
    transform: translateX(0%);
    justify-content: left;
  }

  .deck {
    position: absolute;
    transform: translate(0%, 0%);
  }

  .deck h2 {
    position: absolute;
    color:rgb(248, 239, 104);
    text-align: center;
    z-index: 1000;
    font-family:'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS', sans-serif;
  }

  .game-cards {
    position: relative;
  }

  .foundations {
    display: flex;
    justify-content: left;
    gap: 1.3vw;
    transform: translate(16%,0%);
  }
  
  .column {
    flex-direction: column;
    align-items: right;
    
  }

  .card {
    position: absolute;
  }
  </style>
  