<template>
    <span class="game-vault-button">Game Vault</span>
</template>

<script>
  export default {
    name: "GameVaultButton"
  };
</script>

<style scoped>
  .game-vault-button {
    font-size: 1.8em;
    font-weight: bold;
    cursor: pointer;
    color: #fff;
    padding: 15px 30px;
    background: linear-gradient(45deg, #6a1b9a, #ff1744);
    border-radius: 60px;
    font-family: 'Cursive', sans-serif;
    text-transform: uppercase;
    letter-spacing: 2px;
    box-shadow: 0 0 25px rgba(255, 20, 60, 0.7), 0 0 50px rgba(255, 20, 60, 0.7);
    transition: all 0.3s ease-in-out, transform 0.2s ease-in-out;
  }

  .game-vault-button:hover {
    background: linear-gradient(45deg, #ff1744, #6a1b9a);
    box-shadow: 0 0 30px rgba(255, 20, 60, 1), 0 0 60px rgba(255, 20, 60, 0.9);
    transform: translateY(-4px);
  }

  .game-vault-button:active {
    transform: translateY(0);
    box-shadow: 0 0 20px rgba(255, 20, 60, 0.6), 0 0 40px rgba(255, 20, 60, 0.8);
  }

  @keyframes pulseGlow {
    0% {
      box-shadow: 0 0 25px rgba(255, 20, 60, 0.7), 0 0 50px rgba(255, 20, 60, 0.7);
    }
    50% {
      box-shadow: 0 0 40px rgba(255, 20, 60, 1), 0 0 70px rgba(255, 20, 60, 1);
    }
    100% {
      box-shadow: 0 0 25px rgba(255, 20, 60, 0.7), 0 0 50px rgba(255, 20, 60, 0.7);
    }
  }

  .game-vault-button:hover {
    animation: pulseGlow 1.5s infinite;
  }
</style>
