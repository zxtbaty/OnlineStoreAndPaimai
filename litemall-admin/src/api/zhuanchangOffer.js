import request from '@/utils/request'
import Qs from 'qs'

export function listZhuanchangOffer(query) {
  return request({
    url: '/aucitonZhuanchangOffer/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function detailZhuanchangOffer(query) {
  return request({
    url: '/aucitonZhuanchangOffer/getDetail',
    method: 'get',
    params: query
  })
}

