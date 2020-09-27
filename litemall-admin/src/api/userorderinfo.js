import request from '@/utils/request'

export function listUserOrderinfo(query) {
  return request(
    {
      url: 'userorderinfo/list',
      method: 'get',
      params: query
    }
  )
}

export function readUserOrderinfo(id) {
  return request(
    {
      url: '/userorderinfo/read',
      method: 'get',
      params: { id }
    }
  )
}

