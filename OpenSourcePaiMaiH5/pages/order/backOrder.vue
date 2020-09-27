<template>
	<view class="content">
		

		<swiper :current="tabCurrentIndex" class="swiper-box" duration="300" @change="changeTab">
			<swiper-item class="tab-content" v-for="(tabItem,tabIndex) in navList" :key="tabIndex">
				<scroll-view class="list-scroll-content" scroll-y @scrolltolower="loadData">
					<!-- 空白页 -->
					<empty v-if="tabItem.loaded === true && tabItem.orderList.length === 0"></empty>

					<!-- 订单列表 -->
					<view v-for="(item,index) in tabItem.orderList" :key="index" class="order-item">
						<view class="i-top">
							<text class="time">
								订单编号：{{item.orderSn}}
							</text>
							<text class="state" :style="{color: item.stateTipColor}">{{item.stateTip}}</text>
						</view>


						<view class="goods-box-single" v-for="(goodsItem, goodsIndex) in item.goodsList"
						 :key="goodsIndex">
							<image class="goods-img" :src="goodsItem.image" mode="aspectFill"></image>
							<view class="right">
								<text class="title clamp">{{goodsItem.title}}</text>
								<text class="attr-box"><text v-for="(i) in goodsItem.attr">{{i+" "}}</text>数量：{{goodsItem.number}}</text>
							</view>
							<view class="price">￥{{goodsItem.price}}</view>
						</view>

						 <!-- 待发货  待退款-->
						<view class="action-box b-t" v-if="item.state == 202 || item.state == 203 ">
							<button v-if='item.actualPrice==0' class="action-btn" @click="cancelOrder(item)">取消订单</button>
							<button class="action-btn" @click="orderDetail(item.id)">查看详情</button>
							<button class="action-btn recom" @click="orderBack(item.id)" 
							v-if="item.actualPrice>0 &&( item.state == 201|| item.state == 250||
							(item.adminAllowRefund==true&&item.state!=202&&item.state!=203))">申请退款</button>
						</view>

					</view>
					<uni-load-more :status="tabItem.loadingType"></uni-load-more>
         
				</scroll-view>
			</swiper-item>
		</swiper>
	</view>
</template>

