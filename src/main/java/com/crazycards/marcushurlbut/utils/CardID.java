package com.crazycards.marcushurlbut.utils;

public enum CardID {
    // HEART cards
    HEART_TWO(0), HEART_THREE(1), HEART_FOUR(2), HEART_FIVE(3), HEART_SIX(4),
    HEART_SEVEN(5), HEART_EIGHT(6), HEART_NINE(7), HEART_TEN(8),
    HEART_JACK(9), HEART_QUEEN(10), HEART_KING(11), HEART_ACE(12),

    // DIAMOND cards
    DIAMOND_TWO(13), DIAMOND_THREE(14), DIAMOND_FOUR(15), DIAMOND_FIVE(16), DIAMOND_SIX(17),
    DIAMOND_SEVEN(18), DIAMOND_EIGHT(19), DIAMOND_NINE(20), DIAMOND_TEN(21),
    DIAMOND_JACK(22), DIAMOND_QUEEN(23), DIAMOND_KING(24), DIAMOND_ACE(25),

    // SPADE cards
    SPADE_TWO(26), SPADE_THREE(27), SPADE_FOUR(28), SPADE_FIVE(29), SPADE_SIX(30),
    SPADE_SEVEN(31), SPADE_EIGHT(32), SPADE_NINE(33), SPADE_TEN(34),
    SPADE_JACK(35), SPADE_QUEEN(36), SPADE_KING(37), SPADE_ACE(38),

    // CLUB cards
    CLUB_TWO(39), CLUB_THREE(40), CLUB_FOUR(41), CLUB_FIVE(42), CLUB_SIX(43),
    CLUB_SEVEN(44), CLUB_EIGHT(45), CLUB_NINE(46), CLUB_TEN(47),
    CLUB_JACK(48), CLUB_QUEEN(49), CLUB_KING(50), CLUB_ACE(51);

    private final int id;

    CardID(int id) {
        this.id = id;
    }

    public int getOrdinal() {
        return id;
    }

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

    public static Suit getSuit(int cardID) {
        if (cardID < 13) {
            return Suit.HEART;
        } else if (cardID < 26) {
            return Suit.DIAMOND;
        } else if (cardID < 39) {
            return Suit.SPADE;
        } else {
            return Suit.CLUB;
        }
    }
}