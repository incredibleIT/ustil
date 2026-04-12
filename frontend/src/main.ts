import './assets/main.css'
import './styles/index.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 应用启动时恢复登录状态
const authStore = useAuthStore()
authStore.restoreFromStorage()

app.mount('#app')
