<template>
	<view class="container" v-if="userInfo.id!=null&&userInfo.id!=''">

		<view class="user-section">
			<image class="bg" src="/static/newimg/mine.png"></image>
			<view class="user-info-box">
				<view class="portrait-box">
					<image class="portrait" :src="userInfo.portrait || '/static/missing-face.png'" @click="navToLogin"></image>
				</view>
				<view class="info-box">
					<text class="username" @click="navTo('/pages/order/order?state=0')">{{userInfo.mobile==null?'游客':'会员'}}</text>
					<text class="username">{{userInfo.mobile|| ''}}</text>
				</view>
				<view class="set-box">
					<!-- 					<view class="icon"  @click="navTo('/pages/set/set')">
						<image src="/static/newimg/setting.png" mode=""></image>
					</view> -->

					<view class="icon" @click="navTo('/pages/notice/notice')">
						<min-badge :count="userInfoCount" :maxCount="maxCount">
							<image src="/static/newimg/message.png" mode=""></image>
						</min-badge>
					</view>

				</view>
			</view>
			<view class="vip-card-box">
				<image class="card-bg" src="/static/vip-card-bg.png" mode=""></image>
				<view v-if="userInfo.mobile==null||userInfo.mobile==''" class="b-btn" @click="navToLogin">
					<text>立即开通</text>
				</view>
				<view v-if="userInfo.mobile!=null&&userInfo.mobile!=''" class="b-btn">
					<text>已开通会员</text>
				</view>
				<view class="tit">
					<!-- <text class="yticon icon-iLinkapp-"></text> -->
					会员
				</view>
				<text class="e-m"></text>
				<text class="e-b"></text>
			</view>
		</view>

		<view class="cover-container" :style="[{
				transform: coverTransform,
				transition: coverTransition
			}]"
		 @touchstart="coverTouchstart" @touchmove="coverTouchmove" @touchend="coverTouchend">
			<image class="arc" src="/static/arc.png"></image>

			<view class="tj-sction">
				<!-- <view class="tj-item">
					<text class="num">128.8</text>
					<text>余额</text>
				</view> -->

				<view class="tj-item" @click="navTo('/pages/user/Coupon')">
					<text class="num">{{couponList.length}}</text>
					<text>优惠券</text>
				</view>
				<view class="tj-item">
					<text class="num">{{bonus}}</text>
					<text>积分</text>
				</view>
			</view>
			<!-- 订单 -->
			<view class="order-section">
				<view class="order-item" @click="navTo('/pages/order/order?state=0')" hover-class="common-hover" :hover-stay-time="50">
					<!-- <text class="yticon icon-daifukuan"></text> -->
					<min-badge :count="orderCount" :maxCount="maxCount">
						<image src="/static/newimg/me_order.png" class="order-icon2" mode=""></image>
					</min-badge>

					<text>订单</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/backOrder?state=6')" hover-class="common-hover" :hover-stay-time="50">
						<min-badge :count="backOrderCount" :maxCount="maxCount">
						<image src="/static/newimg/backOrder.png" class="order-icon2" mode=""></image>
						</min-badge> 
					<text>退款/售后</text>
				</view>
				<view class="order-item" @click="navTo('/pages/address/address')" hover-class="common-hover" :hover-stay-time="50">
					<!-- <text class="yticon icon-daifukuan"></text> -->
					<image src="/static/newimg/address.png" class="order-icon2" mode=""></image>
					<text>我的地址管理</text>

				</view>
				<!-- 				<view class="order-item" @click="navTo('/pages/order/order?state=1')" hover-class="common-hover" :hover-stay-time="50">
				 
					<image src="/static/newimg/me_buy.png" class="order-icon3" mode=""></image>
					<text>待付款</text>
				</view>
				<view class="order-item" @click="navTo('/pages/order/order?state=2')" hover-class="common-hover" :hover-stay-time="50">
			 
					<image src="/static/newimg/me_status.png" class="order-icon4" mode=""></image>
					<text>待收货</text>
				</view> -->
			</view>
			<!-- 浏览历史 -->
			<view class="history-section icon">
				<view class="sec-header">
					<!-- <text class="yticon icon-lishijilu"></text> -->
					<view>浏览历史</view>
				</view>
				<!-- v-for="(item, index) in foodList" :key="index" -->
				<scroll-view scroll-x class="h-list">
					<view class="h-scroll" v-for="(item, index) in footprintList" :key="index">
						<image @click="navTo('/pages/product/product?id='+item.goodsId)" :src="item.picUrl" mode="aspectFill" v-if="item.picUrl"></image>
						<text>{{item.title}}</text>
					</view>
				</scroll-view>
				<!-- <list-cell icon="icon-iconfontweixin" iconColor="#e07472" title="我的钱包" tips="您的会员还有3天过期"></list-cell> -->
				<!-- <list-cell class="h_address" title="地址管理" @eventClick="navTo('/pages/address/address')"></list-cell> -->
				 <!-- <list-cell icon="icon-share" iconColor="#9789f7" title="分享" tips="邀请好友赢10万大礼"></list-cell> -->
				<!--<list-cell icon="icon-pinglun-copy" iconColor="#ee883b" title="晒单" tips="晒单抢红包"></list-cell> -->
				<!-- <list-cell icon="icon-pinglun-copy" iconColor="#ee883b" title="预约" tips="我的预约"></list-cell> -->
				<!-- <list-cell icon="icon-shoucang_xuanzhongzhuangtai" iconColor="#54b4ef" title="我的收藏"></list-cell> -->
				<!-- <list-cell icon="icon-shezhi1" iconColor="#e07472" title="设置" border="" @eventClick="navTo('/pages/set/set')"></list-cell> -->
				
				<list-cell class="h_address" title="联系我们" @eventClick="navTo('/pages/user/Contactus')"></list-cell>
				<!-- <list-cell class="h_us" title="在线客服" @eventClick="kefu()"></list-cell> -->
				<list-cell class="h_help" iconColor="#e07472" title="帮助中心" border="" @eventClick="navTo('/pages/user/questionlist')"></list-cell>

			</view>
		<!-- 	<view class="list-cell log-out-btn" @click="toLogout">
				<text class="cell-tit">退出登录</text>
			</view> -->
		</view>


	</view>
