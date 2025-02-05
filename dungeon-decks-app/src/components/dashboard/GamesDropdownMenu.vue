<template>
  <div class="dungeon-decks">
    <ul class="games-list">
      <!-- Hearts Section -->
      <li class="game-item" @click="toggleGameDropdown('hearts')">
        Hearts
        <div v-if="activeDropdown === 'hearts'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'hearts' } }">
            <button class="menu-button">Create Lobby</button>
          </router-link>
          <router-link :to="{ path: '/joinLobby', query: { game: 'hearts' } }">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </div>
      </li>

      <!-- Spades Section -->
      <li class="game-item" @click="toggleGameDropdown('spades')">
        Spades
        <div v-if="activeDropdown === 'spades'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'spades' } }">
            <button class="menu-button">Create Lobby</button>
          </router-link>
          <router-link :to="{ path: '/joinLobby', query: { game: 'spades' } }">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </div>
      </li>

      <li class="game-item" @click="toggleGameDropdown('spiderSolitaire')">
        Spider Solitare
        <div v-if="activeDropdown === 'spiderSolitaire'" class="submenu-dropdown">
          <router-link :to="{ path: '/createLobby', query: { game: 'spiderSolitaire' , suits: '1' } }">
            <button class="menu-button">1 Suit</button>
          </router-link>
          <router-link :to="{ path: '/createLobby', query: { game: 'spiderSolitaire' , suits: '2' } }">
            <button class="menu-button">2 Suits</button>
          </router-link>
          <router-link :to="{ path: '/createLobby', query: { game: 'spiderSolitaire' , suits: '4' } }">
            <button class="menu-button">4 Suits</button>
          </router-link>
        </div>
      </li>
    </ul>
    <BubbleBackground />
  </div>
</template>

<script>
export default {
  name: "GamesDropdownMenu",
  data() {
    return {
      activeDropdown: null, // Tracks which game dropdown is active (Hearts or Spades)
    };
  },
  methods: {
    // Toggle visibility of game-specific dropdown (Hearts or Spades)
    toggleGameDropdown(game) {
      this.activeDropdown = this.activeDropdown === game ? null : game;
    },
  },
};
</script>

<style scoped>
@font-face {
  font-family: 'GameVaultFont';
  src: url('@/assets/ttf/Broadway3D-Italic.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

::-webkit-scrollbar {
  width: 7px;
}

::-webkit-scrollbar-track {
  border-radius: 25px;
}

::-webkit-scrollbar-thumb {
  background: purple;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: #3ba1f5;
}

.games-list {
  position: relative;
  list-style-type: none;
  padding: 5px;
  margin: 0;
  text-align: center;
  font-family: 'GameVaultFont', sans-serif;
  max-height: 45vh;
  scroll-behavior: auto;
  overflow-y: auto;
  overflow-x: hidden;
}

.game-item {
  font-size: 3.2em;
  color: black;
  padding: 1.5vh 1vw;
  cursor: pointer;
  border-radius: 15px;
  letter-spacing: 2px;
  transition: 0.3s ease, transform 0.3s ease;
  text-shadow: 0px 0px 10px #e91442, 0px 0px 20px #e91442;
}

.game-item:hover {
  padding-bottom: 1em;
  transform: scale(1.05);
  text-shadow: 0px 0px 10px #008cff, 0px 0px 20px #3ba1f5;
}

/* Submenu Dropdown for Create/Join Lobby */
.submenu-dropdown {
  padding: 20px 0px;
  display: flexbox;
  justify-content: space-between;
}

.menu-button {
  display: inline-flex;
  background: #0c0c0c;
  color: #ffffff;
  border: 2px solid #ffffff;
  padding: 10px 20px;
  border-radius: 30px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s, box-shadow 0.3s;
  margin-bottom: 2px;
  font-family: 'GameVaultFont', sans-serif;
  font-size: .8em;
  letter-spacing: 1px;
  text-shadow: 0px 0px 10px #ff0000, 0px 0px 20px #ff0000;
  box-shadow: 0 0 10px rgba(253, 1, 198, 0.815), 0 0 20px rgba(255, 0, 34, 0.8);
}

.menu-button:hover {
  transform: translateY(-3px);
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #3ba1f5;
  box-shadow: 0 0 10px rgba(0, 102, 255, 0.8), 0 0 20px #3ba1f5;
}

@media (max-width: 767px) {
  .game-item {
    font-size: 2em;
  }
  .menu-button {
    font-size: .75em;
  }
}
</style>
