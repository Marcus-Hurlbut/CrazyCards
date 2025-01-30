package com.dungeondecks.marcushurlbut.message;

public class TeammateMessage {
    private String gameID;
    private String playerID;
    private String teammate;

    public TeammateMessage() {}

    public TeammateMessage(String playerID, String gameID, String teammate) {
        this.playerID = playerID;
        this.gameID = gameID;
        this.teammate = teammate;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getTeammate() {
        return teammate;
    }
}
