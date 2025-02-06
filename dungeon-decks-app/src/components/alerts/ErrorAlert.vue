<template>
    <div class="error-alert" v-if="message != null" >
        <img class="icon-left" src="@/assets/icons/warning-icon.png">
        <img class="icon-right" src="@/assets/icons/warning-icon.png">
        <p>Error</p>
        <div class="msg">
          <p>{{ message }} </p>
        </div>
    </div>
</template>
<script>
import '@/assets/styles/global.css';
export default {
    name: "ErrorAlert",
    props: {
        msg: String
    },
    data() {
        return {
            message: null,
        }
    },
    mounted() {
        this.message = this.msg;
    },
}
</script>
<style scoped>
.error-alert {
  position: absolute;
  display: inline;
  bottom: 50%;
  background: linear-gradient(to top left, #c300ff, #ff0000, #0066ff);
  color: white;
  font-family: 'Standard', sans-serif;
  font-size: 1.5em;
  padding: 15px 25px;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
  text-align: center;
  width: 25vw;
  height: 25vh;
  left: 50%;
  transform: translateX(-50%);
  box-sizing: border-box;
  margin: 0;
  z-index: 1000;
  transition: transform 0.3s ease-in-out, box-shadow 0.3s ease, opacity 0.3s ease-in-out;
  opacity: 0.4;
  animation: fadeIn 1s forwards;
}
/* comeback - ERROR WITH ACE */
.error-alert .msg {
  margin-top: 15px;
}

.error-alert p {
  font-size: 1.5em;
  margin-top: 8px;
  line-height: 1.5;
}

.error-alert:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5);
}

.icon-left {
    position: absolute;
    height: 5vh;
    left: 20%;
    /* transform: translate(-200%, -340%); */
}

.icon-right {
    position: absolute;
    height: 5vh;
    /* transform: translate(200%, -341%); */
    right: 20%;
}

@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(2vh);
  }
  25% {
    opacity: .5;
  }
  38% {
    opacity: .63;
  }
  50% {
    opacity: .75;
  }
  75% {
    opacity: .9;
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

</style>