package com.dungeondecks.marcushurlbut.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.games.Spades;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.games.card.Name;
import com.dungeondecks.marcushurlbut.games.card.Suit;
import com.dungeondecks.marcushurlbut.util.TestUtils;
import com.dungeondecks.marcushurlbut.utils.CardID;

public class SpadesTest {
    private static UUID gameID;
    private static Spades spades;
    private static TestUtils testUtils;

    private static void setup() {
        gameID = UUID.randomUUID();
        spades = new Spades(gameID);
        testUtils = new TestUtils();
    }

    private void startGame(Spades spade, boolean skipBiddingPhase, boolean skipTeamSelectionPhase, boolean useFakeDeck) {
        spade.initializePlayers(testUtils.getPlayerArray());
        
        if (!useFakeDeck) {
            spade.initialize(spade.players);
        } else {
            spade.setFirstPlayerInitiative();
            spade.active = true;
            testUtils.populateFakeDeck(spade.players);
        }

        if (skipBiddingPhase) {
            spade.biddingPhaseComplete = true;
        }

        if (skipTeamSelectionPhase) {
            spades.teamSelectionPhaseComplete = true;
        }
    }

    @Test
    public void initializePlayersTest() {
        setup();

        Player[] players = testUtils.getPlayerArray();
        spades.initializePlayers(players);
        
        assertEquals(spades.players[0].getPlayerID(), testUtils.player_1_id);
        assertEquals(spades.players[1].getPlayerID(), testUtils.player_2_id);
        assertEquals(spades.players[2].getPlayerID(), testUtils.player_3_id);
        assertEquals(spades.players[3].getPlayerID(), testUtils.player_4_id);
    }
    
    @Test
    public void shuffleDeckTest() {
        setup();
        startGame(spades, false, false, false);

        for (Player player: spades.players) {
            assertFalse(player.getHand().isEmpty());
            assertNotNull(player.getHand());
        }
    }

    @Test
    public void playerPlaysOutOfTurnTest() {
        setup();
        startGame(spades, true, true, true);
        spades.playerInTurn = 0;

        Player player = spades.players[1];
        boolean outOfTurn = spades.playTurn(player.getPlayerID(), CardID.DIAMOND_TEN.getOrdinal());

        assertFalse(outOfTurn);

        player = spades.players[0];
        boolean inTurn = spades.playTurn(player.getPlayerID(), CardID.CLUB_JACK.getOrdinal());

        assertTrue(inTurn);
    }

    @Test
    public void playerPlaysSpadesBeforeSuitBrokenTest() {
        setup();
        startGame(spades, true, true, true);
        boolean validTurn;

        spades.playerInTurn = 0;
        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());
        validTurn = spades.playTurn(spades.players[3].getPlayerID(), CardID.SPADE_SEVEN.getOrdinal());

