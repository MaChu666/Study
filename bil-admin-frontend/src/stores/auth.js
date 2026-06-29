const TOKEN_KEY = 'bil-admin-token'
const PROFILE_KEY = 'bil-admin-profile'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (!token) return
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(PROFILE_KEY)
}

export function getProfile() {
  try {
    return JSON.parse(localStorage.getItem(PROFILE_KEY) || 'null')
  } catch {
    return null
  }
}

export function setProfile(profile) {
  if (!profile) return
  localStorage.setItem(PROFILE_KEY, JSON.stringify(profile))
}
