import request from '@/utils/request'
import Qs from 'qs'

export function listGoodsRebateOrder(query) {
  return request({
    url: '/goodsRebateOrder/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function listMainOrder(query) {
  return request({
    url: '/goodsRebateOrder/listMainOrder',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function createGoodsRebateOrder(data) {
  return request({
    url: '/goodsRebateOrder/create',
    method: 'post',
    data
  })
}

export function deleteGoodsRebateOrder(data) {
  return request({
    url: '/goodsRebateOrder/delete',
    method: 'post',
    data
  })
}

export function editGoodsRebateOrder(data) {
  return request({
    url: '/goodsRebateOrder/update',
    method: 'post',
    data
  })
}
