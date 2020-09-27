<template>
	<view class="container">
		<!-- 小程序头部兼容 -->
		<!-- #ifdef MP -->
		<view class="mp-search-box"><input class="ser-input" type="<text></text>" value="输入关键字搜索" /></view>
		<!-- #endif -->
		
		<uni-page-head  >
			<div class="uni-page-head " style="background-color: #7E0076; color: rgb(0, 0, 0);">
				<div class="uni-page-head-hd">
					<div class="uni-page-head-btn" style="display: none; background-color: rgba(0, 0, 0, 0.5);">
						<i class="uni-btn-icon" style="color: rgb(255, 255, 255); font-size: 27px;"></i>
					</div>
				</div>
			    <div class="uni-page-head-search" style="border-radius: 16px; background-color: rgba(251, 251, 251, 0.6);">
				   <div class="uni-page-head-search-placeholder uni-page-head-search-placeholder-left" style="color: rgb(251, 251, 251);">
				   </div>
				   <uni-input class="uni-page-head-search-input" style="color: rgb(0, 0, 0);">
						<div class="uni-input-wrapper">
							<div class="uni-input-placeholder" style="color: rgb(251, 251, 251);"></div>
							<form action="" class="uni-input-form">
							   <input maxlength="140" step="" v-model="txtSearch" placeholder="搜索商品" autocomplete="off" @confirm="navToSearch" type="search" class="uni-input-input">
							</form>
						</div>
					</uni-input>
				</div>
				<div class="uni-page-head-ft">
				   <view class="set-box">	  			
					<view class="icon"   @click="navTo('/pages/notice/notice')">
						<min-badge :count="userInfoCount" :maxCount="99">
						<image src="/static/newimg/message.png" mode="" ></image>
						</min-badge>			   
					</view>			
				  </view>
				</div>
			</div>
		</uni-page-head>
		
 
		<!-- 头部轮播 -->
		<view class="carousel-section" :style="{backgroundImage:'url('+backgroundpic+')',backgroundSize:'100% auto',backgroundRepeat:'no-repeat'}">
			<!-- 标题栏和状态栏占位符 -->
			<view class="titleNview-placing"></view>
			<!-- 背景色区域 -->
			<view class="titleNview-background" :style="{ backgroundColor: titleNViewBackground }"></view>
			<swiper class="carousel" circular @change="swiperChange" autoplay="1000">
				<swiper-item v-for="(item, index) in bannerList" :key="index" class="carousel-item" 
				
				@click="navToIconPage({ url: item.link })"
				>
					<image :src="item.url" />
					navToIconPage({ url: '/pages/category/category' })
				</swiper-item>
			</swiper>
			<view class="swiper-dots"><text v-for="(item, index) in swiperLength" :key="index" class="num" :class="swiperCurrent == index ? 'sign' : ''"></text></view>
		</view>
		<!-- 首页ICON -->
