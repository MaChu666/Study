import request from '@/api/request'

export const loadCategoryApi = (pCategoryId) => request({ url: '/category/loadCategory', method: 'post', data: { pCategoryId } })
export const saveCategoryApi = (data) => request({ url: '/category/saveCategory', method: 'post', data })
export const delCategoryApi = (categoryId) => request({ url: '/category/delCategory', method: 'post', data: { categoryId } })
export const changeSortApi = (pCategoryId, categoryIds) => request({ url: '/category/changeSort', method: 'post', data: { pCategoryId, categoryIds } })
