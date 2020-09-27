import request from '@/utils/request'

export function listArticleList(query) {
  return request(
    {
      url: 'wenBoGuanArticlelist/list',
      method: 'get',
      params: query
    }
  )
}

export function listRecommendArticleList(query) {
  return request(
    {
      url: 'wenBoGuanArticlelist/listRecommend',
      method: 'get',
      params: query
    }
  )
}

export function listUnRecommendArticleList(query) {
  return request(
    {
      url: 'wenBoGuanArticlelist/listUnRecommendArticleList',
      method: 'get',
      params: query
    }
  )
}

export function createArticleList(data) {
  return request({
    url: '/wenBoGuanArticlelist/create',
    method: 'post',
    data
  })
}

export function addRecommendArticle(data) {
  return request({
    url: '/wenBoGuanArticlelist/addRecommendArticle',
    method: 'post',
    data
  })
}


export function readArticleList(id) {
  return request(
    {
      url: '/wenBoGuanArticlelist/read',
      method: 'get',
      params: { id }
    }
  )
}

export function updateArticleList(data) {
  return request({
    url: '/wenBoGuanArticlelist/update',
    method: 'post',
    data
  })
}

export function deleteArticleList(data) {
  return request({
    url: '/wenBoGuanArticlelist/delete',
    method: 'post',
    data
  })
}

export function updateRecommend(data) {
  return request({
    url: '/wenBoGuanArticlelist/updateRecommend',
    method: 'post',
    data
  })
}

export function updateRecommendBatch(data) {
  return request({
    url: '/wenBoGuanArticlelist/updateRecommendBatch',
    method: 'post',
    data
  })
}


export function deleteRecommend(data) {
  return request({
    url: '/wenBoGuanArticlelist/deleteRecommend',
    method: 'post',
    data
  })
}
