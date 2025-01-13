package com.crazycards.marcushurlbut.API;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crazycards.marcushurlbut.Card;
import com.crazycards.marcushurlbut.Hearts;

@CrossOrigin(origins = "http://localhost:8081")

@RestController
public class HeartsAPI {
    @GetMapping("/startHearts")
    public ResponseEntity<Map<String, Object>> start(@RequestParam("playerID") int playerID) {
        Map<String, Object> result = new HashMap<>();

        // Hearts.initialize();
        // Hearts.deck.shuffleDeck();
        // Hearts.deck.dealDeck(Hearts.players);

        // HashMap<Integer, Card> hand_by_id = Hearts.players[playerID].getHand();
        // List<Map.Entry<Integer, Card>> sortedCards = new ArrayList<>(hand_by_id.entrySet());
        // sortedCards.sort(Map.Entry.comparingByKey());

        // Map<Integer, Card> cardMap = new HashMap<>();
        // for (Map.Entry<Integer, Card> entry : sortedCards) {
        //     Card card = entry.getValue();
        //     int id = Hearts.players[playerID].getCardID(card);
        //     cardMap.put(id, card);
        // }

        // result.put("cards", cardMap);
        // System.out.println("[API CALL] cards: " + result);
        return ResponseEntity.ok(result);
    }
}
