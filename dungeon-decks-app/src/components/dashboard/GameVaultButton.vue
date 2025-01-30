<template>
  <ButtonAnimation :title="'Game Vault'">
      <template #button>
        <button class="game-vault-btn">Game Vault</button>
      </template>
    </ButtonAnimation>
</template>

<script>
import ButtonAnimation from '../animations/ButtonAnimation.vue';

export default {
  name: "GameVaultButton",
  components: {
    ButtonAnimation
  }

};
</script>

<style scoped>
@font-face {
  font-family: 'GameVaultFont';
  src: url('@/assets/ttf/Broadway3D-Italic.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.game-vault-btn {
  background: linear-gradient(to right, #000000, #D50032, #3D5AFE, #000000);
  color: black;
  font-size: 4em;
  font-weight: 700;
  padding: 25px 80px;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-family: 'GameVaultFont', sans-serif;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transition: all 0.4s ease;
  position: relative;
  text-transform: uppercase;
  letter-spacing: 2px;
  text-shadow: 0px 0px 3px #3D5AFE, 0px 0px 3px #ffffff;
}

.game-vault-btn::before {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.game-vault-btn:hover {
  background: linear-gradient(to left, #D50032, #9C27B0, #3D5AFE);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4);
  transform: translateY(-5px);
}

.game-vault-btn:hover::before {
  opacity: 1;
}

.game-vault-btn:active {
  transform: translateY(2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

</style>
