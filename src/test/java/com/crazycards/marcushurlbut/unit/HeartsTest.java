package com.crazycards.marcushurlbut.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.util.Assert;
import org.springframework.core.annotation.Order;

import com.crazycards.marcushurlbut.Card;
import com.crazycards.marcushurlbut.Hearts;
import com.crazycards.marcushurlbut.PassingPhase;
import com.crazycards.marcushurlbut.Player;
import com.crazycards.marcushurlbut.Suit;
import com.crazycards.marcushurlbut.util.TestUtils;
import com.crazycards.marcushurlbut.utils.CardID;

// @SpringBootTest
public class HeartsTest {
    private static UUID gameID;
    private static Hearts hearts;
    private static TestUtils testUtils;


    private static void setup() {
        gameID = UUID.randomUUID();
        hearts = new Hearts(gameID);
        testUtils = new TestUtils();
    }

    private void startGame(Hearts heart, boolean skipPassingPhase, boolean useFakeDeck) {
        heart.initializePlayers(testUtils.getPlayerList());
        if (!useFakeDeck) {
            heart.shuffleAndDeal();
            heart.active = true;
        } else {
            testUtils.populateFakeDeck(hearts.players);
            hearts.setPassingPhase();
        }

        if (skipPassingPhase) {
            hearts.onEndOfPassingPhase();
        }
    }

    @Test
    public void initializePlayersTest() {
        setup();

        ArrayList<Player> players = testUtils.getPlayerList();
        hearts.initializePlayers(players);
        
        assertEquals(hearts.players[0].getPlayerID(), TestUtils.player_1_id);
        assertEquals(hearts.players[1].getPlayerID(), TestUtils.player_2_id);
        assertEquals(hearts.players[2].getPlayerID(), TestUtils.player_3_id);
        assertEquals(hearts.players[3].getPlayerID(), TestUtils.player_4_id);
	}

    @Test
    public void shuffleDeckTest() {
        setup();
        startGame(hearts, false, false);

        for (Player player: hearts.players) {
            assertFalse(player.getHand().isEmpty());
            assertNotNull(player.getHand());
        }
    }

    @Test
    public void firstInitiativeStartsWithTwoOfClubsTest() {
        setup();
        startGame(hearts, true, false);

        Card twoOfClubs = testUtils.getCardFromPlayerHand(hearts.players[hearts.playerInTurn], CardID.CLUB_TWO);
        assertTrue(twoOfClubs != null);
    }

    @Test
    public void playerPlaysOutOfTurnTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        assertTrue(hearts.playerInTurn == 0);

