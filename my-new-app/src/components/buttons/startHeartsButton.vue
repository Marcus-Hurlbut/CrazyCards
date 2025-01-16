<template>
  <div>
    <button class="start-hearts-button" @click="handleClick">Start Hearts</button>
  </div>
</template>
  
  <script>
  export default {
    name: 'StartHeartsButton',
    methods: {
      handleClick() {
        this.$emit('start-hearts');  // Emit a custom event when the button is clicked
      }
    }
  }
  </script>
  
  <style scoped>
  .start-hearts-button {
    background: linear-gradient(to top right, #6A4C9C, #9C27B0); /* Gradient */
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    transition: background 0.3s ease, transform 0.2s ease, box-shadow 0.2s ease;
  }
  
  .start-hearts-button:hover {
    background: linear-gradient(to top right, #7f56d9, #8e24aa);
    transform: translateY(-3px); /* Hover lift */
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2); /* Enhanced shadow on hover */
  }
  
  .start-hearts-button:active {
    transform: translateY(2px); /* Button press effect */
  }
  
  .start-hearts-button:focus {
    outline: none; /* Remove outline on focus */
  }
  </style>
  