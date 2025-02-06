package com.dungeondecks.marcushurlbut.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.dungeondecks.marcushurlbut.GameManager;
import com.dungeondecks.marcushurlbut.GameType;
import com.dungeondecks.marcushurlbut.Lobby;
import com.dungeondecks.marcushurlbut.Player;
import com.dungeondecks.marcushurlbut.games.SpiderSolitaire;
import com.dungeondecks.marcushurlbut.games.card.Card;
import com.dungeondecks.marcushurlbut.message.LobbyMessage;
import com.dungeondecks.marcushurlbut.message.SolitaireMessage;
import com.dungeondecks.marcushurlbut.utils.Utils;

@Controller
public class SpiderSolitaireController {
    public final SimpMessagingTemplate messagingTemplate;
    Messenger messenger;

    @Autowired
    public SpiderSolitaireController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        messenger = new Messenger(messagingTemplate);
    }

    @MessageMapping("/spiderSolitaire/newLobby")
    public void newLobby(LobbyMessage message) throws Exception {
        UUID playerID = UUID.fromString(message.getPlayerID());
        String username = message.getUsername();
        int suitCount = Integer.parseInt(message.getCount());

        Player host = new Player(playerID, username);
        Integer lobbyID = GameManager.newLobby(host, GameType.SpiderSolitaire, suitCount);

        String destination = "/topic/spiderSolitaire/newLobby/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(lobbyID));
        Lobby lobby = GameManager.retreiveLobby(lobbyID);
        
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            notifyGameStart(lobby.gameID, lobbyID);
        }, 2000, TimeUnit.MILLISECONDS);
    }

    @MessageMapping("/spiderSolitaire/revealTopCards")
    public void revealTopCards(SolitaireMessage message) {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());

        SpiderSolitaire spiderSolitaire = (SpiderSolitaire) GameManager.retreiveGame(gameID);
        List<Card> topCards = spiderSolitaire.revealTopCards(playerID);
        GameManager.updateGame(gameID, spiderSolitaire);

        String destination = "/topic/spiderSolitaire/revealTopCards/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(topCards));
    }

    @MessageMapping("/spiderSolitaire/hitDeck")
    public void hitDeck(SolitaireMessage message) {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());

        SpiderSolitaire spiderSolitaire = (SpiderSolitaire) GameManager.retreiveGame(gameID);
        List<Card> cards = spiderSolitaire.dealDeck(playerID);
        GameManager.updateGame(gameID, spiderSolitaire);

        String destination = "/topic/spiderSolitaire/hitDeck/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON((cards)));

        notifyNumberDealsLeft(gameID, playerID, spiderSolitaire.dealLeft);

        if (spiderSolitaire.notifyFoundation) {
            Card card = new Card();
            if (!spiderSolitaire.tableau[spiderSolitaire.foundationColumn].isEmpty()) {
                card = spiderSolitaire.tableau[spiderSolitaire.foundationColumn].peek();
            }
            notifyFoundations(gameID, playerID, spiderSolitaire.foundationColumn, card);
        }
    }

    @MessageMapping("/spiderSolitaire/playTurn")
    public void playTurn(SolitaireMessage message) {
        UUID playerID = UUID.fromString(message.getPlayerID());
        UUID gameID = UUID.fromString(message.getGameID());
        int to = Integer.parseInt(message.getTo());
        int from = Integer.parseInt(message.getFrom());
        List<Card> cards = Utils.cardFromJSON(message.getCards());

        SpiderSolitaire spiderSolitaire = (SpiderSolitaire) GameManager.retreiveGame(gameID);
        boolean validTurn = spiderSolitaire.playTurn(playerID, cards, from, to);

        String destination = "/topic/spiderSolitaire/playTurn/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(validTurn));

        if (spiderSolitaire.gameEnded) {
            notifyGameEnded(gameID, spiderSolitaire.player.getUsername());
            return;
        } 

        if (!validTurn) {
            return;
        }

        notifyCardsTransferredTableau(gameID, playerID, cards, to);

        if (spiderSolitaire.revealedCard != null) {
            notifyNewRevealedCard(gameID, playerID, spiderSolitaire.revealedCard, from);
        }

        if (validTurn && spiderSolitaire.notifyFoundation) {
            Card card = new Card();
            if (!spiderSolitaire.tableau[to].isEmpty()) {
                card = spiderSolitaire.tableau[to].peek();
            }
            notifyFoundations(gameID, playerID, to, card);
        }

        GameManager.updateGame(gameID, spiderSolitaire);
    }

    public void notifyFoundations(UUID gameID, UUID playerID, int source, Card topCard)  {
        String destination = "/topic/spiderSolitaire/notifyFoundations/" + gameID.toString() + "/" + playerID.toString();
        HashMap<Integer, Card> json = new HashMap<Integer, Card>();
        json.put(source, topCard);
        messagingTemplate.convertAndSend(destination, Utils.toJSON(json));
    }

    public void notifyNewRevealedCard(UUID gameID, UUID playerID, Card card, int column) {
        HashMap<Integer, Card> json = new HashMap<Integer, Card>();
        json.put(column, card);
        String destination = "/topic/spiderSolitaire/notifyNewRevealedCard/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(json));
    }

    public void notifyCardsTransferredTableau(UUID gameID, UUID playerID, List<Card> cards, int to) {
        HashMap<Integer, List<Card>> json = new HashMap<Integer, List<Card>>();
        json.put(to, cards);

        String destination = "/topic/spiderSolitaire/notifyCardsTransferredTableau/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(json));
    }

    public void notifyNumberDealsLeft(UUID gameID, UUID playerID, int dealsLeft) {
        String destination = "/topic/spiderSolitaire/notifyNumberDealsLeft/" + gameID.toString() + "/" + playerID.toString();
        messagingTemplate.convertAndSend(destination, Utils.toJSON(dealsLeft));
    }

    public void notifyGameStart(UUID gameID, int lobbyID) {
        messenger.notifyGameStart("spiderSolitaire", gameID, lobbyID);
    }

    public void notifyGameEnded(UUID gameID, String winnerName) {
        messenger.notifyGameEnded("spiderSolitaire", gameID, winnerName);
    }
}
