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
@font-face {
  font-family: 'FancyFont';
  src: url('@/assets/ttf/Embossed-Germanica.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.passingPhasePrompt {
  position: fixed;
  bottom: 25vh;
  background: linear-gradient(to bottom right, #294ffc, red);
  color: rgb(0, 0, 0);
  font-family: 'FancyFont', sans-serif;
  text-shadow: 0px 0px 2px #f6f8f8, 0px 0px 2px #ffffff;
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

.passingPhasePrompt .prompt-content {
  animation: fadeInUp 0.5s ease-in-out;
}

.passingPhasePrompt h3 {
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 2px;
}

.passingPhasePrompt p {
  font-family:Arial, Helvetica, sans-serif;
  font-size: 20px;
  margin-top: 4px;
}

.passingPhasePrompt:hover {
  /* transform: translateY(-5px); */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>
