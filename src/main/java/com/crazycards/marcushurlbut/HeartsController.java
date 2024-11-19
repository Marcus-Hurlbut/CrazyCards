package com.crazycards.marcushurlbut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HeartsController {

    @MessageMapping("/playTurn")
    @SendTo("/topic/playTurn")
    public Message playTurn(Message message) throws Exception {
        int playerID = Integer.parseInt(message.getPlayerID());
        int cardID = Integer.parseInt(message.getCardID());

        System.out.println("[/topic/playTurn] - playerID: " + message.getPlayerID() + " CardID: " + cardID);

        return new Message("Hello, " + HtmlUtils.htmlEscape(message.getPlayerID().toString()) + "!");
    }

    @MessageMapping("/getHand")
    @SendTo("/topic/getHand")
    public Message getHand(Message message) throws Exception {
        int playerID = Integer.parseInt(message.getContent());
        System.out.println("[/topic/getHand] playerID: " + playerID);
        
        HashMap<Integer, Card> playerHand = Hearts.players[playerID].getHand();
        List<Map.Entry<Integer, Card>> sortedCards = new ArrayList<>(playerHand.entrySet());
        sortedCards.sort(Map.Entry.comparingByKey());

        Map<Integer, Card> cardMap = new HashMap<>();
        for (Map.Entry<Integer, Card> entry : sortedCards) {
            Card card = entry.getValue();
            int id = Hearts.players[playerID].getCardID(card);
            cardMap.put(id, card);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(cardMap);

        System.out.println("[/topic/getHand] cards: " + jsonArray);

        return new Message(jsonArray);
    }

    @MessageMapping("/startGame")
    @SendTo("/topic/startGame")
    public Message startGame(Message message) throws Exception {      
        Hearts.initialize();
        Hearts.deck.shuffleDeck();
        Hearts.deck.dealDeck(Hearts.players);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = objectMapper.writeValueAsString(0);

        // System.out.println("[/topic/getStartingCards] cards: " + jsonArray);

        return new Message(jsonArray);
    }
}