import request from '@/utils/requestLongTime'


export function getLogSetting(query) {
  return request({
    url: '/commonLog/getLogSetting',
    method: 'get',
    params: query
  })
}

export function saveLogSetting(data) {
  return request({
    url: '/commonLog/saveLogSetting',
    method: 'post',
    data
  })
}

export function getLog(filePath,startDate,endDate,rows) {
  return request({
    url: '/commonLog/getLog',
    method: 'get',
    params: {filePath:filePath,startDate:startDate,endDate:endDate,rows:rows}
  })
}

export function clearLog() {
  return request({
    url: '/commonLog/clearLog',
    method: 'get',
    params: ''
  })
}

export function downLoadLog(path) {
  return request({
    url: '/commonLog/downLoadLog',
    method: 'get',
    params: {path:path}
  })
}
