<template>
    <div v-if="!isLobbyCreated">
      <form class="lobbyForm" @submit.prevent="handleSubmit">
        <label for="displayName">Enter Your Display Name</label>
        <input type="text" id="displayName" v-model="displayName" name="displayName" placeholder="Your Display Name"><br><br>
        <button type="submit">Create Lobby</button>
      </form>
    </div>
  </template>
  
<script>
import '@/assets/styles/global.css';
import '@/components/lobby/css/styles.css';
import { mapActions } from 'vuex';

export default {
  name: "CreateLobbyForm",
  data() {
    return {
      displayName: ''
    };
  },
  props: {
    isLobbyCreated: Boolean,
  },
  methods: {
    ...mapActions(['storeUsername', 'storePlayerIndex']),
    handleSubmit() {
      this.storeUsername(this.displayName);
      this.storePlayerIndex(0);
      this.$emit('submit', this.displayName); 

      this.$router.push({
        path: '/lobby',
        query: {
          ...this.$route.query
        }
      });
    }
  }
};
</script>
  
<style scoped>
.lobbyForm {
  height: 30vh;
  padding: 8vh 5vw;
}

.lobbyForm label {
  margin-bottom: 10vh;
}
  
.lobbyForm input {
  margin: 20px -50px;
  border-radius: 8px;
  font-size: 1em;
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
  text-align: center;
  transition: 0.3s ease;
  left: 50%;
}

@media (max-width: 767px) {
  .lobbyForm label {
    margin-bottom: 5vh;
  }
}
</style>
  