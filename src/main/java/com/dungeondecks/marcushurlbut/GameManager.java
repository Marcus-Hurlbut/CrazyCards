package com.dungeondecks.marcushurlbut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;

public class GameManager {
    private static Map<UUID, Hearts> sessions = new ConcurrentHashMap<>();
    private static Map<Integer, ArrayList<Player>> waitingLobbies = new ConcurrentHashMap<>();
    
    public static int newLobby(Player player) {
        int roomID = generateLobbyID();
        ArrayList<Player> newLobby = new ArrayList<Player>();
        newLobby.add(player);

        waitingLobbies.putIfAbsent(roomID, newLobby);
        System.err.println("GameManager  - new Lobby: " + newLobby);
        return roomID;
    }

    public static boolean joinLobby(Player player, Integer roomID) {
        ArrayList<Player> lobby = retreiveLobby(roomID);
        lobby.add(player);
        waitingLobbies.replace(roomID, lobby);
        System.out.println("GameManager  - updated lobby: " + lobby);

        if (retreiveLobby(roomID).size() == 4) {
            return true;
        }
        return false;
    }

    public static ArrayList<Player> retreiveLobby(Integer roomID) {
        return waitingLobbies.get(roomID);
    }

    public static UUID startGame(List<Player> players) {
        UUID gameID = UUID.randomUUID();

        Hearts hearts = new Hearts(gameID);
        hearts.initializePlayers(players);
        hearts.shuffleAndDeal();
        hearts.active = true;
        sessions.put(gameID, hearts);

        return gameID;
    }

    public static Hearts retreiveGame(UUID gameID) {
        return sessions.get(gameID);
    }

    public static void updateGame(UUID gameID, Hearts heartsSession) {
        sessions.replace(gameID, heartsSession);
    }

    public static int generateLobbyID() {
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(899999);
    }
}