<!-- 		<view class="cate-section">
			<view class="cate-item" v-for="(item, index) in iconList" :key="index" @click="navToIconPage({ url: item.link })">
				
				<image :src="item.url"></image>
				<text>{{ item.name }}</text>
			</view>
		</view> -->

		<!-- 会员专区楼层 -->
		<view class="huiyuan-section m-t" v-if="userGoodsHome != null && userGoodsHome.length > 0">
			<view class="s-header">
				<view class="">
					<image class="s-img" src="/static/newimg/vip.png" mode="widthFix"></image>
					<text class="tip">会员专享</text>
				</view>
				<view class="more" style="color: #2f2f2f;" @click="navToIconPage({ url: '/pages/promotion/index' })">
					查看更多
					<!-- <image src="../../static/newimg/more.png" class="img_more" mode=""></image> -->
				</view>
			</view>

			<view class="index_nei">
				<scroll-view class="g-swiper" scroll-x="true">
					<view class="index_neili" v-for="(item, index) in userGoodsHome" :key="index" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.goodsId+'&productid='+item.goodsProductId })">
						<!-- <text class="nei_text">{{ item.goodsName }}</text> -->
						<image :src="item.picUrl" mode="" class="goodimg"></image>
						<view class="title">{{ item.goodsName }}</view>
						<view class="nei_bottom">
							<view class="left">
								<view class="Presentprice">
									<text>
										￥
										<text class="huiYuanRed">{{ item.huiYuanPrice }}</text>
									</text>
									<image src="../../static/newimg/vip.png" class="vip_img" mode=""></image>
								</view>
								<view class="Originalprice">
									<text class="del">￥{{ item.sourcePrice }}</text>
									<text>原价</text>
								</view>
							</view>

							<!-- <image src="../../static/newimg/cart.png" class="cart_img" mode=""></image> -->
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
		<!-- 秒杀楼层 -->
		<view class="seckill-section m-t" v-if="seckillList != null && seckillList.length > 0">
			<view class="s-header">
				<view class="">
					<image class="s-img" src="/static/newimg/damin.png"></image>
					<text class="tip">秒杀活动</text>
				</view>
				<view class="more"  @click="navToIconPage({ url: '/pages/promotion/index' })">
					<text class="more" style="color: #2f2f2f;">查看更多</text></view>
			</view>
			<scroll-view class="floor-list" scroll-x="true">
				<view class="floor-listdiv" v-for="(item, index) in seckillList" :key="index"  @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id +'&productid='+item.goodsProductId })">
					<view class="floor-list-left">
						<view class="left-title" style="padding-bottom:20upx">
							<image src="../../static/newimg/time.png" mode=""></image>
							<text>限时秒杀</text>

						</view>
						
						<uni-countdown
						    :displayOneRow="false"
						    :duration="seckillListtimes[index][0]">
						</uni-countdown>

						<view class="left-btn"><text>线上专享</text></view>
						<view class="left-xian">请在线下单</view>
					</view>
					<view class="right-floor-item">
						<view class="scoll-wrapper">
							<!-- <view v-for="(item, index) in seckillList" :key="index" class="floor-item" @click="navToDetailPage({link:'/pages/product/product?id='+item.id})"> -->

							<view  class="floor-item"  >
								<view class="left-floor-item"><image :src="item.picUrl" mode="aspectFill"></image></view>
								<view class="right-floor-item">					
									<view class="autowarptitle top" style="margin: 30upx 20upx 0;">
										{{ item.name }}
									</view>
									<view class="autowarptitle ">{{ item.goodsProductSpecifications }}</view>
									<view class="price" style="margin: 20px 8px 0px;">￥{{ item.seckillPrice }}</view>
									<view class="Originalprice">
										<text class="del">￥{{ item.counterPrice }}</text>
										<text>原价</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 团购楼层 -->
		<view class="seckill-section m-t" v-if="groupList != null && groupList.length > 0">
			<!-- <view class="f-header m-t" v-if="groupList != null&&groupList.length>0"> -->
			<view class="s-header">
				<view class="">
					<image class="s-img" src="/static/newimg/tuanicon.png"></image>
					<text class="tip">拼团优惠</text>
				</view>
				<view class="more"><text class="more" style="color: #2f2f2f;" @click="navToIconPage({ url: '/pages/promotion/index' })">查看更多</text></view>
			</view>
			<view class="group-section">
				<scroll-view class="g-swiper" scroll-x="true">
					<view class="g-swiper-item" v-for="(item, index) in groupList" :key="index" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id+'&productid='+item.goodsProductId  })">
						<view class="g-item left">
							<view class=""><image :src="item.picUrl" mode="aspectFill"></image></view>
							<view class="t-box">
								<text class="title clamp">{{ item.name }}</text>
								<view class="price-box">
									<view class="Presentprice">
										<view class="price">
											￥
											<text>{{ item.grouponPrice }}</text>
											<image src="/static/newimg/pin.png" mode=""></image>
										</view>
										<view class="m-price">
											<text>￥{{ item.counterPrice }}</text>
											原价
										</view>
										<!-- <image src="/static/newimg/add.png" class='add_icon' mode=""></image> -->
									</view>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 首都精品-->
		
<!-- 		<view v-show="shouduList != null && shouduList.length > 0">
			<view class="f-header m-t">
				<image src="/static/newimg/star.png"></image>
				<view class="tit-box">
					<text class="tit">首都机场精品预约</text>
				</view>
				<text class="yticon icon-you" style="color: #2f2f2f;" @click="navToIconPage({ url: '/pages/appointment/appointment?comId=2' })">查看更多</text>
			</view>
 
		    <view class="guess-section">
				<view v-for="(item, index) in shouduList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id })">
					<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
					<text class="title clamp">{{ item.name }}</text>
					<text class="price">￥{{ item.retailPrice }}</text>
				</view>
			</view>
			
		</view>
 -->		
		<!-- 大兴精品 -->
<!-- 		<view v-if="daxingList != null && daxingList.length > 0">
			<view class="f-header m-t"  >
				<image src="/static/newimg/damin.png"></image>
				<view class="tit-box">
					<text class="tit">大兴机场精品预约</text>
				</view>
				<text class="yticon icon-you" style="color: #2f2f2f;" @click="navToIconPage({ url: '/pages/appointment/appointment?comId=3' })">查看更多</text> 
			</view>
			<view class="guess-section">
				<view v-for="(item, index) in daxingList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id })">
					<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
					<text class="title clamp">{{ item.name }}</text>
					<text class="price">￥{{ item.retailPrice }}</text>
				</view>
			</view>
		</view>
 -->


		<!-- 商城优选 -->
		<view class="f-header m-t" v-show="goodsList != null && goodsList.length > 0">
			<image src="/static/newimg/city.png"></image>
			<view class="tit-box">
				<text class="tit">商城优选</text>
				<!-- <text class="tit2">Guess You Like It</text> -->
			</view>
			<text class="yticon icon-you" style="color: #2f2f2f;" @click="navToIconPage({ url: '/pages/category/brand' })">查看更多</text>
		</view>

		<view class="guess-section">
			<view v-for="(item, index) in goodsList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id })">
				<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
				<text class="title clamp">{{ item.name }}</text>
				
<!-- 				<text class="price" v-if="item.priceDesc!=null&&item.priceDesc!=''">￥{{item.priceDesc}}</text>
				<text class="price" v-if="item.priceDesc==null||item.priceDesc==''" >￥{{item.retailPrice}}</text> -->
				<text class="price">￥{{item.retailPrice}}</text>
			</view>
		</view>
 
	</view>
</template>

<script>
import Vue from 'vue';
import minBadge from '@/components/min-badge/min-badge'
import UniCountdown from '@/components/uni-countdown/uni-countdown'
import axios from 'axios'
import wx from 'weixin-js-sdk'
import {
		mapState,
		mapMutations
	} from 'vuex';
