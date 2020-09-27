import request from '@/utils/request'

export function listIssue(query) {
  return request({
    url: '/issue/list',
    method: 'get',
    params: query
  })
}

export function createIssue(data) {
  return request({
    url: '/issue/create',
    method: 'post',
    data
  })
}

export function readIssue(id) {
  return request({
    url: '/issue/read',
    method: 'get',
    params: { id }
  })
}

export function updateIssue(data) {
  return request({
    url: '/issue/update',
    method: 'post',
    data
  })
}

export function deleteIssue(data) {
  return request({
    url: '/issue/delete',
    method: 'post',
    data
  })
}
