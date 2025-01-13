import { createStore } from 'vuex';

const store = createStore({
  state: {
    isLobbyCreated: false,
    lobbyID: "",
    otherPlayers: [],
    stompClient: null,
    playerID: "",
    username: "",
    gameID: ""
  },
  mutations: {
    storeIsLobbyCreated(state, status) {
      state.isLobbyCreated = status;
    },
    storePlayerID(state, id) {
      state.playerID = id
    },
    storeUsername(state, name) {
      state.username = name
    },
    storeGameID(state, id) {
      state.gameID = id
    },
    storeLobbyID(state, id) {
      state.lobbyID = id
    },
    storeStompClient(state, stompClient) {
      state.stompClient = stompClient;
    },
    storeOtherPlayer(state, playerName) {
      if (!state.otherPlayers.includes(playerName)) {
        state.otherPlayers.push(playerName);
      }
    },
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
  },
  getters: {
    stompClient: (state) => state.stompClient,
    username: (state) => state.username,
    playerID: (state) => state.playerID,
    gameID: (state) => state.gameID,
    lobbyID: (state) => state.lobbyID,
    otherPlayers: (state) => state.otherPlayers
  },
});

export default store;
