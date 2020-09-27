<template>
	<view class="container">
		<view class="mask" v-if="isExp"></view>
		<view class="detail-desc" v-for="(desc,index) in imglist" :key="index">
			<image style="width: 100%;" :src="desc.picUrl" mode="widthFix" @click="navToDetailPage(desc)"></image>
		</view>
		<view class="category" >
		
			<!-- 商品列表 -->
			<view class="guess-section" >
				<view class="guess-left">
					<block  v-for="(item, index) in plist" :key="index">
						<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==0">
							<view class="image-wrapper">
								<image :src="item.goodsPicUrl" ></image>
							</view>
							<view class="guess-s-bottom">
								<view class="title clamp">{{item.goodsName}}</view>
							</view>
							<view class="guess-bug">
								<text class="price">￥{{item.goodsPrice}}</text>
							</view>
						</view>
					</block>
				</view>
				<view class="guess-right">
					<block  v-for="(item, index) in plist" :key="index">
						<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==1">
							<view class="image-wrapper">
								<image :src="item.goodsPicUrl" mode="aspectFill"></image>
							</view>
							<view class="guess-s-bottom">
								<view class="title clamp">{{item.goodsName}}</view>
							</view>
							<view class="guess-bug">
								<text class="price">￥{{item.goodsPrice}}</text>
							</view>
						</view>
					</block>
				</view>
			</view>
			<!-- 商品列表 end-->
		</view>
		
	</view>
</template>

