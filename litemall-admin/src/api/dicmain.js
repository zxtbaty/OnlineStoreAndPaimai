import request from '@/utils/request'

export function listDicmain(query) {
  return request({
    url: '/dicmain/list',
    method: 'get',
    params: query
  })
}

export function detailDicmain(id) {
  return request({
    url: '/dicmain/detail',
    method: 'get',
    params: { id }
  })
}

export function createDicmain(data) {
  return request({
    url: '/dicmain/create',
    method: 'post',
    data
  })
}

export function readDicmain(id) {
  return request({
    url: '/dicmain/read',
    method: 'get',
    params: { id }
  })
}

export function updateDicmain(data) {
  return request({
    url: '/dicmain/update',
    method: 'post',
    data
  })
}

export function deleteDicmain(data) {
  return request({
    url: '/dicmain/delete',
    method: 'post',
    data
  })
}
