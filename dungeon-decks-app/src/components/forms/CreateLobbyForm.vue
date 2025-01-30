<template>
    <div v-if="!isLobbyCreated">
      <form class="createLobbyForm" @submit.prevent="handleSubmit">
        <label for="displayName">Enter Your Display Name</label><br>
        <input type="text" id="displayName" v-model="displayName" name="displayName" placeholder="Your Display Name"><br><br>
        <button type="submit">Create Lobby</button>
      </form>
      <BubbleBackground />
    </div>
  </template>
  
<script>
import '@/assets/styles/global.css';
import { mapActions } from 'vuex';
import BubbleBackground from '../animations/BubbleBackground.vue';
export default {
  name: "CreateLobbyForm",
  components: {
    BubbleBackground,
  },
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
  .createLobbyForm {
    position: absolute;
    top: 50%;
    left: 50%; 
    transform: translate(-50%, -50%);
    display: inline-block;
    font-family: 'FancyFont', sans-serif;
    padding: 50px 80px;
    background: linear-gradient(135deg, #1b479a ,#6a1b9a, #ff1744);
    border-radius: 15px;
    border: 3px solid #ffffff;
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
    text-align: center;
    color: black;
    width: 300px;
    transition: all 0.3s ease-in-out;
    z-index: 100;
  }
  
  .createLobbyForm label {
    font-size: 2em;
    margin-bottom: 40px;
    display: block;
    color: black;
    position: relative;
    text-shadow: 0px 0px 2px #f6f8f8, 0px 0px 2px #ffffff;
  }
  
  .createLobbyForm input {
    width: 100%;
    padding: 12px;
    margin: 20px -50px;
    border-radius: 8px;
    border: 2px solid #fff;
    font-size: 1em;
    background: rgba(255, 255, 255, 0.3);
    color: #fff;
    text-align: center;
    transition: 0.3s ease;
    left: 50%;
  }
  
  .createLobbyForm input::placeholder {
    color: #ccc;
  }
  
  .createLobbyForm input:focus {
    outline: none;
    background: rgba(255, 255, 255, 0.5);
  }
  
.createLobbyForm button {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  background-color: rgba(201, 84, 201, 0.507);
  color: black;
  font-weight: bold;
  text-shadow: 0px 0px 1px #f6f8f8, 0px 0px 1px #ffffff;
  border: none;
  font-size: 1.8em;
  cursor: pointer;
  transition: 0.3s ease, transform 0.2s ease;
  font-family: 'FancyFont', sans-serif;
}
  
.createLobbyForm button:hover {
  background-color: rgba(35, 68, 214, 0.507);
  transform: scale(1.05);
  animation: neonGlowingBorder 2s infinite alternate;
}
  
.createLobbyForm button:active {
  background-color: rgba(120, 101, 233, 0.507);
}
  
</style>
  