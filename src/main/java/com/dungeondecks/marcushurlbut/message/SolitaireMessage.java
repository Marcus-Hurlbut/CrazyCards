package com.dungeondecks.marcushurlbut.message;

public class SolitaireMessage {
    private String gameID;
    private String playerID;
    private String cards;
    private String to;
    private String from;

    public SolitaireMessage() {}

    public SolitaireMessage(String playerID, String gameID, String cards, String to, String from) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.cards = cards;
        this.to = to;
        this.from = from;
    }

    public SolitaireMessage(String playerID, String gameID, String cards) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.cards = cards;
    }

    public SolitaireMessage(String playerID, String gameID) {
        this.playerID = playerID;
        this.gameID = gameID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getCards() {
        return cards;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }
}
