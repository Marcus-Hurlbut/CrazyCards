package com.dungeondecks.marcushurlbut.message;

public class LobbyMessage {
    private String lobbyID;
    private String playerID;
    private String username;

    public LobbyMessage() {}

    public LobbyMessage(String playerID, String username) {
        this.playerID = playerID;
        this.username = username;
    }

    public LobbyMessage(String playerID, String lobbyID, String username) {
        this.playerID = playerID;
        this.lobbyID = lobbyID;
        this.username = username;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getUsername() {
        return username;
    }
}
