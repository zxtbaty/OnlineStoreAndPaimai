import request from '@/utils/requestNoAuto'
import Qs from 'qs'

export function listVersion(query) {
  return request({
    url: '/updateVersion/list',
    method: 'get',
    params: query
  })
}

export function createVersion(data) {
  return request({
    url: '/updateVersion/create',
    method: 'post',
    data
  })
}

export function deleteVersion(query) {
  return request({
    url: '/updateVersion/delete',
    method: 'get',
    params: query
  })
}

export function execVersion(query) {
  return request({
    url: '/updateVersion/exec',
    method: 'get',
    params: query
  })
}



export function downLoadVersion(query) {
  return request({
    url: '/updateVersion/downLoad',
    method: 'get',
    params: query
  })
}


export function shellCommand(query) {
  return request({
    url: '/updateVersion/shellCommand',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}
