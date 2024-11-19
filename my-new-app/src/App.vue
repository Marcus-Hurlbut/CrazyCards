<template>
  <CrazyCards msg="Crazy Cards"/>
    <div id="content">
      <HeartsComponent msg="Hearts Game"/>
    </div>
  <router-view />
</template>


<script>
import CrazyCards from './components/CrazyCards.vue'
// import HeartsComponent from './components/Hearts.vue'

export default {
  name: 'App',
  components: {
    CrazyCards,
    // HeartsComponent
  }
}
</script>

<style>
#app {
  display: flex;
  flex-direction: column;
  height: 100vh; /* Full viewport height */
  margin: 0;
  padding: 0;
  overflow: hidden; /* Prevent any scrolling */
}


#content {
  display: flex;
  overflow: hidden; /* Prevents scrolling */
}

html, body {
  height: 100%; /* Make sure the body takes up full height */
  margin: 0; /* Remove default margin */
  padding: 0;
  overflow: hidden; /* Disable scrolling */ 
}


body {
  background-image: url(./assets/poker_background.jpg); /* Replace with your image path */
  background-size: cover; /* Cover the entire area */
  background-position: center; /* Center the image */
  background-repeat: no-repeat; /* Prevent repeating */
  color: #fff; /* Default text color */
}
</style>
