import request from '@/utils/request'

export function listHuodongset(query) {
  return request(
    {
      url: 'huodongset/list',
      method: 'get',
      params: query
    }
  )
}

export function createHuodongset(data) {
  return request({
    url: '/huodongset/create',
    method: 'post',
    data
  })
}

export function readHuodongset(id) {
  return request(
    {
      url: '/huodongset/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateHuodongset(data) {
  return request({
    url: '/huodongset/update',
    method: 'post',
    data
  })
}

export function deleteHuodongset(data) {
  return request({
    url: '/huodongset/delete',
    method: 'post',
    data
  })
}
//
// export function listHuodongPicGoods(id) {
//   return request({
//     url: '/huodongset/ListHuodongPicGoods',
//     method: 'get',
//     params: { id }
//   })
// }
//
// export function listHuodongset(id) {
//   return request({
//     url: '/company/hangzhanlou',
//     method: 'get',
//     params: { id }
//   })
// }



