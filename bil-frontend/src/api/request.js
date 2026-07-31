import axios from 'axios'
import { ElMessage } from 'element-plus'
import { eventBus } from '@/utils/eventBus'
import { clearToken, getToken } from '@/utils/token'

const tokenHeader = import.meta.env.VITE_TOKEN_HEADER || 'thoken'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 120000,
  withCredentials: true
})

// Network errors that are worth retrying (backend starting up, transient issues)
const RETRYABLE_CODES = ['EACCES', 'ECONNREFUSED', 'ECONNRESET', 'ERR_NETWORK', 'ERR_CONNECTION_REFUSED', 'ETIMEDOUT']

function isPlainObject(value) {
  return Object.prototype.toString.call(value) === '[object Object]'
}

function toFormParams(data) {
  const params = new URLSearchParams()
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      params.append(key, String(value))
    }
  })
  return params
}

function isLoginRequired(payload) {
  const info = String(payload?.info || '')
  return payload?.code === 401 || payload?.code === 901 || (payload?.code === 600 && (info.includes('请先登录') || info.includes('未登录')))
}

service.interceptors.request.use((config) => {
  const token = getToken()
  config.headers = config.headers || {}
  if (token) {
    config.headers[tokenHeader] = token
  }
  if (String(config.method || 'get').toLowerCase() === 'post' && isPlainObject(config.data)) {
    config.data = toFormParams(config.data)
    config.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (!payload || typeof payload.code === 'undefined') {
      return payload
    }
    if (payload.code === 200) {
      return payload.data
    }
    if (response.status === 401 || isLoginRequired(payload)) {
      clearToken()
      if (payload.code === 901 && window.location.pathname.includes('/admin')) {
        window.location.href = '/admin/account/login'
        return Promise.reject(new Error(payload.info || '未登录'))
      }
      eventBus.emit('auth:required')
    }
    if (!response.config?.silent) {
      ElMessage.error(payload.info || '请求失败')
    }
    return Promise.reject(new Error(payload.info || '请求失败'))
  },
  async (error) => {
    const config = error.config || {}

    // Auto-retry once for transient network errors (backend startup, etc.)
    if (!config._retried && RETRYABLE_CODES.includes(error.code)) {
      config._retried = true
      await new Promise(resolve => setTimeout(resolve, 1000))
      return service(config)
    }

    if (error.code === 'ERR_CANCELED' || error.name === 'CanceledError' || error.name === 'AbortError') {
      ElMessage.warning('您切换的太快了！')
      return Promise.reject(error)
    }
    if (error.response && error.response.status === 401) {
      clearToken()
      eventBus.emit('auth:required')
      if (!config.silent) {
        ElMessage.warning('请先登录')
      }
      return Promise.reject(error)
    }
    if (!config.silent) {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default service