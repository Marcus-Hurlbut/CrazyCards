<template>
  <div>
    <div class="container">
    <div class="red flame"></div>
    <div class="orange flame"></div>
    <div class="yellow flame"></div>
    <div class="white flame"></div>
    <div class="blue circle"></div>
    <div class="black circle"></div>
    </div>
  </div>
</template>

<script>
export default {
    name: "FlameAnimation"
}
</script>

<style>

.container{
  position:relative;
  transform-origin:center bottom;
  animation-name: flicker;
  animation-duration:10ms;
  animation-delay:1000ms;
  animation-timing-function: ease-in;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

.flame{
  bottom:0;
  position:absolute;
  border-bottom-right-radius: 50%;
  border-bottom-left-radius: 50%;
  border-top-left-radius: 50%;
  transform:rotate(-45deg) scale(1.5,1.5);
}

.yellow{
  left:15px; 
  width: 14px;
  height: 14px;
  background:gold;
  box-shadow: 0px 0px 9px 4px gold;
}

.orange{
  left:10px; 
  width: 25px;
  height: 17px;
  background:orange;
  box-shadow: 0px 0px 9px 4px orange;
}

.red{
  left:5px;
  width: 20px;
  height: 20px;
  background:OrangeRed;
  box-shadow: 0px 0px 5px 4px OrangeRed;
}

.white{
  left:15px; 
  bottom:-4px;
  width: 15px;
  height: 20px;
  background:white;
  box-shadow: 0px 0px 9px 4px white;
}

.circle{
  border-radius: 50%;
  position:absolute;  
}

@keyframes flicker{
  0%   {transform: rotate(-1deg);}
  20%  {transform: rotate(1deg);}
  40%  {transform: rotate(-1deg);}
  60%  {transform: rotate(1deg) scaleY(1.04);}
  80%  {transform: rotate(-2deg) scaleY(0.92);}
  100% {transform: rotate(1deg);}
}
</style>