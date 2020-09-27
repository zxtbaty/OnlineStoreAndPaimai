import request from '@/utils/request'

export function listPrivateMakeRule(query) {
  return request({
    url: '/privateMakeRule/list',
    method: 'get',
    params: query,

  })
}

export function deletePrivateMakeRule(data) {
  return request({
    url: '/privateMakeRule/delete',
    method: 'post',
    data
  })
}

export function addPrivateMakeRule(data) {
  return request({
    url: '/privateMakeRule/create',
    method: 'post',
    data
  })
}

export function updatePrivateMakeRule(data) {
  return request({
    url: '/privateMakeRule/update',
    method: 'post',
    data
  })
}

export function detailPrivateMakeRule(query) {
  return request({
    url: '/privateMakeRule/read',
    method: 'get',
    params: query,
  })
}
