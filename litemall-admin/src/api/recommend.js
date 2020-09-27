import request from '@/utils/request'

export function listRecommend(query) {
  return request({
    url: '/recommend/list',
    method: 'get',
    params: query
  })
}

export function deleteRecommend(data) {
  return request({
    url: '/recommend/delete',
    method: 'post',
    data
  })
}

export function addRecommend(data) {
  return request({
    url: '/recommend/create',
    method: 'post',
    data
  })
}

export function updateRecommend(data) {
  return request({
    url: '/recommend/update',
    method: 'post',
    data
  })
}

