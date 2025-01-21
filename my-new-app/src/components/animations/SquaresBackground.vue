<template>
<ul class="background">
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
   <li></li>
</ul>
</template>
  
<script>
  
export default {
    name: 'SquaresBackground',
}
</script>
  
<style scoped>
@keyframes cube {
    from {
        transform: scale(0) rotate(0deg) translate(-50%, -50%);
        opacity: 1;
    }
    to {
        transform: scale(20) rotate(960deg) translate(-50%, -50%);
        opacity: 0;
    }
}

.background {
    position: relative;
    width: 100vw;
    height: 100vh;
    top: 0;
    left: 0;
    margin: 0;
    padding: 0;
    background: linear-gradient(45deg, #6655fc, #0fb5f7);
    overflow: hidden;
    z-index: -1;
}
.background li {
    position: absolute;
    top: 80vh;
    left: 45vw;
    width: 10px;
    height: 10px;
    border: solid 1px #ff0000;
    color: transparent;
    transform-origin: top left;
    transform: scale(0) rotate(0deg) translate(-50%, -50%);
    animation: cube 12s ease-in forwards infinite;
}
undefined
.background li:nth-child(0) {
    animation-delay: 0s;
    left: 79vw;
    top: 73vh;
    border-color: #b81c8f;
}

.background li:nth-child(1) {
    animation-delay: 2s;
    left: 68vw;
    top: 14vh;
}

.background li:nth-child(2) {
    animation-delay: 4s;
    left: 3vw;
    top: 63vh;
    border-color: #b81c8f;
}

.background li:nth-child(3) {
    animation-delay: 6s;
    left: 48vw;
    top: 84vh;
}

.background li:nth-child(4) {
    animation-delay: 8s;
    left: 40vw;
    top: 66vh;
    border-color: #0a21f7;
}

.background li:nth-child(5) {
    animation-delay: 10s;
    left: 9vw;
    top: 3vh;
    border-color: #b81c8f;
}

.background li:nth-child(6) {
    animation-delay: 12s;
    left: 82vw;
    top: 44vh;
    border-color: #0a21f7;
}

.background li:nth-child(7) {
    animation-delay: 14s;
    left: 26vw;
    top: 48vh;
    border-color: #b81c8f;
}

.background li:nth-child(8) {
    animation-delay: 16s;
    left: 57vw;
    top: 29vh;
}

.background li:nth-child(9) {
    animation-delay: 18s;
    left: 94vw;
    top: 26vh;
    border-color: #b81c8f;
}

.background li:nth-child(10) {
    animation-delay: 20s;
    left: 36vw;
    top: 94vh;
}

</style>
  