<script>
	import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
	import empty from "@/components/empty";
	import minBadge from '@/components/min-badge/min-badge'
	import Json from '@/Json';
	export default {
		components: {
			uniLoadMore,
			empty,
			minBadge
		},
		data() {
			return {
				tabCurrentIndex: 0,
				navList: [{
						state: 6,
						text: '全部单',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0,
					},
					{
						state: 1,
						text: '待付款',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0,
					},
										{
						state: 2,
						text: '待发货',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0
					},
					{
						state: 3,
						text: '待收货',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0,
					},
					{
						state: 4,
						text: '已完成',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0,
					},
					{
						state: 5,
						text: '已取消',
						loadingType: 'more',
						orderList: [],
						page: 1,
						size: 10,
						count:0,
					},
					
				],
			};
		},

		onLoad(options) {
			/**
			 * 修复app端点击除全部订单外的按钮进入时不加载数据的问题
			 * 替换onLoad下代码即可
			 */
			// this.tabCurrentIndex = +options.state;
			// // #ifndef MP
			// this.loadData()
			// // #endif
			// // #ifdef MP
			// if (options.state == 0) {
			// 	this.loadData()
			// }
			// #endif

		},
		onShow(){
			this.loadCount();
			let index = this.tabCurrentIndex;
			let navItem = this.navList[index];
			this.navList[index].orderList=[];
			this.navList[index].page=1;
			this.navList[index].loadingType='more';
			this.loadData('tabChange');
		},

		methods: {
				//加载单据数量
			loadCount(){
			    var that = this;
				that.$http({ //调用接口
					method: 'GET',
					url: this.global.target + "/wx/order/onLinePayListCount" //this指data
				}).then(function(response) { //接口返回数据
					// console.log( response)
					that.navList[0].count = response.data.data.myAllOrderCount;
					that.navList[1].count = response.data.data.myDaiFuKuanCount;
					that.navList[2].count = response.data.data.myDaiFaHuoCount;
					that.navList[3].count = response.data.data.myDaiShouHuoCount;
					that.navList[4].count = response.data.data.myYiWanChengCount;
					that.navList[5].count = response.data.data.myYiQuXiaoCount;
				}, function(error) {})
			},
			// 
			toorderdetail(id) {
				uni.navigateTo({
					url: '/pages/order/orderdetail?goodsid=' + id
				})
			},
			//获取订单列表
			loadData(source) {
				//这里是将订单挂载到tab列表下
				let index = this.tabCurrentIndex;
				let navItem = this.navList[index];
				let state = navItem.state;
				let page = navItem.page;
				let size = navItem.size;

				if (source === 'tabChange' && navItem.loaded === true) {
					//tab切换只有第一次需要加载数据
					this.navList[index].orderList=[];
					this.navList[index].page=1;
					this.navList[index].loadingType='more';
					// return;
				}

				if (navItem.loadingType == 'loading' || navItem.loadingType == 'noMore') {
					//防止重复加载
					return;
				}

				navItem.loadingType = 'loading';
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					params: {
						page: page,
						limit: size,
						showType: state
					},
					url: this.global.target + "/wx/order/list" //this指data
				}).then(function(response) { //接口返回数据
					// console.log(response.data.data)
					var result = response.data;
					if (result.errno == 0) {
						let data = result.data.data;
						let totalPages = result.data.totalPages;
						data.forEach(item => {
							//添加不同状态下订单的表现形式
							item = Object.assign(item, that.orderStateExp(item.state));
							//演示数据所以自己进行状态筛选
							if (state === 0) {
								//0为全部订单
								return item;
							}
							return item.state === state
						});
						data.forEach(item => {
							navItem.orderList.push(item);
						})
						//loaded新字段用于表示数据加载完毕，如果为空可以显示空白页
						that.$set(navItem, 'loaded', true);
						//判断是否还有数据， 有改为 more， 没有改为noMore 
						if (page >= totalPages) {
							navItem.loadingType = 'noMore';
						} else {
							navItem.loadingType = 'more';
						}
						navItem.page = ++page;

					} else {
						that.$api.msg(result.errmsg)
					}
				}, function(error) {})
				console.log(navItem)

			},

			//swiper 切换
			changeTab(e) {
				this.tabCurrentIndex = e.target.current;
				this.navList[e.target.current].page=1;
				this.loadData('tabChange');
			},
			//顶部tab点击
			tabClick(index) {
				this.tabCurrentIndex = index;
				this.navList[index].page=1;
			},
			//删除订单
			deleteOrder(item) {
				var that = this;
				that.$http({ //调用接口
					method: 'POST',
					params: {
						orderId: item.id
					},
					url: this.global.target + "/wx/order/delete" //this指data
				}).then(function(response) { //接口返回数据
					// console.log(response.data.data)
					var result = response.data;
					if (result.errno == 0) {
						let data = result.data.data;

					} else {
						that.$api.msg(result.errmsg)
					}
				}, function(error) {})

				uni.showLoading({
					title: '请稍候'
				})
				setTimeout(() => {
					this.navList[this.tabCurrentIndex].orderList.splice(index, 1);
					uni.hideLoading();
				}, 600)
			},
			//取消订单
			cancelOrder(item) {
				var that = this;
				uni.showModal({
					title: '',
					content: '确认取消订单吗？',
					success: function (res) {
						if (res.confirm) {
							that.$http({ //调用接口
								method: 'POST',
								params: {
									orderId: item.id
								},
								url: that.global.target + "/wx/order/cancel" //this指data
							}).then(function(response) { //接口返回数据
								// console.log(response.data.data)
								var result = response.data;
								if (result.errno == 0) {
									let data = result.data;
									//执行
									//成功后执行
									uni.showLoading({
										title: '请稍候'
									})
									setTimeout(() => {
										let {
											stateTip,
											stateTipColor
										} = that.orderStateExp(9);
										item = Object.assign(item, {
											state: 9,
											stateTip,
											stateTipColor
										})
									
										//取消订单后删除待付款中该项
										let list = that.navList[1].orderList;
										let index = list.findIndex(val => val.id === item.id);
										index !== -1 && list.splice(index, 1);
										uni.hideLoading();
									}, 600)							
								} else {
									that.$api.msg(result.errmsg)
								}
							}, function(error) {})

						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				});
				
			},

			//订单状态文字和颜色
			orderStateExp(state) {
				let stateTip = '',
					stateTipColor = '#fa436a';
				switch (+state) {
					case 101:
						stateTip = '待付款';
						break;
					case 201:
						stateTip = '待发货';
						break;
					case 401:
					case 402:
						stateTip = '订单已关闭';
						stateTipColor = '#909399';
						break;
					case 102:
					case 103:
						stateTip = '已取消';
						stateTipColor = '#909399';
						break;
					case 202://待退款
						stateTip = '待退款';
						break;
					case 203://已退款
						stateTip = '已退款';
						stateTipColor = '#909399';
						break;
					//更多自定义
				}
				return {
					stateTip,
					stateTipColor
				};
			},
			orderDetail(id) {
				console.log(9887)
				uni.navigateTo({
					url: `/pages/order/orderdetail?id=` + id
				})
			},
			orderTerminat(id){
				var that = this;
				 uni.showModal({
					title: '',
					content: '确认已经收货？',
					success: function (res) {
						if (res.confirm) {
							that.$http({ //调用接口
								method: 'POST',
								params: {
									orderId: id
								},
								url: that.global.target + "/wx/order/confirm" //this指data
							}).then(function(response) { //接口返回数据
								// console.log(response.data.data)
								var result = response.data;
								that.orderDetail(id);
							}, function(error) {})
						} else if (res.cancel) {
							// console.log('用户点击取消');
						}
					}
				});
				
			},
			orderBack(id){
					var that = this;
					 uni.showModal({
						title: '',
						content: '确定要申请退款吗？',
						success: function (res) {
							if (res.confirm) {
								that.$http({ //调用接口
									method: 'POST',
									params: {
										orderId: id
									},
									url: that.global.target + "/wx/order/refund" //this指data
								}).then(function(response) { //接口返回数据
									// console.log(response.data.data)
									var result = response.data;
									if(result.errno==501||result.errno==502||result.errno==504||result.errno==725){
										that.$api.msg(result.errmsg)
										return;
									}
									that.orderDetail(id);
								}, function(error) {})
							} else if (res.cancel) {
								// console.log('用户点击取消');
							}
						}
					});
			},
			navToPayPage(item) {
				let id = item.id;
				let orderSn = item.orderSn;
				let pay = item.actualPrice;
				console.log(item)
				uni.navigateTo({
					url: `/pages/money/pay?orderId=` + id + '&ordersn=' + orderSn + '&pay=' + pay
				})
			},
			refund(id){
				var that = this;
			 
				that.$http({ //调用接口
					method: 'POST',
					params: {
						orderId: id
					},
					url: this.global.target + "/wx/order/refund" //this指data
				}).then(function(response) { //接口返回数据
					// console.log(response.data.data)
					var result = response.data;
					
					if(result.errno==501||result.errno==502||result.errno==504||result.errno==725){
						that.$api.msg(result.errmsg)
						return;
					}
				 
					// if (result.errno == 0) {
					// 	let data = result.data;		
					// } else {
					// 	that.$api.msg(result.errmsg)
					// }
					
				}, function(error) {})
				
			}
		}
	}
