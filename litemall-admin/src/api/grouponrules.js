import request from '@/utils/request'


export function listGrouponRules(query) {
  return request({
    url: '/grouponrules/list',
    method: 'get',
    params: query
  })
}

export function deleteGrouponRules(data) {
  return request({
    url: '/grouponrules/delete',
    method: 'post',
    data
  })
}

export function createGrouponRules(data) {
  return request({
    url: '/grouponrules/create',
    method: 'post',
    data
  })
}

export function updateGrouponRules(data) {
  return request({
    url: '/grouponrules/update',
    method: 'post',
    data
  })
}

export function detailGrouponRules(id) {
  return request({
    url: '/grouponrules/read',
    method: 'get',
    params: {id},
  })
}

export function addGoodsStoreNum(query) {
  return request({
    url: '/grouponrules/addGoodsStoreNum',
    method: 'get',
    params: query,
  })
}
