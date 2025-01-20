<template>
    <div class="crazyCards">
      <ul>
        <li class="game-vault-dropdown">
          <GameVaultButton />
  
          <ul class="hearts-dropdown-menu">
            <HeartsDropdownMenu />
          </ul>
        </li>
      </ul>

      <div class="background">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        </div>
    </div>
</template>
  
<script>
import HeartsDropdownMenu from './dashboard/HeartsDropdownMenu.vue';
import GameVaultButton from './dashboard/GameVaultButton.vue'
  
export default {
    name: 'CrazyCards',
    components: {
      HeartsDropdownMenu,
      GameVaultButton
    },
    props: {
      msg: String
    },
}
</script>
  
<style scoped>
  html, body {
    height: 100%;
    margin: 0;
    padding: 0;
  }
  
  .crazyCards {
    background: linear-gradient(to bottom right, #b40e40, #9C27B0);
    animation: gradientAnimation 10s ease-in-out infinite;
    color: white;
    text-align: center;
    text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00ffff;
  }
  
  ul {
    list-style-type: none;
    padding: 0;
    margin: 0;
    align-content: center;
    justify-content: center;
  }
  
  li {
    position: relative;
  }
  
  .game-vault-dropdown {
    position: absolute;
    top: 30%;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
  }
  
  .game-vault-dropdown:hover .hearts-dropdown-menu {
    display: block;
  }
  
  .hearts-dropdown-menu {
    display: none;  
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    background: #9C27B0;
    padding: 15px;
    border-radius: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  }
  
  .hearts-dropdown-menu li {
    color: white;
    padding: 10px;
    cursor: pointer;
  }
  
  .hearts-dropdown-menu li:hover {
    background-color: #b40e40;
  }


  @keyframes move {
    100% {
        transform: translate3d(0, 0, 1px) rotate(360deg);
    }
}

.background {
    width: 100vw;
    height: 100vh;
    top: 0;
    left: 0;
    background: #000000;
    overflow: hidden;
    z-index: 0;
    position: relative;
}

.background span {
    width: 10vmin;
    height: 10vmin;
    border-radius: 10vmin;
    backface-visibility: hidden;
    position: absolute;
    animation: move;
    animation-duration: 45;
    animation-timing-function: linear;
    animation-iteration-count: infinite;
}


.background span:nth-child(0) {
    color: #0400fa;
    top: 74%;
    left: 45%;
    animation-duration: 28s;
    animation-delay: -15s;
    transform-origin: -6vw -10vh;
    box-shadow: 20vmin 0 2.8563953997999176vmin currentColor;
}
.background span:nth-child(1) {
    color: #ff0000;
    top: 19%;
    left: 15%;
    animation-duration: 16s;
    animation-delay: -27s;
    transform-origin: 21vw -14vh;
    box-shadow: 20vmin 0 3.3097027623837323vmin currentColor;
}
.background span:nth-child(2) {
    color: #0400fa;
    top: 37%;
    left: 86%;
    animation-duration: 13s;
    animation-delay: -38s;
    transform-origin: -9vw 10vh;
    box-shadow: -20vmin 0 3.4221071092330853vmin currentColor;
}
.background span:nth-child(3) {
    color: #6727dd;
    top: 41%;
    left: 62%;
    animation-duration: 48s;
    animation-delay: -10s;
    transform-origin: -21vw -1vh;
    box-shadow: 20vmin 0 3.0254187453531376vmin currentColor;
}
.background span:nth-child(4) {
    color: #ff0000;
    top: 89%;
    left: 88%;
    animation-duration: 38s;
    animation-delay: -13s;
    transform-origin: -21vw -19vh;
    box-shadow: 20vmin 0 3.4386233081866515vmin currentColor;
}
.background span:nth-child(5) {
    color: #0400fa;
    top: 29%;
    left: 19%;
    animation-duration: 43s;
    animation-delay: -40s;
    transform-origin: -6vw -6vh;
    box-shadow: -20vmin 0 2.8940103481873827vmin currentColor;
}
.background span:nth-child(6) {
    color: #ff0000;
    top: 55%;
    left: 67%;
    animation-duration: 44s;
    animation-delay: -27s;
    transform-origin: 6vw -18vh;
    box-shadow: 20vmin 0 2.5406518628053956vmin currentColor;
}
.background span:nth-child(7) {
    color: #6727dd;
    top: 93%;
    left: 38%;
    animation-duration: 17s;
    animation-delay: -13s;
    transform-origin: 20vw -2vh;
    box-shadow: 20vmin 0 2.947405444372753vmin currentColor;
}
.background span:nth-child(8) {
    color: #0400fa;
    top: 71%;
    left: 59%;
    animation-duration: 23s;
    animation-delay: -6s;
    transform-origin: -22vw 4vh;
    box-shadow: 20vmin 0 3.047377358628942vmin currentColor;
}
.background span:nth-child(9) {
    color: #0400fa;
    top: 41%;
    left: 47%;
    animation-duration: 46s;
    animation-delay: -13s;
    transform-origin: 22vw -19vh;
    box-shadow: -20vmin 0 2.7889650726333173vmin currentColor;
}
.background span:nth-child(10) {
    color: #0400fa;
    top: 63%;
    left: 24%;
    animation-duration: 19s;
    animation-delay: -24s;
    transform-origin: 20vw 6vh;
    box-shadow: 20vmin 0 2.8036014100108337vmin currentColor;
}
.background span:nth-child(11) {
    color: #0400fa;
    top: 15%;
    left: 22%;
    animation-duration: 48s;
    animation-delay: -2s;
    transform-origin: 24vw -8vh;
    box-shadow: -20vmin 0 2.864413972555595vmin currentColor;
}
.background span:nth-child(12) {
    color: #6727dd;
    top: 28%;
    left: 82%;
    animation-duration: 18s;
    animation-delay: -33s;
    transform-origin: 7vw 23vh;
    box-shadow: 20vmin 0 3.3140783921770227vmin currentColor;
}
.background span:nth-child(13) {
    color: #ff0000;
    top: 42%;
    left: 95%;
    animation-duration: 10s;
    animation-delay: -34s;
    transform-origin: 12vw -7vh;
    box-shadow: -20vmin 0 2.7339536960676196vmin currentColor;
}
.background span:nth-child(14) {
    color: #ff0000;
    top: 68%;
    left: 96%;
    animation-duration: 48s;
    animation-delay: -38s;
    transform-origin: -18vw -7vh;
    box-shadow: -20vmin 0 2.6109652640524352vmin currentColor;
}
.background span:nth-child(15) {
    color: #0400fa;
    top: 56%;
    left: 3%;
    animation-duration: 51s;
    animation-delay: -2s;
    transform-origin: -3vw -5vh;
    box-shadow: -20vmin 0 3.0657559161639725vmin currentColor;
}
.background span:nth-child(16) {
    color: #ff0000;
    top: 61%;
    left: 66%;
    animation-duration: 25s;
    animation-delay: -19s;
    transform-origin: 9vw 9vh;
    box-shadow: -20vmin 0 2.6817325573621194vmin currentColor;
}
.background span:nth-child(17) {
    color: #6727dd;
    top: 2%;
    left: 89%;
    animation-duration: 42s;
    animation-delay: -7s;
    transform-origin: 17vw -2vh;
    box-shadow: 20vmin 0 3.13764417287289vmin currentColor;
}
.background span:nth-child(18) {
    color: #6727dd;
    top: 24%;
    left: 53%;
    animation-duration: 52s;
    animation-delay: -13s;
    transform-origin: -8vw 4vh;
    box-shadow: 20vmin 0 2.752709341720447vmin currentColor;
}
.background span:nth-child(19) {
    color: #6727dd;
    top: 62%;
    left: 69%;
    animation-duration: 29s;
    animation-delay: -36s;
    transform-origin: 11vw -10vh;
    box-shadow: 20vmin 0 2.855315524814471vmin currentColor;
}
.background span:nth-child(20) {
    color: #ff0000;
    top: 98%;
    left: 29%;
    animation-duration: 35s;
    animation-delay: -47s;
    transform-origin: -15vw 4vh;
    box-shadow: 20vmin 0 3.0398381013698454vmin currentColor;
}
.background span:nth-child(21) {
    color: #ff0000;
    top: 38%;
    left: 17%;
    animation-duration: 17s;
    animation-delay: -11s;
    transform-origin: 5vw 22vh;
    box-shadow: -20vmin 0 2.6774041279186243vmin currentColor;
}
.background span:nth-child(22) {
    color: #6727dd;
    top: 30%;
    left: 72%;
    animation-duration: 21s;
    animation-delay: -42s;
    transform-origin: 19vw 23vh;
    box-shadow: -20vmin 0 3.0263592814238907vmin currentColor;
}
.background span:nth-child(23) {
    color: #ff0000;
    top: 17%;
    left: 60%;
    animation-duration: 18s;
    animation-delay: -6s;
    transform-origin: 17vw 9vh;
    box-shadow: 20vmin 0 2.5150842051769553vmin currentColor;
}
.background span:nth-child(24) {
    color: #0400fa;
    top: 8%;
    left: 82%;
    animation-duration: 44s;
    animation-delay: -41s;
    transform-origin: -20vw 8vh;
    box-shadow: -20vmin 0 2.8667923076395327vmin currentColor;
}
.background span:nth-child(25) {
    color: #0400fa;
    top: 36%;
    left: 62%;
    animation-duration: 6s;
    animation-delay: -12s;
    transform-origin: 25vw 12vh;
    box-shadow: 20vmin 0 2.809761767626764vmin currentColor;
}
.background span:nth-child(26) {
    color: #0400fa;
    top: 14%;
    left: 21%;
    animation-duration: 15s;
    animation-delay: -16s;
    transform-origin: -8vw 19vh;
    box-shadow: -20vmin 0 2.972663040157502vmin currentColor;
}
.background span:nth-child(27) {
    color: #0400fa;
    top: 92%;
    left: 7%;
    animation-duration: 44s;
    animation-delay: -18s;
    transform-origin: 15vw -20vh;
    box-shadow: -20vmin 0 3.246949842328709vmin currentColor;
}
.background span:nth-child(28) {
    color: #ff0000;
    top: 83%;
    left: 10%;
    animation-duration: 53s;
    animation-delay: -20s;
    transform-origin: -20vw -1vh;
    box-shadow: 20vmin 0 2.868081598865637vmin currentColor;
}
.background span:nth-child(29) {
    color: #0400fa;
    top: 48%;
    left: 43%;
    animation-duration: 13s;
    animation-delay: -8s;
    transform-origin: -16vw -14vh;
    box-shadow: -20vmin 0 2.8799011078761865vmin currentColor;
}
.background span:nth-child(30) {
    color: #6727dd;
    top: 81%;
    left: 27%;
    animation-duration: 32s;
    animation-delay: -45s;
    transform-origin: 20vw 7vh;
    box-shadow: -20vmin 0 3.112773998449452vmin currentColor;
}
.background span:nth-child(31) {
    color: #0400fa;
    top: 61%;
    left: 89%;
    animation-duration: 50s;
    animation-delay: -20s;
    transform-origin: -12vw -8vh;
    box-shadow: -20vmin 0 2.672428039370435vmin currentColor;
}
.background span:nth-child(32) {
    color: #6727dd;
    top: 71%;
    left: 57%;
    animation-duration: 37s;
    animation-delay: -22s;
    transform-origin: -10vw -19vh;
    box-shadow: -20vmin 0 2.526285330577524vmin currentColor;
}

</style>
  