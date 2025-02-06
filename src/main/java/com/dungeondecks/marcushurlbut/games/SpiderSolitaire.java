package com.dungeondecks.marcushurlbut.games;

import java.util.*;
import java.util.stream.Collectors;

import com.dungeondecks.marcushurlbut.Game;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.games.card.Name;
import com.dungeondecks.marcushurlbut.games.card.Suit;
import com.dungeondecks.marcushurlbut.games.deck.Deck;

public class SpiderSolitaire extends Game {
    public Deck deck = new Deck();
    @SuppressWarnings("unchecked")
    public Stack<Card>[] tableau = new Stack[10];
    public Stack<Card>[] foundation = new Stack[8];

    int NUM_OF_TABLEAUS = 10;
    public int numOfFoundations = 0;
    public int numOfSuits = 4;
    public Player player;

    public int dealLeft = 5;
    public Card revealedCard = null;
    public boolean notifyFoundation = false;
    public int foundationColumn = -1;

    public SpiderSolitaire(UUID gameID, int numOfSuits) {
        this.gameID = gameID;
        this.numOfSuits = numOfSuits;
    }

    public void initialize(Player[] players) {
        active = true;
        shuffleDeck();
        initializePlayer(players[0]);

        for (int i = 0; i < 10; i++) {
            tableau[i] = new Stack<Card>();
        }

        for (int i = 0; i < 8; i++) {
            foundation[i] = new Stack<Card>();
        }
        initialDeal();
    }

    public void initializePlayer(Player player) {
        this.player = player;
    }

    public void shuffleDeck() {
        deck = new Deck();

        Deck deck1 = new Deck();
        Deck deck2 = new Deck();
        deck1.shuffleDeck(numOfSuits);
        deck2.shuffleDeck(numOfSuits);

        while (!deck1.isEmpty()) {
            deck.addToDeck(deck1.popDeck());
        }

        while (!deck2.isEmpty()) {
            deck.addToDeck(deck2.popDeck());
        }
    }

    public void initialDeal() {
        for (int num = 0; num < 44; num++) {
            int index = num % 10;
            Card card = deck.popDeck();
            card.setHidden(true);
            tableau[index].add(card);
        }
        for (int num = 0; num < 10; num++) {
            Card card = deck.popDeck();
            card.setHidden(false);
            tableau[num].add(card);
        }
    }

    private List<Card> dealCardsToTableaus() {
        List<Card> cards = new ArrayList<Card>();
        for (int num = 0; num < NUM_OF_TABLEAUS; num++) {
            Card card = deck.popDeck();
            card.setHidden(false);
            tableau[num].add(card);
            cards.add(card);
        }
        return cards;
    }

    public boolean validateTurn(List<Card> cardStack, int from, int to) {
        // Validate card(s) in the stack are in order & can be moved to another tableau
        if (!validateCardCanMove(cardStack)) {
            System.err.println("These cards can't be moved from the source: " + from);
            return false;
        }

        // Validate cards(s) in the stack & are in order with tableau placed on
        if (!validateCardsDestination(cardStack, to)) {
            System.err.println("These cards can't be placed to destination: " + to);
            return false;
        }

        return true;
    }

    public Stack<Card> getCopyStack(int column) {
        Stack<Card> entry = new Stack<Card>();
        entry = tableau[column].stream()
            .map(card -> new Card(card.suit, card.name, card.value, card.imgPath))
            .collect(Collectors.toCollection(Stack::new));

        return entry;
    }

    public boolean validateCardCanMove(List<Card> cardStack) {
        int next = cardStack.get(0).value;
        Suit leadingSuit = cardStack.get(0).suit;

        for (Card card: cardStack) {
            if (card.value != next) {
                return false;
            }
            next = card.value - 1;
        }

        for (Card card : cardStack) {
            if (card.suit != leadingSuit) {
                System.err.println("Suits don't match");
                return false;
            }
        }
        return true;
    }

