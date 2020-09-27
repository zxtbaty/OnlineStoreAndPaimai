import request from '@/utils/request'

export function listCompany(query) {
  return request(
    {
      url: 'company/list',
      method: 'get',
      params: query
    }
  )
}

export function allCompany() {
  return request(
    {
      url: 'company/all',
      method: 'get'
    }
  )
}

export function createCompany(data) {
  return request({
    url: '/company/create',
    method: 'post',
    data
  })
}

export function readCompany(id) {
  return request(
    {
      url: '/company/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateCompany(data) {
  return request({
    url: '/company/update',
    method: 'post',
    data
  })
}

export function deleteCompany(data) {
  return request({
    url: '/company/delete',
    method: 'post',
    data
  })
}

export function listCompanyBrands(id) {
  return request({
    url: '/company/companybrands',
    method: 'get',
    params: { id }
  })
}

export function listHangzhanlou(id) {
  return request({
    url: '/company/hangzhanlou',
    method: 'get',
    params: { id }
  })
}
export function updateCompanyBrand(data) {
  return request({
    url: '/company/updatecompanybrand',
    method: 'post',
    data
  })
}


