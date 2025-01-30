<template>
  <div>
    <img 
      class="card" 
      :src="imageSrc"
      :class="{'selected-card': isSelected}" 
    />
  </div>
</template>

<script>
 /* eslint-disable vue/multi-word-component-names */
export default {
  name: 'Card',
  props: {
    playerName: String,
    fileName: String,
    isSelected: {
      type: Boolean,
      default: false, 
    }
  },
  computed: {
    imageSrc() {
      return require('@/assets/card-images/PNG-cards/' + this.fileName);
    }
  }
}
</script>

<style scoped>
.card {
  display: inline-block;
  z-index: 0;
  width: 7vw;
  height: 16vh;
  max-width: 180px;
  max-height: 600px;
  min-width: 80px;
  min-height: 150px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  border: 2px solid transparent;
}

.card:hover {
  transform: scale(1.1);
  z-index: 100;
}

.selected-card {
  border: 4px solid gold;
  border-radius: 5px;
  transform: none;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.8);
}
</style>