    public boolean validateCardsDestination(List<Card> cardStack, int destColumn) {
        Stack<Card> destinationTableau = tableau[destColumn];

        if (destinationTableau.isEmpty()) {
            return true;
        }
        Card destTopCard = destinationTableau.peek();
        Card sendTopCard = cardStack.get(0);

        int destVal = destTopCard.value;
        int sendVal = sendTopCard.value;

        if (destVal - sendVal != 1) {
            return false;
        }

        return true;
    }

    public List<Card> popCardsFromTableau(int column, int depth) {
        List<Card> cards = new ArrayList<Card>();
        for (int num = 0; num < depth; num ++) {
            Card card = tableau[column].pop();
            cards.add(card);
        }
        return cards;
    }

    public void addCardsToTableau(List<Card> cards, int column) {
        cards.sort((card1, card2) -> Integer.compare(card2.value, card1.value));
        for (Card card : cards) {
            card.setHidden(false);
            tableau[column].add(card);
        }
    }

    private boolean isColumnComplete(int index) {
        Stack<Card> column = getCopyStack(index);
        if (column.isEmpty() || column.peek().name != Name.ACE) {
            return false;
        }

        Suit leadSuit = column.peek().suit;
        int val = 1;
        int size = column.size();

        for (int i = 0; i < size; i++ ){
            Card card = column.pop();
            
            if (val != card.value || leadSuit != card.suit) {
                return false;
            }

            if (card.name == Name.KING) {
                System.out.println("Foundation found!");
                return true;
            }
            val += 1;
        }
        
        return false;
    }
    

    public void detectFoundation() {
        notifyFoundation = false;
        foundationColumn = -1;
        for (int i = 0; i < NUM_OF_TABLEAUS; i++) {
            if (tableau[i] != null && isColumnComplete(i)) {
                foundation[numOfFoundations] = tableau[i];
                for (int j = 0; j < 13; j++) {
                    tableau[i].pop();
                }
                numOfFoundations++;
                notifyFoundation = true;
                foundationColumn = i;
                break;
            }
        }
    }

    public boolean detectGameWon() {
        for (Stack<Card> column: foundation) {
            if (column.isEmpty() || column.peek() == null || column.peek().name != Name.ACE) {
                return false;
            }
        }
        gameEnded = true;
        gameWinner = player;
        return true;
    }

    public boolean isTableauSlotEmpty() {
        for (Stack<Card> column : tableau) {
            if (column.empty()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkHiddenCardRevealed() {
        for (Stack<Card> column : tableau) {
            if (!column.empty() && column.peek().isHidden()) {
                Card card = column.pop();
                card.setHidden(false);
                column.add(card);
                revealedCard = card;
                return true;
            }
        }
        return false;
    }

    public List<Card> revealTopCards(UUID playerID) {
        List<Card> topCards = new ArrayList<Card>();        
        if (playerID.equals(player.getPlayerID())) {
            for (int i = 0; i < NUM_OF_TABLEAUS; i++) {
                topCards.add(tableau[i].peek());
            }
        }
        return topCards;
    }

    public List<Card> dealDeck(UUID playerID) {
        if (dealLeft <= 0) {
            return new ArrayList<Card>();
        }
        dealLeft -= 1;
        List<Card> cards = new ArrayList<Card>();
        if (isTableauSlotEmpty()) {
            return cards;
        }

        if (playerID.compareTo(player.getPlayerID()) == 0)  {
            cards = dealCardsToTableaus();
        }
        return cards;
    }

    public boolean playTurn(UUID playerID, List<Card> cardStack, int from, int to) {
        if (playerID.compareTo(player.getPlayerID()) != 0) {
            System.err.println("Player ID mismatch");
            return false;
        }

        revealedCard = null;
        if (validateTurn(cardStack, from, to)) {
            List<Card> cards = popCardsFromTableau(from, cardStack.size());
            addCardsToTableau(cards, to);

            checkHiddenCardRevealed();
            detectFoundation();
            if (detectGameWon()) {
                active = false;
            }
            return true;
        }
        System.err.println("Unable to Validate Turn");
        return false;
    }
}