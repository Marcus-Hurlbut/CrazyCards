package com.crazycards.marcushurlbut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.crazycards.marcushurlbut.utils.CardID;

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
    public boolean endOfRound = true;
    public boolean endOfTrick = false;
    public boolean firstTrick = true;

    private boolean gameEnded = false;
    public Player gameWinner = null;

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

        if (roundPassingType == PassingPhase.KEEP) {
            passingPhaseComplete = true;
            for (Player player : players) {
                player.didPassCards = true;
                player.didReceiveCards = true;
            }
        }
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
        if (isSuitValid(card.suit) || isPlayerVoidSuit(playerID)) {
            return true;
        }

        HashMap<Integer, Card> hand = players[playerID].getHand();
        for (Map.Entry<Integer, Card> entry : hand.entrySet()) {
            Card c = entry.getValue();
            Suit suit = c.suit;
            int value = c.value;
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
            player.tricks = new HashMap<Integer, Card>();
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

            if (cardPlayed != null) {
                int cardValue = cardPlayed.name.ordinal();

                if (cardValue > maxValue && cardPlayed.suit == startingTrickCard.suit) {
                    maxValue = cardValue;
                    intPlayerID = i;
                }
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

        // UUID targetUUID = players[targetID].ID;
        // System.out.println("Player with ID: " + targetUUID + " received Cards passed from other player");
    }

    public UUID passCards(UUID playerID, List<Integer> cardIDs) {
        int intTargetID = 0;
        int intPlayerID = playerIDtoInt.get(playerID);

        // [0 -> 1, 1 -> 2, 2 -> 3, 3 -> 0]
        if (roundPassingType == PassingPhase.LEFT) {
            if (intPlayerID < 3) {
                intTargetID = intPlayerID + 1;
            } else { intTargetID = 0; }

        }
        // [0 -> 3, 3 -> 2, 2 -> 1, 1 -> 0]
        else if (roundPassingType == PassingPhase.RIGHT) {
            if (intPlayerID > 0) {
                intTargetID = intPlayerID - 1;
            } else { intTargetID = 3; }
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
        firstTrick = true;
        passingPhaseComplete = false;
        for (Player player : players) {
            player.didPassCards = false;
            player.didReceiveCards = false;
        }
    }

    public boolean isHeartsBroken() {
        for (Player player : players) {
            boolean heartsPlayed = player.tricks.values().stream().anyMatch(card -> 
                (card.suit == Suit.HEART || (card.suit == Suit.SPADE && card.name == Name.QUEEN))
            );
            if (heartsPlayed) {
                return true;
            }
        }
        return false;
    }

    public boolean queenOfSpadesFirstRoundValidation(int cardID) {
        return (firstTrick && cardID == CardID.SPADE_QUEEN.getOrdinal());
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameWinner() {
        if (gameEnded) {
            int playerWithLowestScore = 0;
            for (Player player : players) {
                int playerScore = player.getScore();
                playerWithLowestScore = Math.min(playerWithLowestScore, playerScore);
            }

            gameWinner = players[playerWithLowestScore];
        }
    }

    public Player getGameWinner() {
        return gameWinner;
    }

    public Boolean playTurn(UUID playerID, int cardID) {
        int playerIDindex = playerIDtoInt.get(playerID);
        Card card = players[playerIDindex].hand.get(cardID);

        if (!passingPhaseComplete || playerIDindex != playerInTurn) {
            System.err.println("Error: Player not in turn");
            return false;
        }

        if (queenOfSpadesFirstRoundValidation(cardID)) {
            System.err.println("Error: Queen of spades cannot be played first round");
            return false;
        }

        // Hearts cannot be broken until played mid-trick as wildcard
        if (endOfTrick && card.suit == Suit.HEART && !isHeartsBroken()) {
            return false;
        }

        // First turn of new Round - Must be 2 of clubs
        if (endOfRound && playerIDindex == playerInTurn) {
            if (cardID != CardID.CLUB_TWO.getOrdinal()) {
                return false;
            }
            endOfRound = false;
            endOfTrick = false;
            startingTrickCard = card;
        }

        // First turn of new trick
        else if(endOfTrick && playerIDindex == playerInTurn) {
            endOfTrick = false;
            startingTrickCard = card;
        }

        // Validate card against game logic then allow turn
        if (validateCardToPlay(playerIDindex, card)) {
            
            addCardToVoidPile(playerIDindex, card); // Add card to void pile after all validation checks

            if (isEndOfTrick()) {
                playerInTurn = calculateTrickWinner();
                addTrickToWinner(players[playerInTurn].ID, voidCardPile);
                voidCardPile = new ArrayList<Card>();
                for (int i = 0; i < 4; i++) {
                    voidCardPile.add(null);
                }

                cardsPlayedThisTrick = 0;
                endOfTrick = true;
                firstTrick = false;
            }

            // End of round requires a re-shuffle & new pass phase
            if (isEndOfRound()) {
                resetRoundFields();
                calculateScore(); 
                shuffleAndDeal(); 
            }

            if (isGameOver()) {
                gameEnded = true;
                System.out.print("\nGame ended!\n");
                setGameWinner();
                active = false;
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
