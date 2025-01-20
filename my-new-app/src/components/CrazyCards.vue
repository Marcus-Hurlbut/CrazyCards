<template>
  <div class="crazyCards">
    <div class="dashboard">
      <h1 class="app-title">
        Crazy Cards
        <img alt="Crazy Cards logo" src="./card_icon.png" class="banner-logo" />
      </h1>

      <div class="dropdown-container">
        <button class="dropdown-button">
          <img src="@/assets/burger-menu.png" alt="Menu" class="menu-icon" />
        </button>
        <ul class="options-menu">
          <router-link to="/">
            <li>Main Menu</li>
          </router-link>
          <li class="has-sublist">Game Vault
            <ul class="sublist">
              <li class="has-sublist">Hearts
                <ul class="second-sublist">
                  <router-link to="/createHeartsLobby">
                    <li>Create Lobby</li>
                  </router-link>
                  <router-link to="/joinHearts">
                    <li>Join Lobby</li>
                  </router-link>
                </ul>
              </li>
              <li>Cheat (Coming next..)</li>
            </ul>
          </li>
          <li>About</li>
          <li>Report a Bug</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CrazyCards',
  components: {},
  props: {
    msg: String
  }
}
</script>

<style scoped>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.crazyCards {
  background: linear-gradient(to bottom right, #b40e40, #9C27B0);
  animation: gradientAnimation 10s ease-in-out infinite;
  color: white;
  text-align: center;
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
}

.dashboard {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  justify-content: center;
}

.app-title {
  color: white;
  padding: 5px 0;
  font-size: 3em;
  font-weight: bold;
  font-family: 'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 10px;
}

.banner-logo {
  height: 60px;
  width: auto;
}

/* Dropdown styles */
.dropdown-container {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 1000;
}

.dropdown-button {
  background-color: transparent;
  border: none;
  cursor: pointer;
}

.menu-icon {
  height: 70px;
}

.options-menu {
  display: none;
  position: absolute;
  left: 0;
  width: 200px;
  background-color: #9C27B0;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
}

.dropdown-container:hover .options-menu {
  display: block;
}

.options-menu li {
  padding: 10px;
  cursor: pointer;
  color: white;
}

.options-menu li:hover {
  background: linear-gradient(to right, #9C27B0, #D50032, #3D5AFE);
  border-radius: 10px;
  transform: translateY(-3px);
}

/* Sublist for Game Vault */
.has-sublist {
  position: relative; /* Ensures sublist is positioned correctly */
}

.has-sublist:hover .sublist {
  display: block;
}

.sublist {
  display: none;
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #7b1fa2;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
  width: 180px;
}

.sublist li {
  padding: 8px;
  cursor: pointer;
  color: white;
}

.sublist li:hover {
  background-color: #b40e40;
}

/* Second-level sublist under "Hearts" */
.has-sublist > .second-sublist {
  display: none;
}

.has-sublist:hover > .second-sublist {
  display: block;
}

.second-sublist {
  display: none;
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #8e24aa;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  list-style-type: none;
  margin: 0;
  padding: 4px;
  width: 180px;
}

.second-sublist li {
  padding: 8px;
  cursor: pointer;
  color: white;
}

.second-sublist li:hover {
  background-color: #b40e40;
}
</style>
