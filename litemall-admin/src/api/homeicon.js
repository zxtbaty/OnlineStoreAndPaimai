import request from '@/utils/request'

export function listHomeIcon(query) {
  return request(
    {
      url: 'homeicon/list',
      method: 'get',
      params: query
    }
  )
}

export function createHomeIcon(data) {
  return request({
    url: '/homeicon/create',
    method: 'post',
    data
  })
}

export function readHomeIcon(id) {
  return request(
    {
      url: '/homeicon/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateHomeIcon(data) {
  return request({
    url: '/homeicon/update',
    method: 'post',
    data
  })
}

export function deleteHomeIcon(data) {
  return request({
    url: '/homeicon/delete',
    method: 'post',
    data
  })
}