<script>
	import share from '@/components/share';
	import add1 from '../../static/add1.png';
	import add2 from '../../static/add2.png';
	import dis1 from '../../static/dis1.png';
	import dis2 from '../../static/dis2.png';
	import minBadge from '@/components/min-badge/min-badge'
	import wx from 'weixin-js-sdk'
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import UniCountdown from '@/components/uni-countdown/uni-countdown'
	// var wx = require('jweixin-module')
	export default {
		components: {
			...mapState(['hasLogin', 'userInfo']),
			share,
			UniCountdown,
			minBadge
		},
		data() {
			return {
				isExp:false,
				mode:'widthfix',
				huodongMain:undefined,
				imglist: [
					 //    {
						// picUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/bpzervj15nrjchpnfsk7.jpg',
						// goodsId:'1181209'},
						// {picUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/gpu7i334a01cen8eqh7m.jpg',
						// goodsId:'1181210'},
						// {picUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/gpu7i334a01cen8eqh7m.jpg',
						// goodsId:'1181211'},
						],
				plist:[
					// {goodsPicUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/bpzervj15nrjchpnfsk7.jpg',
					// 	goodsName:'御食园160g罐装果干系列',
					// 	goodsId:'1181209'
					// 	},
					// 	{goodsPicUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/a91qtyq1rlnq6pcpgcer.jpg',
					// 	 goodsName:'御食园160g罐装果',
					// 	 goodsId:'1181209'
					// 	},
					// 	{goodsPicUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/a91qtyq1rlnq6pcpgcer.jpg',
					// 	goodsName:'御食园160g罐装果干系列之芒果干',
					// 	goodsId:'1181209'
					// 	},
					// 	{goodsPicUrl:'https://xiaowan-img.oss-cn-beijing.aliyuncs.com/a91qtyq1rlnq6pcpgcer.jpg',
					// 	goodsName:'御食园',
					// 	goodsId:'1181209'
					// 	},
						],
			};
		},
		async onLoad(options) {
			//接收传值,这时传入的是商品ID,而不是产品规格型号ID
			let id = options.id;
			this.mainId = id;
			// if(id){
			// 	this.$api.msg(`点击了${id}`);
			// }
			var that = this;
			that.$http({
				//调用接口
				method: 'GET',
				params: {
					mainId: id
				},
				url: this.global.target + '/wx/huodong/index' //this指data
			}).then(
				function(response) {
					//接口返回数据
					var data = response.data.data;
					that.huodongMain=data;
					console.log(data);
					that.isExp=data.exp;
					if(that.isExp){
						that.$api.msg("活动已过期");
					}
					that.imglist=data.picLink;
					that.plist=data.goodsList;
					//分享信息
					that.getShareInfo();
				},
				function(error) {}
			);

		},
		methods: {
			...mapMutations(['login']),
			getShareInfo() {
				var that = this;

				var url = encodeURIComponent(window.location.href.split('#')[0]);
				if (window.__wxjs_is_wkwebview) {

					// return;
				} else {
					url = encodeURIComponent(window.location.href.split('#')[0]);
				}
				that.$http({
					//调用接口
					method: 'POST',
					params: {
						url: url
					},
					url: this.global.target + '/wx/share/info' //this指data
				}).then(
					function(response) {
						//接口返回数据
						let result = response.data.data;

						wx.config({
							debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
							appId: result.appid, // 必填，公众号的唯一标识
							timestamp: result.timestamp, // 必填，生成签名的时间戳
							nonceStr: result.nonceStr, // 必填，生成签名的随机串
							signature: result.signature, // 必填，签名
							jsApiList: ['onMenuShareAppMessage', 'onMenuShareTimeline','updateAppMessageShareData','updateTimelineShareData'] // 必填，需要使用的JS接口列表
						});
						that.wxready();
						
					},
					function(error) {

					}
				);

			},

			wxready() {
				var that =this;
			
				wx.ready(function() {
					//分享朋友圈
					wx.updateTimelineShareData({
						title: that.huodongMain.name, // 分享标题
						desc: "", // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: that.huodongMain.linkPicUrl, // 分享图标
						success: function() {
							// 用户确认分享后执行的回调函数
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
					
						}
					});
					// 分享好友
					wx.updateAppMessageShareData({
						title: that.huodongMain.goodsName, // 分享标题
						desc: "", // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: that.huodongMain.linkPicUrl, // 分享图标
						type: "", // 分享类型,music、video或link，不填默认为link
						dataUrl: "", // 如果type是music或video，则要提供数据链接，默认为空
						success: function() {
							
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
						}
					});
				});
			},


			refund(id) {
				var that = this;

				that.$http({
					//调用接口
					method: 'POST',
					params: {
						userId: uni.getStorageSync('userInfo').id,
						orderId: id
					},
					url: this.global.target + '/wx/order/refund' //this指data
				}).then(
					function(response) {
						//接口返回数据
						// console.log(response.data.data)
						var result = response.data;
						if(result.errno==501||result.errno==502||result.errno==504||result.errno==725){
							that.$api.msg(result.errmsg)
							return;
						}
						// if (result.errno == 0) {
						// 	let data = result.data.data;
						// } else {
						// 	that.$api.msg(result.errmsg);
						// }
					},
					function(error) {}
				);
			},

			//验证是否登录
			tologin() {
				// var that = this;
				// that.$http({ //调用接口
				// 	method: 'POST',
				// 	url: that.global.target + "/wx/auth/loginInit" //this指data
				// }).then(function(response) { //接口返回数据
				//
				// 	window.location.href = response.data;
				//
				// }, function(error) {
				//
				// })

				uni.navigateTo({
					url: '/pages/public/tologin'
				});
			},

			//加载购物车角标
			getcartNumber() {
				var that = this;
				//验证
				that.$http({ //调用接口
					method: 'GET',
					url: that.global.target + "/wx/cart/cartNumber" //this指data
				}).then(function(response) { //接口返回数据
					var result = response.data;
					that.cartNumberTotal = result.data;
					if (result.errno == 0) {
						console.log(result)
						uni.setTabBarBadge({
							index: 3,
							text: "" + result.data
						})

					} else {

					}
				}, function(error) {
					// that.$api.msg("系统错误");
				})
			},
			//详情页
			navToDetailPage(item) {
				let id = item.goodsId;
				uni.navigateTo({
					url: `/pages/product/product?id=${id}`
				})
			},

		}

	};
