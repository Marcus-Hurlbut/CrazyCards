<template>
  <div v-if="playersTurn && !invalidTurn" class="yourTurnPrompt">
    <h3>Your Turn!</h3>
    <div class="prompt-content">
      <p>Select a card from the first suite played if you have one, otherwise play any card as a wildcard</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "YourTurnPrompt",
  props: {
    playersTurn: Boolean,
    invalidTurn: Boolean,
  },
};
</script>

<style scoped>
@font-face {
  font-family: 'FancyFont';
  src: url('@/assets/ttf/Embossed-Germanica.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.yourTurnPrompt {
  position: fixed;
  bottom: 25vh;
  background: linear-gradient(to bottom right, #294ffc, red);
  text-shadow: 0px 0px 2px #f6f8f8, 0px 0px 2px #ffffff;
  color: black;
  font-family: 'Roboto', sans-serif;
  font-size: 20px;
  font-weight: 600;
  padding: 5px;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 80%;
  max-width: 500px;
  min-width: 280px;
  box-sizing: border-box;
  margin: 0;
  z-index: 1000;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
  opacity: 0.8;
}

.yourTurnPrompt .prompt-content {
  animation: fadeInUp 0.5s ease-in-out;
}

.yourTurnPrompt h3 {
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 1px;
}

.yourTurnPrompt p {
  font-size: 18px;
  margin-top: 8px;
  opacity: 0.9;
}

.yourTurnPrompt:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>