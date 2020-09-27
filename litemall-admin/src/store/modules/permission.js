import { asyncRouterMap, constantRouterMap } from '@/router'
import {mapGetters, mapActions, mapMutations} from 'vuex'
import { getMenuSecondLevel} from '@/api/menu'

/**
 * 通过meta.perms判断是否与当前用户权限匹配
 * @param perms
 * @param route
 */
function hasPermission(perms, route) {
  if (route.meta && route.meta.perms) {
    return perms.some(perm => route.meta.perms.includes(perm))
  } else {
    return true
  }
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param routes asyncRouterMap
 * @param perms
 */
function filterAsyncRouter(routes, perms) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (tmp.children) {
      tmp.children = filterAsyncRouter(tmp.children, perms)
      if (tmp.children && tmp.children.length > 0) {
        res.push(tmp)
      }
    } else {
      if (hasPermission(perms, tmp)) {
        res.push(tmp)
      }
    }
  })

  return res
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        const { perms } = data
        let accessedRouters
        let newAccessedRouters=[]
        let subAccessedRouters=[]
        let menus= this.getters.menus;
        let secondmenus=this.getters.secondmenus;

        if (perms.includes('*')) {
          accessedRouters = asyncRouterMap
        } else {
          accessedRouters = filterAsyncRouter(asyncRouterMap, perms)
        }
        if(menus!=null&&menus.length>0){
          for(var itemMenu of menus)
          {
            for(var itemRoute of accessedRouters)
            {
                if(itemRoute.name==itemMenu)
                {

                  //判断该用户所属角色是否有二级目录权限
                      let subChildren=itemRoute.children;
                      subAccessedRouters=[];
                      if(secondmenus!=null&&secondmenus.length>0){
                        for(var subItemMenu of secondmenus)
                        {
                          for(var subItemRoute of subChildren)
                          {
                            if(subItemRoute.name==subItemMenu.menuCode)
                            {
                              subAccessedRouters.push(subItemRoute);
                            }
                          }
                        }
                        itemRoute.children=[]
                        if(subAccessedRouters!=null&&subAccessedRouters.length>0)
                        {
                          itemRoute.children=subAccessedRouters
                        }

                      }
                      newAccessedRouters.push(itemRoute)

                }
            }
          }
        } else
        {
          newAccessedRouters=accessedRouters;
        }


        commit('SET_ROUTERS', newAccessedRouters)
        resolve()
      })
    }
  }
}

export default permission
