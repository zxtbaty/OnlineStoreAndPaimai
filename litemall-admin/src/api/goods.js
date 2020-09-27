import request from '@/utils/request'
import Qs from 'qs'

export function listGoods(query) {
  return request({
    url: '/goods/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, {arrayFormat: 'repeat'})
    }
  })
}

export function deleteGoods(data) {
  return request({
    url: '/goods/delete',
    method: 'post',
    data
  })
}

export function publishGoods(data) {
  return request({
    url: '/goods/create',
    method: 'post',
    data
  })
}

export function detailGoods(id) {
  return request({
    url: '/goods/detail',
    method: 'get',
    params: { id }
  })
}


export function goodsMain(id) {
  return request({
    url: '/goods/goodsmain',
    method: 'get',
    params: { id }
  })
}

export function goodsProductById(id) {
  return request({
    url: '/goods/goodsProductById',
    method: 'get',
    params: { id }
  })
}

export function getGoodsProductViewById(id) {
  return request({
    url: '/goods/goodsProductViewById',
    method: 'get',
    params: { id }
  })
}

export function listGoodsByIdList(query) {
  return request({
    url: '/goods/listGoodsByIdList',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
    return Qs.stringify(params, { arrayFormat: 'repeat' })
   }
  })
}

export function listProductByIdList(query) {
  return request({
    url: '/goods/listProductByIdList',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}



export function editGoods(data) {
  return request({
    url: '/goods/update',
    method: 'post',
    data
  })
}

export function listCatAndBrand(comId) {
  return request({
    url: '/goods/catAndBrand',
    method: 'get',
    params: { comId }
  })
}

export function getGoodsProduct(id) {
  return request({
    url: '/goods/getGoodsProduct',
    method: 'get',
    params: { id }
  })
}

export function listGoodsProduct(query) {
  return request({
      url: '/goods/listGoodsProduct',
      method: 'get',
      params: query,
      paramsSerializer: function (params) {
        return Qs.stringify(params, {arrayFormat: 'repeat'})
      }
    })
}

export function updateGoodsProduct(data) {
  return request({
    url: '/goods/updateStore',
    method: 'post',
    data
  })
}


