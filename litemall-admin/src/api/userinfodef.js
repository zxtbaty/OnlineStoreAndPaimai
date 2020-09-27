import request from '@/utils/request'

export function listUserinfoDef(query) {
  return request(
    {
      url: 'userinfodef/list',
      method: 'get',
      params: query
    }
  )
}

export function createUserinfoDef(data) {
  return request({
    url: '/userinfodef/create',
    method: 'post',
    data
  })
}

export function readUserinfoDef(id) {
  return request(
    {
      url: '/userinfodef/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateUserinfoDef(data) {
  return request({
    url: '/userinfodef/update',
    method: 'post',
    data
  })
}

export function deleteUserinfoDef(data) {
  return request({
    url: '/userinfodef/delete',
    method: 'post',
    data
  })
}
export function listUserinfopubs(id) {
  return request({
    url: '/userinfodef/userinfopubs',
    method: 'get',
    params: { id }
  })
}
