import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // Import the router
import naive from 'naive-ui'; // Import Naive UI

import 'vfonts/Lato.css';
import 'vfonts/FiraCode.css';

const app = createApp(App);

app.use(router); // Use Vue Router
app.use(naive);  // Use Naive UI

app.mount('#app');