        validTurn = hearts.playTurn(hearts.players[2].getPlayerID(), CardID.CLUB_QUEEN.getOrdinal());
        assertFalse(validTurn);
    }

    @Test
    public void playerPlaysHeartBeforeSuitBrokenTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        simulateFirstTrick(hearts); // Last player wins simulated trick
        validTurn = hearts.playTurn(hearts.players[3].getPlayerID(), CardID.HEART_THREE.getOrdinal());

        assertFalse(validTurn);
    }

    @Test
    public void passLeftTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;
    
        // Create mutable lists for pass cards
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);  // Get 3 cards
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);  // Get 3 cards
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);  // Get 3 cards
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);  // Get 3 cards

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        // Simulate the pass phase
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_2_receivedCards));
        assertTrue(player_2_passCards.equals(player_3_receivedCards));
        assertTrue(player_3_passCards.equals(player_4_receivedCards));
        assertTrue(player_4_passCards.equals(player_1_receivedCards));
    }

    @Test
    public void passRightTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.RIGHT;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_4_receivedCards));
        assertTrue(player_2_passCards.equals(player_1_receivedCards));
        assertTrue(player_3_passCards.equals(player_2_receivedCards));
        assertTrue(player_4_passCards.equals(player_3_receivedCards));
    }

    @Test
    public void passAcrossTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.ACROSS;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_3_receivedCards));
        assertTrue(player_2_passCards.equals(player_4_receivedCards));
        assertTrue(player_3_passCards.equals(player_1_receivedCards));
        assertTrue(player_4_passCards.equals(player_2_receivedCards));
    }

    public void passKeepTest() {
        setup();
        startGame(hearts, false, false);
        Player[] players = hearts.players;

        hearts.roundPassingType = PassingPhase.KEEP;
    
        List<Integer> player_1_passCards = new ArrayList<>(players[0].getHand().keySet()).subList(0, 3);
        List<Integer> player_2_passCards = new ArrayList<>(players[1].getHand().keySet()).subList(0, 3);
        List<Integer> player_3_passCards = new ArrayList<>(players[2].getHand().keySet()).subList(0, 3);
        List<Integer> player_4_passCards = new ArrayList<>(players[3].getHand().keySet()).subList(0, 3);

        Collections.sort(player_1_passCards);
        Collections.sort(player_2_passCards);
        Collections.sort(player_3_passCards);
        Collections.sort(player_4_passCards);
    
        simulatePassPhase(hearts.players, player_1_passCards, player_2_passCards, player_3_passCards, player_4_passCards);
    
        List<Integer> player_1_receivedCards = new ArrayList<>(hearts.players[0].passedCards.keySet());
        List<Integer> player_2_receivedCards = new ArrayList<>(hearts.players[1].passedCards.keySet());
        List<Integer> player_3_receivedCards = new ArrayList<>(hearts.players[2].passedCards.keySet());
        List<Integer> player_4_receivedCards = new ArrayList<>(hearts.players[3].passedCards.keySet());

        Collections.sort(player_1_receivedCards);
        Collections.sort(player_2_receivedCards);
        Collections.sort(player_3_receivedCards);
        Collections.sort(player_4_receivedCards);
    
        assertTrue(player_1_passCards.equals(player_3_receivedCards));
        assertTrue(player_2_passCards.equals(player_4_receivedCards));
        assertTrue(player_3_passCards.equals(player_1_receivedCards));
        assertTrue(player_4_passCards.equals(player_2_receivedCards));
    }

    public void isEndOfTrickTest() {}

    public void isEndOfRoundTest() {}

    public void calculateTrickWinnerTest() {}

    public void trickAddedToWinnerTest() {}

    public void calculateScoreTest() {}

    public void shootingTheMoonTest() {}
    
    public void queenOfSpadesPlayedFirstRoundTest() {}

    public void simulatePassPhase(Player[] players,  List<Integer> player_1_passCards,  List<Integer> player_2_passCards,  List<Integer> player_3_passCards,  List<Integer> player_4_passCards) {
        hearts.passCards(players[0].getPlayerID(), player_1_passCards);
        hearts.passCards(players[1].getPlayerID(), player_2_passCards);
        hearts.passCards(players[2].getPlayerID(), player_3_passCards);
        hearts.passCards(players[3].getPlayerID(), player_4_passCards);
    }

    public void simulateFirstTrick(Hearts hearts) {

        Player player1 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player1.getPlayerID(), CardID.CLUB_TWO.getOrdinal());

        Player player2 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player2.getPlayerID(), CardID.CLUB_THREE.getOrdinal());

        Player player3 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player3.getPlayerID(), CardID.CLUB_QUEEN.getOrdinal());

        Player player4 = hearts.players[hearts.playerInTurn];
        hearts.playTurn(player4.getPlayerID(), CardID.CLUB_ACE.getOrdinal());
    }

    // TODO: convert this and other trick tests to integration tests using web sockets
    // as it aligns better to the test & tests more thoroughly 
    @Test
    public void simulateFirstTrickTest() {
        setup();
        startGame(hearts, true, true);
        boolean validTurn;

        // Player 0 starts with 2-club
        Player player0 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 0);
        validTurn = hearts.playTurn(player0.getPlayerID(), CardID.CLUB_TWO.getOrdinal());
        assertTrue(validTurn);

        // Player 1 
        Player player1 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 1);
        validTurn = hearts.playTurn(player1.getPlayerID(), CardID.CLUB_THREE.getOrdinal());
        assertTrue(validTurn);

        // Player 2
        Player player2 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 2);
        validTurn = hearts.playTurn(player2.getPlayerID(), CardID.CLUB_QUEEN.getOrdinal());
        assertTrue(validTurn);

        // Player 3
        Player player3 = hearts.players[hearts.playerInTurn];
        assertTrue(hearts.playerInTurn == 3);
        validTurn = hearts.playTurn(player3.getPlayerID(), CardID.CLUB_ACE.getOrdinal());
        assertTrue(validTurn);

        // Player 3 won hand
        assertTrue(hearts.playerInTurn == 3);
    }
}
