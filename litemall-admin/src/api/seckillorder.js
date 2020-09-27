import request from '@/utils/request'
import Qs from 'qs'

export function listSeckillOrders(query) {
  return request({
    url: '/seckillorders/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

// export function listMainOrder(query) {
//   return request({
//     url: '/seckillorders/listMainOrder',
//     method: 'get',
//     params: query,
//     paramsSerializer: function(params) {
//       return Qs.stringify(params, { arrayFormat: 'repeat' })
//     }
//   })
// }

export function createSeckillOrder(data) {
  return request({
    url: '/seckillorders/create',
    method: 'post',
    data
  })
}

export function deleteSeckillOrder(data) {
  return request({
    url: '/seckillorders/delete',
    method: 'post',
    data
  })
}

export function editSeckillOrder(data) {
  return request({
    url: '/seckillorders/update',
    method: 'post',
    data
  })
}
