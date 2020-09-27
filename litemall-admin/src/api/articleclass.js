import request from '@/utils/request'

export function listArticleClass(query) {
  return request({
    url: '/articleclass/list',
    method: 'get',
    params: query
  })
}
export function listArticleClassCascade(query) {
  return request({
    url: '/articleclass/listcascade',
    method: 'get',
    params: query
  })
}
export function listArticleClassL1() {
  return request({
    url: '/articleclass/l1',
    method: 'get'
  })
}

export function createArticleClass(data) {
  return request({
    url: '/articleclass/create',
    method: 'post',
    data
  })
}

export function readArticleClass(id) {
  return request({
    url: '/articleclass/read',
    method: 'get',
    params: { id }
  })
}

export function updateArticleClass(data) {
  return request({
    url: '/articleclass/update',
    method: 'post',
    data
  })
}

export function deleteArticleClass(data) {
  return request({
    url: '/articleclass/delete',
    method: 'post',
    data
  })
}



export function postArticleClass() {
  return request({
    url: '/articleclass/createorder',
    method: 'get'

  })
}



