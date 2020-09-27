import request from '@/utils/request'
import Qs from 'qs'

export function listUserGoodsOrders(query) {
  return request({
    url: '/userGoodsOrders/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function listMainOrder(query) {
  return request({
    url: '/userGoodsOrders/listMainOrder',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}



export function createUserGoodsOrders(data) {
  return request({
    url: '/userGoodsOrders/create',
    method: 'post',
    data
  })
}

export function deleteUserGoodsOrders(data) {
  return request({
    url: '/userGoodsOrders/delete',
    method: 'post',
    data
  })
}

export function editUserGoodsOrders(data) {
  return request({
    url: '/userGoodsOrders/update',
    method: 'post',
    data
  })
}
