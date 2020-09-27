import request from '@/utils/requestLongTime'

export function execSql(data) {
  return request({
    url: '/commonDB/exec',
    method: 'post',
    data
  })
}

export function insertSql(data) {
  return request({
    url: '/commonDB/insert',
    method: 'post',
    data
  })
}

export function selectSql(data) {
  return request({
    url: '/commonDB/select',
    method: 'post',
    data
  })
}

export function selectPageSql(data) {
  return request({
    url: '/commonDB/selectPage',
    method: 'post',
    data,
  })
}

