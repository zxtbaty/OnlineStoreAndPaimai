import request from '@/utils/request'
import Qs from 'qs'

export function listDajiaPaimaiOffer(query) {
  return request({
    url: '/dajiaPaimaiOffer/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function detailDajiaPaimaiOffer(query) {
  return request({
    url: '/dajiaPaimaiOffer/getDetail',
    method: 'get',
    params: query,
  })
}
