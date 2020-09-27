import request from '@/utils/request'

export function listPickSite(query) {
  return request(
    {
      url: 'picksite/list',
      method: 'get',
      params: query
    }
  )
}

export function createPickSite(data) {
  return request({
    url: '/picksite/create',
    method: 'post',
    data
  })
}

export function readPickSite(id) {
  return request(
    {
      url: '/picksite/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updatePickSite(data) {
  return request({
    url: '/picksite/update',
    method: 'post',
    data
  })
}

export function deletePickSite(data) {
  return request({
    url: '/picksite/delete',
    method: 'post',
    data
  })
}
