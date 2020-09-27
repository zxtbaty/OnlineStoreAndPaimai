import request from '@/utils/request'
import Qs from 'qs'

export function listZhuanchangOrder(query) {
  return request({
    url: '/aucitonZhuanchangOrder/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}


