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
      return require(`@/components/card-images/PNG-cards/${this.fileName}`);
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
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.card:hover {
  transform: scale(1.1);
  z-index: 100;
}

.selected-card {
  border: 2px solid gold;
  border-radius: 5px;
  transform: scale(1.1);
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.8);
}
</style>
