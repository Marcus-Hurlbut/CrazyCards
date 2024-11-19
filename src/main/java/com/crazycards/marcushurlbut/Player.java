package com.crazycards.marcushurlbut;
import java.util.*;

public class Player {
    String accountID;
    String username;
    HashMap<Integer, Card> hand = new HashMap<Integer, Card>();
    

    public Player() {
        this.accountID = "DEFAULT";
        this.username = "DEFAULT";
    }
    public Player(String accountID, String username) {
        this.accountID = accountID;
        this.username = username;
    }

    public int getCardID(Card card) {
        int offset = 0;
        switch (card.suit) {
            case HEART:
                offset = 0;
                break;

            case DIAMOND:
                offset = 12;
                break;
            
            case SPADE:
                offset = 25;
                break;

            case CLUB:
                offset = 38;
                break;
        
            default:
                break;
        }

        if (card.name.ordinal() == 0) {
            return offset;
        }
        return (card.name.ordinal() + offset);
    }

    public void addCardToHand(Card card) {
        int id = getCardID(card);
        hand.put(id, card);
    }

    public Card removeCardFromHand(Card card) {
        int id = getCardID(card);
        return hand.remove(id);
    }

    public HashMap<Integer, Card> getHand() {
        return hand;
    }
}