export default {

	components: {
		minBadge,UniCountdown
	},
 
	computed: {
		...mapState(['hasLogin', 'userInfo'])
	},
	data() {

		return {
			errorCount:0,//获取会员商品允许错误的次数
		    ifGetUser:undefined,
			txtSearch:'',
			userInfoCount:0,
			titleNViewBackground: '',
			swiperCurrent: 0,
			swiperLength: 0,
			userGoodsHome: [], //会员专区
			bannerList: [], //轮播
			goodsList: [],
			iconList: [], //图标
			seckillList: [], //秒杀
			groupList: [],
			shouduList: [],
			daxingList: [],
			seckillListtimes: [[]], //定时器列表
			currenttime: '', //当前时间
 
			backgroundpic:"",//轮播上的背景图
			hour: '00',
			minute: '00',
			second: '00',
			intDay:0,
			intHour:0,
			intMinute:0,
			intSecond:0,
			duration:0
		};
	},
	onLoad(options) {
		// if(this.isWeiXin()){
			var userInfo = uni.getStorageSync('userInfo');
			if(userInfo==undefined || userInfo==null || userInfo==""){
				let rurl=this.global.website
				uni.navigateTo({
					url: '/pages/public/tologin?rurl='+rurl
				})
			} else
			{
				this.loadData();
				this.getcartNumber();
				this.getUserGoods();
			}
			if(window.__wxjs_is_wkwebview){
				console.log(window.location.href.split('#')[0])
				this.getShareInfo();	
			}
			
		// } else
		// {
		// 	this.loadData();
		// }
	},
    onShow() {
		// if(this.isWeiXin()){
			 let userInfo = uni.getStorageSync('userInfo');
			   if(userInfo==null||userInfo.id==null){
				
			   }else{
				 this.loadData();
				 this.getcartNumber();
			   }
			   this.getUserGoods();
		// }else
		// {
		// 	this.loadData();
		// 	this.getcartNumber();
		// 	this.getUserGoods();
		// }
	},
	methods: {
		...mapMutations(['login','userHaveGet']),
		isWeiXin() {
			var ua = window.navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				return true; // 是微信端
			} else {
				return false;
			}
		},
		getShareInfo(){
			var that = this;
			// alert(window.location.href.split('#')[0])
			let _url = window.location.href.split('#')[0];
			if(_url.indexOf('http:')!=0 && _url.indexOf('https:')!=0){
				// alert(window.location.href.split('#')[0])
				return;
			}
			that.$http({
				//调用接口
				method: 'POST',
				params: {
					url: window.location.href.split('#')[0]
				},
				url: this.global.target + '/wx/share/info' //this指data
			}).then(
				function(response) {
					//接口返回数据
					 let result= response.data.data;
					 wx.config({
					    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
					    appId: result.appid, // 必填，公众号的唯一标识
					    timestamp: result.timestamp , // 必填，生成签名的时间戳
					    nonceStr: result.nonceStr, // 必填，生成签名的随机串
					    signature: result.signature,// 必填，签名
					    jsApiList: ['updateAppMessageShareData','updateTimelineShareData'] // 必填，需要使用的JS接口列表
					  });
				},
				function(error) {}
			);
			
			
		},
		
		//根据用户的ID，返回用户商品
		getUserGoods(){
			
			var that = this;
			that.$http({ //调用接口
				method: 'POST',
				url: that.global.target + "/wx/auth/checkToken" //this指data
			}).then(function(response) { //接口返回数据
				// console.log(response)
				var result = response.data;
				if (result.errno == 0) {
					that.login(result.data);
					let userInfo = uni.getStorageSync('userInfo');
					that.$http({ //调用接口
						method: 'GET',
						url: that.global.target + "/wx/home/getUserGoods" //this指data
					}).then(function(resUserGoods) { //接口返回数据
							 // console.log( response.data.data)
									 
						that.userGoodsHome = resUserGoods.data.data.list;
						console.log(that.userGoodsHome)
					}, function(error) {})
					}else{
						that.errorCount++;
						that.userGoodsHome =[];
						if(that.errorCount<3){
							setTimeout(function(){
							    that.getUserGoods();
							}, 500);
						}	
					}
				},
				function(error) {
					that.errorCount++;
					that.userGoodsHome =[];
					if(that.errorCount<3){
						 setTimeout(function(){
						     that.getUserGoods();
						 }, 500);
						
					}
				})

			
		},
		
			//获取我的预约，我的特产订单角标的数量,我的消息角标的数量
		getInfoCount(){	
			var that = this;
			 that.$http({ //调用接口
				method: 'GET',
				url: this.global.target + "/wx/user/userinfopubscount" //this指data
			 }).then(function(response) { //接口返回数据
				  //console.log( response.data.data)
				that.userInfoCount = response.data.data;
			 }, function(error) {})
			
		},
		//加载购物车角标
		getcartNumber(){
			var that = this;
			//验证
			that.$http({ //调用接口
				method: 'GET',
				url: that.global.target + "/wx/cart/cartNumber" //this指data
			}).then(function(response) { //接口返回数据
				var result = response.data;
				if (result.errno == 0) {
					// console.log(result)
					uni.setTabBarBadge({
					  index: 3,
					  text: ""+result.data
					})
				} else {
					
				}
			}, function(error) {
				// that.$api.msg("系统错误");
			})
		},
		/**
		 * 请求静态数据只是为了代码不那么乱
		 * 分次请求未作整合
		 */
		async loadData() {
			// let carouselList = await this.$api.json('carouselList');
			// this.titleNViewBackground = carouselList[0].background;
			// this.swiperLength = carouselList.length;
			// this.carouselList = carouselList;

			// let goodsList = await this.$api.json('goodsList');
			// this.goodsList = goodsList || [];
			// console.log(this.global.target);

			var that = this;
			that.$http({
				//调用接口
				method: 'GET',
				url: this.global.target + '/wx/home/index' //this指data
			}).then(
				function(response) {
					//接口返回数据
					// console.log(response.data.data);
					var data = response.data.data;
					if(data==null){
						return;
					}
					that.userGoodsHome = data.userGoodsHome;
					that.bannerList = data.banner;
					that.swiperLength = data.banner.length;
					that.iconList = data.icon;
					that.seckillList = data.seckillHome;

					if(data.backimg){
						that.backgroundpic=data.backimg.url;
					}
			
					that.groupList = data.grouponHome;
					that.goodsList = data.ecHome;
					that.shouduList = data.shouduHome;
					that.daxingList = data.daxingHome;
					that.daotime();
	                that.getInfoCount();
				},
				function(error) {}
			);
			
		},

		async daotime() {
			 
			//设置倒计时
			var that = this;
			if(that.seckillList==null||that.seckillList.length==0){
				return;
			}
			var myDate = Math.round(new Date().getTime() / 1000).toString();
			that.currenttime = myDate;
			var seckillList = that.seckillList;
			that.seckillListtimes=[{}];
			// if(seckillList==null||seckillList.length==0){
			// 	return;
			// }
			
			 var duration = 0;
			// setInterval(() => {
			// 	duration += 1;
				for (var i = 0; i < that.seckillList.length; i++) {
					let times = new Array();
					let seckillEndDate=that.seckillList[i].seckillEndDate;
					if(seckillEndDate==null){
						return;
					}
					let seckillEndDateReplace = seckillEndDate.replace(/-/g,'/');
					
					 var endtime = (Date.parse(new Date(seckillEndDate))||Date.parse(new Date(seckillEndDateReplace))) / 1000 - myDate - duration;
					
					endtime = endtime > 0 ? endtime : 0;
					
					that.duration=endtime;
					times.push(endtime);
					// var daoD = Math.floor(endtime / 3600 / 24);
					// console.log(daoD);
				 //    that.intDay=daoD;	
					// 
					// var daoH = Math.floor((endtime-daoD*3600*24) / 3600);
					// console.log(daoH);
					// //that.intHour=daoH;
					// that.intHour=daoH;
					// daoH=daoH<9?('0'+daoH):daoH;
					// times.push(daoH);
					// that.hour=daoH;
				 // 
					// var daoM = Math.floor((endtime-daoD*3600*24 - daoH * 3600) / 60);
					// that.intMinute=daoM;
					// daoM=daoM<9?('0'+daoM):daoM;
					// that.minute=daoM;
					// times.push(daoM);
					// var daoS = endtime -daoD*3600*24- daoH * 3600 - daoM * 60;
					// that.intSecond=daoS;
					// daoS=daoS<9?('0'+daoS):daoS;
					// that.second=daoS;
					// times.push(daoS);
					that.seckillListtimes[i] = times;
					that.$forceUpdate();
				}
			// }, 1000);
		},
		//轮播图切换修改背景色
		swiperChange(e) {
			const index = e.detail.current;
			this.swiperCurrent = index;
			//this.titleNViewBackground = this.carouselList[index].background;
		},
		//详情页
		navToDetailPage(item) {
			if (item.link.indexOf('http') == 0 || item.link.indexOf('https') == 0) {
				window.location.href = item.link;
			} else {
				uni.navigateTo({
					url: item.link
				});
			}
		},
		//ICON点击
		navToIconPage(item) {
			console.log(item);
			let url = item.url;
			if (url.indexOf('http') == 0 || url.indexOf('https') == 0) {
				window.location.href = url;
			} else {
				if(url.toString().indexOf('comId')>=0){
				   let paras=item.url.toString().split('?');
				   var app = getApp();
				   var comId=paras[1];
				   let comParas=comId.split('=');
				   app.comId = comParas[1];
				   console.log(app.comId);
				}
				if(url.toString().indexOf('brandId')>=0){
				   let paras=item.url.toString().split('?');
				   var app = getApp();
				   var brandId=paras[1];
				   let comParas=brandId.split('=');
				   app.brandId = comParas[1];
				   console.log(app.brandId);
				}
				if(this.isTabBar(url)){
					uni.switchTab({
					    url: url
					});
				}else{
					uni.navigateTo({
						url:url
					});
				}
				 // uni.reLaunch(item);
				
			}
		},
			// 搜索栏
		navToSearch() {
			uni.navigateTo({
				url: `/pages/category/search?keyword=` + this.txtSearch
			});
		},
		navTo(url) {
			uni.navigateTo({
				url
			})
		},
		isTabBar(url){
			if(url.indexOf("pages/index/index")>0||
			   url.indexOf("pages/category/category")>0||
			   url.indexOf("pages/appointment/appointment")>0||
			   url.indexOf("pages/cart/cart")>0||
			   url.indexOf("pages/user/user")>0){
				return true;
			}
		}
		
	},
	// #ifndef MP
	// 标题栏input搜索框点击
	onNavigationBarSearchInputClicked: async function(e) {
		this.$api.msg('点击了搜索框');
	},
	//点击导航栏 buttons 时触发
	onNavigationBarButtonTap(e) {
		const index = e.index;
		// if (index === 0) {
		// 	// this.$api.msg('点击了扫描');
		// } else if (index === 1) {
			// #ifdef APP-PLUS
			const pages = getCurrentPages();
			const page = pages[pages.length - 1];
			const currentWebview = page.$getAppWebview();
			currentWebview.hideTitleNViewButtonRedDot({
				index
			});
			// #endif
			uni.navigateTo({
				url: '/pages/notice/notice'
			});
		// }
	},
	// 搜索栏
	onNavigationBarSearchInputConfirmed(e) {
		//console.log('输入内容为：',e.text)
		//alert(e.text);
		uni.navigateTo({
			url: `/pages/category/search?keyword=` + e.text
		});
	}
	// #endif
};
</script>

