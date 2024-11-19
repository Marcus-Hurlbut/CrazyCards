package com.crazycards.marcushurlbut;

public class Message {

    private String content;
    private String playerID;
    private String cardID;

    public Message() {}

    public Message(String content) {
        this.content = content;
    }

    public Message(String playerID, String cardID) {
        this.playerID = playerID;
        this.cardID = cardID;
    }
    
    public String getContent() {
        return content;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getCardID() {
        return cardID;
    }
}
