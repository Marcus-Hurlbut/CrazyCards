package com.dungeondecks.marcushurlbut;

import java.util.UUID;

import com.dungeondecks.marcushurlbut.games.Hearts;
import com.dungeondecks.marcushurlbut.games.Spades;
import com.dungeondecks.marcushurlbut.games.SpiderSolitaire;

public class Lobby {
    private int id;
    public GameType type;
    public Player[] players = new Player[4];
    public int playerIndex = 0;
    public int maxPlayers = Integer.MAX_VALUE;

    public Game game;
    public UUID gameID = UUID.randomUUID();

    public void setGameObject(GameType type) {
        switch (type) {
            case Hearts:
                game = (Hearts) new Hearts(gameID);
                maxPlayers = 4;
                break;

            case Spades:
                game = (Spades) new Spades(gameID);
                maxPlayers = 4;
                break;
        
            default:
                break;
        }
    }

    public void setGameObject(GameType type, int count) {
        switch (type) {
            case SpiderSolitaire:
                game = (SpiderSolitaire) new SpiderSolitaire(gameID, count);
                maxPlayers = 1;
                break;
                
            default:
                break;
        }
    }

    public void addPlayerToLobby(Player player) {
        if (playerIndex < maxPlayers) {
            players[playerIndex] = player;
            playerIndex++;
        }
    }

    public boolean isLobbyFull() {
        return playerIndex == maxPlayers;
    }
}
