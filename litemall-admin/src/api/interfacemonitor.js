import request from '@/utils/request'

export function listInterfaceMonitor(query) {
  return request(
    {
      url: 'interfacemonitor/list',
      method: 'get',
      params: query
    }
  )
}
export function readInterfaceMonitor(id) {
  return request(
    {
      url: 'interfacemonitor/read',
      method: 'get',
      params: {id}
    }
  )
}
