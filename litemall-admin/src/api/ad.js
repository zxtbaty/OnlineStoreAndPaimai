import request from '@/utils/request'

export function listAd(query) {
  return request({
    url: '/ad/list',
    method: 'get',
    params: query
  })
}

export function createAd(data) {
  return request({
    url: '/ad/create',
    method: 'post',
    data
  })
}

export function readAd(id) {
  return request({
    url: '/ad/read',
    method: 'get',
    params: { id }
  })
}

export function updateAd(data) {
  return request({
    url: '/ad/update',
    method: 'post',
    data
  })
}

export function deleteAd(data) {
  return request({
    url: '/ad/delete',
    method: 'post',
    data
  })
}