<style lang="scss">
	.set-box {
		flex: 1;
	
		.icon {
			float: right;
			margin-left: 0upx;
			margin-right: 20upx
		}
	
		image {
			width: 44upx;
			height: 40upx;
		}
	}
	
/* #ifdef MP */
.mp-search-box {
	position: absolute;
	left: 0;
	top: 30upx;
	z-index: 9999;
	width: 100%;
	padding: 0 80upx;

	.ser-input {
		flex: 1;
		height: 56upx;
		line-height: 56upx;
		text-align: center;
		font-size: 28upx;
		color: $font-color-base;
		border-radius: 20px;
		background: rgba(255, 255, 255, 0.6);
	}
}

.h5-search-box {
 
    backgroundColor: rgba(251, 251, 251,.6),
    borderRadius: 16px,
	placeholder: '搜索商品名称',
	disabled: false,
	placeholderColor: #FBFBFB,
	align: left,
	position: absolute,
	
// 	left: 0;
// 	top: 30upx;
// 	z-index: 9999;
// 	width: 100%;
// 	padding: 0 80upx;
// 
// 	.ser-input {
// 		flex: 1;
// 		height: 56upx;
// 		line-height: 56upx;
// 		text-align: center;
// 		font-size: 28upx;
// 		color: $font-color-base;
// 		border-radius: 20px;
// 		background: rgba(255, 255, 255, 0.6);
// 	}
}


