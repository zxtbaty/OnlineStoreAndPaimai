import { loginByUsername, logout, getUserInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'

const user = {
  state: {
    user: '',
    status: '',
    code: '',
    ifModifyOnSale:false,
    token: getToken(),
    name: '',
    userid:'',
    avatar: '',
    introduction: '',
    roles: [],
    perms: [],
    menus: [],
    // fistLevelMenus:[],
    secondmenus:[],
    setting: {
      articlePlatform: []
    }
  },

  mutations: {
    SET_CODE: (state, code) => {
      state.code = code
    },
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_INTRODUCTION: (state, introduction) => {
      state.introduction = introduction
    },
    SET_SETTING: (state, setting) => {
      state.setting = setting
    },
    SET_STATUS: (state, status) => {
      state.status = status
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_USERID: (state, userid) => {
      state.userid = userid
    },
    SET_IF_MODIFY_ON_SALE: (state, ifModifyOnSale) => {
      state.ifModifyOnSale = ifModifyOnSale
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMS: (state, perms) => {
      state.perms = perms
    },
    SET_MENUS: (state, menus) => {
      state.menus = menus
    },
    SET_SECONDMENUS: (state, secondmenus) => {
      state.secondmenus = secondmenus
    }
  },

  actions: {
    // 用户名登录
    LoginByUsername({ commit }, userInfo) {
      const username = userInfo.username.trim()
      return new Promise((resolve, reject) => {
        loginByUsername(username, userInfo.password).then(response => {
          const token = response.data.data
          commit('SET_TOKEN', token)
          setToken(token)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetUserInfo({ commit, state }) {
      return new Promise((resolve, reject) => {
        getUserInfo(state.token).then(response => {
          const data = response.data.data

          if (data.perms && data.perms.length > 0) { // 验证返回的perms是否是一个非空数组
            commit('SET_PERMS', data.perms)
          } else {
            reject('getInfo: perms must be a non-null array !')
          }

          commit('SET_ROLES', data.roles)
          commit('SET_USERID', data.userid)
          commit('SET_NAME', data.name)
          commit('SET_IF_MODIFY_ON_SALE', data.ifModifyOnSale)
          commit('SET_AVATAR', data.avatar)
          commit('SET_INTRODUCTION', data.introduction)
          commit('SET_MENUS', data.menus)
          commit('SET_SECONDMENUS', data.secondmenus)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 第三方验证登录
    // LoginByThirdparty({ commit, state }, code) {
    //   return new Promise((resolve, reject) => {
    //     commit('SET_CODE', code)
    //     loginByThirdparty(state.status, state.email, state.code).then(response => {
    //       commit('SET_TOKEN', response.data.token)
    //       setToken(response.data.token)
    //       resolve()
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // },

    // 登出
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMS', [])
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    },

    // 动态修改权限
    ChangeRoles({ commit, dispatch }, role) {
      return new Promise(resolve => {
        commit('SET_TOKEN', role)
        setToken(role)
        getUserInfo(role).then(response => {
          const data = response.data
          commit('SET_ROLES', data.roles)
          commit('SET_PERMS', data.perms)
          commit('SET_MENUS', data.menus)
          commit('SET_SECONDMENUS', data.secondmenus)
          commit('SET_NAME', data.name)
          commit('SET_IF_MODIFY_ON_SALE', data.ifModifyOnSale)
          commit('SET_AVATAR', data.avatar)
          commit('SET_INTRODUCTION', data.introduction)
          dispatch('GenerateRoutes', data) // 动态修改权限后 重绘侧边菜单
          resolve()
        })
      })
    }
  }
}

export default user
