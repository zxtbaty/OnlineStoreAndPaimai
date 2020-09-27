import request from '@/utils/request'

export function listAuthororcorp(query) {
  return request(
    {
      url: 'authororcorp/list',
      method: 'get',
      params: query
    }
  )
}

export function queryAllAuthororcorp(query) {
  return request(
    {
        url: 'authororcorp/queryall',
      method: 'get',
      params: query
    }
  )
}

export function createAuthororcorp(data) {
  return request({
    url: '/authororcorp/create',
    method: 'post',
    data
  })
}

export function readAuthororcorp(id) {
  return request(
    {
      url: '/authororcorp/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateAuthororcorp(data) {
  return request({
    url: '/authororcorp/update',
    method: 'post',
    data
  })
}

export function deleteAuthororcorp(data) {
  return request({
    url: '/authororcorp/delete',
    method: 'post',
    data
  })
}
