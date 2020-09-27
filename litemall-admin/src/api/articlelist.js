import request from '@/utils/request'

export function listArticleList(query) {
  return request(
    {
      url: 'articlelist/list',
      method: 'get',
      params: query
    }
  )
}

export function createArticleList(data) {
  return request({
    url: '/articlelist/create',
    method: 'post',
    data
  })
}

export function readArticleList(id) {
  return request(
    {
      url: '/articlelist/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateArticleList(data) {
  return request({
    url: '/articlelist/update',
    method: 'post',
    data
  })
}

export function deleteArticleList(data) {
  return request({
    url: '/articlelist/delete',
    method: 'post',
    data
  })
}
