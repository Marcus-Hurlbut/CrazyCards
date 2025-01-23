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
.invalidTurnPrompt {
  position: fixed;
  bottom: 20%;
  background: linear-gradient(to bottom right, #6A4C9C, red);
  color: white;
  font-family: 'Roboto', sans-serif;
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
  font-size: 18px;
  margin-top: 8px;
  opacity: 0.9;
}

.invalidTurnPrompt:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>