page {
	.cate-section {
		position: relative;
		z-index: 5;
		border-radius: 16upx 16upx 0 0;
		margin-top: -20upx;
	}

	.carousel-section {
		padding: 0;

		// background: #fff;
		.titleNview-placing {
			padding-top: 0;
			height: 0;
		}

		.carousel {
			.carousel-item {
				padding: 0;
			}
		}

		.swiper-dots {
			left: 45upx;
			bottom: 40upx;
		}
	}
}

/* #endif */

page {
	background: #f5f5f5;
}

.m-t {
	margin-top: 27upx;
}

/* 头部 轮播图 */
.carousel-section {
	position: relative;
	padding-top: 10px;
	background: #fff;
	// background: url('../../static/newimg/indexbg.png') #fff no-repeat;
	background-size: 100% auto;

	.titleNview-placing {
		height: var(--status-bar-height);
		padding-top: 44px;
		box-sizing: content-box;
	}

	.titleNview-background {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 426upx;

		transition: 0.4s;
	}
}

.carousel {
	width: 100%;
	height: 430upx;

	.carousel-item {
		width: 100%;
		height: 100%;
		padding: 0 28upx;
		overflow: hidden;
	}

	image {
		width: 100%;
		height: 100%;
		border-radius: 10upx;
	}
}

.swiper-dots {
	display: flex;
	position: absolute;
	left: 0upx;
	bottom: 15upx;
	width: 100%;
	height: 36upx;
	justify-content: center;
	align-items: center;
	// background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAABkCAYAAADDhn8LAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTMyIDc5LjE1OTI4NCwgMjAxNi8wNC8xOS0xMzoxMzo0MCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OTk4MzlBNjE0NjU1MTFFOUExNjRFQ0I3RTQ0NEExQjMiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OTk4MzlBNjA0NjU1MTFFOUExNjRFQ0I3RTQ0NEExQjMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6Q0E3RUNERkE0NjExMTFFOTg5NzI4MTM2Rjg0OUQwOEUiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6Q0E3RUNERkI0NjExMTFFOTg5NzI4MTM2Rjg0OUQwOEUiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4Gh5BPAAACTUlEQVR42uzcQW7jQAwFUdN306l1uWwNww5kqdsmm6/2MwtVCp8CosQtP9vg/2+/gY+DRAMBgqnjIp2PaCxCLLldpPARRIiFj1yBbMV+cHZh9PURRLQNhY8kgWyL/WDtwujjI8hoE8rKLqb5CDJaRMJHokC6yKgSCR9JAukmokIknCQJpLOIrJFwMsBJELFcKHwM9BFkLBMKFxNcBCHlQ+FhoocgpVwwnv0Xn30QBJGMC0QcaBVJiAMiec/dcwKuL4j1QMsVCXFAJE4s4NQA3K/8Y6DzO4g40P7UcmIBJxbEesCKWBDg8wWxHrAiFgT4fEGsB/CwIhYE+AeBAAdPLOcV8HRmWRDAiQVcO7GcV8CLM8uCAE4sQCDAlHcQ7x+ABQEEAggEEAggEEAggEAAgQACASAQQCCAQACBAAIBBAIIBBAIIBBAIABe4e9iAe/xd7EAJxYgEGDeO4j3EODp/cOCAE4sYMyJ5cwCHs4rCwI4sYBxJ5YzC84rCwKcXxArAuthQYDzC2JF0H49LAhwYUGsCFqvx5EF2T07dMaJBetx4cRyaqFtHJ8EIhK0i8OJBQxcECuCVutxJhCRoE0cZwMRyRcFefa/ffZBVPogePihhyCnbBhcfMFFEFM+DD4m+ghSlgmDkwlOgpAl4+BkkJMgZdk4+EgaSCcpVX7bmY9kgXQQU+1TgE0c+QJZUUz1b2T4SBbIKmJW+3iMj2SBVBWz+leVfCQLpIqYbp8b85EskIxyfIOfK5Sf+wiCRJEsllQ+oqEkQfBxmD8BBgA5hVjXyrBNUQAAAABJRU5ErkJggg==);
	// background-size: 100% 100%;

	.num {
		width: 16upx;
		height: 16upx;
		border-radius: 16upx;
		background: rgba(255, 255, 255, 0.6);
		margin: 0 13upx;
	}

	.sign {
		width: 22upx;
		height: 22upx;
		background: rgba(255, 255, 255, 1);
		box-shadow: 0px 0px 3upx 0px rgba(0, 0, 0, 0.58);
		border-radius: 50%;
	}
}

