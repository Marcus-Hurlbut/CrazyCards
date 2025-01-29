package com.dungeondecks.marcushurlbut.games;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dungeondecks.marcushurlbut.Game;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.games.card.Suit;
import com.dungeondecks.marcushurlbut.games.deck.Deck;

public class Spades extends Game {
    public Deck deck = new Deck();
    public List<Card> voidCardPile = new ArrayList<Card>(4);
    HashMap<UUID, UUID> teammate = new HashMap<UUID, UUID>();
    int[] playerBid = new int[4];

    public Card startingTrickCard;
    int cardsPlayedThisTrick = 0;

    public boolean teamSelectionPhaseComplete = false;
    public boolean biddingPhaseComplete = false;
    public boolean endOfTrick = true;
    public boolean firstTrick = false;

    public List<String> team1Names = new ArrayList<String>();
    public List<String> team2Names = new ArrayList<String>();

    public List<Player> gameWinners = new ArrayList<Player>();
    
    public Spades(UUID gameID) {
        this.gameID = gameID;
    }

    public void initialize(Player[] players) {
        initializePlayers(players);
        shuffleAndDeal();
        setFirstPlayerInitiative();
        active = true;
    }

    public void initializePlayers(Player[] playerList) {
        for (int i = 0; i < 4; i++) {
            players[i] = playerList[i];
            playerIDtoInt.put(playerList[i].ID, i);
            voidCardPile.add(null);
        }
    }

    public Player[] updateOrientationRing(List<String> playerList) {
        playerIDtoInt.clear();
        Player[] copy = new Player[4];

        for (int newIndex = 0; newIndex < players.length; newIndex++) {
            String name = playerList.get(newIndex);

            for (Player player: players) {
                if (player.getUsername().strip().equals(name)) {
                    playerIDtoInt.put(player.ID, newIndex);
                    copy[newIndex] = player;
                }
            }            
        }

        players = copy;

        System.out.println("\nOrientation ring updated: ");
        int i = 0;
        for (Player player: players) { 
            System.out.println("Player " + i + " - " + player.getPlayerID());
            i++;
        }

        return players;
    }

    public void shuffleAndDeal() {
        deck.shuffleDeck();
        deck.dealDeck(players);
    }

    public void setFirstPlayerInitiative() { 
        SecureRandom secureRandom = new SecureRandom();
        playerInTurn = secureRandom.nextInt(3);
    }

    public boolean setTeams(Player hostPlayer, String teammateName) {
        if (teamSelectionPhaseComplete || hostPlayer.getPlayerID() != players[0].getPlayerID()) {
            return false;
        }

        UUID teammateID = UUID.randomUUID();
        for (Player p : players) {
            if (p.getUsername().strip().equals(teammateName.strip())) {
                teammateID  = p.getPlayerID();
                team1Names.add(p.getUsername());
                team1Names.add(hostPlayer.getUsername());
                break;
            }
        }

        System.out.println("playerIDtoInt keys: " + playerIDtoInt.keySet());
        // Set the chosen teammate for host
        hostPlayer.teammate = teammateID;
        players[playerIDtoInt.get(teammateID)].teammate = hostPlayer.ID;
    
        // Assign the remaining two players to the other team
        List<Player> remainingPlayers = new ArrayList<>();
        
        // Collect the remaining players who don't have teammates yet
        for (int i = 0; i < players.length; i++) {
            if (players[i].teammate == null) {
                remainingPlayers.add(players[i]);
                team2Names.add(players[i].getUsername());
            }
        }
    
        // Ensure there are exactly 2 players left to pair up
        if (remainingPlayers.size() == 2) {
            // Assign the teammates for the second team
            remainingPlayers.get(0).teammate = remainingPlayers.get(1).getPlayerID();
            remainingPlayers.get(1).teammate = remainingPlayers.get(0).getPlayerID();

            players[playerIDtoInt.get(remainingPlayers.get(0).ID)] = remainingPlayers.get(0);
            players[playerIDtoInt.get(remainingPlayers.get(1).ID)] = remainingPlayers.get(1);
        } else {  
            System.err.println("Remaining players is more than it should be");
            return false;
        }

        teamSelectionPhaseComplete = true;
        return true;
    }

    public boolean setBid(UUID playerID, int bidNumber) {
        int id = playerIDtoInt.get(playerID);

        if (!biddingPhaseComplete && players[id].bid == null) {
            players[id].bid = bidNumber;
            playerInTurn = (playerInTurn + 1) % 4;

            checkBiddingPhaseComplete();
            return true;
        }
        return false;
    }

