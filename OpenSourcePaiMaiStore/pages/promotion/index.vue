<template>
	<view class="container">
		 <!--会员-->
		 <view v-show="userGoodsHome != null && userGoodsHome.length > 0">
			<view class="f-header m-t">
				<image src="/static/newimg/star.png"></image>
				<view class="tit-box">
					<text class="tit">会员专享</text>
					<!-- <text class="tit2">Guess You Like It</text> -->
				</view>
				<!-- <text class="yticon icon-you" @click="navToIconPage({ url: '/pages/appointment/appointment?comId=2' })">查看更多</text> -->
			</view>
		  
			 <view class="guess-section">
				<view v-for="(item, index) in userGoodsHome" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.goodsId+'&productid='+item.goodsProductId })">
					<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
					<text class="title clamp">{{ item.goodsName }}</text>
					<text class="price">￥{{ item.huiYuanPrice }}</text>
					<view class="Originalprice">
						<text class="del">￥{{ item.sourcePrice }}</text>
						<text>原价</text>
					</view>
				</view>
			</view>
			
		 </view>
		<!-- 秒杀-->
		<view v-show="seckillList != null && seckillList.length > 0">
			<view class="f-header m-t">
				<image src="/static/newimg/star.png"></image>
				<view class="tit-box">
					<text class="tit">秒杀商品</text>
					<!-- <text class="tit2">Guess You Like It</text> -->
				</view>
				<!-- <text class="yticon icon-you" @click="navToIconPage({ url: '/pages/appointment/appointment?comId=2' })">查看更多</text> -->
			</view>
		 
		    <view class="guess-section">
				<view v-for="(item, index) in seckillList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id +'&productid='+item.goodsProductId})">
					<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
					<view class="autowarptitle ">{{ item.goodsProductSpecifications }}</view>
					<text class="title clamp">{{ item.name }}</text>
					<text class="price">￥{{ item.seckillPrice }}</text>
					<view class="Originalprice">
						<text class="del">￥{{ item.counterPrice }}</text>
						<text>原价</text>
					</view>
					<uni-countdown
					    :displayOneRow="false"
					    :duration="seckillListtimes[index][0]">
					</uni-countdown>
				</view>
			</view>
			
		</view>
		<!-- 团购-->
		<view v-show="groupList != null && groupList.length > 0">
			<view class="f-header m-t">
				<image src="/static/newimg/star.png"></image>
				<view class="tit-box">
					<text class="tit">团购商品</text>
					<!-- <text class="tit2">Guess You Like It</text> -->
				</view>
				<!-- <text class="yticon icon-you" @click="navToIconPage({ url: '/pages/appointment/appointment?comId=2' })">查看更多</text> -->
			</view>
		 
		    <view class="guess-section">
				<view v-for="(item, index) in groupList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.id+'&productid='+item.goodsProductId })">
					<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
					<text class="title clamp">{{ item.name }}</text>
					<text class="price">￥{{ item.grouponPrice }}</text>
					<view class="Originalprice">
						<text class="del">￥{{ item.counterPrice }}</text>
						<text>原价</text>
					</view>
				</view>
			</view>
			
		</view>
		
		
		<!-- 折扣 -->
		<view class="f-header m-t" v-show="zhekouList != null && zhekouList.length > 0">
			<image src="/static/newimg/city.png"></image>
			<view class="tit-box">
				<text class="tit">折扣商品</text>
				<!-- <text class="tit2">Guess You Like It</text> -->
			</view>
			<!-- <text class="yticon icon-you" @click="navToIconPage({ url: '/pages/category/category' })">查看更多</text> -->
		</view>

		<view class="guess-section">
			<view v-for="(item, index) in zhekouList" :key="index" class="guess-item" @click="navToDetailPage({ link: '/pages/product/product?id=' + item.goodsId+'&productid='+item.goodsProductId })">
				<view class="image-wrapper"><image :src="item.picUrl" mode="aspectFill"></image></view>
				<text class="title clamp">{{ item.goodsName }}</text>
				<text class="price">￥{{ item.rebatePrice }}</text>
				<view class="Originalprice">
					<text class="del">￥{{ item.sourcePrice }}</text>
					<text>原价</text>
				</view>
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
			userGoodsHome: [], //会员专区
			goodsList: [],
			seckillList: [
				// {
				// 	id: 0,
				// 	picUrl: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553187020783&di=bac9dd78b36fd984502d404d231011c0&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201609%2F26%2F20160926173213_s5adi.jpeg',
				// 	name: 'namenamenamenamenamenamename',
				// 	retailPrice: '99',
				// 	time: '1559294823'
				// }, {
				// 	id: 1,
				// 	picUrl: 'https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553187020783&di=bac9dd78b36fd984502d404d231011c0&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201609%2F26%2F20160926173213_s5adi.jpeg',
				// 	name: 'name1name1name1name1name1name1name1',
				// 	retailPrice: '99',
				// 	time: '1559291823'
				// }
			], //秒杀
			groupList: [],
			zhekouList:[],
			seckillListtimes: [[]], //定时器列表
			currenttime: '', //当前时间
			
			neilist: [
				{
					//内部专享列表
					img: 'http://yanxuan.nosdn.127.net/b2adc3fd9b84a289a1be03e8ee400e61.png',
					title: '【三袋分享装】稻香【三袋分享装】稻香...',
					price: 120,
					original_price: 179,
					text: '鸡净重1斤'
				},
				{
					img: 'http://yanxuan.nosdn.127.net/b2adc3fd9b84a289a1be03e8ee400e61.png',
					title: '【三袋分享装】稻香【三袋分享装】稻香...',
					price: 120,
					original_price: 179,
					text: '鸡净重1斤'
				}
			],
			
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
		this.loadData();
		this.getUserGoods();
     if(window.__wxjs_is_wkwebview){
     	console.log(window.location.href.split('#')[0])
     }
	},
    onShow() {

	},
	methods: {
		...mapMutations(['login','userHaveGet']),
		
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
						console.log( that.userGoodsHome)
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
		
		/**
		 * 请求静态数据只是为了代码不那么乱
		 * 分次请求未作整合
		 */
		async loadData() {

			var that = this;
			that.$http({
				//调用接口
				method: 'GET',
				url: this.global.target + '/wx/huodong/list' //this指data
			}).then(
				function(response) {
					//接口返回数据
					// console.log(response.data.data);
					var data = response.data.data;
					if(data==null){
						return;
					}
					that.seckillList = data.seckillHome;
					that.groupList = data.grouponHome;
					that.zhekouList =data.zhekouGoodsHome;
					that.daotime();
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
		navTo(url) {
			uni.navigateTo({
				url
			})
		},
		
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
	}
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
	.Originalprice {
		font-size: 22upx;
		color: #a1a29d;
		margin: 20upx 20upx 15upx 0upx;
		.del {
			text-decoration: line-through;
			margin: 0 10upx 0 0;
		}
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
