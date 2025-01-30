package com.dungeondecks.marcushurlbut.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.dungeondecks.marcushurlbut.GameManager;
import com.dungeondecks.marcushurlbut.GameType;
import com.dungeondecks.marcushurlbut.Lobby;
import com.dungeondecks.marcushurlbut.Message;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Spades;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.message.BidMessage;
import com.dungeondecks.marcushurlbut.message.LobbyMessage;
import com.dungeondecks.marcushurlbut.message.TeammateMessage;
import com.dungeondecks.marcushurlbut.message.TurnMessage;
import com.dungeondecks.marcushurlbut.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SpadesController {
    public final SimpMessagingTemplate messagingTemplate;
    Messenger messenger;

    @Autowired
    public SpadesController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        messenger = new Messenger(messagingTemplate);
    }

    @MessageMapping("/spades/newLobby")
    public void newLobby(LobbyMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        String username = message.getUsername();

        Player host = new Player(playerID, username);
        Integer roomID = GameManager.newLobby(host, GameType.Spades);

        String destination = "/topic/spades/newLobby/" + playerID;
        messagingTemplate.convertAndSend(destination, Utils.toJSON(roomID));
    }

    @MessageMapping("/spades/joinLobby")
    public void joinLobby(LobbyMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        int roomID = Integer.parseInt(message.getLobbyID());
        String username = message.getUsername();

        Player player = new Player(playerID, username);
        boolean gameFull = GameManager.joinLobby(player, roomID, GameType.Spades);
        Lobby lobby = GameManager.retreiveLobby(roomID);

        // Notify others in lobby
        notifyPlayerJoined(player, roomID);

        Player[] currentPlayers = lobby.players;
        ArrayList<String> otherPlayerNames = new ArrayList<>();

        // Return current player list to this player
        for (Player existingPlayer: currentPlayers) {
            if (existingPlayer != null && player.ID != existingPlayer.ID) {
                otherPlayerNames.add(existingPlayer.getUsername());
            }
        }

        String destination = "/topic/spades/joinLobby/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, Utils.toJSON(otherPlayerNames));

        
        // Start Game & notify game start
        if (gameFull) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                notifyGameStart(lobby.gameID, roomID);
            }, 1000, TimeUnit.MILLISECONDS);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                Spades spades = (Spades) GameManager.retreiveGame(lobby.gameID);
                notifySelectTeammate(lobby.gameID, spades.players[0].ID);
            }, 3000, TimeUnit.MILLISECONDS);
        }
    }

    @MessageMapping("/spades/updateScoreboard")
    public void updateScoreboard(UUID gameID, Player[] players) throws Exception {
        HashMap<String, String> userScores = new HashMap<String, String>();
        for (Player player: players) {
            userScores.put(player.getUsername(), String.valueOf(player.getScore()));
        }
        String destination = "/topic/spades/updateScoreboard/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(userScores));
    }

    @MessageMapping("/spades/selectTeammate")
    public void selectTeammate(TeammateMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        String teammateName = message.getTeammate();

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        int index = spades.playerIDtoInt.get(playerID);

        boolean success = spades.setTeams(spades.players[index], teammateName);
        GameManager.updateGame(gameID, spades);

        String destination = "/topic/spades/selectTeammate/" + gameID.toString() + "/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, Utils.toJSON(!success));

        if (spades.teamSelectionPhaseComplete) {
            List<List<String>> teams = new ArrayList<List<String>>() {{add(spades.team1Names); add(spades.team2Names); }};
            notifyTeamsChosen(gameID, teams);
        }
    }

    @SuppressWarnings("unchecked")
    @MessageMapping("/spades/orientationChange")
    public void orientationChange(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());
        String playersObj = message.getCardIDs();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> ringList = objectMapper.readValue(playersObj, List.class);

        Spades spades = (Spades) GameManager.retreiveGame(gameID);

        Player[] sortedPlayers = spades.updateOrientationRing(ringList);
        GameManager.updateGame(gameID, spades);

        ArrayList<String> sortedPlayerNames = new ArrayList<>();

        for (Player player: sortedPlayers) {
            sortedPlayerNames.add(player.getUsername());
        }

        String destination = "/topic/spades/orientationChange/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(sortedPlayerNames));

        // Wait for player array to be recallibrated with teams set before notifying turn
        if (spades.teamSelectionPhaseComplete) {
            notifyBiddingPhase(gameID, true);
            notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
            notifyNameInTurn(gameID, spades.players[spades.playerInTurn].getUsername());
        }
    }

    @MessageMapping("/spades/placeBid")
    public void placeBid(BidMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        int bid = Integer.parseInt(message.getBid());
        
        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        if (spades.biddingPhaseComplete) {
            return;
        }

        Boolean validBid = spades.setBid(playerID, bid);
        GameManager.updateGame(gameID, spades);
        int id = spades.playerIDtoInt.get(playerID);

        System.out.println("Bid received from player:" + playerID.toString() + " - bid: " + bid + " - valid: " + validBid);

        if (validBid && !spades.biddingPhaseComplete) {
            notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
            notifyNameInTurn(gameID, spades.players[spades.playerInTurn].getUsername());
        }

        if (validBid) {
            broadcastBid(gameID, bid, spades.players[id]);
        }

        if (spades.biddingPhaseComplete) {
            notifyBiddingPhase(gameID, false);
            notifyPlayersTurn(gameID, spades.players[spades.playerInTurn].getPlayerID());
            notifyNameInTurn(gameID, spades.players[spades.playerInTurn].getUsername());
        }

        String destination = "/topic/spades/placeBid/" + gameID.toString() + "/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, Utils.toJSON(validBid));
    }

    @MessageMapping("/spades/playTurn")
    public void playTurn(TurnMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        int cardID = Integer.parseInt(message.getCard());

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        int playerIDindex = spades.playerIDtoInt.get(playerID);
        Card cardToBePlayed = spades.players[playerIDindex].hand.get(cardID);

        Boolean validTurn = spades.playTurn(playerID, cardID);
        GameManager.updateGame(gameID, spades);

        String destination = "/topic/spades/playTurn/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(validTurn));

        if (validTurn) {
            notifyVoidCards(gameID, cardToBePlayed, spades.players[playerIDindex].getUsername());

            if (!spades.endOfRound) {
                notifyPlayersTurn(gameID, spades.players[spades.playerInTurn].ID);
                notifyNameInTurn(gameID, spades.players[spades.playerInTurn].getUsername());
            }

            if (spades.endOfTrick) {
                notifyEndOfTrick(gameID, spades.players[spades.playerInTurn].getUsername());
            }

            if (spades.endOfRound) {
                notifyEndOfRound(gameID);
                updateScoreboard(gameID, spades.players);
                notifyBiddingPhase(gameID, true);
                notifyPlayersBid(gameID, spades.players[spades.playerInTurn].ID);
                notifyNameInTurn(gameID, spades.players[spades.playerInTurn].getUsername());
            }

            if (spades.gameEnded) {
                notifyGameEnded(gameID, spades.getGameWinner().getUsername());
            }
        }
    }

    @MessageMapping("/spades/getHand")
    public void getHand(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());

        Spades spades = (Spades) GameManager.retreiveGame(gameID);
        int index = spades.playerIDtoInt.get(playerID);
        
        HashMap<Integer, Card> playerHand = spades.players[index].getHand();
        String json = Utils.toJSON(playerHand);

        String destination = "/topic/spades/getHand/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);
    }

    public void notifySelectTeammate(UUID gameID, UUID playerID) {
        messenger.notifySelectTeammate("spades", gameID, playerID);
    }

    public void notifyTeamsChosen(UUID gameID, List<List<String>> teams) {
        messenger.notifyTeamsChosen("spades", gameID, teams);
    }

    public void broadcastBid(UUID gameID, int bidAmount, Player userWithBid) {
        messenger.broadcastBid("spades", gameID, bidAmount, userWithBid);
    }

    public void notifyNameInTurn(UUID gameID, String name) {
        messenger.notifyNameInTurn("spades", gameID, name);
    }

    public void notifyPlayersBid(UUID gameID, UUID playerID) {
        messenger.notifyPlayersBid("spades", gameID, playerID);
    }

    public void notifyPlayersTurn(UUID gameID, UUID playerID) {
        messenger.notifyPlayersTurn("spades", gameID, playerID);
    }

    public void notifyVoidCards(UUID gameID,  Card voidCard, String playedByName) {
        messenger.notifyVoidCards("spades", gameID, voidCard, playedByName);
    }

    public void notifyGameEnded(UUID gameID, String winnerName) {
        messenger.notifyGameEnded("spades", gameID, winnerName);
    }

    public void notifyEndOfRound(UUID gameID) {
        messenger.notifyEndOfRound("spades", gameID);
    }

    public void notifyEndOfTrick(UUID gameID, String trickWinnerUsername) {
        messenger.notifyEndOfTrick("spades", gameID, trickWinnerUsername);
    }

    public void notifyBiddingPhase(UUID gameID, boolean inBidPhase) {
        messenger.notifyBiddingPhase("spades", gameID, inBidPhase);
    }
    
    public void notifyPlayerJoined(Player joinedPlayer, Integer lobbyID) {
        messenger.notifyPlayerJoined("spades", joinedPlayer, lobbyID);
    }

    public void notifyGameStart(UUID gameID, Integer roomID) {
        messenger.notifyGameStart("spades", gameID, roomID);
    }
}
