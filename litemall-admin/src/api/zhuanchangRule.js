import request from '@/utils/request'

export function listZhuanchangRule(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/list',
    method: 'get',
    params: query,

  })
}

export function deleteZhuanchangRule(data) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/delete',
    method: 'post',
    data
  })
}

export function addZhuanchangRule(data) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/create',
    method: 'post',
    data
  })
}

export function updateZhuanchangRule(data) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/update',
    method: 'post',
    data
  })
}

export function detailZhuanchangRule(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/read',
    method: 'get',
    params: query,
  })
}

export function getZhuanchangGoodsDetail(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/getGoodsDetail',
    method: 'get',
    params: query,
  })
}

export function updateZhuanchangGoodsDetail(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/updateGoodsDetail',
    method: 'get',
    params: query,
  })
}

export function unLockMoneyByRuleMxId(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/unLockMoneyByRuleMxId',
    method: 'get',
    params: query,
  })
}

export function unLockMoneyByZhuanChangId(query) {
  return request({
    url: '/auctionZhuanchangRuleCurrent/unLockMoneyByZhuanChangId',
    method: 'get',
    params: query,
  })
}
