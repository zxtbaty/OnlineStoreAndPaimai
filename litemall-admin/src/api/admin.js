import request from '@/utils/request'

export function listAdmin(query) {
  return request({
    url: '/admin/list',
    method: 'get',
    params: query
  })
}

export function createAdmin(data) {
  return request({
    url: '/admin/create',
    method: 'post',
    data
  })
}

export function readminAdmin(id) {
  return request({
    url: '/admin/read',
    method: 'get',
    params: { id }
  })
}

export function updateAdmin(data) {
  return request({
    url: '/admin/update',
    method: 'post',
    data
  })
}

export function deleteAdmin(data) {
  return request({
    url: '/admin/delete',
    method: 'post',
    data
  })
}
