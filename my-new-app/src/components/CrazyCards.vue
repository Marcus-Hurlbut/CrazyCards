<template>
  <div class="dashboard">
    <div class="dashboard-content">
      <img alt="Crazy Cards logo" src="./card_icon.png" class="banner-logo" />
      <h1 class="dashboard-title">{{ msg }}</h1>
      <nav class="dashboard-items">
        <ul>
          <!-- Game Vault Dropdown -->
          <li class="dropdown">
            <GameVaultButton />
            <ul class="dropdown-menu"> <HeartsDropdownMenu /></ul>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>
import GameVaultButton from './dashboard/GameVaultButton.vue';
import HeartsDropdownMenu  from './dashboard/HeartsDropdownMenu.vue'
export default {
  name: 'CrazyCards',
  components: {
    HeartsDropdownMenu,
    GameVaultButton,
  },
  props: {
    msg: String
  }
}
</script>

<style scoped>
/* Your global styles */
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
} 

.dashboard {
  background: linear-gradient(to bottom right, #C2185B, #9C27B0);
  animation: gradientAnimation 1m ease infinite;
  color: white;
  text-align: center;
}

@keyframes gradientAnimation {
  0% {
    background: linear-gradient(to bottom right, #D50032, #9C1B80); /* More vibrant red and purple */
  }
  33% {
    background: linear-gradient(to bottom right, #9C1B80, #1976D2); /* More vibrant purple to blue */
  }
  66% {
    background: linear-gradient(to bottom right, #1976D2, #D50032); /* More vibrant blue to red */
  }
  100% {
    background: linear-gradient(to bottom right, #D50032, #9C1B80); /* More vibrant red to purple */
  }
}

.dashboard-title {
  color: white; 
  padding: 5px 0;
  font-size: 3em;
  font-weight: bold;
  font-family:'Trebuchet MS', 'Lucida Sans Unicode', 'Lucida Grande', 'Lucida Sans', Arial, sans-serif;
  text-align: center;
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 10px;
}

.dashboard-items {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px; 
  width: 100%;
  z-index: 0;
}

.banner-logo {
  height: 60px;
  margin-bottom: 20px;
}

nav ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

nav li {
  position: relative;
}

.menu-item {
  font-size: 1.5em;
  font-weight: bold;
  cursor: pointer;
  color: #FFF;
  padding: 10px;
  background: linear-gradient(to right, #9C27B0, #C2185B);
  border-radius: 25px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.menu-item:hover {
  background: linear-gradient(#C2185B, #9C27B0); /* Invert gradient on hover */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background: #9C27B0;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.dropdown:hover .dropdown-menu {
  display: inline;
}
</style>
