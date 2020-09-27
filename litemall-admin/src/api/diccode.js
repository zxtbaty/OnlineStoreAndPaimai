import request from '@/utils/request'

export function listDiccode(query) {
  return request({
    url: '/diccode/list',
    method: 'get',
    params: query
  })
}

// export function createDiccode(data) {
//   return request({
//     url: '/diccode/create',
//     method: 'post',
//     data
//   })
// }
//
// export function readDiccode(data) {
//   return request({
//     url: '/diccode/read',
//     method: 'get',
//     data
//   })
// }
//
// export function updateDiccode(data) {
//   return request({
//     url: '/diccode/update',
//     method: 'post',
//     data
//   })
// }
//
// export function deleteDiccode(data) {
//   return request({
//     url: '/diccode/delete',
//     method: 'post',
//     data
//   })
// }