/* 分类 */
.cate-section {
	display: flex;
	justify-content: space-around;
	align-items: center;
	flex-wrap: wrap;
	padding: 30upx 22upx;
	background: #fff;

	.cate-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		font-size: $font-sm + 2upx;
		color: $font-color-dark;
		width: 110upx;
	}

	/* 原图标颜色太深,不想改图了,所以加了透明度 */
	image {
		width: 79upx;
		height: 79upx;
		margin-bottom: 14upx;
		border-radius: 50%;
		opacity: 0.7;
		box-shadow: 4upx 4upx 20upx rgba(250, 67, 106, 0.3);
	}
}

.ad-1 {
	width: 100%;
	height: 210upx;
	padding: 10upx 0;
	background: #fff;

	image {
		width: 100%;
		height: 100%;
	}
}

/* 会员专区 */
.huiyuan-section {
	padding: 4upx 30upx 0;
	background: #fff;
	justify-content: space-around;
	.s-header {
		display: flex;
		align-items: center;
		height: 92upx;
		line-height: 1;
		justify-content: space-between;

		.s-img {
			width: 60upx;
			height: 30upx;
		}

		.tip {
			font-size: 34upx;
			color: #2f2f2f;
			font-weight: bold;
			margin: 0 0 0 10upx;
		}

		.more {
			color: #a1a29d;
			// color: #2f2f2f;
			font-size: 20upx;

			.img_more {
				width: 13upx;
				height: 23upx;
				float: right;
				margin: 0 0 0 11upx;
			}
		}
		.icon-you {
			font-size: $font-lg;
			color: $font-color-light;
			flex: 1;
			text-align: right;
		}
	}
	.g-swiper {
		// height: 650upx;
		// padding-bottom: 30upx;
		padding: 20upx 27upx 0upx;
		white-space: nowrap;
	}
}

