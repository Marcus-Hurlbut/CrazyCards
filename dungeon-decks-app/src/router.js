import { createRouter, createWebHistory } from 'vue-router';
import HeartsComponent from './components/HeartsComponent.vue';
import JoinHeartsComponent from './components/JoinHeartsComponent.vue';
import LobbyComponent from './components/LobbyComponent.vue';
import CreateLobbyForm from './components/forms/CreateLobbyForm.vue';
import MainMenu from './components/MainMenu.vue';
import BugReport from './components/index/BugReport.vue';

const routes = [
  { path: '/', component: MainMenu },
  { path: '/heartsGame', component: HeartsComponent },
  { path: '/joinHearts', component: JoinHeartsComponent },
  { path: '/createHeartsLobby', component: CreateLobbyForm },
  { path: '/heartsLobby', component: LobbyComponent },
  { path: '/bugReport', component: BugReport}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
