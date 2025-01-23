<template>
  <DungeonDecks/>
  <router-view />
</template>

<script>
import DungeonDecks from './components/DungeonDecks.vue'

export default {
  name: 'App',
  components: {
    DungeonDecks
  }
}
</script>

<style>
#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
}


#content {
  display: flex;
  overflow: hidden;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

body {
  background-color: rgb(12, 11, 11);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: #fff;
  font-family: 'Roboto', sans-serif;
  font-size: 16px;
  font-weight: bold;
}
</style>
