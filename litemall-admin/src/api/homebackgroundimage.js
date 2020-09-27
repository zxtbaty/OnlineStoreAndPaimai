
import request from '@/utils/request'

export function listHomeBackgroundImage(query) {
  return request(
    {
      url: '/homebackgroundimage/list',
      method: 'get',
      params: query
    }
  )
}

export function createHomeBackgroundImage(data) {
  return request({
    url: '/homebackgroundimage/create',
    method: 'post',
    data
  })
}

export function readHomeBackgroundImage(id) {
  return request(
    {
      url: '/homebackgroundimage/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateHomeBackgroundImage(data) {
  return request({
    url: '/homebackgroundimage/update',
    method: 'post',
    data
  })
}

export function deleteHomeBackgroundImage(data) {
  return request({
    url: '/homebackgroundimage/delete',
    method: 'post',
    data
  })
}

