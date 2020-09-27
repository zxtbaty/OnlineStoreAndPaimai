const getters = {
  sidebar: state => state.app.sidebar,
  language: state => state.app.language,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  userId: state => state.user.userid,
  name: state => state.user.name,
  ifModifyOnSale: state => state.user.ifModifyOnSale,
  introduction: state => state.user.introduction,
  status: state => state.user.status,
  roles: state => state.user.roles,
  perms: state => state.user.perms,
  setting: state => state.user.setting,
  menus: state => state.user.menus,
  secondmenus: state => state.user.secondmenus,
  permission_routers: state => state.permission.routers,
  addRouters: state => state.permission.addRouters
}
export default getters
