import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/views/layout/Layout'

/** note: Submenu only appear when children.length>=1
 *  detail see  https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 **/

/**
 * hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
 *                                if not set alwaysShow, only more than one route under the children
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    perms: ['GET /aaa','POST /bbb']     will visited the page perms (you can set multiple perms)
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
    noCache: true                if true ,the page will no be cached(default is false)
  }
 **/
export const constantRouterMap = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/authredirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/errorPage/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/errorPage/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '拍卖微商城', icon: 'dashboard', noCache: true }
      }
    ]
  }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})

export const asyncRouterMap = [
  {
    path: '/goods',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'goodsManage',
    meta: {
      title: '商品管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'category',
        component: () => import('@/views/mall/category'),
        name: 'category',
        meta: {
          perms: ['GET /admin/category/list', 'POST /admin/category/create', 'GET /admin/category/read', 'POST /admin/category/update', 'POST /admin/category/delete'],
          title: '商品类目',
          noCache: true
        }
      },
      {
        path: 'brand',
        component: () => import('@/views/mall/brand'),
        name: 'brand',
        meta: {
          perms: ['GET /admin/brand/list', 'POST /admin/brand/create', 'GET /admin/brand/read', 'POST /admin/brand/update', 'POST /admin/brand/delete'],
          title: '专栏/品牌',
          noCache: true
        }
      },
      {
        path: 'list',
        component: () => import('@/views/goods/list'),
        name: 'goodsList',
        meta: {
          perms: ['GET /admin/goods/list', 'POST /admin/goods/delete'],
          title: '商品列表',
          noCache: true
        }
      },
      {
        path: 'create',
        component: () => import('@/views/goods/create'),
        name: 'goodsCreate',
        meta: {
          perms: ['POST /admin/goods/create'],
          title: '商品上架',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'edit',
        component: () => import('@/views/goods/edit'),
        name: 'goodsEdit',
        meta: {
          perms: ['GET /admin/goods/detail', 'POST /admin/goods/update', 'POST /admin/goods/catAndBrand'],
          title: '商品编辑',
          noCache: true
        },
        hidden: true
      },
      // {
      //   path: 'comment',
      //   component: () => import('@/views/goods/comment'),
      //   name: 'goodsComment',
      //   meta: {
      //     perms: ['GET /admin/comment/list', 'POST /admin/comment/delete'],
      //     title: '商品评论',
      //     noCache: true
      //   }
      // }
    ]
  },

  {
    path: '/mall',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'mallManage',
    meta: {
      title: '线上订单',
      icon: 'chart'
    },
    children: [
      {
        path: 'order',
        component: () => import('@/views/mall/orderlist'),
        name: 'order',
        meta: {
          perms: ['GET /admin/order/list', 'GET /admin/order/detail', 'POST /admin/order/ordership', 'POST /admin/order/orderrefund', 'POST /admin/order/orderreply'],
          title: '线上订单',
          noCache: true
        }
      },
      {
        path: 'issue',
        component: () => import('@/views/mall/issue'),
        name: 'issue',
        meta: {
          perms: ['GET /admin/issue/list', 'POST /admin/issue/create', 'GET /admin/issue/read', 'POST /admin/issue/update', 'POST /admin/issue/delete'],
          title: '通用问题',
          noCache: true
        }
      },
      // {
      //   path: 'keyword',
      //   component: () => import('@/views/mall/keyword'),
      //   name: 'keyword',
      //   meta: {
      //     perms: ['GET /admin/keyword/list', 'POST /admin/keyword/create', 'GET /admin/keyword/read', 'POST /admin/keyword/update', 'POST /admin/keyword/delete'],
      //     title: '关键词',
      //     noCache: true
      //   }
      // },
      {
        path: 'ordermobilesendlog',
          component: () => import('@/views/mall/ordermobilesendlog'),
        name: 'ordermobilesendlog',
        meta: {
        perms: ['GET /admin/order/list', 'GET /admin/order/detail', 'POST /admin/order/ordership', 'POST /admin/order/orderrefund', 'POST /admin/order/orderreply'],
          title: '手机发货',
          noCache: true
      }
      },
    ]
  },

  {
    path: '/user',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'userManage',
    meta: {
      title: '会员管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'user',
        component: () => import('@/views/user/user'),
        name: 'user',
        meta: {
          perms: ['GET /admin/user/list'],
          title: '会员管理',
          noCache: true
        }
      },
      {
        path: 'address',
        component: () => import('@/views/user/address'),
        name: 'address',
        meta: {
          perms: ['GET /admin/address/list'],
          title: '收货地址',
          noCache: true
        }
      },
      // {
      //   path: 'collect',
      //   component: () => import('@/views/user/collect'),
      //   name: 'collect',
      //   meta: {
      //     perms: ['GET /admin/collect/list'],
      //     title: '会员收藏',
      //     noCache: true
      //   }
      // },
      {
        path: 'footprint',
        component: () => import('@/views/user/footprint'),
        name: 'footprint',
        meta: {
          perms: ['GET /admin/footprint/list'],
          title: '会员足迹',
          noCache: true
        }
      },
      {
        path: 'history',
        component: () => import('@/views/user/history'),
        name: 'history',
        meta: {
          perms: ['GET /admin/user/list'],
          title: '搜索历史',
          noCache: true
        }
      },
      {
        path: 'baozhengjin',
        component: () => import('@/views/user/baozhengjin'),
        name: 'baozhengjin',
        meta: {
          perms: ['GET /admin/user/baozhengjin'],
          title: '拍保证金',
          noCache: true
        }
      },
      {
        path: 'chargeMoney',
        component: () => import('@/views/user/chargeMoney'),
        name: 'chargeMoney',
        meta: {
          perms: ['GET /admin/user/chargeMoney'],
          title: '充值记录',
          noCache: true
        }
      },
      // {
      //   path: 'feedback',
      //   component: () => import('@/views/user/feedback'),
      //   name: 'feedback',
      //   meta: {
      //     perms: ['GET /admin/feedback/list'],
      //     title: '意见反馈',
      //     noCache: true
      //   }
      // }
    ]
  },
  {
    path: '/promotion',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'promotionManage',
    meta: {
      title: '营销管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'ad',
        component: () => import('@/views/promotion/ad'),
        name: 'ad',
        meta: {
          perms: ['GET /admin/ad/list', 'POST /admin/ad/create', 'GET /admin/ad/read', 'POST /admin/ad/update', 'POST /admin/ad/delete'],
          title: '首页Banner',
          noCache: true
        }
      },
      {
        path: 'coupon',
        component: () => import('@/views/promotion/coupon'),
        name: 'coupon',
        meta: {
          perms: ['GET /admin/coupon/list', 'POST /admin/coupon/create', 'POST /admin/coupon/update', 'POST /admin/coupon/delete'],
          title: '优惠券管理',
          noCache: true
        }
      },
      {
        path: 'couponDetail',
        component: () => import('@/views/promotion/couponDetail'),
        name: 'couponDetail',
        meta: {
          perms: ['GET /admin/coupon/list', 'GET /admin/coupon/listuser'],
          title: '优惠券详情',
          noCache: true
        },
        hidden: true
      },
      // {
      //   path: 'topic',
      //   component: () => import('@/views/promotion/topic'),
      //   name: 'topic',
      //   meta: {
      //     perms: ['GET /admin/topic/list', 'POST /admin/topic/create', 'GET /admin/topic/read', 'POST /admin/topic/update', 'POST /admin/topic/delete'],
      //     title: '专题管理',
      //     noCache: true
      //   }
      // },
      {
        path: 'groupon-rule',
        component: () => import('@/views/promotion/grouponRule'),
        name: 'grouponRule',
        meta: {
          perms: ['GET /admin/groupon/list', 'POST /admin/groupon/create', 'POST /admin/groupon/update', 'POST /admin/groupon/delete'],
          title: '团购活动规则',
          noCache: true
        }
      },
      {
        path: 'groupon-activity',
        component: () => import('@/views/promotion/grouponActivity'),
        name: 'grouponActivity',
        meta: {
          perms: ['GET /admin/groupon/list'],
          title: '团购活动订单',
          noCache: true
        }
      },
      {
        path: 'secKill-rule',
        component: () => import('@/views/promotion/secKillRule'),
        name: 'secKillRule',
        meta: {
          perms: ['GET /admin/groupon/list', 'POST /admin/groupon/create', 'POST /admin/groupon/update', 'POST /admin/groupon/delete'],
          title: '秒杀活动规则',
          noCache: true
        }
      },
      {
        path: 'SecKill-activity',
        component: () => import('@/views/promotion/secKillActivity'),
        name: 'secKillActivity',
        meta: {
          perms: ['GET /admin/groupon/list'],
          title: '秒杀活动订单',
          noCache: true
        }
      },
    {
      path: 'user-goods-rule',
        component: () => import('@/views/promotion/userGoodsRule'),
      name: 'userGoodsRule',
      meta: {
      perms: ['GET /admin/userGoodsRule/list', 'POST /admin/userGoodsRule/create', 'POST /admin/userGoodsRule/update', 'POST /admin/userGoodsRule/delete'],
        title: '会员专享规则',
        noCache: true
    }
    },
    {
      path: 'user-goods-activity',
        component: () => import('@/views/promotion/userGoodsActivity'),
      name: 'userGoodsActivity',
      meta: {
      perms: ['GET /admin/userGoodsActivity/list'],
        title: '会员专享订单',
        noCache: true
    }
    },
    {
      path: 'goods-rebate-rule',
        component: () => import('@/views/promotion/goodsRebateRule'),
      name: 'goodsRebateRule',
      meta: {
      perms: ['GET /admin/goodsRebateRule/list', 'POST /admin/goodsRebateRule/create', 'POST /admin/goodsRebateRule/update', 'POST /admin/goodsRebateRule/delete'],
        title: '品项折扣规则',
        noCache: true
    }
    },
      {
        path: 'goods-rebate-order',
        component: () => import('@/views/promotion/goodsRebateOrder'),
        name: 'goodsRebateOrder',
        meta: {
          perms: ['GET /admin/goodsRebateOrder/list', 'POST /admin/goodsRebateOrder/create', 'POST /admin/goodsRebateOrder/update', 'POST /admin/goodsRebateOrder/delete'],
          title: '品项折扣订单',
          noCache: true
        }
      },
    ]
  },
  {
    path: '/paimai',
      component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'paimaiManage',
    meta: {
    title: '拍卖管理',
      icon: 'chart'
    },
    children: [
     {
       path: 'zhuanchang-paimai-rule',
       component: () => import('@/views/paimai/zhuanchangPaimaiRule'),
        name: 'zhuanchangPaimaiRule',
      meta: {
      perms: ['GET /admin/zhuanchangPaimaiRule/list', 'POST /admin/zhuanchangPaimaiRule/create', 'POST /admin/zhuanchangPaimaiRule/update', 'POST /admin/zhuanchangPaimaiRule/delete'],
        title: '专场拍规则',
        noCache: true
    }
    },
      {
        path: 'zhuanchang-paimai-order',
          component: () => import('@/views/paimai/zhuanchangPaimaiOrder'),
        name: 'zhuanchangPaimaiOrder',
        meta: {
        perms: ['GET /admin/zhuanchangPaimaiOrder/list'],
          title: '专场拍订单',
          noCache: true
      }
      },
      {
        path: 'dajia-paimai-rule',
          component: () => import('@/views/paimai/dajiaPaimaiRule'),
        name: 'dajiaPaimaiRule',
        meta: {
        perms: ['GET /admin/dajiaPaimaiRule/list', 'POST /admin/dajiaPaimaiRule/create', 'POST /admin/dajiaPaimaiRule/update', 'POST /admin/dajiaPaimaiRule/delete'],
          title: '大家拍设置',
          noCache: true
      }
      },
      {
        path: 'dajia-paimai-order',
          component: () => import('@/views/paimai/dajiaPaimaiOrder'),
        name: 'dajiaPaimaiOrder',
        meta: {
        perms: ['GET /admin/dajiaPaimaiOrder/list'],
          title: '大家拍订单',
          noCache: true
      }
      },
      {
        path: 'private-make-rule',
          component: () => import('@/views/paimai/privateMakeRule'),
        name: 'privateMakeRule',
        meta: {
        perms: ['GET /admin/privateMakeRule/list', 'POST /admin/privateMakeRule/create', 'POST /admin/privateMakeRule/update', 'POST /admin/privateMakeRule/delete'],
          title: '私人定制规则',
          noCache: true
      }
      },
      {
        path: 'private-make-order',
          component: () => import('@/views/paimai/privateMakeOrder'),
        name: 'privateMakeOrder',
        meta: {
        perms: ['GET /admin/privateMakeOrder/list'],
          title: '私人定制订单',
          noCache: true
      }
      },
     ]
  },
  {
    path: '/config',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'configManage',
    meta: {
      title: '商城设置',
      icon: 'chart'
    },
    children: [
      {
        path: 'mall',
        component: () => import('@/views/config/mall'),
        name: 'configMall',
        meta: {
          perms: ['GET /admin/config/mall', 'POST /admin/config/mall'],
          title: '基本信息',
          noCache: true
        }
      },
      // {
      //   path: 'ModelManage',
      //     component: () => import('@/views/config/template'),
      //   name: 'template',
      //   meta: {
      //   title: '商城模板管理',
      //     noCache: true
      //   }
      // },
      // {
      //   path: 'PeisongManage',
      //     component: () => import('@/views/config/peisong'),
      //   name: 'baseInfo',
      //   meta: {
      //   title: '配送方式管理',
      //     noCache: true
      //   }
      // },
      {
        path: 'express',
        component: () => import('@/views/config/express'),
        name: 'configExpress',
        meta: {
          perms: ['GET /admin/config/express', 'POST /admin/config/express'],
          title: '运费配置',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/config/order'),
        name: 'configOrder',
        meta: {
          perms: ['GET /admin/config/order', 'POST /admin/config/order'],
          title: '订单配置',
          noCache: true
        }
      },
      {
        path: 'region',
        component: () => import('@/views/mall/region'),
        name: 'region',
        meta: {
          title: '行政区域',
          noCache: true
        }
      },
      // {
      //   path: 'wx',
      //     component: () => import('@/views/config/wx'),
      //   name: 'configWx',
      //   meta: {
      //   perms: ['GET /admin/config/wx', 'POST /admin/config/wx'],
      //     title: '小程序配置',
      //     noCache: true
      //   }
      // },
      {
        path: 'weishang',
        component: () => import('@/views/config/weishang'),
        name: 'configWeiShang',
        meta: {
          perms: ['GET /admin/config/weishang', 'POST /admin/config/weishang'],
          title: '首页配置',
          noCache: true
        }
      },
      {
        path: 'company',
        component: () => import('@/views/config/company'),
        name: 'configCompany',
        meta: {
          perms: ['GET /admin/company/list', 'POST /admin/company/create', 'GET /admin/company/read', 'POST /admin/company/update', 'POST /admin/company/delete'],
          title: '公司信息',
          noCache: true
        }
      },
    {
      path: 'authororcorp',
        component: () => import('@/views/config/authororcorp'),
      name: 'configAuthororcorp',
      meta: {
      perms: ['GET /admin/authororcorp/list', 'POST /admin/authororcorp/create', 'GET /admin/authororcorp/read', 'POST /admin/authororcorp/update', 'POST /admin/authororcorp/delete'],
        title: '出品人信息',
        noCache: true
    }
    },
      {
        path: 'store',
        component: () => import('@/views/config/store'),
        name: 'configStore',
        meta: {
          perms: ['GET /admin/store/list', 'POST /admin/store/create', 'GET /admin/store/read', 'POST /admin/store/update', 'POST /admin/store/delete'],
          title: '店铺信息',
          noCache: true
        }
      },
    {
      path: 'picksite',
        component: () => import('@/views/config/picksite'),
      name: 'configPickSite',
      meta: {
      perms: ['GET /admin/picksite/list', 'POST /admin/picksite/create', 'GET /admin/picksite/read', 'POST /admin/picksite/update', 'POST /admin/picksite/delete'],
        title: '自提货点',
        noCache: true
    }
    },
    {
      path: 'homebackgroundimage',
        component: () => import('@/views/config/homebackgroundimage'),
      name: 'configHomeBackgroundImage',
      meta: {
      perms: ['GET /admin/homebackgroundimage/list', 'POST /admin/homebackgroundimage/create', 'GET /admin/homebackgroundimage/read', 'POST /admin/homebackgroundimage/update', 'POST /admin/homebackgroundimage/delete'],
        title: '首页背景',
        noCache: true
    }
    },
      {
        path: 'homeicon',
        component: () => import('@/views/config/homeicon'),
        name: 'configHomeIcon',
        meta: {
          perms: ['GET /admin/homeicon/list', 'POST /admin/homeicon/create', 'GET /admin/homeicon/read', 'POST /admin/homeicon/update', 'POST /admin/homeicon/delete'],
          title: '首页导航',
          noCache: true
        }
      },
      {
        path: 'recommend',
        component: () => import('@/views/config/recommend'),
        name: 'ConfigRecommend',
        meta: {
          perms: ['GET /admin/recommend/list', 'POST /admin/recommend/create', 'POST /admin/recommend/update', 'POST /admin/recommend/delete'],
          title: '推荐排序',
          noCache: true
        }
      },
      {
        path: 'hudongset',
          component: () => import('@/views/config/huodongset'),
        name: 'ConfigHuodongSet',
        meta: {
        perms: ['GET /admin/huodongset/list', 'POST /admin/huodongset/create', 'POST /admin/huodongset/update', 'POST /admin/huodongset/delete'],
          title: '活动设置',
          noCache: true
      }
      },
      // {
      //   path: 'linkother',
      //   component: () => import('@/views/config/linkother'),
      //   name: 'linkother',
      //   meta: {
      //     perms: ['GET /admin/issue/list', 'POST /admin/issue/create', 'GET /admin/issue/read', 'POST /admin/issue/update', 'POST /admin/issue/delete'],
      //     title: '合作链接',
      //     noCache: true
      //   }
      // }
    ]
  },
{
  path: '/info',
    component: Layout,
  redirect: 'noredirect',
  alwaysShow: true,
  name: 'infoManage',
  meta: {
  title: '消息管理',
    icon: 'chart'
},
  children: [

  {
    path: 'userInfoDef',
      component: () => import('@/views/info/userinfodef'),
    name: 'userInfoDef',
    meta: {
      perms: ['GET /userinfodef/list', 'POST /userinfodef/create', 'POST /userinfodef/update', 'POST /userinfodef/delete'],
        title: '前端消息定义',
        noCache: true
    }
  },
  // {
  //   path: 'userOrderInfo',
  //     component: () => import('@/views/info/userorderinfo'),
  //   name: 'userOrderInfo',
  //   meta: {
  //     perms: ['GET /userorderinfo/list'],
  //       title: '用户订单消息',
  //       noCache: true
  //    }
  // },
  {
    path: 'opadminDef',
      component: () => import('@/views/info/opadmindef'),
    name: 'opadminDef',
    meta: {
    perms: ['GET /opadmindef/list', 'POST /opadmindef/create', 'POST /opadmindef/update', 'POST /opadmindef/delete'],
      title: '后端消息定义',
      noCache: true
  }
  },
  {
    path: 'opAdminInfo',
      component: () => import('@/views/info/opadmininfo'),
    name: 'opAdminInfo',
    meta: {
    perms: ['GET /opadmininfo/list'],
      title: '后端消息列表',
      noCache: true
  }
  },
  {
    path: 'interfaceMonitor',
      component: () => import('@/views/info/interfacemonitor'),
    name: 'interfaceMonitor',
    meta: {
    perms: ['GET /interfacemonitor/list'],
      title: '接口消息监控',
      noCache: true
  }
  },
]
},

  {
    path: '/article',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'articleManage',
    meta: {
      title: '文章管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'articleClassList',
        component: () => import('@/views/article/articleClassList'),
        name: 'articleClassList',
        meta: {
          perms: ['GET /article/articleClassList/list', 'POST /article/articleClassList/create', 'POST /article/articleClassList/update', 'POST /article/articleClassList/delete'],
          title: '文章分类',
          noCache: true
        }
      },
      {
        path: 'articleList',
        component: () => import('@/views/article/articleList'),
        name: 'articleList',
        meta: {
          perms: ['GET /article/articleList/list', 'POST /article/articleList/create', 'POST /article/articleList/update', 'POST /article/articleList/delete'],
          title: '文章列表',
          noCache: true
        }
      },
      {
        path: 'wenBoGuanArticleList',
        component: () => import('@/views/article/wenBoGuanArticleList'),
        name: 'wenBoGuanArticleList',
        meta: {
          perms: ['GET /article/articleList/list', 'POST /article/articleList/create', 'POST /article/articleList/update', 'POST /article/articleList/delete'],
          title: '文博馆文',
          noCache: true
        }
      }
    ]
  },

  // {
  //   path: '/fenxiao',
  //     component: Layout,
  //   redirect: 'noredirect',
  //   alwaysShow: true,
  //   name: 'fenxiaoManage',
  //   meta: {
  //   title: '分销管理',
  //     icon: 'chart'
  //   },
  //   children: [
  //     {
  //       path: 'tichengSet',
  //       component: () => import('@/views/fenxiao/tichengSet'),
  //       name: 'tichengSet',
  //       meta: {
  //       perms: ['GET /fenxiao/articleClassList/list', 'POST /fenxiao/articleClassList/create', 'POST /fenxiao/articleClassList/update', 'POST /fenxiao/articleClassList/delete'],
  //         title: '提成设置',
  //         noCache: true
  //       }
  //     },
  //     {
  //       path: 'tichengList',
  //         component: () => import('@/views/fenxiao/tichengList'),
  //       name: 'tichengList',
  //       meta: {
  //       perms: ['GET /fenxiao/tichengList/list', 'POST /fenxiao/tichengList/create', 'POST /fenxiao/tichengList/update', 'POST /fenxiao/tichengList/delete'],
  //         title: '分销提成',
  //         noCache: true
  //       }
  //     },
  //   //   {
  //   //   path: 'tixianSet',
  //   //     component: () => import('@/views/fenxiao/tixianSet'),
  //   //     name: 'tixianSet',
  //   //     meta: {
  //   //     perms: ['GET /fenxiao/tixianSet/list', 'POST /fenxiao/tixianSet/create', 'POST /fenxiao/tixianSet/update', 'POST /fenxiao/tixianSet/delete'],
  //   //       title: '提现设置',
  //   //       noCache: true
  //   //   }
  //   // },
  //     {
  //       path: 'tixianList',
  //         component: () => import('@/views/fenxiao/tixianList'),
  //       name: 'tixianList',
  //       meta: {
  //       perms: ['GET /fenxiao/tixianList/list', 'POST /fenxiao/tixianList/create', 'POST /fenxiao/tixianList/update', 'POST /fenxiao/tixianList/delete'],
  //         title: '提现申请',
  //         noCache: true
  //       }
  //     }
  //   ]
  // },

  {
    path: '/stat',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'statManage',
    meta: {
      title: '统计报表',
      icon: 'chart'
    },
    children: [
      {
        path: 'zongHe',
        component: () => import('@/views/stat/zongHe'),
        name: 'zongHe',
        meta: {
          perms: ['GET /admin/stat/zongHe'],
          title: '综合统计',
          noCache: true
        }
      },
      {
        path: 'browser',
        component: () => import('@/views/stat/browser'),
        name: 'browser',
        meta: {
          perms: ['GET /admin/stat/browser'],
          title: '访问统计',
          noCache: true
        }
      },
      // {
      //   path: 'purchase',
      //   component: () => import('@/views/stat/purchase'),
      //   name: 'purchase',
      //   meta: {
      //     perms: ['GET /admin/stat/purchase'],
      //     title: '购买统计',
      //     noCache: true
      //   }
      // },
      // {
      //   path: 'share',
      //   component: () => import('@/views/stat/share'),
      //   name: 'share',
      //   meta: {
      //     perms: ['GET /admin/stat/share'],
      //     title: '分享统计',
      //     noCache: true
      //   }
      // },
      {
        path: 'user',
        component: () => import('@/views/stat/user'),
        name: 'statUser',
        meta: {
          perms: ['GET /admin/stat/user'],
          title: '用户统计',
          noCache: true
        }
      },
      {
        path: 'order',
        component: () => import('@/views/stat/order'),
        name: 'statOrder',
        meta: {
          perms: ['GET /admin/stat/order'],
          title: '订单统计',
          noCache: true
        }
      },
      {
        path: 'goods',
        component: () => import('@/views/stat/goods'),
        name: 'statGoods',
        meta: {
          perms: ['GET /admin/stat/goods'],
          title: '商品统计',
          noCache: true
        }
      },
      {
        path: 'storewarning',
          component: () => import('@/views/stat/storewarning'),
        name: 'storeWarning',
        meta: {
        perms: ['GET /admin/stat/storewarning'],
          title: '库存预警',
          noCache: true
      }
      }
    ]
  },

  {
    path: '/sys',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'sysManage',
    meta: {
      title: '系统管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'dicmain/list',
        component: () => import('@/views/sys/dicmain'),
        name: 'dicmainList',
        meta: {
          perms: ['GET /admin/dicmain/list', 'POST /admin/dicmain/create', 'POST /admin/dicmain/update', 'POST /admin/dicmain/delete'],
          title: '字典列表',
          noCache: true
        }
      },
      {
        path: 'dicmain/create',
        component: () => import('@/views/sys/editdicmain'),
        name: 'dicmainCreate',
        meta: {
          perms: ['POST /admin/dicmain/create'],
          title: '新增字典',
          noCache: true
        },
        hidden: true
      },
      {
        path: 'dicmain/edit',
        component: () => import('@/views/sys/editdicmain'),
        name: 'dicmainEdit',
        meta: {
          perms: ['GET /admin/dicmain/detail', 'POST /admin/dicmain/update'],
          title: '字典编辑',
          noCache: true
        },
        hidden: true
      },
    {
      path: 'menu',
        component: () => import('@/views/sys/menu'),
      name: 'menu',
      meta: {
      perms: ['GET /admin/menu/list', 'POST /admin/menu/create', 'POST /admin/menu/update', 'POST /admin/menu/delete', 'GET /admin/menu/permissions', 'POST /admin/menu/permissions'],
        title: '菜单管理',
        noCache: true
    }
    },
    {
      path: 'role',
        component: () => import('@/views/sys/role'),
      name: 'role',
      meta: {
      perms: ['GET /admin/role/list', 'POST /admin/role/create', 'POST /admin/role/update', 'POST /admin/role/delete', 'GET /admin/role/permissions', 'POST /admin/role/permissions'],
        title: '角色管理',
        noCache: true
    }
    },
      {
        path: 'admin',
        component: () => import('@/views/sys/admin'),
        name: 'admin',
        meta: {
          perms: ['GET /admin/admin/list', 'POST /admin/admin/create', 'POST /admin/admin/update', 'POST /admin/admin/delete'],
          title: '管理员',
          noCache: true
        }
      },


    {
      path: 'log',
        component: () => import('@/views/sys/log'),
      name: 'log',
      meta: {
      perms: ['GET /admin/log/list'],
        title: '操作日志',
        noCache: true
    }
    },
      {
        path: 'os',
        component: () => import('@/views/sys/os'),
        name: 'os',
        meta: {
          perms: ['GET /admin/os/list', 'POST /admin/os/create', 'POST /admin/os/update', 'POST /admin/os/delete'],
          title: '对象存储',
          noCache: true
        }
      }
    ]
  },

  {
    path: '/maintain',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    name: 'maintainManage',
    meta: {
      title: '运维管理',
      icon: 'chart'
    },
    children: [
      {
        path: 'maintain/sqlExecute',
        component: () => import('@/views/maintain/sqlExecute'),
        name: 'sqlExecute',
        meta: {
          perms: ['GET /admin/commonDB/exec', 'POST /admin/commonDB/insert', 'POST /admin/commonDB/select', 'POST /admin/commonDB/selectPage'],
          title: '执行语句',
          noCache: true
        }
      },
      {
        path: 'maintain/backLog',
        component: () => import('@/views/maintain/backLog'),
        name: 'backLog',
        meta: {
          perms: ['POST /admin/commonLog/getLog','POST /admin/commonLog/downLoadLog'],
          title: '查询日志',
          noCache: true
        },
      },
      {
        path: 'maintain/versionUpdate',
        component: () => import('@/views/maintain/versionUpdate'),
        name: 'versionUpdate',
        meta: {
          perms: ['GET /admin/dicmain/detail', 'POST /admin/dicmain/update'],
          title: '后台发布',
          noCache: true
        },
      },
      {
        path: 'maintain/frontVersionUpdate',
        component: () => import('@/views/maintain/frontVersionUpdate'),
        name: 'frontVersionUpdate',
        meta: {
          perms: ['GET /admin/dicmain/detail', 'POST /admin/dicmain/update'],
          title: '前端发布',
          noCache: true
        },
      },
      // {
      //   path: 'maintain/dbBackup',
      //   component: () => import('@/views/maintain/dbBackup'),
      //   name: 'dbBackup',
      //   meta: {
      //     perms: ['GET /admin/dicmain/detail', 'POST /admin/dicmain/update'],
      //     title: '库表备份',
      //     noCache: true
      //   },
      // }
    ]
  },


  // {
  //   path: 'external-link',
  //   component: Layout,
  //   redirect: 'noredirect',
  //   alwaysShow: true,
  //   name: 'externalLink',
  //   meta: {
  //     title: '外链',
  //     icon: 'link'
  //   },
  //   children: [
  //     {
  //       path: 'https://cloud.tencent.com/product/cos',
  //       meta: { title: '腾讯云存储', icon: 'link' }
  //     },
  //     {
  //       path: 'https://cloud.tencent.com/product/sms',
  //       meta: { title: '腾讯云短信', icon: 'link' }
  //     },
  //     {
  //       path: 'https://pay.weixin.qq.com/index.php/core/home/login',
  //       meta: { title: '微信支付', icon: 'link' }
  //     },
  //     {
  //       path: 'https://mpkf.weixin.qq.com/',
  //       meta: { title: '小程序客服', icon: 'link' }
  //     },
  //     {
  //       path: 'https://www.alibabacloud.com/zh/product/oss',
  //       meta: { title: '阿里云存储', icon: 'link' }
  //     },
  //     {
  //       path: 'https://www.qiniu.com/products/kodo',
  //       meta: { title: '七牛云存储', icon: 'link' }
  //     },
  //     {
  //       path: 'http://www.kdniao.com/api-track',
  //       meta: { title: '快递鸟', icon: 'link' }
  //     }
  //   ]
  // },

  {
    path: '/profile',
    component: Layout,
    redirect: 'noredirect',
    alwaysShow: true,
    children: [
      {
        path: 'password',
        component: () => import('@/views/profile/password'),
        name: 'password',
        meta: { title: '修改密码', noCache: true }
      }
    ],
    hidden: true
  },

  { path: '*', redirect: '/404', hidden: true }
]
