package com.crazycards.marcushurlbut;

import java.util.*;

public class Deck {

    private Stack<Card> deck = new Stack<Card>();

    public List<Card> getDeck() {
        return deck;
    }

    public void shuffleDeck() {
        deck = new Stack<Card>();

        initializeSuit(Suit.HEART);
        initializeSuit(Suit.DIAMOND);
        initializeSuit(Suit.SPADE);
        initializeSuit(Suit.CLUB);

        Collections.shuffle(deck);
    }

    public Card popDeck() {
        return deck.pop();
    }

    private void initializeSuit(Suit suit) {
        for (int index = 1; index < 14; index++) {
            Card card = new Card();
            card.suit = suit;
                
            String imgPathSuffix = "";
            switch (suit) {
                case HEART:
                    imgPathSuffix = "hearts.png";
                    break;

                case DIAMOND:
                    imgPathSuffix = "diamonds.png";
                    break;
                
                
            
                default:
                    break;
            }
            
            switch(index) {
                case 1:
                    card.name = Name.ACE;
                    card.value = 1;
                    card.imgPath = "";
                    break;
                case 2:
                    card.name = Name.TWO;
                    card.value = 2;
                    break;
                case 3:
                    card.name = Name.THREE;
                    card.value = 3;
                    break;
                case 4:
                    card.name = Name.FOUR;
                    card.value = 4;
                    break;
                case 5:
                    card.name = Name.FIVE;
                    card.value = 5;
                    break;
                case 6:
                    card.name = Name.SIX;
                    card.value = 6;
                    break;
                case 7:
                    card.name = Name.SEVEN;
                    card.value = 7;
                    break;
                case 8:
                    card.name = Name.EIGHT;
                    card.value = 8;
                    break;
                case 9:
                    card.name = Name.NINE;
                    card.value = 9;
                    break;
                case 10:
                    card.name = Name.TEN;
                    card.value = 10;
                    break;
                case 11:
                    card.name = Name.JACK;
                    card.value = 10;
                    break;
                case 12:
                    card.name = Name.QUEEN;
                    card.value = 10;
                    break;
                case 13:
                    card.name = Name.KING;
                    card.value = 10;
                    break;
                default:
                    break;
            }
            deck.push(card);
        }
    }
}
