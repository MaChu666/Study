import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const rewriteApiPrefix = env.VITE_API_PROXY_REWRITE !== 'false'

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:7071',
          changeOrigin: true,
          rewrite: rewriteApiPrefix ? (path) => path.replace(/^\/api/, '') : (path) => path,
          timeout: 300000,
          proxyTimeout: 300000
        },
        '/videos': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:7071',
          changeOrigin: true,
          timeout: 300000,
          proxyTimeout: 300000
        },
        '/images': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:7071',
          changeOrigin: true,
          timeout: 300000,
          proxyTimeout: 300000
        }
      }
    }
  }
})