import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/variables.css'
import './styles/base.css'
import './styles/element-plus-overrides.css'

createApp(App)
  .use(createPinia())
  .use(router)
  .use(ElementPlus)
  .mount('#app')
