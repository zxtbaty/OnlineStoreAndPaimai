import request from '@/utils/request'


export function listBackDb(query) {
  return request({
    url: '/backupDb/list',
    method: 'get',
    params: query
  })
}

export function createBackDb(data) {
  return request({
    url: '/backupDb/create',
    method: 'post',
    data
  })
}

export function deleteBackDb(query) {
  return request({
    url: '/backupDb/delete',
    method: 'get',
    params: query
  })
}

export function backupBackDb(query) {
  return request({
    url: '/backupDb/backup',
    method: 'get',
    params: query
  })
}

export function downLoadDb(query) {
  return request({
    url: '/backupDb/downLoad',
    method: 'get',
    params: query
  })
}
