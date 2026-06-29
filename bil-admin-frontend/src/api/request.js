import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearToken, getToken } from '@/stores/auth'

const service = axios.create({
  baseURL: '/admin',
  timeout: 15000,
  withCredentials: true
})

function isPlainObject(value) {
  return Object.prototype.toString.call(value) === '[object Object]'
}

function toFormParams(data) {
  const params = new URLSearchParams()
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.append(key, String(value))
    }
  })
  return params
}

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers = config.headers || {}
    config.headers.thokenAdmin = token
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
    if (!payload || typeof payload.code === 'undefined') return payload
    if (payload.code === 200) return payload.data !== undefined ? payload.data : payload
    if (payload.code === 401 || payload.code === 901 || response.status === 401) {
      clearToken()
      window.location.href = '/login'
    }
    ElMessage.error(payload.info || '请求失败')
    return Promise.reject(new Error(payload.info || '请求失败'))
  },
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 901) {
      clearToken()
      window.location.href = '/login'
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