/* 秒杀专区 */
.seckill-section {
	padding: 4upx 30upx 0;
	background: #fff;
	justify-content: space-around;
	
	.s-header {
		display: flex;
		align-items: center;
		height: 92upx;
		line-height: 1;
		justify-content: space-between;

		.s-img {
			width: 40upx;
			height: 40upx;
			float: left;
		}

		.tip {
			font-size: 34upx;
			color: #2f2f2f;
			font-weight: bold;
			margin: 0 0 0 10upx;
		}

		.more {
			color: #a1a29d;
			font-size: 20upx;

			.img_more {
				width: 13upx;
				height: 23upx;
				float: right;
				margin: 0 0 0 11upx;
			}
		}

		.timer {
			display: inline-block;
			width: 30upx;
			height: 30upx;
			text-align: center;
			line-height: 30upx;
			margin-right: 14upx;
			font-size: $font-sm + 2upx;
			color: #fff;
			border-radius: 2px;
			background: #fa436a;
			box-shadow: 0 0 2px 2px #fa436a;
		}

		.icon-you {
			font-size: $font-lg;
			color: $font-color-light;
			flex: 1;
			text-align: right;
		}
	}

	.dao_header {
		height: auto;
		font-size: 10upx;
	}

	.floor-list {
		white-space: nowrap;
		width: 100vw;
		margin: 0 0 0 -30upx;
		background: #f5f5f5;
		padding: 27upx;
		box-sizing: border-box;
	}

	.floor-listdiv {
		background: url('../../static/newimg/miaobg.png');
		background-size: 100% 100%;
		height: 361upx;
		padding: 27upx 33upx 26upx 31upx;
		box-sizing: border-box;
		
	    width: 700upx !important;
		// padding: 0 30upx;
		display: flex;
		display: inline-block;
		margin: 0 0upx 0 0;

		.floor-list-left {
			color: #fff;
			float: left;

			image {
				width: 32upx;
				height: 31upx;
				margin: 0 9upx 0 0;
				float: left;
			}

			.left-title {
				font-size: 29upx;
				vertical-align: middle;
				line-height: 31upx;
			}

			.left-time {
				font-size: 23upx;
				margin: 21upx 0 40upx 0;
				display: flex;
				.time {
					width: 35upx;
					height: 35upx;
					background: #2f2f2f;
					display: inline-block;
					text-align: center;
					line-height: 35upx;
					border-radius: 3upx;
				}

				.dots {
					color: #2f2f2f;
					margin: 0 10upx;
					font-weight: 900;
				}

				.second {
					background: #ff0019;
				}
			}

			.left-btn {
				width: 141upx;
				height: 45upx;
				background: rgba(255, 255, 255, 1);
				box-shadow: 0px 0px 9upx 0px rgba(47, 47, 47, 0.26);
				border-radius: 5upx;
				font-size: 27upx;
				text-align: center;
				line-height: 45upx;

				text {
					background: linear-gradient(0deg, rgba(255, 142, 153, 1) 0%, rgba(255, 71, 71, 1) 100%);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
				}
			}

			.left-xian {
				font-size: 17upx;
				line-height: 17upx;
				margin: 25upx 0 0;
			}
		}
	}

	.scoll-wrapper {
		display: flex;
		align-items: flex-start;
		width: 449upx;
		height: 309upx;
		background: #fff;
		box-shadow: 0px 0px 9upx 0px rgba(47, 47, 47, 0.26);
		float: right;
		padding: 0 19upx;
	}

	.floor-item {
		width: 280upx;
		margin-right: 20upx;
		font-size: 24upx;
		color: $font-color-dark;
		line-height: 1.8;
		color: #ff0019;
		width: 201upx;

		display: flex;
		.left-floor-item {
			image {
				width: 241upx;
				height: 264upx;
				margin: 20upx auto 4upx;
				// border-radius: 6upx;
			}
		}
		.right-floor-item {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			width: 200upx;
			height: 300upx;
			.floor_icon {
				width: 64upx;
				height: 44upx;
				background: linear-gradient(0deg, rgba(196, 0, 19, 1), rgba(255, 0, 25, 1));
				border-radius: 0px 0px 5upx 5upx;
				color: #fff;
				font-size: 17upx;
				float: right;
				text-align: center;
				line-height: 44upx;
			}

			.Originalprice {
				color: #a1a29d;
				margin: 20upx 20upx 15upx;
				.del {
					text-decoration: line-through;
					margin: 0 10upx 0 0;
				}
			}

			.price {
				text-align: left;
				font-size: 29upx;
				font-weight: 900;
				line-height: 29upx;
				
				margin: 20upx 20upx 0;
				// padding-top: 20px;
				// color: $uni-color-primary;
			}
			.autowarptitle {
				width: 150upx;
				// height: 200upx;
				// display:block;
				word-break: normal;
				word-wrap: break-word;
				// white-space: nowrap;
				// text-overflow: ellipsis;
				flex-shrink: 0;
				white-space: normal;
				line-height: 28upx;
				font-size: 29upx;
				color: #2f2f2f;
				// overflow: hidden;
				margin: 10upx 20upx 0;
				.top {
					padding-top: 50upx;
				}
			}
		}
	}
}

