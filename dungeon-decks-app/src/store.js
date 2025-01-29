import { createStore } from 'vuex';

const store = createStore({
  state: {
    isLobbyCreated: false,
    lobbyID: "",
    otherPlayers: [],
    stompClient: null,
    playerID: "",
    playerIndex: "",
    username: "",
    gameID: "",
    hand: {},
    playerCardFile: "",
  },
  mutations: {
    storeIsLobbyCreated(state, status) {
      state.isLobbyCreated = status;
    },
    storePlayerID(state, id) {
      state.playerID = id;
    },
    storeUsername(state, name) {
      state.username = name;
    },
    storePlayerIndex(state, index) {
      state.playerIndex = index
    },
    storeGameID(state, id) {
      state.gameID = id;
    },
    storeLobbyID(state, id) {
      state.lobbyID = id;
    },
    storeStompClient(state, stompClient) {
      state.stompClient = stompClient;
    },
    storeOtherPlayer(state, playerName) {
      if (!state.otherPlayers.includes(playerName)) {
        state.otherPlayers.push(playerName);
      }
    },
    storeOtherPlayers(state, otherPlayers) {
      state.otherPlayers = null;
      state.otherPlayers = otherPlayers;
    },
    storeHand(state, hand) {
      state.hand = null;
      state.hand = hand;
    },
    storePlayerCardFile(state, playerCardFile) {
      state.playerCardFile = playerCardFile;
    }
  },
  actions: {
    storeIsLobbyCreated({ commit }, status) {
      commit('storeIsLobbyCreated', status);
    },
    storePlayerID({ commit },  id) {
      commit('storePlayerID', id);
    },
    storeUsername({ commit }, name) {
      commit('storeUsername', name);
    },
    storePlayerIndex({ commit }, index) {
      commit('storePlayerIndex', index);
    },
    storeGameID({ commit }, id) {
      commit('storeGameID', id);
    },
    storeLobbyID({ commit }, id) {
      commit('storeLobbyID', id);
    },
    storeStompClient({ commit }, stompClient) {
      commit('storeStompClient', stompClient);
    },
    storeOtherPlayer({ commit }, playerName) {
      commit('storeOtherPlayer', playerName);
    },
    storeOtherPlayers({ commit }, otherPlayers) {
      commit('storeOtherPlayers', otherPlayers);
    },
    storeHand({ commit }, hand) {
      commit('storeHand', hand);
    },
    storePlayerCardFile({ commit }, playerCardFile) {
      commit('storePlayerCardFile', playerCardFile);
    }
  },
  getters: {
    stompClient: (state) => state.stompClient,
    username: (state) => state.username,
    playerID: (state) => state.playerID,
    playerIndex: (state) => state.playerIndex,
    gameID: (state) => state.gameID,
    lobbyID: (state) => state.lobbyID,
    otherPlayers: (state) => state.otherPlayers,
    hand: (state) => state.hand,
    playerCardFile: (state) => state.playerCardFile,
  },
});

export default store;
