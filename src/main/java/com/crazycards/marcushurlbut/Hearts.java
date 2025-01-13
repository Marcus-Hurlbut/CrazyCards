package com.crazycards.marcushurlbut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.crazycards.marcushurlbut.utils.CardID;

enum PassingPhase {
    LEFT,
    RIGHT,
    ACROSS,
    KEEP
}

public class Hearts {
    public Deck deck = new Deck();
    int roundNumber = 0;
    public Player[] players = new Player[4];
    public HashMap<UUID, Integer> playerIDtoInt = new HashMap<UUID, Integer>();

    public UUID gameID;
    boolean active = false;

    public int playerInitiative = -1;
    public int playerInTurn = -1;
    public PassingPhase roundPassingType;
    public boolean passingPhaseComplete = false;

    int cardsPlayed = 0;
    public Card startingCard;
    public List<Card> voidCardPile = new ArrayList<Card>(4);
    public boolean newRound = false;

    static int PLAYER_1 = 0;
    static int PLAYER_2 = 1;
    static int PLAYER_3 = 2;
    static int PLAYER_4 = 3;

    Hearts() {}
    Hearts(UUID gameID) {
        this.gameID = gameID;
    }

    public void initializePlayers(List<Player> playerList) {
        for (int i = 0; i < 4; i++) {
            players[i] = playerList.get(i);  // Initialize player with a name
            playerIDtoInt.put(playerList.get(i).ID, i);
        }
    }

    public void start() {
        deck.shuffleDeck();
        deck.dealDeck(players);
        setPassingPhase(roundNumber);
        setPlayerInitiative();
    }

    public int getPlayerInitiative() {
        if (playerInitiative >= 0) {
            return playerInitiative;
        }
        System.err.println("Problem getting player initiative - not initialized");
        return -1;
    }

    public void setPlayerInitiative() {
        int playerID = 0;
        for (Player player : players) {
            if (player.hand.containsKey(CardID.CLUB_TWO.ordinal())) {
                playerInitiative = playerID;
                break;
            }
            playerID++;
        }
    }

    public void setPassingPhase(int roundNumber) {
        PassingPhase[] phases = {PassingPhase.LEFT, PassingPhase.RIGHT, PassingPhase.ACROSS, PassingPhase.KEEP};
        roundPassingType = phases[roundNumber % 4];
        System.out.println("Setting passing type to: " + roundPassingType);
    }

    public boolean isEndOfTrick() {
        return cardsPlayed >= players.length;
    }

    public boolean isSuitValid(Suit suit) {
        return suit == startingCard.suit;
    }

    public boolean isPlayerVoidSuit(int playerID) {
        HashMap<Integer, Card> hand = players[playerID].hand;
        for (Card card : hand.values()) {
            if (card.suit == startingCard.suit) {
                return false;
            }
        }
        return true;

    }

    public boolean validateCardToPlay(int playerID, Card card) {
        // If card is not in same suit, check player hand to ensure
        // they're void in that suit
        if (isSuitValid(card.suit) || isPlayerVoidSuit(playerID)) {
            return true;
        }
        return false;
    }

    public void addCardToInPlay(int playerID, Card card) {
        voidCardPile.set(playerID, card);
        cardsPlayed++;
    }

    public boolean shootingTheMoon(int playerID) {
        HashMap<Integer, Card> tricks = players[playerID].tricks;
        Set<Integer> cardSet = tricks.keySet();

        List<Integer> cardsRequired = Arrays.asList(
            CardID.HEART_TWO.getOrdinal(), CardID.HEART_THREE.getOrdinal(), CardID.HEART_FOUR.getOrdinal(),
            CardID.HEART_FIVE.getOrdinal(), CardID.HEART_SIX.getOrdinal(), CardID.HEART_SEVEN.getOrdinal(),
            CardID.HEART_EIGHT.getOrdinal(), CardID.HEART_NINE.getOrdinal(), CardID.HEART_TEN.getOrdinal(),
            CardID.HEART_JACK.getOrdinal(), CardID.HEART_QUEEN.getOrdinal(), CardID.HEART_KING.getOrdinal(),
            CardID.HEART_ACE.getOrdinal(), CardID.SPADE_QUEEN.getOrdinal()
        );

        return cardSet.containsAll(cardsRequired);
    }

