import request from '@/api/request'

export const getResourceApi = (data) => request({ url: '/file/getResource', method: 'get', params: data })
export const uploadImageApi = (data) => request({ url: '/file/uploadImage', method: 'post', data })
export const preUploadVideoApi = (data) => request({ url: '/file/preUploadVideo', method: 'post', data })
export const uploadVideoApi = (data) => request({ url: '/file/uploadVideo', method: 'post', data })
export const completeUploadApi = (data) => request({ url: '/file/completeUpload', method: 'post', data })
export const delUploadVideoApi = (data) => request({ url: '/file/delUploadVideo', method: 'post', data })
