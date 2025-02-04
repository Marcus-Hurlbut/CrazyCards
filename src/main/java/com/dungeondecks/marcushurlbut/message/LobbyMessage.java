package com.dungeondecks.marcushurlbut.message;

public class LobbyMessage {
    private String lobbyID;
    private String playerID;
    private String username;
    private String count;

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

    public LobbyMessage(String playerID, String lobbyID, String username, String count) {
        this.playerID = playerID;
        this.lobbyID = lobbyID;
        this.username = username;
        this.count = count;
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

    public String getCount() {
        return count;
    }
}
