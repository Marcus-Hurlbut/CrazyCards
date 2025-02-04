package com.dungeondecks.marcushurlbut.games.card;

public class Card {
    public int id;
    public Suit suit;
    public Name name;
    public int value;
    public String imgPath;
    private boolean hidden = false;

    public Card() {}

    public Card(Suit suit, Name name, int value, String imgPath) {
        this.id = getCardID(suit, name);
        this.suit = suit;
        this.name = name;
        this.value = value;
        this.imgPath = imgPath;
    }

    public int getCardID(Suit suit, Name name) {
        int offset = 0;
        if (suit == Suit.HEART) {
            offset = 0;
        } else if (suit == Suit.DIAMOND) {
            offset = 13;
        } else if (suit == Suit.SPADE) {
            offset = 26;
        } else if (suit == Suit.CLUB) {
            offset = 39;
        }

        if (name.ordinal() == 0) {
            return offset;
        }
        return (name.ordinal() + offset);
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

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
