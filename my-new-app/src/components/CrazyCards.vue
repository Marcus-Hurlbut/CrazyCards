<template>
  <div class="dashboard">
    <div class="dashboard-content">
      <img alt="Crazy Cards logo" src="./card_icon.png" class="banner-logo" />
      <h1 class="dashboard-title">{{ msg }}</h1>
      <nav class="dashboard-items">
        <ul>
          <li>
            <button @click="callApi">View Profile</button>
          </li>
          <li>
            <router-link to="/hearts">
              <button @click="callApi">Start Hearts</button>
            </router-link>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<script>

export default {
  name: 'CrazyCards',
  props: {
    msg: String
  },
  methods: {
    
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
} 

.dashboard {
  background-color: #C0C0C0; /* Banner background color */
  background: linear-gradient(to bottom right, red, purple);
  color: white; /* Text color */
  padding: 20px 0; /* Top and bottom padding */
  text-align: center; /* Center the content */
}

.dashboard-title {
  color: black; /* Text color */
  padding: 20px 0; /* Top and bottom padding */
  text-align: center; /* Center the content */
  font-style: oblique;
  font-size: xx-large;
}

.dashboard-content {
  display: flex;
  align-items: center;
  max-width: 1200px; /* Optional: limit max width */
  margin: 0 auto; /* Center align the content */
  width: 100%;
  font-size: large;
}

.dashboard-items {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-grow: 1;
}

.banner-logo {
  height: 50px; /* Adjust logo size */
  margin-right: 20px; /* Space between logo and title */
}

nav ul {
  list-style-type: none;
  padding: 0;
  display: flex;
  gap: 20px; /* Space between buttons */
  margin: 0; /* Remove default margin */
}

/* Dashboard Menu buttons */
nav button {
  background-color: #FFFDD0; /* Button background color */
  color: purple; /* Text color */
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s; /* Smooth transition */
  font: 1em sans-serif;
  justify-content: right;
}

nav button:hover {
  background-color: #e7e7e7; /* Change color on hover */
}
</style>

