package com.dungeondecks.marcushurlbut.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.utils.Utils;

public class Messenger {
    public final SimpMessagingTemplate messagingTemplate;

    public Messenger(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyPlayerJoined(String game, Player joinedPlayer, Integer lobbyID) {
        try {
            String destination = "/topic/" + game + "/notifyPlayerJoined/" + lobbyID.toString();
            String json = Utils.toJSON(joinedPlayer);
    
            System.out.println("Player joined: " + json);
            messagingTemplate.convertAndSend(destination, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGameStart(String game, UUID gameID, Integer lobbyID) {
        try {
            String destination = "/topic/" + game + "/notifyGameStart/" + lobbyID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(gameID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifySelectTeammate(String game, UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/" + game + "/notifySelectTeammate/" + gameID.toString() + "/" + playerID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyTeamsChosen(String game, UUID gameID, List<List<String>> teams) {
        try {
            String destination = "/topic/" + game + "/notifyTeamsChosen/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(teams));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyBiddingPhase(String game, UUID gameID, boolean inBidPhase) {
        try {
            String destination = "/topic/" + game + "/notifyBiddingPhase/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(inBidPhase));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPlayersBid(String game, UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/" + game + "/notifyPlayersBid/" + gameID.toString() + "/" + playerID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(true));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastBid(String game, UUID gameID, int bidAmount, Player userWithBid) {
        String destination = "/topic/" + game + "/broadcastBid/" + gameID.toString();
        HashMap<String, Integer> playerToBidAmount = new HashMap<String, Integer>();

        playerToBidAmount.put(userWithBid.getUsername(), bidAmount);
        messagingTemplate.convertAndSend(destination, Utils.toJSON(playerToBidAmount));
    }

    public void notifyPassingPhase(String game, UUID gameID, boolean inPassPhase) {
        try {
            String destination = "/topic/" + game + "/notifyPassingPhase/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(inPassPhase));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPassCardsReceived(String game, UUID playerID, HashMap<Integer, Card> passedCards) {
        try {
            String destination = "/topic/" + game + "/notifyPassCardsReceived/" + playerID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(passedCards));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyNameInTurn(String game, UUID gameID, String name) {
        try {
            String destination = "/topic/" + game + "/notifyNameInTurn/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(name));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyPlayersTurn(String game, UUID gameID, UUID playerID) {
        try {
            String destination = "/topic/" + game + "/notifyPlayersTurn/" + gameID.toString() + "/" + playerID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(true));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyVoidCards(String game, UUID gameID, Card voidCard, String playedByName) {
        try {
            HashMap<Integer, Card> idToCard = new HashMap<Integer, Card>();
            HashMap<String, Object> nameToContent = new HashMap<String, Object>();

            String destination = "/topic/" + game + "/notifyVoidCards/" + gameID.toString();

            int cardID = voidCard.getCardID(voidCard);
            idToCard.put(cardID, voidCard);
            nameToContent.put(playedByName, idToCard);

            messagingTemplate.convertAndSend(destination, Utils.toJSON(nameToContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyEndOfTrick(String game, UUID gameID, String trickWinnerUsername) {
        try {
            String destination = "/topic/" + game + "/notifyEndOfTrick/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(trickWinnerUsername));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyEndOfRound(String game, UUID gameID) {
        try {
            String destination = "/topic/" + game + "/notifyEndOfRound/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyGameEnded(String game, UUID gameID, String winnerName) {
        try {
            String destination = "/topic/" + game + "/notifyGameEnded/" + gameID.toString();
            messagingTemplate.convertAndSend(destination, Utils.toJSON(winnerName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
