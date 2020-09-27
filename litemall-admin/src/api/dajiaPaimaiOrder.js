import request from '@/utils/request'
import Qs from 'qs'

export function listDajiaPaimaiOrder(query) {
  return request({
    url: '/dajiaPaimaiOrder/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}