</template>
<script>
	import listCell from '@/components/mix-list-cell';
	import minBadge from '@/components/min-badge/min-badge'
	// import {uniBadge} from '@dcloudio/uni-ui';
	import {
		mapState,
		mapMutations
	} from 'vuex';
	let startY = 0,
		moveY = 0,
		pageAtTop = true;
	export default {
		components: {
			listCell,
			minBadge
		},
		data() {
			return {
				couponList: [],
				bonus: 0,
				coverTransform: 'translateY(0px)',
				coverTransition: '0s',
				moving: false,
				footprintList: [],
				rurl: this.global.website + "/pages/index/index",
				yuYueCount: 0,
				orderCount: 0,
				userInfoCount: 0,
				backOrderCount:0,
				maxCount: 99,

			}
		},

		onShow() {
			this.checkIfLogin();
			
		},
		// #ifndef MP
		onNavigationBarButtonTap(e) {
			const index = e.index;
			if (index === 0) {
				this.navTo('/pages/set/set');
			} else if (index === 1) {
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
				})
			}
		},
		// #endif
		computed: {
			...mapState(['hasLogin', 'userInfo'])
		},
		methods: {
			...mapMutations(['login', 'logout']),

			//退出登录
			toLogout() {
				uni.showModal({
					content: '确定要退出登录么',
					success: (e) => {
						if (e.confirm) {
							this.logout();
							setTimeout(() => {
								uni.navigateBack();
							}, 200)
						}
					}
				});
			},
			/**
			 * 请求静态数据只是为了代码不那么乱
			 * 分次请求未作整合
			 */
			async checkIfLogin() {
				var that = this;
				that.$http({ //调用接口
					method: 'POST',
					url: this.global.target + "/wx/auth/checkToken" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {
						that.login(result.data);
						let userInfo = uni.getStorageSync('userInfo');
						if(userInfo.bonus!=null){
							that.bonus = userInfo.bonus;
						} else{
							that.bonus =0
						}
						
						if(userInfo.ticketsList!=null){
							that.couponList = userInfo.ticketsList;
						} else
						{
							that.couponList =[]
						}
						
	  
						// if(userInfo.vipCode!=null&&userInfo.vipCode!=''){
							that.isbind=true;
							that.getInfoCount();
						// } else
						// {
						//    that.navToLogin()
						// }

					} else {

					}
				}, function(error) {
					console.log(error.data)
				})
				that.getFootprint();
			},
			navToLogin() {

				uni.navigateTo({
					url: '/pages/public/login'
				})

			},
			getFootprint() {
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					url: this.global.target + "/wx/footprint/list" //this指data
				}).then(function(response) { //接口返回数据
					// console.log( response)
					that.footprintList = response.data.data.footprintList;
				}, function(error) {})

			},

			/**
			 * 统一跳转接口,拦截未登录路由
			 * navigator标签现在默认没有转场动画，所以用view
			 */
			navTo(url) {
				// if (!this.hasLogin) {
				// 	var that = this;
				// 	that.$http({ //调用接口
				// 		method: 'POST',
				// 		url: that.global.target + "/wx/auth/loginInit" //this指data
				// 	}).then(function(response) { //接口返回数据
				// 	
				// 		window.location.href = response.data;
				// 		
				// 	}, function(error) {
				// 	
				// 	})
				// 	// uni.navigateTo({
				// 	// 	url: '/pages/public/login'
				// 	// })
				// } else {
				uni.navigateTo({
					url
				})
				// }

			},

			/**
			 *  会员卡下拉和回弹
			 *  1.关闭bounce避免ios端下拉冲突
			 *  2.由于touchmove事件的缺陷（以前做小程序就遇到，比如20跳到40，h5反而好很多），下拉的时候会有掉帧的感觉
			 *    transition设置0.1秒延迟，让css来过渡这段空窗期
			 *  3.回弹效果可修改曲线值来调整效果，推荐一个好用的bezier生成工具 http://cubic-bezier.com/
			 */
			coverTouchstart(e) {
				if (pageAtTop === false) {
					return;
				}
				this.coverTransition = 'transform .1s linear';
				startY = e.touches[0].clientY;
			},
			coverTouchmove(e) {
				moveY = e.touches[0].clientY;
				let moveDistance = moveY - startY;
				if (moveDistance < 0) {
					this.moving = false;
					return;
				}
				this.moving = true;
				if (moveDistance >= 50 && moveDistance < 100) {
					moveDistance = 50;
				}

				if (moveDistance > 0 && moveDistance <= 50) {
					this.coverTransform = `translateY(${moveDistance}px)`;
				}
			},
			coverTouchend() {
				if (this.moving === false) {
					return;
				}
				this.moving = false;
				this.coverTransition = 'transform 0.3s cubic-bezier(.21,1.93,.53,.64)';
				this.coverTransform = 'translateY(0px)';
			},
			//获取我的预约，我的特产订单角标的数量,我的消息角标的数量
			getInfoCount() {
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					url: this.global.target + "/wx/user/getUserMyInfoCount" //this指data
				}).then(function(response) { //接口返回数据
					// console.log( response)
					that.yuYueCount = response.data.data.yuYueCount;
					that.orderCount = response.data.data.orderCount;
					that.userInfoCount = response.data.data.userInfoCount;
					that.backOrderCount = response.data.data.backOrderCount
				}, function(error) {})

			},
			kefu() {
				location.href = 'http://uclient.yunque360.com/frame.html?company_id=c38uk4hpcd36n';
			}
		}
	}
