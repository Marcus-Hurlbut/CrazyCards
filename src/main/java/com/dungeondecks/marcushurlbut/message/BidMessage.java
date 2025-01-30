package com.dungeondecks.marcushurlbut.message;

public class BidMessage {
    private String gameID;
    private String playerID;
    private String bid;

    public BidMessage() {}

    public BidMessage(String playerID, String gameID, String bid) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.bid = bid;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getBid() {
        return bid;
    }
}
