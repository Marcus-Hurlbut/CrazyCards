package com.dungeondecks.marcushurlbut.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dungeondecks.marcushurlbut.games.card.*;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.SpiderSolitaire;
import com.dungeondecks.marcushurlbut.util.TestUtils;

public class SpiderSolitaireTest {
    private static UUID gameID;
    private static SpiderSolitaire spiderSolitaire;
    private static TestUtils testUtils;

    private static void setup() {
        gameID = UUID.randomUUID();
        spiderSolitaire = new SpiderSolitaire(gameID, 4);
        testUtils = new TestUtils();

        for (int i = 0; i < spiderSolitaire.tableau.length; i++) {
            spiderSolitaire.tableau[i] = new Stack<Card>();
        }
    }

    @Test
    public void initializePlayersTest() {
        setup();

        Player player = testUtils.getPlayerArray()[0];
        spiderSolitaire.initializePlayer(player);
        assertEquals(spiderSolitaire.player.getPlayerID(), testUtils.player_1_id);
    }

    @Test
    public void shuffleDeckTest() {
        setup();

        spiderSolitaire.shuffleDeck();
        assertTrue(spiderSolitaire.deck.getSize() == 104);
    }

    @Test
    public void initialDealTest() {
        setup();

        spiderSolitaire.shuffleDeck();
        spiderSolitaire.initialDeal();

        assertTrue(spiderSolitaire.tableau[0].size() == 6);
        assertTrue(spiderSolitaire.tableau[1].size() == 6);
        assertTrue(spiderSolitaire.tableau[2].size() == 6);
        assertTrue(spiderSolitaire.tableau[3].size() == 6);
        assertTrue(spiderSolitaire.tableau[4].size() == 5);
        assertTrue(spiderSolitaire.tableau[5].size() == 5);
        assertTrue(spiderSolitaire.tableau[6].size() == 5);
        assertTrue(spiderSolitaire.tableau[7].size() == 5);
        assertTrue(spiderSolitaire.tableau[8].size() == 5);
        assertTrue(spiderSolitaire.tableau[9].size() == 5);
    }

    @Test 
    public void validateCardsCanMoveTest() {
        setup();

        List<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Suit.SPADE, Name.KING, 13, "king_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.QUEEN, 12, "queen_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.JACK, 11, "jack_of_spades.png"));
        boolean valid = spiderSolitaire.validateCardCanMove(cards);
        assertTrue(valid);

        cards = new ArrayList<Card>();
        cards.add(new Card(Suit.SPADE, Name.KING, 13, "king_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.JACK, 11, "jack_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.TEN, 10, "10_of_spades.png"));
        valid = spiderSolitaire.validateCardCanMove(cards);
        assertFalse(valid);

        cards = new ArrayList<Card>();
        cards.add(new Card(Suit.SPADE, Name.KING, 13, "king_of_spades.png"));
        cards.add(new Card(Suit.HEART, Name.QUEEN, 12, "queen_of_hearts.png"));
        cards.add(new Card(Suit.SPADE, Name.JACK, 11, "jack_of_spades.png"));
        valid = spiderSolitaire.validateCardCanMove(cards);
        assertFalse(valid);
    }

    @Test
    public void validateCardsDestinationTest() {
        setup();

        spiderSolitaire.tableau[0].add(new Card(Suit.SPADE, Name.SEVEN, 7, "seven_of_spades.png"));
        spiderSolitaire.tableau[0].add(new Card(Suit.SPADE, Name.SIX, 6, "six_of_spades.png"));

        List<Card> cards = new ArrayList<Card>();
        cards.add(new Card(Suit.SPADE, Name.FIVE, 5, "five_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.FOUR, 4, "four_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.THREE, 3, "three_of_spades.png"));
        boolean validDestionation = spiderSolitaire.validateCardsDestination(cards, 0);
        assertTrue(validDestionation);

        cards = new ArrayList<Card>();
        cards.add(new Card(Suit.CLUB, Name.FIVE, 5, "five_of_clubs.png"));
        validDestionation = spiderSolitaire.validateCardsDestination(cards, 0);
        assertTrue(validDestionation);

        cards = new ArrayList<Card>();
        cards.add(new Card(Suit.SPADE, Name.FOUR, 4, "four_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.THREE, 3, "three_of_spades.png"));
        cards.add(new Card(Suit.SPADE, Name.TWO, 2, "two_of_spades.png"));
        validDestionation = spiderSolitaire.validateCardsDestination(cards, 0);
        assertFalse(validDestionation);
    }

    @Test
    public void dealDeckTest() {
        setup();

        Player[] players = testUtils.getPlayerArray();
        spiderSolitaire.initialize(players);
        
        spiderSolitaire.dealDeck(players[0].getPlayerID());

        assertTrue(spiderSolitaire.tableau[0].size() == 7);
        assertTrue(spiderSolitaire.tableau[1].size() == 7);
        assertTrue(spiderSolitaire.tableau[2].size() == 7);
        assertTrue(spiderSolitaire.tableau[3].size() == 7);
        assertTrue(spiderSolitaire.tableau[4].size() == 6);
        assertTrue(spiderSolitaire.tableau[5].size() == 6);
        assertTrue(spiderSolitaire.tableau[6].size() == 6);
        assertTrue(spiderSolitaire.tableau[7].size() == 6);
        assertTrue(spiderSolitaire.tableau[8].size() == 6);
        assertTrue(spiderSolitaire.tableau[9].size() == 6);
    }

    @Test
    public void isTableauSlotEmptyTest() {
        setup();

        Player[] players = testUtils.getPlayerArray();
        spiderSolitaire.initialize(players);
        int size = spiderSolitaire.tableau[8].size();
        for (int i = 0 ; i < size; i++) {
            spiderSolitaire.tableau[8].pop();
        }

        boolean emptySlot = spiderSolitaire.isTableauSlotEmpty();
        assertTrue(emptySlot);
    }

    @Test
    public void detectFoundationTest() {
        Player[] players = testUtils.getPlayerArray();
        spiderSolitaire.initialize(players);

        spiderSolitaire.tableau[4] = new Stack<Card>();

        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.KING, 13, "king_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.QUEEN, 12, "queen_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.JACK, 11, "jack_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.TEN, 10, "ten_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.NINE, 9, "nine_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.EIGHT, 8, "eight_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.SEVEN, 7, "seven_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.SIX, 6, "six_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.FIVE, 5, "five_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.FOUR, 4, "four_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.THREE, 3, "three_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.TWO, 2, "two_of_diamonds.png"));
        spiderSolitaire.tableau[4].add(new Card(Suit.DIAMOND, Name.ACE, 1, "ace_of_diamonds.png"));

        spiderSolitaire.detectFoundation();
        assertTrue(spiderSolitaire.numOfFoundations == 1);
    }
}
