import request from '@/utils/request'

export function listSeckillrule(query) {
  return request({
    url: '/seckillrule/list',
    method: 'get',
    params: query
  })
}

export function deleteSeckillrule(data) {
  return request({
    url: '/seckillrule/delete',
    method: 'post',
    data
  })
}

export function addSeckillrule(data) {
  return request({
    url: '/seckillrule/create',
    method: 'post',
    data
  })
}

export function updateSeckillrule(data) {
  return request({
    url: '/seckillrule/update',
    method: 'post',
    data
  })
}

export function detailSeckillrule(id) {
  return request({
    url: '/seckillrule/read',
    method: 'get',
    params: {id},
  })
}

export function addGoodsStoreNum(query) {
  return request({
    url: '/seckillrule/addGoodsStoreNum',
    method: 'get',
    params: query,
  })
}