</script>
<style lang="scss">
	page {
		background: $page-color-base;
		padding-bottom: 160upx;
	}
	.mask {
		position: fixed;
		top:0;
		left:0;
		z-index:999;
		width:100%;
		height:100vh;
		background:rgba(0,0,0,0.4);
	}
	.icon-you {
		font-size: $font-base + 2upx;
		color: #888;
	}

	.carousel {
		height: 728upx;
		position: relative;

		swiper {
			height: 100%;
		}

		.image-wrapper {
			width: 100%;
			height: 100%;
		}

		.swiper-item {
			display: flex;
			justify-content: center;
			align-content: center;
			height: 750upx;
			overflow: hidden;

			image {
				width: 100%;
				height: 100%;
			}
		}
	}

	/* 标题简介 */
	.introduce-section {
		background: #fff;
		padding: 20upx 24upx 32upx 24upx;

		.redprice {
			font-size: 40upx;
			color: #fff;
			padding: 3upx 16upx;
			background: #ff0000;
			margin: 0 10upx 0 0;
		}

		.remainingTime {
			font-size: 40upx;
			color: #fff;
			padding: 3upx 16upx;
			background: #ff0000;
			margin: 0 10upx 0 0;
		}

		.title {
			font-size: 35upx;
			line-height: 40upx;

			.red {
				font-size: 20upx;
				color: #fff;
				padding: 3upx 16upx;
				background: #ff0000;
				border-radius: 15upx;
				margin: 0 10upx 0 0;
			}
		}

		.price-boxs {
			display: flex;
			justify-content: space-between;
			align-items: baseline;
			font-size: 30upx;
			color: #909090;
		}

		.price-zhe {
			font-size: 27upx;
			padding: 5upx 21upx;
			border: 1px solid currentColor;
			border-radius: 5upx;
			margin: 0 22upx 0 0;
		}

		.Kucun {
			color: black;
			font-size: 30upx;
			// padding: 5upx 21upx;		
			padding: 5upx 21upx;
			// border: 1px solid currentColor;
			// border-radius: 5upx;
			//margin-left: 10upx;
			margin: 0 22upx 0 0upx;
		}



		.price-box {
			view {
				margin: 16upx 0 0;
			}

			.now {
				color: #ff0000;
			}

			.old {
				color: #909090;
			}

			.price-zhe {
				font-size: 27upx;
				padding: 5upx 21upx;
				border: 1px solid currentColor;
				border-radius: 5upx;
				margin: 0 22upx 0 0;
			}

			.store-number {
				color: black;
				font-size: 27upx;
				//padding: 5upx 21upx;
				padding: 5upx 10upx;
				// border: 1px solid currentColor;
				// border-radius: 5upx;
				//margin: 0 22upx 0 40upx;
				margin: 0 0upx 0 10upx;
			}

			.store-price {
				color: black;
				font-size: 27upx;
				margin: 0;
				// font-weight: bold;

			}

			.price {
				color: #fff;
				font-weight: bold;
			}
		}

		.m-price {
			font-size: 28upx;
			margin: 0 12 0 10upx;
			color: $font-color-light;
			text-decoration: line-through;
		}

		.coupon-tip {
			align-items: center;
			padding: 4upx 10upx;
			background: $uni-color-primary;
			font-size: $font-sm;
			color: #fff;
			border-radius: 6upx;
			line-height: 1;
			transform: translateY(-4upx);
		}

		.bot-row {
			display: flex;
			align-items: center;
			height: 50upx;
			font-size: $font-sm;
			color: $font-color-light;

			text {
				flex: 1;
			}
		}
	}

	/* 分享 */
	.share-section {
		display: flex;
		align-items: center;
		color: $font-color-base;
		background: linear-gradient(left, #fdf5f6, #fbebf6);
		padding: 12upx 30upx;

		.share-icon {
			display: flex;
			align-items: center;
			width: 70upx;
			height: 30upx;
			line-height: 1;
			border: 1px solid $uni-color-primary;
			border-radius: 4upx;
			position: relative;
			overflow: hidden;
			font-size: 22upx;
			color: $uni-color-primary;

			&:after {
				content: '';
				width: 50upx;
				height: 50upx;
				border-radius: 50%;
				left: -20upx;
				top: -12upx;
				position: absolute;
				background: $uni-color-primary;
			}
		}

		.icon-xingxing {
			position: relative;
			z-index: 1;
			font-size: 24upx;
			margin-left: 2upx;
			margin-right: 10upx;
			color: #fff;
			line-height: 1;
		}

		.tit {
			font-size: $font-base;
			margin-left: 10upx;
		}

		.icon-bangzhu1 {
			padding: 10upx;
			font-size: 30upx;
			line-height: 1;
		}

		.share-btn {
			flex: 1;
			text-align: right;
			font-size: $font-sm;
			color: $uni-color-primary;
		}

		.icon-you {
			font-size: $font-sm;
			margin-left: 4upx;
			color: $uni-color-primary;
		}
	}

	.c-list {
		font-size: 27upx;
		margin: 25upx 0;

		.c-row {
			display: flex;
			align-items: center;
			padding: 0 27upx;
			position: relative;
			height: 80upx;
			line-height: 80upx;
			background: #fff;
		}

		.tit {
			width: 131upx;
			color: #909090;
		}

		.con {
			flex: 1;

			.selected-text {
				margin-right: 27upx;
				float: right;
			}
		}

		.bz-list {
			height: 40upx;
			font-size: $font-sm + 2upx;
			color: $font-color-dark;

			text {
				display: inline-block;
			}
		}

		.t-r {
			text-align: right;

			text {
				padding: 4upx 12upx;
				height: 33upx;
				border: 1px solid #ff0000;
				border-radius: 5upx;
			}
		}

		.con-list {
			flex: 1;
			display: flex;
			flex-direction: column;
			color: $font-color-dark;
			line-height: 40upx;
		}

		.top-25 {
			margin: 25upx 0;
		}

		.red {
			color: #ff0000;
		}
	}

	/* 评价 */
	.eva-section {
		display: flex;
		flex-direction: column;
		padding: 20upx 30upx;
		background: #fff;
		margin-top: 16upx;

		.e-header {
			display: flex;
			align-items: center;
			height: 70upx;
			font-size: $font-sm + 2upx;
			color: $font-color-light;

			.tit {
				font-size: $font-base + 2upx;
				color: $font-color-dark;
				margin-right: 4upx;
			}

			.tip {
				flex: 1;
				text-align: right;
			}

			.icon-you {
				margin-left: 10upx;
			}
		}
	}

	.eva-box {
		display: flex;
		padding: 20upx 0;

		.portrait {
			flex-shrink: 0;
			width: 80upx;
			height: 80upx;
			border-radius: 100px;
		}

		.right {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: $font-base;
			color: $font-color-base;
			padding-left: 26upx;

			.con {
				font-size: $font-base;
				color: $font-color-dark;
				padding: 20upx 0;
			}

			.bot {
				display: flex;
				justify-content: space-between;
				font-size: $font-sm;
				color: $font-color-light;
			}
		}
	}

	/*  详情 */
	.detail-desc {
		background: #fff;
		margin-top: -10upx;
		
	}





	
	.category{
	page,
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
		.content {
			height: 100%;
			/* background-color: #f8f8f8; */
			background-color: #f9f9f9;
		}
	
		.content {
			display: flex;
		}
	
		.left-aside {
			flex-shrink: 0;
			width: 200upx;
			height: 100%;
			background-color: #fff;
		}
	
		.f-item {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 100%;
			height: 100upx;
			font-size: 28upx;
			color: $font-color-base;
			position: relative;
	
			&.active {
				color: $base-color;
				background: #f8f8f8;
	
				&:before {
					content: '';
					position: absolute;
					left: 0;
					top: 50%;
					transform: translateY(-50%);
					height: 36upx;
					width: 8upx;
					background-color: $base-color;
					border-radius: 0 4px 4px 0;
					opacity: .8;
				}
			}
		}
	
		.right-aside {
			flex: 1;
			overflow: hidden;
			padding-left: 20upx;
		}
	
		.s-item {
			display: flex;
			align-items: center;
			height: 70upx;
			padding-top: 8upx;
			font-size: 28upx;
			color: $font-color-dark;
		}
	
		.t-list {
			display: flex;
			flex-wrap: wrap;
			width: 100%;
			background: #fff;
			padding-top: 12upx;
	
			&:after {
				content: '';
				flex: 99;
				height: 0;
			}
		}
	
		.t-item {
			flex-shrink: 0;
			display: flex;
			justify-content: center;
			align-items: center;
			flex-direction: column;
			width: 176upx;
			font-size: 26upx;
			color: #666;
			padding-bottom: 20upx;
	
			image {
				width: 140upx;
				height: 140upx;
			}
		}
	
		.c_tab {
			width: 100vw;
			position: fixed;
			z-index: 999;
			height: 67upx;
			top: 44px;
			background: #fff;
			display: flex;
			display: -webkit-flex;
			font-size: 29upx;
			/* box-shadow:0px 0px 14px 0px rgba(0, 0, 0, 0.15); */
			/* border-bottom: 2upx solid #f8f8f8; */
			/* padding: 0 20upx;
			box-sizing: border-box; */
	
		}
	
		.cg_f_list {
			/* display: flex;
			display: -webkit-flex;
			width: 100vw;
			flex-wrap: wrap;
			-webkit-flex-wrap: wrap; */
			white-space: nowrap;
			overflow: auto;
			width: 750upx;
			/* width: 650upx; */
		}
	
		.cg_c_list {
			display: flex;
			display: -webkit-flex;
			flex-wrap: wrap;
			-webkit-flex-wrap: wrap;
			margin: 20upx 0 0;
			position: fixed;
			z-index: 99;
			top: 44px;
			margin: 110upx 0 0 0;
			background: #fff;
		}
	
		.cg_show_cl {
			width: 100upx;
			height: 100upx;
			/* padding: 20upx; */
			/* box-sizing: border-box; */
	
		}
	
		.cg_show_cl .cd_show_btnimg {
			width: 100%;
			height: 100%;
		}
	
		.cg_f_li {
			display: inline-block;
			padding: 0upx 35upx;
			box-sizing: border-box;
			line-height: 67upx;
			position: relative;
			
			&:after {
				position: absolute;
				content: '';
				width: 1px;
				height: 30upx;
				background: #E7E7E7;
				right: 0;
				top:17upx;
			}
		}
		
		.cg_c_list .active {
			border: 2upx solid rgb(250, 67, 106);
		}
	
		.cg_c_li {
			padding: 10upx 14upx;
			box-sizing: border-box;
			font-size: 32upx;
			border-radius: 8upx;
			border: 2upx solid #f8f8f8;
			margin: 10upx;
		}
	
		.active {
			color:#FF0000;
	
		}
	
	/* 	.cg_f_list .active::after {
			position: absolute;
			content: '';
			width: 88%;
			height: 2upx;
			background: rgb(250, 67, 106);
			left: 6%;
			bottom: 0;
		}
	 */
		.guess-section {
			display: flex;
			flex-wrap: wrap;
			padding: 0 27upx;
			width: 100vw;
			margin: 67upx 0 100upx 0;
			.guess-left,.guess-right{
				flex: 1;
				display: flex;
				flex-wrap: wrap;
				flex-direction: column;
			}
			.guess-left{
				padding-right: 10upx;
			}
			.guess-right{
				padding: 0 0 0 10upx;
			}
			.guess-item {
				display: flex;
				flex-direction: column;
				width:100%;
				flex-shrink: 0;
				padding-bottom: 22upx;
				margin: 17upx 0 0 0;
				background: #fff;
				box-shadow:0px 0px 9upx 0px rgba(47,47,47,0.1);
				
			}
	
			.image-wrapper {
				width: 100%;
				height: 327upx;
				border-radius: 3px;
				overflow: hidden;
	
				image {
					width: 100%;
					height: 100%;
					opacity: 1;
				}
			}
	
			.title {
				font-size:29upx;
				font-weight:bold;
				font-weight:800;
				color: $font-color-dark;
				margin: 14upx 0 0;
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-line-clamp: 2;
				-webkit-box-orient:vertical;
			}
	
			.price {
				font-size:24upx;
				color: #FF3B30;
				font-weight: bold;
			}
			.guess-s-bottom{
				padding: 0 27upx 0 11upx;
				display: flex;
				justify-content: left;
				.clamp{
					white-space: normal;
				}
				.clamp text{
					background: #FF0000;
					color: #fff;
					font-size: 19upx;
					padding: 6upx 15upx;
					border-radius:13upx;
					line-height: 18upx;
					display: inline-block;
					margin: 0 10upx 0 13upx;
				}
			}
			.guess-bug{
				padding: 0 27upx 0 11upx;
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin: 3upx 0 0 0;
				image{
					width: 48upx;
					height: 48upx;
				}
			}
	
		}
	}
</style>
