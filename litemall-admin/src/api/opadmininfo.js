import request from '@/utils/request'
import Qs from 'qs'

export function listOpadmininfo(query) {
  return request(
    {
      url: 'opadmininfo/list',
      method: 'get',
      params: query
    }
  )
}
export function readOpadmininfo(id) {
  return request(
    {
      url: 'opadmininfo/read',
      method: 'get',
      params: {id}
    }
  )
}
export function countOpadmininfo(query) {
  return request(
    {
      url: 'opadmininfo/count',
      method: 'get',
      params: query
    }
  )
}


export function haveViewOpadmininfo(infoIds) {
  return request(
    {
      url: 'opadmininfo/haveview',
      method: 'get',
      params: infoIds,
      paramsSerializer: function(infoIds) {
        return Qs.stringify(infoIds, { arrayFormat: 'repeat' })
      }
    }
  )
}
