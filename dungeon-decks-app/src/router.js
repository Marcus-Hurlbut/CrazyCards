import { createRouter, createWebHistory } from 'vue-router';
import HeartsComponent from './components/games/HeartsComponent.vue';
import SpadesComponent from './components/games/SpadesComponent.vue';
import JoinLobby from './components/lobby/JoinLobby.vue';
import LobbyComponent from './components/lobby/LobbyComponent.vue';
import CreateLobbyForm from './components/lobby/CreateLobbyForm.vue';
import MainMenu from './components/MainMenu.vue';
import BugReport from './components/index/BugReport.vue';
import SpiderSolitaireComponent from './components/games/SpiderSolitaireComponent.vue';

const routes = [
  { path: '/', component: MainMenu },
  { path: '/hearts', component: HeartsComponent },
  { path: '/spades', component: SpadesComponent },
  { path: '/spiderSolitaire', component: SpiderSolitaireComponent },

  { path: '/joinLobby', component: JoinLobby },
  { path: '/createLobby', component: CreateLobbyForm },
  { path: '/lobby', component: LobbyComponent },
  { path: '/bugReport', component: BugReport}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