        assertFalse(validTurn);
    }

    @Test
    public void playerBidTest() {
        setup();
        startGame(spades, false, true, true);

        int turn = 0;
        while(turn < 4) {
            spades.setBid(spades.players[spades.playerInTurn].getPlayerID(), 2);
            turn += 1;
        }

        assertTrue(spades.players[0].bid == 2);
        assertTrue(spades.players[1].bid == 2);
        assertTrue(spades.players[2].bid == 2);
        assertTrue(spades.players[3].bid == 2);
    };

    @Test
    public void playerBidsOutOfTurnTest() {
        setup();
        startGame(spades, true, true, true);

        spades.playerInTurn = 2;
        spades.setBid(spades.players[2].getPlayerID(), 3);
        
        boolean validBid = spades.setBid(spades.players[1].getPlayerID(), 3);
        assertFalse(validBid);
    };

    @Test
    public void playerBidsOutOfPhase() {
        setup();
        startGame(spades, false, true, true);

        int turn = 0;
        while(turn < 4) {
            spades.setBid(spades.players[spades.playerInTurn].getPlayerID(), 2);
            turn += 1;
        }

        boolean validBid = spades.setBid(spades.players[0].getPlayerID(), 1);
        assertFalse(validBid);
    }

    @Test
    public void teammateSelectionTest() {
        setup();
        startGame(spades, false, false, true);

        boolean nonHostTeamSet = spades.setTeams(spades.players[1], spades.players[0].getUsername());
        assertFalse(nonHostTeamSet);

        boolean validTeamSet = spades.setTeams(spades.players[0], spades.players[3].getUsername());
        
        assertTrue(validTeamSet);
        assertTrue(spades.players[0].teammate == spades.players[3].getPlayerID());
        assertTrue(spades.players[1].teammate == spades.players[2].getPlayerID());
        assertTrue(spades.players[2].teammate == spades.players[1].getPlayerID());
        assertTrue(spades.players[3].teammate == spades.players[0].getPlayerID());
    };

    @Test
    public void playerSelectsTeammateOutOfPhase() {
        setup();
        startGame(spades, false, false, true);
        spades.setTeams(spades.players[0], spades.players[2].getUsername());
        
        boolean validTeamSet = spades.setTeams(spades.players[0], spades.players[3].getUsername());
        assertFalse(validTeamSet);
    }

    @Test
    public void playerReorientationTest1() {
        setup();
        startGame(spades, false, false, true);
        List<String> playerList1 = new ArrayList<String>(4);
        String default_p1_name = spades.players[0].getUsername();
        String default_p2_name = spades.players[0].getUsername();
        String default_p3_name = spades.players[0].getUsername();
        String default_p4_name = spades.players[0].getUsername();

        playerList1.add(default_p1_name);
        playerList1.add(default_p2_name);
        playerList1.add(default_p4_name);
        playerList1.add(default_p3_name);

        spades.setTeams(spades.players[0], default_p4_name);
        spades.updateOrientationRing(playerList1);

        assertTrue(default_p1_name == spades.players[0].getUsername());
        assertTrue(default_p2_name == spades.players[1].getUsername());
        assertTrue(default_p4_name == spades.players[3].getUsername());
        assertTrue(default_p3_name == spades.players[2].getUsername());
    };

    @Test
    public void playerReorientationTest2() {
        setup();
        startGame(spades, false, false, true);
        List<String> playerList1 = new ArrayList<String>();
        String default_p1_name = spades.players[0].getUsername();
        String default_p2_name = spades.players[0].getUsername();
        String default_p3_name = spades.players[0].getUsername();
        String default_p4_name = spades.players[0].getUsername();

        playerList1.add(default_p1_name);
        playerList1.add(default_p2_name);
        playerList1.add(default_p3_name);
        playerList1.add(default_p4_name);

        spades.setTeams(spades.players[0], default_p3_name);
        spades.updateOrientationRing(playerList1);

        assertTrue(default_p1_name == spades.players[0].getUsername());
        assertTrue(default_p2_name == spades.players[1].getUsername());
        assertTrue(default_p3_name == spades.players[2].getUsername());
        assertTrue(default_p4_name == spades.players[3].getUsername());
    };

    @Test
    public void playerReorientationTest3() {
        setup();
        startGame(spades, false, false, true);
        List<String> playerList1 = new ArrayList<String>();
        String default_p1_name = spades.players[0].getUsername();
        String default_p2_name = spades.players[0].getUsername();
        String default_p3_name = spades.players[0].getUsername();
        String default_p4_name = spades.players[0].getUsername();

        playerList1.add(default_p1_name);
        playerList1.add(default_p3_name);
        playerList1.add(default_p2_name);
        playerList1.add(default_p4_name);

        spades.setTeams(spades.players[0], default_p3_name);
        spades.updateOrientationRing(playerList1);

        assertTrue(default_p1_name == spades.players[0].getUsername());
        assertTrue(default_p3_name == spades.players[1].getUsername());
        assertTrue(default_p2_name == spades.players[2].getUsername());
        assertTrue(default_p4_name == spades.players[3].getUsername());
    };

    @Test
    public void isEndOfTrickTest() {
        setup();
        startGame(spades, true, true, true);
        
        spades.playerInTurn = 0;
        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());
        assertTrue(spades.endOfTrick);
    };
    
    @Test
    public void calculateTrickWinnerTest() {
        setup();
        startGame(spades, true, true, true);
        int player4 = 3;
        spades.playerInTurn = 0;

        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());

        assertTrue(spades.playerInTurn == player4);
        assertTrue(spades.players[player4].tricks.get(CardID.CLUB_TWO.getOrdinal()) != null);
    };

    @Test
    public void calculateSpadesTrumpCardTest() {
        setup();
        startGame(spades, true, true, true);
        int player2 = 1;
        spades.playerInTurn = 0;
        spades.startingTrickCard = new Card(Suit.HEART, Name.ACE, 1, "ace_of_hearts.png");

        spades.addCardToVoidPile(0, new Card(Suit.HEART, Name.ACE, 1, "ace_of_hearts.png"));
        spades.addCardToVoidPile(1, new Card(Suit.SPADE, Name.ACE, 1, "ace_of_spades.png"));
        spades.addCardToVoidPile(2, new Card(Suit.DIAMOND, Name.ACE, 1, "ace_of_diamonds.png"));
        spades.addCardToVoidPile(3, new Card(Suit.CLUB, Name.ACE, 1, "ace_of_clubs.png"));
        
        int winnerIntID = spades.calculateTrickWinner();

        assertTrue(winnerIntID == player2);
    }

    @Test
    public void trickAddedToWinnerTest() {
        setup();
        startGame(spades, true, true, true);
        spades.playerInTurn = 0;

        assertTrue(spades.players[3].getTricks().isEmpty());
        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());

        assertTrue(spades.playerInTurn == 3);
        assertFalse(spades.players[spades.playerInTurn].getTricks().isEmpty());
        assertTrue(spades.players[spades.playerInTurn].getTricks().containsKey(CardID.CLUB_TWO.getOrdinal()));
    };

    @Test
    public void playingWildcardWhenNotVoidTest() {
        setup();
        startGame(spades, true, true, true);
        spades.playerInTurn = 2;

        spades.playTurn(spades.players[2].getPlayerID(), CardID.DIAMOND_FOUR.getOrdinal());
        boolean validTurn = spades.playTurn(spades.players[3].getPlayerID(), CardID.CLUB_KING.getOrdinal());

        assertFalse(validTurn);
    };

    @Test
    public void calculateRoundScoreTest() {
        setup();
        startGame(spades, false, false, true);
        spades.playerInTurn = 0;
        spades.setTeams(spades.players[0], spades.players[1].getUsername());

        spades.setBid(spades.players[0].getPlayerID(), 3);
        spades.setBid(spades.players[1].getPlayerID(), 4);
        spades.setBid(spades.players[2].getPlayerID(), 4);
        spades.setBid(spades.players[3].getPlayerID(), 3);

        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());

        spades.players[0].tricks.putAll(spades.players[0].getHand());
        spades.players[1].tricks.putAll(spades.players[1].getHand());
        spades.players[2].tricks.putAll(spades.players[2].getHand());
        spades.players[3].tricks.putAll(spades.players[3].getHand());
        
        System.out.println(spades.players[0].getScore());
        System.out.println(spades.players[1].getScore());
        System.out.println(spades.players[2].getScore());
        System.out.println(spades.players[3].getScore());

        spades.calculateScore();

        System.out.println(spades.players[0].getScore());
        System.out.println(spades.players[1].getScore());
        System.out.println(spades.players[2].getScore());
        System.out.println(spades.players[3].getScore());

        assertTrue(spades.players[0].getScore() == 0);
        assertTrue(spades.players[1].getScore() == 0);
        assertTrue(spades.players[2].getScore() == 70);
        assertTrue(spades.players[3].getScore() == 70);
    };

    @Test 
    public void nilBidScoringTest() {
        setup();
        startGame(spades, false, false, true);
        spades.playerInTurn = 0;
        spades.setTeams(spades.players[0], spades.players[1].getUsername());

        spades.setBid(spades.players[0].getPlayerID(), 0);
        spades.setBid(spades.players[1].getPlayerID(), 4);
        spades.setBid(spades.players[2].getPlayerID(), 0);
        spades.setBid(spades.players[3].getPlayerID(), 5);

        simulateTrick(spades, testUtils.getFirstSimulatedTrickCardIDs());

        spades.players[1].tricks.putAll(spades.players[0].getHand());
        spades.players[1].tricks.putAll(spades.players[1].getHand());
        spades.players[2].tricks.putAll(spades.players[2].getHand());
        spades.players[3].tricks.putAll(spades.players[3].getHand());
        
        System.out.println(spades.players[0].getScore());
        System.out.println(spades.players[1].getScore());
        System.out.println(spades.players[2].getScore());
        System.out.println(spades.players[3].getScore());

        spades.calculateScore();

        System.out.println(spades.players[0].getScore());
        System.out.println(spades.players[1].getScore());
        System.out.println(spades.players[2].getScore());
        System.out.println(spades.players[3].getScore());

        assertTrue(spades.players[0].getScore() == 100);
        assertTrue(spades.players[1].getScore() == 100);
        assertTrue(spades.players[2].getScore() == -100);
        assertTrue(spades.players[3].getScore() == -100);
    }

    @Test
    public void detectEndOfGameTest() {
        setup();
        startGame(spades, true, false, true);
        spades.playerInTurn = 0;
        spades.setTeams(spades.players[0], spades.players[3].getUsername());

        spades.players[0].setScore(200);
        spades.players[1].setScore(150);
        spades.players[2].setScore(150);
        spades.players[3].setScore(200);

        spades.playTurn(spades.players[0].getPlayerID(), CardID.DIAMOND_NINE.getOrdinal());

        assertTrue(spades.gameEnded);
        assertTrue(spades.isGameOver());
    };

    @Test
    public void determineGameWinnerTest() {
        setup();
        startGame(spades, true, false, true);
        spades.playerInTurn = 0;
        spades.setTeams(spades.players[0], spades.players[3].getUsername());

        spades.players[0].setScore(200);
        spades.players[1].setScore(150);
        spades.players[2].setScore(150);
        spades.players[3].setScore(200);

        // play a valid turn to trigger the end game logic
        spades.playTurn(spades.players[0].getPlayerID(), CardID.CLUB_TWO.getOrdinal());
        spades.setGameWinner();

        assertTrue(spades.gameWinners.get(0).getPlayerID() == spades.players[0].getPlayerID());
        assertTrue(spades.gameWinners.get(1).getPlayerID() == spades.players[3].getPlayerID());

    };


    private void simulateTrick(Spades spades, List<Integer> cardIDList) {
        Player player1 = spades.players[spades.playerInTurn];
        spades.playTurn(player1.getPlayerID(), cardIDList.get(0));

        Player player2 = spades.players[spades.playerInTurn];
        spades.playTurn(player2.getPlayerID(), cardIDList.get(1));

        Player player3 = spades.players[spades.playerInTurn];
        spades.playTurn(player3.getPlayerID(), cardIDList.get(2));

        Player player4 = spades.players[spades.playerInTurn];
        spades.playTurn(player4.getPlayerID(), cardIDList.get(3));
    }
}
