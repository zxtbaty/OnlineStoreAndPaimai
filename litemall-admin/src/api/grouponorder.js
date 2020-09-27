import request from '@/utils/request'
import Qs from 'qs'

export function listGrouponOrders(query) {
  return request({
    url: '/grouponorders/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

// export function listMainOrder(query) {
//   return request({
//     url: '/grouponorders/listMainOrder',
//     method: 'get',
//     params: query,
//     paramsSerializer: function(params) {
//       return Qs.stringify(params, { arrayFormat: 'repeat' })
//     }
//   })
// }

export function createGrouponOrder(data) {
  return request({
    url: '/grouponorders/create',
    method: 'post',
    data
  })
}

export function deleteGrouponOrder(data) {
  return request({
    url: '/grouponorders/delete',
    method: 'post',
    data
  })
}

export function editGrouponOrder(data) {
  return request({
    url: '/grouponorders/update',
    method: 'post',
    data
  })
}
