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
  bottom: 70%;
  background: linear-gradient(to top left, #9C27B0, #3718a8); /* More vibrant gradient */
  color: white;
  font-family: 'Roboto', sans-serif;
  font-size: 22px;
  font-weight: 700;
  padding: 15px 25px; /* Reduced top/bottom padding (15px) and kept side padding (25px) */
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3); /* Softer, larger shadow */
  text-align: center;
  width: 80%;
  max-width: 300px;
  min-width: 250px;
  left: 50%;
  transform: translateX(-50%);
  box-sizing: border-box;
  margin: 0;
  z-index: 10000;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease, opacity 0.3s ease-in-out;
  opacity: 0; /* Start with hidden */
  animation: fadeIn 1s forwards; /* Add fadeIn animation */
}

.winnerPrompt .prompt-content {
  margin-top: 15px;
}

.winnerPrompt h3 {
  font-size: 28px;
  margin-bottom: 10px; /* Adjusted margin for more compact layout */
  font-weight: 900; /* Increase font weight for better emphasis */
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5); /* Subtle shadow to enhance visibility */
}

.winnerPrompt p {
  font-size: 18px;
  margin-top: 8px; /* Reduced top margin */
  opacity: 0.9;
  line-height: 1.5;
}

.winnerPrompt:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5);
}

@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(10px); /* Start off with a slight downward movement */
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0); /* Fade in and move to final position */
  }
}


</style>
  