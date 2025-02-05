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
  background-image: url('@/assets/texture-img/texture1.webp');
  background-size: cover;
  background-position: center;
  color: black;
  font-size: 4em;
  font-weight: bold;
  max-width: 50vw;
  padding: 3vh 7vw;
  border: none;
  border-radius: 50px;
  cursor: pointer;
  font-family: 'GameVaultFont', sans-serif;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
  text-transform: uppercase;
  letter-spacing: 2px;
  text-shadow: 0px 0px 3px #3D5AFE, 0px 0px 3px #ffffff;
}

.game-vault-btn:hover {
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.4);
  transform: translateY(-5px);
}

@media (max-width: 767px) {
  .game-vault-btn {
    font-size: 1.8em;
}
}
</style>
