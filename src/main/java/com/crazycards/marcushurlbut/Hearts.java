package com.crazycards.marcushurlbut;

import java.util.*;

public class Hearts {
    public static Deck deck = new Deck();
    static int playerCount = 0;
    static int roundNumber = 1;
    public static Player[] players = new Player[4];

    static int PLAYER_1 = 0;
    static int PLAYER_2 = 1;
    static int PLAYER_3 = 2;
    static int PLAYER_4 = 3;


    public static void initialize() {
        for (int i = 0; i < 4; i++) {
            players[i] = new Player();  // Initialize player with a name
        }
        deck = new Deck();
    }
    public static void setScoreboard(String nameOfPlayer, int score) {}
    

    public static void startGame() {
        // Shuffle cards
        deck.shuffleDeck();

        // Deal cards out
        dealDeck();

        // Passing phase
        // handlePassingPhase(roundNumber);

        // Begin round - start with club of 2's
        // int playerInitiative = 2;

        // Start main game loop
        // handleRound(playerInitiative);

        // End round when all cards have been played

        // Check if anyone has over 100 points
    }

    public static void dealDeck() {
        for (int n = 0; n < 53; n++) {
            int playerIndex = n % 4;
            Card topCard = deck.popDeck();
            players[playerIndex].addCardToHand(topCard);
        }
    }

    public static void handlePassingPhase(int roundNumber) {
        int passingType = (roundNumber % 4);
        switch (passingType) {
            case 0:
                // PASS RIGHT - [1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1]
                break;

            case 1:
                // PASS LEFT - [1 -> 4, 4 -> 3, 3 -> 2, 2 -> 1]
                break;

            case 2:
                // PASS ACROSS - [1 -> 3, 3 -> 4]
                break;

            default:
                // KEEP - do nothing
                break;
        }
    }

    public static void handleRound(int playerInitiative) {
        // getPlayersCardOption();
    }

    public static String printHello() {
        return "Hello";
    }
}
