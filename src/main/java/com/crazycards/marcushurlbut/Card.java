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
}
