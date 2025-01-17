<template>
    <div>
      <div class="scoreboard">
        <h3>Scoreboard</h3>
        <ul>
          <li v-for="(score, player) in playerScores" :key="player" class="player-score">
            <div class="player-info">
              <img :src="getPlayerAvatar(player)" alt="avatar" class="avatar" />
              <span class="player-name">{{ player }}</span>
            </div>
            <div class="score-bar">
              <div :style="{ width: score + '%' }" class="score-progress"></div>
            </div>
            <div class="score-value">{{ score }}</div>
          </li>
        </ul>
      </div>
    </div>
  </template>
  
  <script>
  /* eslint-disable vue/multi-word-component-names */
  export default {
    name: "Scoreboard",
    props: {
      username: String,
      otherPlayerNames: Array,
      usernameToScore: Object,
    },
    computed: {
      playerScores() {
        // Combine username and other players into a single list of scores
        return {
          [this.username]: this.getUsernameScore(this.username),
          ...this.otherPlayerNames.reduce((acc, player) => {
            acc[player] = this.getUsernameScore(player);
            return acc;
          }, {}),
        };
      },
    },
    methods: {
      getUsernameScore(username) {
        return this.usernameToScore[username] || 0;
      },
      getPlayerAvatar(player) {
        return `https://ui-avatars.com/api/?name=${player}&background=random&color=fff`;
      },
    },
  };
  </script>
  
  <style scoped>
  .scoreboard {
    position: fixed;
    bottom: 20px;
    left: 20px;
    background: linear-gradient(135deg, purple, #af002d, #1f4e79);
    color: white;
    font-family: 'Arial', sans-serif;
    font-size: 18px;
    font-weight: bold;
    padding: 10px;
    border-radius: 15px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    text-align: left;
    width: 300px;
    max-width: 100%;
    min-width: 250px;
    box-sizing: border-box;
    margin: 0;
    overflow-y: auto;
    max-height: 90%;
    z-index: 0;
  }
  
  .scoreboard h3 {
    text-align: center;
    font-size: 20px;
    margin-bottom: 1px;
    text-transform: uppercase;
  }
  
  ul {
    padding: 0;
    margin: 0;
    list-style-type: none;
  }
  
  .player-score {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.2);
    margin-bottom: 10px;
  }
  
  .player-info {
    display: flex;
    align-items: center;
  }
  
  .avatar {
    width: 35px;
    height: 35px;
    border-radius: 50%;
    margin-right: 10px;
  }
  
  .player-name {
    font-size: 16px;
    font-weight: normal;
    text-transform: capitalize;
  }
  
  .score-bar {
    width: 60%;
    height: 10px;
    background-color: #f0f0f0;
    border-radius: 10px;
    margin-left: 10px;
    position: relative;
  }
  
  .score-progress {
    height: 100%;
    background-color: #4caf50;
    border-radius: 10px;
    transition: width 0.5s ease;
  }
  
  .score-value {
    font-size: 16px;
    font-weight: normal;
    width: 50px;
    text-align: center;
  }
  </style>
  