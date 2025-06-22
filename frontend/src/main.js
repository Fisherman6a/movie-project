import { createApp } from 'vue';
import { createPinia } from 'pinia'; // 引入 Pinia
import App from './App.vue';
import router from './router';
import naive from 'naive-ui';

import 'vfonts/Lato.css';
import 'vfonts/FiraCode.css';

const app = createApp(App);
const pinia = createPinia(); // 创建 Pinia 实例 

app.use(pinia); // 使用 Pinia
app.use(router);
app.use(naive);

app.mount('#app');