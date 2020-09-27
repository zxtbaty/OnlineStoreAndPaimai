import request from '@/utils/request'

export function listStore(query) {
  return request(
    {
      url: 'store/list',
      method: 'get',
      params: query,

    }
  )
}

export function allStore() {
  return request(
    {
      url: 'store/all',
      method: 'get'
    }
  )
}

export function createStore(data) {
  return request({
    url: '/store/create',
    method: 'post',
    data
  })
}

export function readStore(id) {
  return request(
    {
      url: '/store/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateStore(data) {
  return request({
    url: '/store/update',
    method: 'post',
    data
  })
}

export function deleteStore(data) {
  return request({
    url: '/store/delete',
    method: 'post',
    data
  })
}
export function listStoreBrands(id) {
  return request({
    url: '/store/storebrands',
    method: 'get',
    params: { id }
  })
}
