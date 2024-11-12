package com.crazycards.marcushurlbut;

enum Suit {
    HEART,
    DIAMOND,
    SPADE,
    CLUB
}

enum Name {
    ACE,
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
    KING
}

public class Card {
    Suit suit;
    Name name;
    int value;
    String imgPath;
}
