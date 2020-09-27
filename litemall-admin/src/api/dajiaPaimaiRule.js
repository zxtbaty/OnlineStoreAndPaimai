import request from '@/utils/request'

export function listDajiaPaimaiRule(query) {
  return request({
    url: '/dajiaPaimaiRule/list',
    method: 'get',
    params: query,

  })
}

export function deleteDajiaPaimaiRule(data) {
  return request({
    url: '/dajiaPaimaiRule/delete',
    method: 'post',
    data
  })
}

export function addDajiaPaimaiRule(data) {
  return request({
    url: '/dajiaPaimaiRule/create',
    method: 'post',
    data
  })
}

export function updateDajiaPaimaiRule(data) {
  return request({
    url: '/dajiaPaimaiRule/update',
    method: 'post',
    data
  })
}

export function detailDajiaPaimaiRule(query) {
  return request({
    url: '/dajiaPaimaiRule/read',
    method: 'get',
    params: query,
  })
}

export function unLockMoney(query) {
  return request({
    url: '/dajiaPaimaiRule/unLockMoney',
    method: 'get',
    params: query,
  })
}
