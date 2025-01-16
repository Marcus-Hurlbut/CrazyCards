<template>
    <div>
      <div v-if="trickWinnerName !== null" class="winnerPrompt">
        <h3>{{ trickWinnerName }} won that hand!</h3>
      </div>
  
      <div v-if="winnerName !== null" class="winnerPrompt">
        <h3>Game Over</h3>
        <div class="prompt-content">
          <p>{{ winnerName }} won the game!</p>
        </div>
      </div>
    </div>
</template>
  
<script>
  export default {
    name: "WinnerPrompt",
    props: {
      trickWinnerName: String,
      winnerName: String,
    }
  };
</script>
  
<style scoped>

.winnerPrompt {
  position: absolute;
  bottom: 50%;
  background: linear-gradient(to bottom right, #6A4C9C, red);
  color: white;
  font-family: 'Roboto', sans-serif;
  font-size: 20px;
  font-weight: 600;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  text-align: center;
  width: 80%;
  max-width: 250px;
  min-width: 180px;
  box-sizing: border-box;
  margin: 0;
  z-index: 0;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease;
}

.winnerPrompt .prompt-content {
  animation: fadeInUp 0.7s ease-out;
}

.winnerPrompt h3 {
  font-size: 26px;
  margin-bottom: 10px;
  font-weight: 700;
  letter-spacing: 1px;
}

.winnerPrompt p {
  font-size: 18px;
  margin-top: 8px;
  opacity: 0.9;
}

.winnerPrompt:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}
</style>
  