</script>

<style lang="scss">
	page,
	.content {
		background: $page-color-base;
		height: 100%;
	}
   .threeWordWidth {
    width: 105rpx;
   }
	.swiper-box {
		height: calc(100% - 40px);
	}

	.list-scroll-content {
		height: 100%;
	}

	.navbar {
		display: flex;
		height: 80upx;
		padding: 17upx 27upx 0 27upx;
		background: #fff;
		justify-content: space-between;
		position: relative;
		z-index: 10;

		.nav-item {
			height: 100%;
			font-size: 29upx;
			position: relative;

			&.current {

				&:after {
					content: '';
					position: absolute;
					left: 50%;
					bottom: 13upx;
					transform: translateX(-50%);
					width: 100%;
					height: 5upx;
					background: url(../../static/newimg/order.png);
					background-size: 100% 100%;
				}
			}
		}
	}

	.uni-swiper-item {
		height: auto;
	}

	.order-item {
		display: flex;
		flex-direction: column;
		padding:0 27upx;
		background: #fff;
		margin-top: 27upx;

		.i-top {
			display: flex;
			align-items: center;
			height: 71upx;
			font-size: 21upx;
			color: #909090;
			position: relative;
			.icon-you{
				color: #000000;
				margin: 0 0 0 20upx;
				font-size: 20upx;
			}
			.time {
				flex: 1;
			}

			.state {
				color: $base-color;
			}

			.delete_img{
				width: 44upx;
				height: 44upx;
			}
		}

		/* 多条商品 */
		.goods-box {
			height: 160upx;
			padding: 20upx 0;
			white-space: nowrap;

			.goods-item {
				width: 120upx;
				height: 120upx;
				display: inline-block;
				margin-right: 24upx;
			}

			.goods-img {
				display: block;
				width: 100%;
				height: 100%;
			}
		}

		/* 单条商品 */
		.goods-box-single {
			display: flex;
			padding:0 0 20upx 0;
			align-items: center;
			.goods-img {
				display: block;
				width: 154upx;
				height: 146upx;
				flex-shrink: 0;
			}

			.right {
				flex: 1;
				display: flex;
				flex-direction: column;
				padding: 0 32upx 0 27upx;
				overflow: hidden;

				.title {
					font-size:32upx;
					line-height: 1;
				}

				.attr-box {
					font-size:20upx;
					color: #909090;
				}

				.price {
					font-size:19upx;
					font-weight: bold;
					flex-shrink: 0;
					&:before {
						content: '￥';
						font-size: $font-sm;
						margin: 0 2upx 0 8upx;
					}
				}
			}
		}

		.price-box {
			display: flex;
			justify-content: flex-end;
			align-items: baseline;
			padding: 20upx 30upx;
			font-size: $font-sm + 2upx;
			color: $font-color-light;

			.num {
				margin: 0 8upx;
				color: $font-color-dark;
			}

			.price {
				font-size: $font-lg;
				color: $font-color-dark;

				&:before {
					content: '￥';
					font-size: $font-sm;
					margin: 0 2upx 0 8upx;
				}
			}
		}

		.action-box {
			display: flex;
			justify-content: flex-end;
			align-items: center;
			height: 100upx;
			position: relative;
			padding-right: 30upx;
		}

		.action-btn {
			width: 160upx;
			height: 60upx;
			margin: 0;
			margin-left: 24upx;
			padding: 0;
			text-align: center;
			line-height: 60upx;
			font-size: $font-sm + 2upx;
			color: $font-color-dark;
			background: #fff;
			border-radius: 100px;

			&:after {
				border-radius: 100px;
			}

			&.recom {
				background: #fff9f9;
				color: $base-color;

				&:after {
					border-color: #f7bcc8;
				}
			}
		}
	}


	/* load-more */
	.uni-load-more {
		display: flex;
		flex-direction: row;
		height: 80upx;
		align-items: center;
		justify-content: center
	}

	.uni-load-more__text {
		font-size: 28upx;
		color: #999
	}

	.uni-load-more__img {
		height: 24px;
		width: 24px;
		margin-right: 10px
	}

	.uni-load-more__img>view {
		position: absolute
	}

	.uni-load-more__img>view view {
		width: 6px;
		height: 2px;
		border-top-left-radius: 1px;
		border-bottom-left-radius: 1px;
		background: #999;
		position: absolute;
		opacity: .2;
		transform-origin: 50%;
		animation: load 1.56s ease infinite
	}

	.uni-load-more__img>view view:nth-child(1) {
		transform: rotate(90deg);
		top: 2px;
		left: 9px
	}

	.uni-load-more__img>view view:nth-child(2) {
		transform: rotate(180deg);
		top: 11px;
		right: 0
	}

	.uni-load-more__img>view view:nth-child(3) {
		transform: rotate(270deg);
		bottom: 2px;
		left: 9px
	}

	.uni-load-more__img>view view:nth-child(4) {
		top: 11px;
		left: 0
	}

	.load1,
	.load2,
	.load3 {
		height: 24px;
		width: 24px
	}

	.load2 {
		transform: rotate(30deg)
	}

	.load3 {
		transform: rotate(60deg)
	}

	.load1 view:nth-child(1) {
		animation-delay: 0s
	}

	.load2 view:nth-child(1) {
		animation-delay: .13s
	}

	.load3 view:nth-child(1) {
		animation-delay: .26s
	}

	.load1 view:nth-child(2) {
		animation-delay: .39s
	}

	.load2 view:nth-child(2) {
		animation-delay: .52s
	}

	.load3 view:nth-child(2) {
		animation-delay: .65s
	}

	.load1 view:nth-child(3) {
		animation-delay: .78s
	}

	.load2 view:nth-child(3) {
		animation-delay: .91s
	}

	.load3 view:nth-child(3) {
		animation-delay: 1.04s
	}

	.load1 view:nth-child(4) {
		animation-delay: 1.17s
	}

	.load2 view:nth-child(4) {
		animation-delay: 1.3s
	}

	.load3 view:nth-child(4) {
		animation-delay: 1.43s
	}

	@-webkit-keyframes load {
		0% {
			opacity: 1
		}

		100% {
			opacity: .2
		}
	}
</style>
