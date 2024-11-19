import { createRouter, createWebHistory } from 'vue-router';
// import CrazyCards from './components/CrazyCards.vue';
import HeartsComponent from './components/HeartsComponent.vue';

const routes = [
  // { path: '/', component: CrazyCards },
  { path: '/hearts', component: HeartsComponent },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
