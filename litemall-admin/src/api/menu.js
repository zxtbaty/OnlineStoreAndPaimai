import request from '@/utils/request'

export function listMenu(query) {
  return request({
    url: '/menu/list',
    method: 'get',
    params: query
  })
}

export function createMenu(data) {
  return request({
    url: '/menu/create',
    method: 'post',
    data
  })
}

export function readMenu(id) {
  return request({
    url: '/menu/read',
    method: 'get',
    params: { id }
  })
}

export function updateMenu(data) {
  return request({
    url: '/menu/update',
    method: 'post',
    data
  })
}

export function deleteMenu(data) {
  return request({
    url: '/menu/delete',
    method: 'post',
    data
  })
}

export function deleteMenulist(data) {
  return request({
    url: '/menu/deletelist',
    method: 'post',
    data
  })
}


export function menuOptions(query) {
  return request({
    url: '/menu/options',
    method: 'get',
    params: query
  })
}

export function getRoleMenus(query) {
  return request({
    url: '/menu/getmenurole',
    method: 'get',
    params: query
  })
}

export function updateRoleMenu(data) {
  return request({
    url: '/menu/updatemenurole',
    method: 'post',
    data
  })
}

export function getMenuFirstLevel(query) {
  return request({
    url: '/menu/getMenuFirstLevel',
    method: 'get',
    params: query
  })
}

export function getMenuSecondLevel(query) {
  return request({
    url: '/menu/getMenuSecondLevel',
    method: 'get',
    params: query
  })
}

export function batchAddMenu(data) {
  return request({
    url: '/menu/batchAddMenu',
    method: 'post',
    data
  })
}

export function getMenuByPath(query) {
  return request({
    url: '/menu/getMenuByPath',
    method: 'get',
    params: query
  })
}


