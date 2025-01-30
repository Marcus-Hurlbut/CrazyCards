<template>
    <div>
      <div v-if="invalidTurn && playersTurn && !passPhase" class="invalidTurnPrompt">
        <div class="prompt-content">
        <h3>Invalid Card</h3>
          <p>You must play a card with the same suite of the starting card if present</p>
        </div>
      </div>
    </div>
</template>

<script>
  export default {
    name: "InvalidCardPrompt",
    props: {
      invalidTurn: {
        type: Boolean,
        default: false
      },
      playersTurn: {
        type: Boolean,
        default: false
      },
      passPhase: Boolean
    }
  };
</script>

<style scoped>
@font-face {
  font-family: 'FancyFont';
  src: url('@/assets/ttf/Embossed-Germanica.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.invalidTurnPrompt {
  position: fixed;
  bottom: 25vh;
  background: linear-gradient(to bottom right, #294ffc, red);
  color: black;
  text-shadow: 0px 0px 3px #f6f8f8, 0px 0px 3px #ffffff;
  font-family: 'FancyFont', sans-serif;
  font-size: 20px;
  font-weight: 600;
  padding: 5px;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 80%;
  max-width: 500px;
  min-width: 500px;
  box-sizing: border-box;
  margin: 0;
  z-index: 1000;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0.8;
}

.invalidTurnPrompt .prompt-content {
  animation: fadeInUp 0.7s ease-out;
}

.invalidTurnPrompt h3 {
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 1px;
}

.invalidTurnPrompt p {
  font-family: Arial, Helvetica, sans-serif;
  font-size: 20px;
  margin-top: 4px;
}

.invalidTurnPrompt:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>