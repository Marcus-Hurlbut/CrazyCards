<template>
    <li class="submenu">
      <span class="submenu-item">Hearts</span>
      <ul class="submenu-dropdown">
        <li>
          <router-link to="/createHeartsLobby">
            <button class="menu-button">Create Lobby</button>
          </router-link>
        </li>
        <li>
          <router-link to="/joinHearts">
            <button class="menu-button">Join Lobby</button>
          </router-link>
        </li>
      </ul>
    </li>
  </template>
  
  <script>
  export default {
    name: "HeartsDropdownMenu"
  };
  </script>
  
  <style scoped>
    /* Sci-Fi / Fantasy background */
    .submenu-dropdown {
      display: none;
      background: linear-gradient(45deg, #6600cc, #003366);
      padding: 12px;
      border-radius: 15px;
      list-style-type: none;
      margin: 0;
      min-width: 200px;
      box-shadow: 0 0 15px rgba(0, 255, 255, 0.5);
    }
  
    /* Sci-Fi Fantasy Font */
    .submenu-item {
      font-size: 1.4em;
      font-weight: 500;
      color: #e0e0e0;
      padding: 18px 40px;
      cursor: pointer;
      border-radius: 15px;
      font-family: 'Orbitron', sans-serif;
      letter-spacing: 2px;
      min-width: 200px;
      text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
      transition: 0.3s ease, transform 0.3s ease;
    }
  
    .submenu-item:hover {
      padding-bottom: 2em;
    }
  
    /* Show submenu on hover */
    .submenu:hover .submenu-dropdown {
      display: block;
    }
  
    .menu-button {
      background: #004466;
      color: #00ffff;
      border: 2px solid #00ffff;
      padding: 16px 20px;
      border-radius: 30px;
      cursor: pointer;
      transition: background-color 0.3s, transform 0.2s, box-shadow 0.3s;
      margin-bottom: 14px; 
      font-family: 'Orbitron', sans-serif;
      font-size: 1.2em;
      letter-spacing: 1px;
      text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff; 
      box-shadow: 0 0 10px rgba(0, 255, 255, 0.6), 0 0 20px rgba(0, 255, 255, 0.8);
    }
  
    /* Button hover effects with glowing effect */
    .menu-button:hover {
      background-color: #001c33;
      transform: translateY(-3px); 
      box-shadow: 0 0 20px rgba(0, 255, 255, 1), 0 0 30px rgba(0, 255, 255, 0.9); /* Intense glowing */
    }
  </style>
  