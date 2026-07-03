import request from '@/api/request'

export const loadBannersApi = () => request({ url: '/banner/loadBanners', method: 'post' })
export const saveBannerApi = (data) => request({ url: '/banner/saveBanner', method: 'post', data })
export const deleteBannerApi = (bannerId) => request({ url: '/banner/deleteBanner', method: 'post', data: { bannerId } })
