<template>
    <div class="dungeon-decks">
      <ul>
        <li class="game-vault-dropdown">
          <GameVaultButton />
  
          <ul class="hearts-dropdown-menu">
            <GamesDropdownMenu />
          </ul>
        </li>
      </ul>
    </div> 
</template>
  
<script>
import GamesDropdownMenu from './dashboard/GamesDropdownMenu.vue';
import GameVaultButton from './dashboard/GameVaultButton.vue'
  
export default {
    name: 'MainMenu',
    components: {
      GamesDropdownMenu,
      GameVaultButton,
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
  
  .dungeon-decks {
    background-image: url('../assets/texture-img/texture1.webp');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    animation: gradientAnimation 10s ease-in-out infinite;
    color: white;
    text-align: center;
    border-radius: 25px;
    opacity: 1;
  }
  
  ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
    align-content: center;
    justify-content: center;
  }
  
  li {
    position: relative;
  }
  
  .game-vault-dropdown {
    position: absolute;
    top: 30%;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
  }
  
  .game-vault-dropdown:hover .hearts-dropdown-menu {
    display: block;
    border-radius: 30px;
  }
  
  .hearts-dropdown-menu {
    display: none;  
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    padding: 15px;
    border-radius: 30px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  
  .hearts-dropdown-menu li {
    color: white;
    padding: 10px;
    cursor: pointer;
  }
  

</style>
  