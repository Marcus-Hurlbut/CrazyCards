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
}