    public void checkBiddingPhaseComplete() {
        for (Player player : players) {
            if (player.bid == null) {
                return;
            }
        }
        biddingPhaseComplete = true;
    }

    public void addCardToVoidPile(int intPlayerID, Card card) {
        voidCardPile.set(intPlayerID, card);
        int cardID = getCardID(card);
        players[intPlayerID].hand.remove(cardID);
        cardsPlayedThisTrick++;
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
        return false;
    }

    public boolean isSpadesBroken() {
        for (Player player : players) {
            boolean spadePlayed = player.tricks.values().stream().anyMatch(card -> 
                (card.suit == Suit.SPADE)
            );
            if (spadePlayed) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return Arrays.stream(players).anyMatch(player -> player.getScore() > 199);
    }

    public boolean isEndOfTrick() {
        return cardsPlayedThisTrick >= players.length;
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

    public int calculateTrickWinner() {
        int maxValue = Integer.MIN_VALUE;
        int intPlayerID = -1;

        for (int i = 0; i < voidCardPile.size(); i++) {
            Card cardPlayed = voidCardPile.get(i);

            if (cardPlayed != null) {
                int cardValue = cardPlayed.name.ordinal();

                if (cardValue > maxValue && (cardPlayed.suit == startingTrickCard.suit || cardPlayed.suit == Suit.SPADE)) {
                    maxValue = cardValue;
                    intPlayerID = i;
                }
            }
        }
        return intPlayerID;
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
        biddingPhaseComplete = false;

        for (Player player : players) {
            player.bid = null;
            player.tricks = new HashMap<Integer, Card>();

            int bags = player.bags;
            if (bags >= 10) {
                player.bags = (bags % 10);
            }
        }
    }

    public void checkBagPenalty(Player player) {
        if (player.bags >= 10) {
            int score = player.getScore() - 100;
            player.setScore(score);
        }
    }

    public boolean checkNilBid(Player player, Player teammate) {
        if (player.bid == 0) {
            if (player.tricks.size() > 0) {
                player.setScore(player.getScore() - 100);
            }
            else {
                player.setScore(player.getScore() + 100);
            }
            return true;
        }

        if (teammate.bid == 0) {
            if (teammate.tricks.size() > 0) {
                player.setScore(player.getScore() - 100);
            }
            else {
                player.setScore(player.getScore() + 100);
            }
            return true;
        }
        return false;
    }

    public void deducePoints(Player player, UUID teammateID) {
        Player teammate = players[playerIDtoInt.get(teammateID)];
        
        if (checkNilBid(player, teammate) == false) {
            int guess = player.bid + teammate.bid;
            int score = 0;
            int bags = 0;

            HashMap<Integer, Card> combinedTricks = new HashMap<>();
            combinedTricks.putAll(player.tricks);
            combinedTricks.putAll(teammate.tricks);

            int won = (combinedTricks.keySet().size() / 4);

            if (won >= guess) {
                bags = guess - guess;
                score = (guess * 10) + bags;
                player.setScore(score);
                player.bags += bags;
            }

            checkBagPenalty(player);
        }
    }

    

    public void calculateScore() {
        for (Player player: players) {
            deducePoints(player, player.teammate);
        }
    }

    public void setGameWinner() {
        int winner = 0;
        if (gameEnded) {
            int playerindex = 0;
            int highest = Integer.MIN_VALUE;
            for (Player player : players) {
                int playerScore = player.getScore();

                if(playerScore > highest) {
                    winner = playerindex;
                    highest = playerScore;
                }
                playerindex++;
            }

            gameWinners.add(players[winner]);
            gameWinners.add(players[playerIDtoInt.get(players[winner].teammate)]);
        }
    }
    
    public boolean playTurn(UUID playerID, int cardID) {
        int playerIDindex = playerIDtoInt.get(playerID);
        Card card = players[playerIDindex].hand.get(cardID);

        if (!teamSelectionPhaseComplete || !biddingPhaseComplete) {
            return false;
        }

        if (playerIDindex != playerInTurn) {
            System.err.println("Error: Player not in turn");
            return false;
        }

        // Spades cannot be broken until played mid-trick as wildcard
        if (endOfTrick && card.suit == Suit.SPADE && !isSpadesBroken()) {
            return false;
        }

        // First turn of new Round
        if (endOfRound) {
            endOfRound = false;
            endOfTrick = false;
            startingTrickCard = card;
        }
        // First turn of new trick
        else if (endOfTrick) {
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
                calculateScore(); 
                resetRoundFields();
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

}
