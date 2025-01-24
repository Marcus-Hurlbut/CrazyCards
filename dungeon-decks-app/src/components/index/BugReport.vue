<template>
    <div class="bug-report">
      <div class="images">
        <img class="left-img" src="@/assets/pictures/toy-story-bug-meme.jpg" alt="Bug Meme">
        <img class="right-img" src="@/assets/pictures/bug-report-plain-img.jpg" alt="Bug Report Image">
      </div>
      <h1>Bug Report</h1>
      <h3>Oops.. Did you encounter a problem?</h3>
      <div class="content">
        <p>
          Please share the problems or errors you encountered below. Submitting this will send an email
          to support submission & help contribute to making this app better.
        </p>
        <textarea v-model="bugReport" placeholder="Type your report here"></textarea>
        <button class="submit-btn" @click="sendEmailReport()">Submit Report</button>
      </div>
    </div>
    <BubbleBackground />
</template>
  
<script>
import axios from 'axios';
import BubbleBackground from '../animations/BubbleBackground.vue';

  export default {
    name: "BugReport",
    components: {
        BubbleBackground,
    },
    methods: {
      async sendEmailReport() {
        try {
          // Define the email content
          const emailData = {content: this.bugReport};

          const url = process.env.NODE_ENV === 'development'
          ? 'http://localhost:8080/api/bugReport'
          : 'https://dungeondecks.net/api/bugReport'

          // Make the POST request to the backend
          const response = await axios.post(url, emailData);

          // Handle the response (maybe show a success message)
          console.log('Email sent successfully:', response.data);
        } catch (error) {
          // Handle errors (show an error message)
          console.error('Error sending email:', error);
        }
      }
    }
};
</script>
  
<style scoped>
  /* Overall container */
  .bug-report {
    position: fixed;
    left: 19%;
    top: 15%;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #333;
    color: white;
    border-radius: 12px;
    width: 60vw;
    padding: 25px;
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.3);
    text-align: center;
    margin: 40px auto;
    font-family: 'Arial', sans-serif;
    border: 5px;
    border-color: #054394;
    z-index: 10;
  }
  
  /* Images */
  .images {
    display: flex;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 20px;
  }
  
  .left-img, .right-img {
    height: 20vh;
    width: auto;
    border-radius: 8px;
    border: 5px solid rgb(150, 4, 247);
  }

  h1 {
    position: absolute;
    text-shadow: 0px 0px 10px #00ffff, 0px 0px 20px #00a2ff;
    font-size: 3em;
    margin: 10px 0;
    height: 80vh;
    font-weight: bold;
    top: 10%;
  }
  
  h3 {
    font-size: 1.5em;
    color: #ddd;
    margin: 10px 0;
    font-family: fantasy;
  }
  
  /* Content section */
  .content {
    width: 100%;
    max-width: 600px;
  }
  
  .content p {
    font-size: 1em;
    margin: 10px 0;
    line-height: 1.6;
  }
  
  /* Textarea */
  textarea {
    width: 100%;
    height: 150px;
    padding: 10px;
    margin-top: 15px;
    border: 2px solid #666;
    border-radius: 8px;
    background-color: #222;
    color: white;
    font-size: 1em;
    resize: vertical;
  }
  
  textarea:focus {
    outline: none;
    border-color: #6A4C9C;
  }
  
  /* Submit Button */
  .submit-btn {
    position: relative;
    margin-top: 15px;
    padding: 12px 20px;
    font-size: 1.2em;
    background: linear-gradient(to bottom right, rgb(150, 4, 247), rgb(255, 0, 0));
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .submit-btn:hover {
    transform: translateY(-2px);
    background: linear-gradient(to bottom left, rgb(150, 4, 247), rgb(255, 0, 0));
  }
  </style>
  