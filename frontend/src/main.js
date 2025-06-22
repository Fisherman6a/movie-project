import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedState from 'pinia-plugin-persistedstate'
import naive from 'naive-ui'

// 引入 vfonts
import 'vfonts/Lato.css'      // 通用字体
import 'vfonts/FiraCode.css'  // 等宽字体，适合显示代码等

const app = createApp(App)
const pinia = createPinia()

pinia.use(piniaPluginPersistedState)

app.use(pinia)
app.use(router)
app.use(naive) // 注册 Naive UI

app.mount('#app')