.f-header {
	display: flex;
	align-items: center;
	height: 98upx;
	padding: 6upx 30upx 8upx;
	background: #fff;

	image {
		flex-shrink: 0;
		width: 42upx;
		height: 42upx;
		margin-right: 11upx;
	}

	.tit-box {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.tit {
		font-size: 34upx;
		color: #2f2f2f;
		font-weight: bold;
		margin: 0 0 0 10upx;
	}

	.tit2 {
		font-size: $font-sm;
		color: $font-color-light;
	}

	.icon-you {
		font-size: 20upx;
		color: #a1a29d;
	}

	.icon-you:before {
		content: '';
	}

	.icon-you:after {
		content: '\E606';
	}
}

/* 团购楼层 */
.group-section {
	background: #fff;
	justify-content: space-around;

	.g-swiper {
		// height: 650upx;
		// padding-bottom: 30upx;
		padding: 20upx 27upx 0upx;
		white-space: nowrap;
	}

	.g-swiper-item {
		width: 330upx !important;
		// padding: 0 30upx;
		display: flex;
		display: inline-block;
		margin: 0 0upx 0 0;
	}

	image {
		width: 100%;
		height: 330upx;
		border-radius: 4px;
	}

	.g-item {
		display: flex;
		flex-direction: column;
		overflow: hidden;

		.price-box {
			display: flex;
			justify-content: space-between;
			align-items: center;
		}
		.nei_text {
			position: absolute;
			top: 0;
			overflow: hidden;
			color: #fff;
			font-size: 20upx;
			padding: 5upx 16upx 5upx 7upx;
			box-sizing: border-box;
			border-radius: 0 0 100upx 0;
			display: inline-block;
			background: linear-gradient(0deg, rgba(196, 0, 19, 1), rgba(255, 0, 25, 1));
		}
	}

	.left {
		// flex: 1.2;
		width: 303upx;
		margin-right: 61upx;

		.t-box {
			padding-top: 18upx;

			.clamp {
				font-size: 20upx;
				color: #2f2f2f;
			}
		}
	}

	.right {
		flex: 0.8;
		flex-direction: column-reverse;

		.t-box {
			padding-bottom: 20upx;
		}
	}

	.t-box {
		height: 160upx;
		font-size: $font-base + 2upx;
		color: $font-color-dark;
		line-height: 1.6;
	}

	.Presentprice {
		font-size: 24upx;

		image {
			width: 27upx;
			height: 25upx;
			vertical-align: middle;
		}

		.price text {
			color: #ff0019;
			margin: 0 10upx 0 0;
			font-weight: 900;
		}
	}

	.price {
		color: #2f2f2f;
	}

	.m-price {
		font-size: 24upx;
		color: #a1a29d;
		margin-left: 8upx;

		text {
			text-decoration: line-through;
		}
	}

	.add_icon {
		width: 42upx;
		height: 42upx;
	}

	.pro-box {
		display: flex;
		align-items: center;
		margin-top: 10upx;
		font-size: $font-sm;
		color: $font-base;
		padding-right: 10upx;
	}

	.progress-box {
		flex: 1;
		border-radius: 10px;
		overflow: hidden;
		margin-right: 8upx;
	}
}

/* 分类推荐楼层 */
.hot-floor {
	width: 100%;
	overflow: hidden;
	margin-bottom: 20upx;

	.floor-img-box {
		width: 100%;
		height: 320upx;
		position: relative;

		&:after {
			content: '';
			position: absolute;
			left: 0;
			top: 0;
			width: 100%;
			height: 100%;
			background: linear-gradient(rgba(255, 255, 255, 0.06) 30%, #f8f8f8);
		}
	}

	.floor-img {
		width: 100%;
		height: 100%;
	}

	.floor-list {
		white-space: nowrap;
		padding: 20upx;
		padding-right: 50upx;
		border-radius: 6upx;
		margin-top: -140upx;
		margin-left: 30upx;
		background: #fff;
		box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.2);
		position: relative;
		z-index: 1;
	}

	.scoll-wrapper {
		display: flex;
		align-items: flex-start;
	}

	.floor-item {
		width: 180upx;
		margin-right: 20upx;
		font-size: $font-sm + 2upx;
		color: $font-color-dark;
		line-height: 1.8;

		image {
			width: 180upx;
			height: 180upx;
			border-radius: 6upx;
		}

		.price {
			color: $uni-color-primary;
		}
	}

	.more {
		display: flex;
		align-items: center;
		justify-content: center;
		flex-direction: column;
		flex-shrink: 0;
		width: 180upx;
		height: 180upx;
		border-radius: 6upx;
		background: #f3f3f3;
		// font-size: $font-base;
		font-size: 20upx;

		color: $font-color-light;

		text:first-child {
			margin-bottom: 4upx;
		}
	}
}

/* 猜你喜欢 */
.guess-section {
	display: flex;
	flex-wrap: wrap;
	padding: 0 27upx;
	background: #fff;

	.guess-item {
		display: flex;
		flex-direction: column;
		width: 46.9% !important;
		padding-bottom: 40upx;
		box-shadow: 0px 0px 9px 0px rgba(47, 47, 47, 0.1);
		padding: 15upx;
		margin-bottom: 30upx;

		&:nth-child(2n + 1) {
			margin-right: 20upx;
			// margin-right: 6%;
		}
	}

	.guess-items {
	}

	.image-wrapper {
		width: 100%;
		height: 330upx;
		border-radius: 3px;
		overflow: hidden;

		image {
			width: 100%;
			height: 100%;
			opacity: 1;
		}
	}

	.title {
		font-size: 28upx;
		color: $font-color-dark;
		line-height: 80upx;
	}

	.price {
		font-size: 22upx;
		color: $uni-color-primary;
		line-height: 1;
	}
}

.guess-sections {
	white-space: nowrap;
	.guess-item {
		display: inline-block;
	}
}

.index_nei {
	padding: 29upx 0 25upx 0;
	display: flex;
	justify-content: space-between;

	.nei_text {
		position: absolute;
		top: 0;
		overflow: hidden;
		color: #fff;
		font-size: 20upx;
		padding: 5upx 16upx 5upx 7upx;
		box-sizing: border-box;
		border-radius: 0 0 100upx 0;
		display: inline-block;
		background: linear-gradient(0deg, rgba(196, 0, 19, 1), rgba(255, 0, 25, 1));
	}

	.goodimg {
		width: 302upx;
		height: 330upx;
		margin: 50upx 0 0;
		display: block;
	}

	.vip_img {
		width: 39upx;
		height: 23upx;
		margin: 0 0 0 11upx;
	}

	.cart_img {
		width: 48upx;
		height: 48upx;
		flex-shrink: 0;
	}

	.title {
		white-space: nowrap;
		text-overflow: ellipsis;
		font-size: 29upx;
		color: #2f2f2f;
		overflow: hidden;
		margin: 18upx 0 0;
	}

	.index_neili {
		width: 330upx !important;

		display: flex;
		display: inline-block;
		margin: 0 0upx 0 0;

		// width: 227upx;
		// position: relative;
	}

	.nei_bottom {
		font-size: 24upx;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.Presentprice {
			margin: 10upx 0;
		}

		.huiYuanRed {
			font-size: 30upx;
			color: #ff0019;
			font-weight: bold;
		}
		.red {
			color: #ff0019;
			font-weight: bold;
		}

		.Originalprice {
			color: #a1a29d;

			.del {
				text-decoration: line-through;
				margin: 0 10upx 0 0;
			}
		}
	}
}
</style>
