<template>
  <div class="crazyCards">
    <div class="dashboard">
      <h1 class="app-title">{{ msg }}
        <img alt="Crazy Cards logo" src="./card_icon.png" class="banner-logo" />
      </h1>

      <nav>
        <ul>
          <li class="dropdown">
             <GameVaultButton />

            <ul class="hearts-dropdown-menu">
              <HeartsDropdownMenu />
            </ul>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import HeartsDropdownMenu from './dashboard/HeartsDropdownMenu.vue';
import GameVaultButton from './dashboard/GameVaultButton.vue'

export default {
  name: 'CrazyCards',
  components: {
    HeartsDropdownMenu,
    GameVaultButton
  },
  props: {
    msg: String
  },
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
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 10px;
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

nav ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  align-content: center;
  justify-content: center;
}

nav li {
  position: relative;
}

.dropdown:hover .hearts-dropdown-menu {
  display: block;
}

.hearts-dropdown-menu {
  display: none;  
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  background: #9C27B0;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.hearts-dropdown-menu li {
  color: white;
  padding: 10px;
  cursor: pointer;
}

.hearts-dropdown-menu li:hover {
  background-color: #b40e40;
}
</style>
