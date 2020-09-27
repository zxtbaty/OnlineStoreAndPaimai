import request from '@/utils/request'

export function listOpadminDef(query) {
  return request(
    {
      url: 'opadmindef/list',
      method: 'get',
      params: query
    }
  )
}

export function createOpadminDef(data) {
  return request({
    url: '/opadmindef/create',
    method: 'post',
    data
  })
}

export function readOpadminDef(id) {
  return request(
    {
      url: '/opadmindef/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateOpadminDef(data) {
  return request({
    url: '/opadmindef/update',
    method: 'post',
    data
  })
}

export function deleteOpadminDef(data) {
  return request({
    url: '/opadmindef/delete',
    method: 'post',
    data
  })
}
export function listOpadminpubs(id) {
  return request({
    url: '/opadmindef/opadminpubs',
    method: 'get',
    params: { id }
  })
}
