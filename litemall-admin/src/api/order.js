import request from '@/utils/requestLongTime'
import Qs from 'qs'

export function listOrder(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function listBySql(query) {
  return request({
    url: '/order/listBySql',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function listOrderDetail(query) {
  return request({
    url: '/order/listDetail',
    method: 'get',
    params: query,
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function detailOrder(id) {
  return request({
    url: '/order/detail',
    method: 'get',
    params: { id }
  })
}

export function beiHuoOrder(data) {
  return request({
    url: '/order/beihuo',
    method: 'post',
    data
  })
}

export function beiHuoBatchConfirmOrder(orderIds) {
  return request({
    url: '/order/beihuoBatchConfirm',
    method: 'get',
    params: orderIds ,
    paramsSerializer: function(params) {
    return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}

export function yuYueBeiHuoOrder(data) {
  return request({
    url: '/order/yuYueBeiHuo',
    method: 'post',
    data
  })
}


export function shipOrder(data) {
  return request({
    url: '/order/ship',
    method: 'post',
    data
  })
}


export function notRefundOrder(query) {
  return request({
    url: '/order/notAllowRefund',
    method: 'get',
    params: query
  })
}

export function refundOrder(data) {
  return request({
    url: '/order/refund',
    method: 'post',
    data
  })
}

export function refundCharge(query) {
  return request({
    url: '/order/refundCharge',
    method: 'Get',
    params: query
  })
}

export function setRefundStatus(data) {
  return request({
    url: '/order/setRefundStatus',
    method: 'post',
    data
  })
}

export function updateFapiaoStatus(data) {
  return request({
    url: '/order/updateFapiaoStatus',
    method: 'post',
    data
  })
}

export function getFapiaoDetail(orderId) {
  return request({
    url: '/order/getFapiaoDetail',
    method: 'get',
    params: { orderId }
  })
}


export function replyComment(data) {
  return request({
    url: '/order/reply',
    method: 'post',
    data
  })
}

// 打印外借单,输出到EXCEL

export function printWaiJie(orderIds) {
  return request({
    url: '/order/excel',
    method: 'get',
    params: orderIds ,
    responseType: 'blob',
    paramsSerializer: function(params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' })
    }
  })
}


// 打印出货单

export function printOutPdf(orderIds) {
  return request({
    url: '/order/excel',
    method: 'get',
    params: orderIds
  })
}

//获取预约的数量
export function getYuyueCountInfo(query) {
  return request({
    url: '/order/getYuyueCountInfo',
    method: 'get',
    params: query
  })
}
//获取电商单备货的数量
export function getBeihuoCountInfo(query){
  return request({
    url: '/order/getBeihuoCountInfo',
    method: 'get',
    params: query
  })
}

//获取电商单快递备货的数量
export function getBeihuoCountInfo_KuaiDi(query){
  return request({
    url: '/order/getBeihuoCountInfoKuaiDi',
    method: 'get',
    params: query
  })
}

//获取电商单航站楼备货的数量
export function getBeihuoCountInfo_HangZhanLou(query){
  return request({
    url: '/order/getBeihuoCountInfoHangZhanLou',
    method: 'get',
    params: query
  })
}

//获取电商单退款单的数量
export function getTuiKuanCountInfo(query){
  return request({
    url: '/order/getTuiKuanCountInfo',
    method: 'get',
    params: query
  })
}

//获取电商单待开票的单据数量
export function getKaipiaoCountInfo(query){
  return request({
    url: '/order/getKaipiaoCountInfo',
    method: 'get',
    params: query
  })
}

/**
 * 单个订单是否和支付平台一致
 * @param query
 */
export function orderIsPayCheck(query){
  return request({
    url: '/order/orderispay',
    method: 'get',
    params: query
  })
}

/**
 * 批量进行验证
 * @param query
 */
export function orderAllArePayCheck(){
  return request({
    url: '/order/orderlisttest',
    method: 'get',
    params: ''
  })
}

export function updateOrderDealRemark(query){
  return request({
    url: '/order/updateOrderDealRemark',
    method: 'get',
    params: query
  })
}

export function createPaiMaiOrder(data){
  return request({
    url: '/order/createPaiMaiOrder',
    method: 'Post',
    data
  })
}


