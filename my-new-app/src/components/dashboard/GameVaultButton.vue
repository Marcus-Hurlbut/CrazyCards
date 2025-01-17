<template>
    <button class="game-vault-btn">Game Vault</button>
</template>

<script>
  export default {
    name: "GameVaultButton"
  };
</script>

<style scoped>
.game-vault-btn {
  background: linear-gradient(to right, #9C27B0, #D50032);
  color: white;
  font-size: 1.5em;
  font-weight: bold;
  padding: 12px 30px;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  position: relative;
  text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
}

.game-vault-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 50px;
  transition: all 0.3s ease;
  opacity: 0;
}

.game-vault-btn:hover {
  background: linear-gradient(to right, #D50032, #9C27B0);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.3);
  transform: translateY(-3px);
}

.game-vault-btn:focus {
  outline: none;
}

.game-vault-btn:hover::before {
  opacity: 1;  /* Show overlay on hover */
}
</style>
