import request from '@/utils/requestLongTime'

export function fetchList(query) {
  return request({
    url: '/user/list',
    method: 'get',
    params: query
  })
}


export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

/**
 * 获取用户的充值记录
 * @param query
 */
export function listChargeMoney(query) {
  return request({
    url: '/user/listChargeMoney',
    method: 'get',
    params: query
  })
}

/**
 * 获取用户的锁定列表
 * @param query
 */
export function listChargeMoneyLock(query) {
  return request({
    url: '/user/listChargeMoneyLock',
    method: 'get',
    params: query
  })
}

export function listAddress(query) {
  return request({
    url: '/address/list',
    method: 'get',
    params: query
  })
}

export function listCollect(query) {
  return request({
    url: '/collect/list',
    method: 'get',
    params: query
  })
}

export function listFeedback(query) {
  return request({
    url: '/feedback/list',
    method: 'get',
    params: query
  })
}

export function listFootprint(query) {
  return request({
    url: '/footprint/list',
    method: 'get',
    params: query
  })
}

export function listHistory(query) {
  return request({
    url: '/history/list',
    method: 'get',
    params: query
  })
}

export function getUserOfferList(query) {
  return request({
    url: '/user/getUserOfferList',
    method: 'get',
    params: query
  })
}



