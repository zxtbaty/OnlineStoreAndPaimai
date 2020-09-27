import request from '@/utils/request'

export function listUsergoodsrule(query) {
  return request({
    url: '/userGoodsRule/list',
    method: 'get',
    params: query,

  })
}

export function deleteUsergoodsrule(data) {
  return request({
    url: '/userGoodsRule/delete',
    method: 'post',
    data
  })
}

export function addUsergoodsrule(data) {
  return request({
    url: '/userGoodsRule/create',
    method: 'post',
    data
  })
}

export function updateUsergoodsrule(data) {
  return request({
    url: '/userGoodsRule/update',
    method: 'post',
    data
  })
}

export function detailUsergoodsrule(query) {
  return request({
    url: '/userGoodsRule/read',
    method: 'get',
    params: query,
  })
}

export function addGoodsStoreNum(query) {
  return request({
    url: '/userGoodsRule/addGoodsStoreNum',
    method: 'get',
    params: query,
  })
}

export function queryByName(query) {
  return request({
    url: '/userGoodsRule/queryByName',
    method: 'get',
    params: query,
  })
}


