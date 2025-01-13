import { createRouter, createWebHistory } from 'vue-router';
// import CrazyCards from './components/CrazyCards.vue';
import HeartsComponent from './components/HeartsComponent.vue';
import JoinHeartsComponent from './components/JoinHeartsComponent.vue';
import LobbyComponent from './components/LobbyComponent.vue';

const routes = [
  // { path: '/', component: CrazyCards },
  { path: '/heartsGame', component: HeartsComponent },
  { path: '/joinHearts', component: JoinHeartsComponent },
  { path: '/heartsLobby', component: LobbyComponent },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
