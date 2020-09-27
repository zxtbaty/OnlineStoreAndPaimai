import request from '@/utils/request'
import Qs from 'qs'

export function listPrivateMakeOrder(query) {
  return request({
    url: '/privateMakeOrder/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}


