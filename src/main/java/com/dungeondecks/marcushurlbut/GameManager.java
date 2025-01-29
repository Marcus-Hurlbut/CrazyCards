package com.dungeondecks.marcushurlbut;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.security.SecureRandom;

public class GameManager {
    private static Map<Integer, GameType> gameType = new ConcurrentHashMap<>();
    private static Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();
    private static Map<UUID, Game> gameSessions = new ConcurrentHashMap<>();
    
    public static int newLobby(Player player, GameType gameSelected) {
        int lobbyID = generateLobbyID();
        Lobby lobby = new Lobby();

        lobby.type = gameSelected;
        lobby.setGameObject(gameSelected);

        gameType.put(lobbyID, gameSelected);
        lobby.addPlayerToLobby(player);

        lobbies.put(lobbyID, lobby);
        return lobbyID;
    }

    public static boolean joinLobby(Player player, Integer lobbyID, GameType type) {
        Lobby lobby = retreiveLobby(lobbyID);

        if (type != lobby.type) {
            return false;
        }

        lobby.addPlayerToLobby(player);
        updateLobby(lobbyID, lobby);

        boolean full = lobby.isLobbyFull();

        if (full) {
            startGameSession(lobby);

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                updateLobby(lobbyID, new Lobby());
            }, 5000, TimeUnit.MILLISECONDS);
        }

        return full;
    }

    public static Lobby retreiveLobby(Integer lobbyID) {
        return lobbies.get(lobbyID);
    }

    public static void updateLobby(Integer lobbyID, Lobby lobby) {
        lobbies.replace(lobbyID, lobby);
    }

    public static UUID startGameSession(Lobby lobby) {
        Game game = lobby.game;

        game.initialize(lobby.players);
        gameSessions.put(game.gameID, game);

        return game.gameID;
    }

    public static Game retreiveGame(UUID gameID) {
        return gameSessions.get(gameID);
    }

    public static void updateGame(UUID gameID, Game game) {
        gameSessions.replace(gameID, game);
    }

    public static int generateLobbyID() {
        SecureRandom secureRandom = new SecureRandom();
        Integer lobbyID = 100000 + secureRandom.nextInt(899999);

        while(lobbies.containsKey(lobbyID)) {
            lobbyID = 100000 + secureRandom.nextInt(899999);
        }

        return lobbyID;
    }
}
