<template>
  <div v-if="passPhase && !playerPassedCards" class="passingPhasePrompt">
    <div class="prompt-content">
      <h3>Passing Phase</h3>
      <p>Select 3 Cards to Pass</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "PassingPhasePrompt",
  props: {
    passPhase: Boolean,
    playerPassedCards: Boolean,
  },
};
</script>

<style scoped>
.passingPhasePrompt {
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
  min-width: 280px;
  box-sizing: border-box;
  margin: 0;
  z-index: 1000;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
}

.passingPhasePrompt .prompt-content {
  animation: fadeInUp 0.5s ease-in-out;
}

.passingPhasePrompt h3 {
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 1px;
}

.passingPhasePrompt p {
  font-size: 18px;
  margin-top: 8px;
  opacity: 0.9;
}

.passingPhasePrompt:hover {
  /* transform: translateY(-5px); */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>
