package com.dungeondecks.marcushurlbut.message;

public class TurnMessage {
    private String gameID;
    private String playerID;
    private String card;

    public TurnMessage() {}

    public TurnMessage(String playerID, String gameID, String card) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.card = card;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getCard() {
        return card;
    }
}
