import request from '@/api/request'

export const loadAllThemesApi = () => request({ url: '/theme/loadAllThemes', method: 'post' })
export const addThemeApi = (data) => request({ url: '/theme/addTheme', method: 'post', data })
export const updateThemeApi = (data) => request({ url: '/theme/updateTheme', method: 'post', data })
export const deleteThemeApi = (data) => request({ url: '/theme/deleteTheme', method: 'post', data })
