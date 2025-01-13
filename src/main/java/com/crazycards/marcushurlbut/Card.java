package com.crazycards.marcushurlbut;

enum Suit {
    HEART,
    DIAMOND,
    SPADE,
    CLUB
}

enum Name {
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING,
    ACE
}

public class Card {
    public Suit suit;
    public Name name;
    public int value;
    public String imgPath;

    Card() {}

    public Card(Suit suit, Name name, int value, String imgPath) {
        this.suit = suit;
        this.name = name;
        this.value = value;
        this.imgPath = imgPath;
    }

    public int getCardID(Card card) {
        int offset = 0;
        switch (card.suit) {
            case HEART:
                offset = 0;
                break;

            case DIAMOND:
                offset = 13;
                break;
            
            case SPADE:
                offset = 26;
                break;

            case CLUB:
                offset = 39;
                break;
        
            default:
                break;
        }

        if (card.name.ordinal() == 0) {
            return offset;
        }
        return (card.name.ordinal() + offset);
    }
}
