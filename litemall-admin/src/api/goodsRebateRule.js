import request from '@/utils/request'

export function listGoodsRebaterule(query) {
  return request({
    url: '/goodsRebateRule/list',
    method: 'get',
    params: query,

  })
}

export function listGoodsRebateOrder(query) {
  return request({
    url: '/goodsRebateRule/order',
    method: 'get',
    params: query,

  })
}

export function deleteGoodsRebaterule(data) {
  return request({
    url: '/goodsRebateRule/delete',
    method: 'post',
    data
  })
}

export function addGoodsRebaterule(data) {
  return request({
    url: '/goodsRebateRule/create',
    method: 'post',
    data
  })
}

export function updateGoodsRebaterule(data) {
  return request({
    url: '/goodsRebateRule/update',
    method: 'post',
    data
  })
}

export function detailGoodsRebaterule(query) {
  return request({
    url: '/goodsRebateRule/read',
    method: 'get',
    params: query,
  })
}