    public void calculateScore() {
        int playerID = 0;
        for (Player player : players) {
            int points = 0;
            for (Card card: player.tricks.values()) {
                if (shootingTheMoon(playerID)) {
                    points -= 26;
                }
                else if (card.name == Name.QUEEN && card.suit == Suit.SPADE) {
                    points += 13;
                }
                else if (card.suit == Suit.HEART) {
                    points++;
                }
            }
            int current = players[playerID].getScore();
            players[playerID].setScore(current + points);

            playerID++;
        }
    }

    public int calculateTrickWinner() {
        int highest = Integer.MIN_VALUE;
        for (int i = 0; i < voidCardPile.size(); i++) {
            int cardIndex = voidCardPile.get(i).name.ordinal();
            if (cardIndex > highest) {
                highest = i;
            }
        }
        return highest;
    }

    public boolean isEndOfRound() {
        for (Player player : players) {
            int cardsLeft = player.getHand().size(); 
            if (cardsLeft > 0) {
                return false;
            }
        }
        return true;
    }

    public void transferCards(int sourceID, int targetID, List<Integer> cardIDs) {
        HashMap<Integer, Card> passedCards = new HashMap<Integer, Card>();
        for (Integer cardID : cardIDs) {
            Card card = players[sourceID].hand.remove(cardID);
            passedCards.put(cardID, card);
        }

        players[sourceID].didPassCards = true;
        players[targetID].didReceiveCards = true;
        players[targetID].passedCards = passedCards;

        UUID targetUUID = players[targetID].ID;
        System.out.println("Player with ID: " + targetUUID + " received Cards passed from other player");
    }

    public UUID passCards(UUID playerID, List<Integer> cardIDs) {
        int intTargetID = 0;
        int intPlayerID = playerIDtoInt.get(playerID);

        // [0 -> 1, 1 -> 2, 2 -> 3, 3 -> 0]
        if (roundPassingType == PassingPhase.LEFT) {
            if (intPlayerID < 3) {
                intTargetID = intPlayerID + 1;
            } else { intTargetID = 0;}

        }
        // [0 -> 3, 3 -> 2, 2 -> 1, 1 -> 0]
        else if (roundPassingType == PassingPhase.RIGHT) {
            if (intPlayerID > 0) {
                intTargetID -= 1;
            } else { intTargetID =3;}
        }
        // [0 -> 2, 2 -> 0, 1 -> 3, 3 -> 1]
        else if (roundPassingType == PassingPhase.ACROSS) {
            if ((intPlayerID + 1) % 2 == 1) {
                // For playerID 0 and 2
                intTargetID = intPlayerID == 0 ? 2 : 0;
            } else {
                // For playerID 1 and 3
                intTargetID = intPlayerID == 1 ? 3 : 1;
            }
        }
        else {
            System.out.println("NO PASSING THIS ROUND");
            return playerID;
        }
        
        transferCards(intPlayerID, intTargetID, cardIDs);
        
        UUID targetID = players[intTargetID].ID;
        return targetID;
    }

    public Boolean playTurn(UUID playerID, int cardID) {
        Card card;
        int playerIDindex = playerIDtoInt.get(playerID);
        card = players[playerIDindex].hand.get(cardID);

        if (!passingPhaseComplete) {
            return false;
        }

        // Reset round fields
        if (newRound && playerIDindex == playerInitiative) {
            cardsPlayed = 0;
            newRound = false;
            startingCard = card;
        }

        if (playerIDindex == playerInTurn && validateCardToPlay(playerIDindex, card)) {
            addCardToInPlay(playerIDindex, card);

            if (isEndOfTrick()) {
                playerInitiative = calculateTrickWinner();
            }

            if (isEndOfRound()) {
                newRound = true;
                calculateScore();
                // playerInitiative = 
            }

            // if (isGameOver()) {
                
            // }
            return true;
        }
        return false;
    }
}
