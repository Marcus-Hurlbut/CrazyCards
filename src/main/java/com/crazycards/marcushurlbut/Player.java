package com.crazycards.marcushurlbut;
import java.util.*;

public class Player {
    String accountID;
    String username;
    HashMap<Integer, Card> hand = new HashMap<Integer, Card>();
    

    public Player() {}
    public Player(String accountID, String username) {
        this.accountID = accountID;
        this.username = username;
    }

    private int getCardID(Card card) {
        return card.name.ordinal() * card.suit.ordinal();
    }

    public void addCardToHand(Card card) {
        int id = getCardID(card);
        hand.put(id, card);
    }

    public Card removeCardFromHand(Card card) {
        int id = getCardID(card);
        return hand.remove(id);
    }
}
