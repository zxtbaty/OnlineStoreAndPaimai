import request from '@/utils/request'

export function statBrowse(query) {
  return request({
    url: '/stat/browse',
    method: 'get',
    params: query
  })
}

export function statUser(query) {
  return request({
    url: '/stat/user',
    method: 'get',
    params: query
  })
}

export function statOrder(query) {
  return request({
    url: '/stat/order',
    method: 'get',
    params: query
  })
}

export function statGoods(query) {
  return request({
    url: '/stat/goods',
    method: 'get',
    params: query
  })
}

export function statZongHe(query) {
  return request({
    url: '/stat/zongHe',
    method: 'get',
    params: query
  })
}


export function statHomeLineChartData(query) {
  return request({
    url: '/stat/homelinechart',
    method: 'get',
    params: query
  })
}

export function statWarningQuery(query) {
  return request({
    url: '/stat/warningquery',
    method: 'get',
    params: query
  })
}
export function statStoreQuery(query) {
  return request({
    url: '/stat/storequery',
    method: 'get',
    params: query
  })
}

