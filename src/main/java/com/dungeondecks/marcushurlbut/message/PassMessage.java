package com.dungeondecks.marcushurlbut.message;

public class PassMessage {
    private String gameID;
    private String playerID;
    private String cards;

    public PassMessage() {}

    public PassMessage(String playerID, String gameID, String cards) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.cards = cards;
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
}
