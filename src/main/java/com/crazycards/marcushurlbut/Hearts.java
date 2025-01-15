package com.crazycards.marcushurlbut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public int playerInTurn = -1;
    public PassingPhase roundPassingType;

    int cardsPlayedThisTrick = 0;
    public Card startingTrickCard  = new Card(Suit.CLUB, Name.TWO, 2, "2_of_clubs.png");
    public List<Card> voidCardPile = new ArrayList<Card>(4);

    public boolean active = false;
    public boolean passingPhaseComplete = false;
    public boolean endOfRound = false;
    public boolean endOfTrick = false;
    boolean gameEnded = false;

    Hearts() {}
    public Hearts(UUID gameID) {
        this.gameID = gameID;
    }

    public void initializePlayers(List<Player> playerList) {
        for (int i = 0; i < 4; i++) {
            players[i] = playerList.get(i);
            playerIDtoInt.put(playerList.get(i).ID, i);

            voidCardPile.add(null);
        }
    }

    public void shuffleAndDeal() {
        deck.shuffleDeck();
        deck.dealDeck(players);
        setPassingPhase();
    }

    public void onEndOfPassingPhase() {
        passingPhaseComplete = true;
        setFirstPlayerInitiative();

        for (Player player : players) {
            player.passedCards.forEach((id, card) -> {
                player.hand.put(id, card);
            });
            player.passedCards.clear();
        }        
    }

    public int getPlayerInitiative() {
        if (playerInTurn >= 0) {
            return playerInTurn;
        }
        System.err.println("Problem getting player initiative - not initialized");
        return -1;
    }

    public void setFirstPlayerInitiative() {
        for (int intPlayerID = 0; intPlayerID < players.length; intPlayerID++) {
            if (players[intPlayerID].hand.containsKey(CardID.CLUB_TWO.getOrdinal())
                || players[intPlayerID].passedCards.containsKey(CardID.CLUB_TWO.getOrdinal())
            ) {
                playerInTurn = intPlayerID;
                break;
            }
        }

        System.out.println("Setting first player initiative to: " + playerInTurn);
    }

    public void setPassingPhase() {
        PassingPhase[] phases = {PassingPhase.LEFT, PassingPhase.RIGHT, PassingPhase.ACROSS, PassingPhase.KEEP};
        roundPassingType = phases[roundNumber % 4];
        System.out.println("Setting passing type to: " + roundPassingType);

        roundNumber += 1;
    }

    public boolean isEndOfTrick() {
        return cardsPlayedThisTrick >= players.length;
    }

    public boolean isSuitValid(Suit suit) {
        return suit == startingTrickCard.suit;
    }

    public boolean isPlayerVoidSuit(int playerID) {
        HashMap<Integer, Card> hand = players[playerID].hand;
        for (Card card : hand.values()) {
            if (card.suit == startingTrickCard.suit) {
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
        System.out.println("Unable to validate card as same suit or as a wildcard play - suit needed: " + startingTrickCard.suit);
        System.out.println("Players hand in question" + players[playerID].getHand().entrySet() + "\n\n");

        HashMap<Integer, Card> hand = players[playerID].getHand(); // Getting the hand of the player
        for (Map.Entry<Integer, Card> entry : hand.entrySet()) { // Iterating over each entry in the map
            Card c = entry.getValue(); // Get the Card object from the entry
            Suit suit = c.suit; // Get the suit of the card
            int value = c.value; // Get the value of the card
            
            // Print out the Card ID, Suit, and Value
            System.out.println("Card ID: " + entry.getKey() + " | Suit: " + suit + " | Value: " + value);
        }
        return false;
    }

    public void addCardToVoidPile(int intPlayerID, Card card) {
        voidCardPile.set(intPlayerID, card);
        int cardID = getCardID(card);
        players[intPlayerID].hand.remove(cardID);
        cardsPlayedThisTrick++;
    }

    public boolean shootingTheMoon(Player player) {
        HashMap<Integer, Card> tricks = player.tricks;
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

    public int deducePoints(Player player) {
        int points = 0;
        for (Card card: player.tricks.values()) {
            // Queen of spades is 13 points
            if (card.name == Name.QUEEN && card.suit == Suit.SPADE) {
                points += 13;
            }
            else if (card.suit == Suit.HEART) {
                points++;
            }
        }
        return points;
    }

    public void calculateScore() {
        int intPlayerID = 0;

        for (Player player : players) {
            int points = 0;

            if (shootingTheMoon(player)) {
                points -= 26;
            } else {
                points = deducePoints(player);
            }

            int currentScore = players[intPlayerID].getScore();
            players[intPlayerID].setScore(currentScore + points);

            intPlayerID++;
        }
    }

    public boolean isGameOver() {
        return Arrays.stream(players).anyMatch(player -> player.getScore() > 99);
    }

    public int calculateTrickWinner() {
        int maxValue = Integer.MIN_VALUE;
        int intPlayerID = -1;

        for (int i = 0; i < voidCardPile.size(); i++) {
            Card cardPlayed = voidCardPile.get(i);
            int cardValue = cardPlayed.name.ordinal();

            System.out.println("Player " + i + ": cardPlayed value: " + cardPlayed.value + ", name: " + cardPlayed.name + ", ordinal: " + cardValue);

            if (cardValue > maxValue && cardPlayed.suit == startingTrickCard.suit) {
                maxValue = cardValue;
                intPlayerID = i;
            }
        }
        return intPlayerID;
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

    public void addTrickToWinner(UUID playerID, List<Card> trickCards) {
        trickCards.forEach(card -> {
            int index = playerIDtoInt.get(playerID);
            players[index].tricks.put(getCardID(card), card);
        });
    }

    public void resetRoundFields() {
        endOfRound = true;
        passingPhaseComplete = false;
        for (Player player : players) {
            player.didPassCards = true;
            player.didReceiveCards = true;
        }
    }

    public boolean isHeartsBroken() {
        for (Player player : players) {
            boolean heartsPlayed = player.tricks.values().stream().anyMatch(card -> card.suit == Suit.HEART);
            if (heartsPlayed) {
                return true;
            }
        }
        return false;
    }

    public Boolean playTurn(UUID playerID, int cardID) {
        int playerIDindex = playerIDtoInt.get(playerID);
        Card card = players[playerIDindex].hand.get(cardID);

        // Validate player's turn
        if (!passingPhaseComplete || playerIDindex != playerInTurn || (card.suit == Suit.HEART && !isHeartsBroken())) {
            return false;
        }

        // First turn of new Round
        if (endOfRound && playerIDindex == playerInTurn) {
            endOfRound = false;
            startingTrickCard = card;
        }
        // First turn of new trick
        else if(endOfTrick && playerIDindex == playerInTurn) {
            endOfTrick = false;
            startingTrickCard = card;
        }

        // Validate card against game logic then allow turn
        if (validateCardToPlay(playerIDindex, card)) {
            // Add Card to play pile
            addCardToVoidPile(playerIDindex, card);

            if (isEndOfTrick()) {
                playerInTurn = calculateTrickWinner();
                addTrickToWinner(players[playerInTurn].ID, voidCardPile);
                voidCardPile = new ArrayList<Card>();
                for (int i = 0; i < 4; i++) {
                    voidCardPile.add(null);
                }

                cardsPlayedThisTrick = 0;
                endOfTrick = true;
            }

            // End of round requires a re-shuffle & new pass phase
            if (isEndOfRound()) {
                resetRoundFields();
                calculateScore(); 
                shuffleAndDeal(); 
            }

            // TODO: figure out what I want to do when game ends aside from announcing winner
            if (isGameOver()) {
                gameEnded = true;
                System.out.print("Game ended!");
            }

            // Trick still in play, play in order around the table
            if (!endOfTrick && !endOfRound) {
                playerInTurn = (playerInTurn + 1) % 4;
            }
            return true;
        }
        return false;
    }

    public int getCardID(Card card) {
        int offset = 0;
        switch (card.suit) {
            case HEART:
                offset = 0;
                break;

            case DIAMOND:
                offset = 13;
                break;
            
            case SPADE:
                offset = 26;
                break;

            case CLUB:
                offset = 39;
                break;
        
            default:
                break;
        }

        if (card.name.ordinal() == 0) {
            return offset;
        }
        return (card.name.ordinal() + offset);
    }
}
