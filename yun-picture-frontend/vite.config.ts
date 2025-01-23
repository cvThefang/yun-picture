import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    VueDevTools({
      // 启用 Vue DevTools
      componentInspector: true,
      // 将默认编辑器从 VS Code 更改为 WebStorm (自己改的配置)
      launchEditor: 'webstorm',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
