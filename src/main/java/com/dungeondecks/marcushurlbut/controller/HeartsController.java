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
import com.dungeondecks.marcushurlbut.PassingPhase;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.message.LobbyMessage;
import com.dungeondecks.marcushurlbut.message.PassMessage;
import com.dungeondecks.marcushurlbut.message.TurnMessage;
import com.dungeondecks.marcushurlbut.utils.Utils;

@Controller
public class HeartsController {
    public final SimpMessagingTemplate messagingTemplate;
    Messenger messenger;

    @Autowired
    public HeartsController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        messenger = new Messenger(messagingTemplate);
    }

    @MessageMapping("/hearts/newLobby")
    public void newLobby(LobbyMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        String username = message.getUsername();

        Player host = new Player(playerID, username);
        Integer roomID = GameManager.newLobby(host, GameType.Hearts);

        String destination = "/topic/hearts/newLobby/" + playerID;
        messagingTemplate.convertAndSend(destination, Utils.toJSON(roomID));
    }

    @MessageMapping("/hearts/joinLobby")
    public void joinLobby(LobbyMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        int lobbyID = Integer.parseInt(message.getLobbyID());
        String username = message.getUsername();

        Player player = new Player(playerID, username);
        boolean gameFull = GameManager.joinLobby(player, lobbyID, GameType.Hearts);
        Lobby lobby = GameManager.retreiveLobby(lobbyID);

        // Notify others in lobby
        notifyPlayerJoined(player, lobbyID);

        Player[] currentPlayers = lobby.players;
        ArrayList<String> otherPlayerNames = new ArrayList<>();

        // Return current player list to this player
        for (Player existingPlayer: currentPlayers) {
            if (existingPlayer != null && player.ID != existingPlayer.ID) {
                otherPlayerNames.add(existingPlayer.getUsername());
            }
        }

        String destination = "/topic/hearts/joinLobby/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, Utils.toJSON(otherPlayerNames));
        
        if (gameFull) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                notifyGameStart(lobby.gameID, lobbyID);
            }, 1000, TimeUnit.MILLISECONDS);
        }
    }

    @MessageMapping("/hearts/updateScoreboard")
    public void updateScoreboard(UUID gameID, Player[] players) throws Exception {
        HashMap<String, String> userScores = new HashMap<String, String>();
        for (Player player: players) {
            userScores.put(player.getUsername(), String.valueOf(player.getScore()));
        }
        String destination = "/topic/hearts/updateScoreboard/" + gameID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(userScores));
    }

    @MessageMapping("/hearts/playTurn")
    public void playTurn(TurnMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        int cardID = Integer.parseInt(message.getCard());
        
        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);
        int playerIDindex = hearts.playerIDtoInt.get(playerID);
        Card cardToBePlayed = hearts.players[playerIDindex].hand.get(cardID);

        Boolean validTurn = hearts.playTurn(playerID, cardID);
        GameManager.updateGame(gameID, hearts);

        String destination = "/topic/hearts/playTurn/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(validTurn));

        if (validTurn) {
            notifyVoidCards(gameID, cardToBePlayed, hearts.players[playerIDindex].getUsername());

            // Need to re-shuffle first & pass
            if (!hearts.endOfRound) {
                notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].getPlayerID());
                notifyNameInTurn(gameID, hearts.players[hearts.playerInTurn].getUsername());
            }

            if (hearts.endOfTrick) {
                notifyEndOfTrick(gameID, hearts.players[hearts.playerInTurn].getUsername());
            }

            if (hearts.endOfRound && (!hearts.passingPhaseComplete || hearts.roundPassingType == PassingPhase.KEEP)) {
                notifyEndOfRound(gameID);
                updateScoreboard(gameID, hearts.players);
                if (hearts.roundPassingType != PassingPhase.KEEP) {
                    notifyPassingPhase(gameID, true);
                } else {
                    hearts = (Hearts) GameManager.retreiveGame(gameID);
                    hearts.onEndOfPassingPhase();
                    GameManager.updateGame(gameID, hearts);
                    notifyPassingPhase(gameID, false);
                    notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].getPlayerID());
                    notifyNameInTurn(gameID, hearts.players[hearts.playerInTurn].getUsername());
                }
            }

            if (hearts.gameEnded) {
                notifyGameEnded(gameID, hearts.getGameWinner().getUsername());
            }
        }
    }

    @MessageMapping("/hearts/getHand")
    public void getHand(Message message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getRoomID());

        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);
        int index = hearts.playerIDtoInt.get(playerID);
        
        HashMap<Integer, Card> playerHand = hearts.players[index].getHand();
        String json = Utils.toJSON(playerHand);

        String destination = "/topic/hearts/getHand/" + playerID.toString(); 
        messagingTemplate.convertAndSend(destination, json);
    }

    @MessageMapping("/hearts/passCards")
    public void passCards(PassMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        String strCardIDs = message.getCards();

        List<Integer> cardIDs = Utils.intListFromJSON(strCardIDs);
        Hearts hearts = (Hearts) GameManager.retreiveGame(gameID);

        int playerIDInt = hearts.playerIDtoInt.get(playerID);
        if (hearts.players[playerIDInt].didPassCards) {
            return;
        }

        UUID sentToID = hearts.passCards(playerID, cardIDs);
        GameManager.updateGame(gameID, hearts);

        // Notify other player of passed cards
        int sentToIDIndex = hearts.playerIDtoInt.get(sentToID);
        HashMap<Integer, Card> sentToPassedCards = hearts.players[sentToIDIndex].passedCards;

        if (hearts.players[sentToIDIndex].didPassCards) {
            notifyPassCardsReceived(sentToID, sentToPassedCards);
        }

        int playerIDIndex = hearts.playerIDtoInt.get(playerID);
        String destination = "/topic/hearts/passCards/" + playerID.toString();

        // Pass cards for player are receieved
        if (hearts.players[playerIDIndex].didReceiveCards) {
            HashMap<Integer, Card> playerPassedCards = hearts.players[playerIDIndex].passedCards;
            String json = Utils.toJSON(playerPassedCards);
            messagingTemplate.convertAndSend(destination, json);
        }
        // Passed cards must be received later
        else {
            messagingTemplate.convertAndSend(destination, Utils.toJSON(false));
        }

        Boolean passingPhaseOver = true;
        for (int index = 0; index < hearts.players.length; index++) {
            if (hearts.players[index].didPassCards == false) {
                passingPhaseOver = false;
                break;
            }
        }

        if (passingPhaseOver) {
            hearts = (Hearts) GameManager.retreiveGame(gameID);
            hearts.onEndOfPassingPhase();
            GameManager.updateGame(gameID, hearts);
            notifyPassingPhase(gameID, false);
            notifyPlayersTurn(gameID, hearts.players[hearts.playerInTurn].getPlayerID());
            notifyNameInTurn(gameID, hearts.players[hearts.playerInTurn].getUsername());
        }
    }

    public void notifyNameInTurn(UUID gameID, String name) {
        messenger.notifyNameInTurn("hearts", gameID, name);
    }

    public void notifyPlayersTurn(UUID gameID, UUID playerID) {
        messenger.notifyPlayersTurn("hearts", gameID, playerID);
    }

    public void notifyVoidCards(UUID gameID,  Card voidCard, String playedByName) {
        messenger.notifyVoidCards("hearts", gameID, voidCard, playedByName);
    }

    public void notifyGameEnded(UUID gameID, String winnerName) {
        messenger.notifyGameEnded("hearts", gameID, winnerName);
    }

    public void notifyEndOfRound(UUID gameID) {
        messenger.notifyEndOfRound("hearts", gameID);
    }
   
    public void notifyEndOfTrick(UUID gameID, String trickWinnerUsername) {
        messenger.notifyEndOfTrick("hearts", gameID, trickWinnerUsername);
    }

    public void notifyPassingPhase(UUID gameID, boolean inPassPhase) {
        messenger.notifyPassingPhase("hearts", gameID, inPassPhase);
    }

    public void notifyPassCardsReceived(UUID playerID, HashMap<Integer, Card> passedCards) {
        messenger.notifyPassCardsReceived("hearts", playerID, passedCards);
    }

    public void notifyPlayerJoined(Player joinedPlayer, Integer lobbyID) {
        messenger.notifyPlayerJoined("hearts", joinedPlayer, lobbyID);
    }

    public void notifyGameStart(UUID gameID, Integer roomID) {
        messenger.notifyGameStart("hearts", gameID, roomID);
    }
}