</script>
<style lang='scss'>
	.test {
		width: 100rpx;
		height: 100rpx;
		background: #dddddd;
	}

	.list-cell {
		display: flex;
		align-items: baseline;
		padding: 20upx $page-row-spacing;
		line-height: 60upx;
		position: relative;
		background: #fff;
		justify-content: center;

		&.log-out-btn {
			margin-top: 40upx;

			.cell-tit {
				color: #ff0000;
				text-align: center;
				margin-right: 0;
			}
		}

		&.cell-hover {
			background: #fafafa;
		}

		&.b-b:after {
			left: 30upx;
		}

		&.m-t {
			margin-top: 16upx;
		}

		.cell-more {
			align-self: baseline;
			font-size: $font-lg;
			color: $font-color-light;
			margin-left: 10upx;
		}

		.cell-tit {
			flex: 1;
			font-size: $font-base + 2upx;
			color: $font-color-dark;
			margin-right: 10upx;
		}

		.cell-tip {
			font-size: $font-base;
			color: $font-color-light;
		}

		switch {
			transform: translateX(16upx) scale(.84);
		}
	}

	.cell {
		margin: 22rpx;
	}

	%flex-center {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}

	%section {
		display: flex;
		justify-content: space-around;
		align-content: center;
		background: #fff;
		border-radius: 10upx;
	}

	.user-section {
		height: 520upx;
		padding: 30upx 26upx 0 23upx;
		position: relative;

		.bg {
			position: absolute;
			left: 0;
			top: 0;
			width: 100%;
			height: 100%;
			filter: blur(1px);
			opacity: .7;
		}
	}

	.user-info-box {
		height: 180upx;
		display: flex;
		align-items: center;
		position: relative;
		z-index: 1;

		.portrait {
			width: 143upx;
			height: 139upx;
			border: 5upx solid #fff;
			border-radius: 50%;
		}

		.username {
			font-size: 36upx;
			color: #fff;
			margin-left: 32upx;
		}

		.set-box {
			flex: 1;

			.icon {
				float: right;
				margin-left: 41upx;
			}

			image {
				width: 44upx;
				height: 40upx;
			}
		}
	}

	.vip-card-box {
		display: flex;
		flex-direction: column;
		color: #79572D;
		height: 240upx;
		background: linear-gradient(left, #FDF2D9, #E5C697);
		border-radius: 16upx 16upx 0 0;
		overflow: hidden;
		position: relative;
		padding: 20upx 24upx;

		.card-bg {
			position: absolute;
			top: 80upx;
			right: 20upx;
			width: 388upx;
			height: 112upx;
		}

		.b-btn {
			position: absolute;
			right: 20upx;
			top: 16upx;
			width: 132upx;
			height: 40upx;
			text-align: center;
			line-height: 40upx;
			font-size: 22upx;
			border-radius: 20px;
			background: #986C36;
			z-index: 1;

			text {
				background: linear-gradient(to right, #F4E8CF, #F5C78C);
				-webkit-background-clip: text;
				color: transparent;
			}
		}

		.tit {
			font-size: $font-base+2upx;
			margin-bottom: 28upx;

			.yticon {
				color: #f6e5a3;
				margin-right: 16upx;
			}
		}

		.e-b {
			font-size: $font-sm;
			color: #d8cba9;
			margin-top: 10upx;
		}
	}

	.cover-container {
		background: $page-color-base;
		margin-top: -150upx;
		position: relative;
		background: #f5f5f5;
		padding-bottom: 20upx;

		.arc {
			position: absolute;
			left: 0;
			top: -34upx;
			width: 100%;
			height: 36upx;
		}
	}

	.tj-sction {
		position: relative;

		::after {
			position: absolute;
			content: '';
			width: 1px;
			height: 80upx;
			background: #B4B4B4;
			top: 35upx;
			left: 50%;
		}

		@extend %section;

		.tj-item {
			@extend %flex-center;
			flex-direction: column;
			height: 149upx;
			font-size: 22upx;
			color: #000;
		}

		.num {
			font-size: 53upx;
			color: $font-color-dark;
			margin-bottom: 8upx;
		}
	}

	.order-section {
		@extend %section;
		padding: 29upx 0 26upx 0;
		margin-top: 20upx;

		.order-item {
			@extend %flex-center;
			width: 200upx;
			border-radius: 10upx;
			font-size: $font-sm;
			color: $font-color-dark;
		}

		.yticon {
			font-size: 48upx;
			margin-bottom: 18upx;
			color: #fa436a;
		}

		.icon-shouhoutuikuan {
			font-size: 44upx;
		}

		.order-icon1 {
			width: 39upx;
			height: 52upx;
		}

		.order-icon2 {
			width: 48upx;
			height: 52upx;
		}

		.order-icon3 {
			width: 55upx;
			height: 44upx;
			margin: 4upx 0 0 0;
		}

		.order-icon4 {
			width: 59upx;
			height: 50upx;
		}

		text {
			margin: 20upx 0 0 0;
		}
	}

	.history-section {
		/* padding: 30upx 0 0; */
		margin-top: 20upx;
		background: #fff;
		border-radius: 10upx;

		.sec-header {
			/* display: flex; */
			/* align-items: center; */
			font-size: $font-base;
			color: $font-color-dark;
			line-height: 87upx;
			padding: 0 27upx;

			/* margin-left: 30upx; */
			view {
				border-bottom: 1px solid #B4B4B4;
			}

			.yticon {
				font-size: 44upx;
				color: #5eba8f;
				margin-right: 16upx;
				line-height: 40upx;
			}
		}

		.h-list {
			white-space: nowrap;
			padding: 33upx 27upx 19upx;

			.h-scroll {
				display: inline-block;
				text-align: center;
				margin-right: 27upx;
			}

			image {
				display: block;
				width: 154upx;
				height: 146upx;
				/* border-radius: 10upx; */
			}

			text {
				color: #B4B4B4;
				font-size: 19upx;

			}
		}

		.h_address {
			border-top: 1px solid #f2f2f2;
			position: relative;
			padding-left: 69upx;

			&::after {
				position: absolute;
				content: "";
				width: 42upx;
				height: 41upx;
				background: url('/static/newimg/address.png') no-repeat;
				background-size: 100% 100%;
				left: 27upx;
				top: 27upx;
			}
		}

		.h_us {
			border-top: 1px solid #f2f2f2;
			position: relative;
			padding-left: 69upx;

			&::after {
				position: absolute;
				content: "";
				width: 48upx;
				height: 40upx;
				background: url('/static/newimg/service.png') no-repeat;
				background-size: 100% 100%;
				left: 27upx;
				top: 27upx;
			}
		}
		
		.h_help {
			border-top: 1px solid #f2f2f2;
			position: relative;
			padding-left: 69upx;
		
			&::after {
				position: absolute;
				content: "";
				width: 42upx;
				height: 41upx;
				background: url('/static/newimg/help.png') no-repeat;
				background-size: 100% 100%;
				left: 27upx;
				top: 27upx;
			}
		}
	}
</style>
