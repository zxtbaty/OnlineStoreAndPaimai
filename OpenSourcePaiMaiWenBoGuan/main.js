import Vue from 'vue'
import store from './store'
import App from './App'
import axios from 'axios'

import globalVue from 'global/global'
 
Vue.prototype.global = globalVue
Vue.prototype.$http=axios

// // so easy
// router.beforeEach(function (to, from, next) {
//   // loading 相关
//   // Vue.$vux.confirm.hide()
//   // Vue.$vux.toast.hide()
//   const agent = navigator.userAgent
//   const isiOS = !!agent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/) // ios终端
//   // XXX: 修复iOS版微信HTML5 History兼容性问题
//   if (isiOS && to.path !== location.pathname) {
//     // 此处不可使用location.replace
//     location.assign(to.fullPath)
//   } else {
//     next()
//   }
// })
//  
// router.afterEach(function (to, from) {
// })



// 在发送请求之前做些什么
axios.interceptors.request.use(function (config) {
	
	// var redirectUrl = this.global.website+rurl;
	//rurl = encodeURIComponent(redirectUrl);
    // 在发送请求之前做些什么
	var userInfo = uni.getStorageSync('userInfo');
	// console.log(userInfo)
	if(userInfo==undefined || userInfo==null || userInfo==""){
		//console.log(1)
		// uni.navigateTo({
		// 	url: '/pages/public/tologin?rurl='+rurl
		// })
	}else{
		config.headers.common['X-Litemall-Token']=userInfo.token;
	}
	
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 在响应返回后请求之前做些什么
axios.interceptors.response.use(function (response) { 
    // 用户信息是否超时，重定向到登录页面 
	// var rurl = encodeURIComponent(response.config.url);
	var redirectUrl = window.location.href;
	var rurl = encodeURIComponent(redirectUrl);
	
    if (response.data.errno== 501){
		// var start = redirectUrl.lastIndexOf("http");
		// redirectUrl = redirectUrl.slice(start, redirectUrl.length)
		// var arr = redirectUrl.split("rurl");
		// if(arr.lenth>2){
		// 	console.log(arr)
		// 	for(i=2;i<arr.length;i++){
		// 		redirectUrl = decodeURIComponent(redirectUrl)
		// 			alert(redirectUrl)
		// 	}
		// }
		var userInfo = uni.getStorageSync('userInfo');
		if(userInfo==undefined || userInfo==null || userInfo==""){
			uni.navigateTo({
				url: '/pages/public/tologin?rurl='+rurl
			})
		}else{
			uni.removeStorage({  
			    key: 'userInfo'  
			})
			uni.navigateTo({
				url: '/pages/public/tologin?rurl='+rurl
			})
		}
    }

	//console.log(response)
    return response 
}, function (error) { 

    return Promise.reject(error) 
})

/**
 *  因工具函数属于公司资产, 所以直接在Vue实例挂载几个常用的函数
 *  所有测试用数据均存放于根目录json.js
 *  
 *  css部分使用了App.vue下的全局样式和iconfont图标，有需要图标库的可以留言。
 *  示例使用了uni.scss下的变量, 除变量外已尽量移除特有语法,可直接替换为其他预处理器使用
 */
const msg = (title, duration=2000, mask=false, icon='none')=>{
	//统一提示方便全局修改
	if(Boolean(title) === false){
		return;
	}
	uni.showToast({
		title,
		duration,
		mask,
		icon
	});
}

const msgTop = (title, duration=2000, mask=false, icon='none')=>{
	//统一提示方便全局修改
	if(Boolean(title) === false){
		return;
	}
	uni.showToast({
		title,
		duration,
		mask,
		icon,
		position: "top"
	});
}

const json = type=>{
	//模拟异步请求数据
	return new Promise(resolve=>{
		setTimeout(()=>{
			resolve(Json[type]);
		}, 500)
	})
}
// const cartlists=type=>{
// 	return new Promise(resolve=>{
// 		setTimeout(()=>{
// 			resolve(Cartlist[type]);
// 		},500)
// 	})
// }
const prePage = ()=>{
	let pages = getCurrentPages();
	let prePage = pages[pages.length - 2];
	// #ifdef H5
	return prePage;
	// #endif
	return prePage.$vm;
}


Vue.config.productionTip = false
Vue.prototype.$fire = new Vue();
Vue.prototype.$store = store;
// Vue.prototype.$api = {msg,msgTop, json, prePage,cartlists};
Vue.prototype.$api = {msg,msgTop, json, prePage};
App.mpType = 'app'

const app = new Vue({
    ...App
})
app.$mount()
