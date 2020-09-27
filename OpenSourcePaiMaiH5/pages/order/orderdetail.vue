<template>
	<view class="orderlist">
		<!-- 发货状态+物流+收货地址 -->
		<view class="ol_top">
			<view class="ol_top_mai">
				<view class="ol_topmai_left">
					<view class="ol_topm_top" v-if="goods.status==101 && contentTime<=0">已超时取消</view>
					<view class="ol_topm_top" v-else>{{goods.mai}}</view>
					<view class="ol_topm_time" v-if="goods.status==101 && contentTime>0">还剩{{contentTime}}分钟自动取消</view>
				</view>
				<image src="../../static/car.png" mode="" class="ol_top_img"></image>
			</view>
			<!-- 	<view class="ol_top_wuliu">
				<view class="ol_topw_img">
					<image src="../../static/wuliu.png" class="icon_wuliu" mode=""></image>
				</view>
				<view class="ol_topw_status">
					<view class="ol_topw_text">{{goods.wuliu}}</view>
					<view class="ol_topw_time">{{goods.wuliutime}}</view>
				</view>
			</view> -->
			<view class="ol_top_address">
				<view class="ol_topw_img">
					<image src="../../static/address.png" class="icon_wuliu" mode=""></image>
				</view>
				<view class="ol_topw_status">
					<view class="ol_topa_user">
						<text class="name">{{goods.username}}</text>
						<text>{{goods.phone}}</text>
					</view>
					<view class="ol_topa_text">{{goods.address}}</view>
				</view>
			</view>
		</view>
		<!-- 发货状态+物流+收货地址 end-->
		<!-- 商品信息 -->
		<view class="ol_goods">
			<view   @click="navToDetailPage(item)"  class="ol_goods_li" v-for="(item,index) in goodslist" :key="index">
				<image :src="item.img" class="ol_goodsimg" mode=""></image>
				<view class="ol_goods_right">
					<view class="ol_goods_info">
						<view class="ol_goods_title">{{item.title}}</view>
						<view class="ol_roogs_num">
							<view class="price">￥{{item.price}}</view>
							<view class="num">x{{item.num}}</view>
						</view>
					</view>
					<view class="ol_goods_size"><text v-for="(i) in item.size">{{i+" "}}</text></view>
					<text class="ol_goods_other" v-show="item.others">{{item.others}}</text>
					<!-- <view class="ol_goods_back">退款</view> -->
				</view>
			</view>
			<view class="ol_goods_count">
				<view class="ol_goodsc_gray">
					<text>商品总价</text><text class="right">￥{{goodscount.countgoods}}</text>
				</view>
				<view class="ol_goodsc_gray" v-if="goodscount.kuaidi>0">
					<text>运费（快递）</text><text class="right">+￥{{goodscount.kuaidi}}</text>
				</view>
				<view class="ol_goodsc_black">
					<text>订单总价</text><text class="right">￥{{goodscount.countorder}}</text>
				</view>
			</view>
			<view class="ol_goods_pay">
				<view class="ol_goodsp_top">
					<text class="right">支付明细</text>
				</view>
				<view class="ol_goodsc_gray" v-if="goodscount.cut>0">
					<text>优惠券</text><text class="right">-￥{{goodscount.cut}}</text>
				</view>
				<view class="ol_goodsc_gray" v-if="goodscount.integralPrice>0">
					<text>积分抵现</text><text class="right">-￥{{goodscount.integralPrice}}</text>
				</view>
				<view class="ol_goods_paytrue">
					<text>实付款</text>
					<text class="right red">￥{{goodscount.pay}}</text>
				</view>
			</view>
		</view>
		<!-- 自提信息 -->
		<view class="ol_goods_infos" v-if="goodscount.send_way=='航站楼取货'">
			<view class="ol_goodsi_title">
				提货信息
			</view>
			<view class="ol_goods_orderinfo">

				<view class="info_li">
					<text>取货航站楼：</text>
					<text>{{goodscount.PickSiteName}}</text>
				</view>

				<view class="info_li">
					<text>取货时间：</text>
					<text>{{goodscount.PickTime}}</text>
				</view>
				<view class="info_li">
					<text>取货人真实姓名：</text>
					<text>{{goodscount.PickPerson}}</text>
				</view>
				<view class="info_li">
					<text>取货人手机号码：</text>
					<text>{{goodscount.PickMobile}}</text>
				</view>
			</view>
		</view>
		<!-- 商品信息 end-->
		<!-- 订单信息 -->
		<view class="ol_goods_infos">
			<view class="ol_goodsi_title">
				订单信息
			</view>
			<view class="ol_goods_orderinfo">
				<!--<view class="info_li">
					<text>获得积分：</text>
					<text>{{goodscount.jifen}}</text>
				</view> -->
				<view class="info_li">
					<text>订单编号：</text>
					<text>{{goodscount.ordernumber}}</text>
					<!-- <text class="copy">复制</text> -->
				</view>
				<!-- <view class="info_li" hidden="{autoHidden2}">
					<text>支付宝交易号：</text>
					<text>{{goodscount.countnumber}}</text>
				</view> -->
				<view class="info_li">
					<text>创建时间：</text>
					<text>{{goodscount.createtime}}</text>
				</view>
				<view class="info_li" v-if="goods.status==201 || goods.status==301 || goods.status==401">
					<text>付款时间：</text>
					<text>{{goodscount.paytime}}</text>
				</view>
				<view class="info_li" v-if="goods.status>=301">
					<text>承运商：</text>
					<text>{{goodscount.kuaidiCom}}</text>
				</view>
				<view class="info_li" v-if="goods.status>=301">
					<text>运单号：</text>
					<text>{{goodscount.shipSn}}</text>
					<!-- <view @click="copy()">(点击复制)</view> -->
				</view>
				<view class="info_li" v-if="goods.status>=401">
					<text>收货时间：</text>
					<text>{{goodscount.receiveTime}}</text>
				</view>
			</view>
		</view>

		<!-- 订单信息 end-->
		<!-- 悬浮固定 -->
		<view class="ol_bottom">
			<!-- <view class="ol_bottom_wuliu">
				查看物流
			</view> -->

			<view class="ol_bottom_ok" v-if="(goods.status==101 && contentTime>0)||
			(goods.actualPrice==0 && (goods.status == 201 || goods.status == 250))" @click="cancelOrder(goods)">
				取消订单
			</view>
			<view class="ol_bottom_ok" v-if="goods.status==301" @click="orderTerminat()">
				确认收货
			</view>
			<view class="ol_bottom_ok" v-if="goods.status==101 && contentTime>0" @click="topay()">
				去支付
			</view>
			<view class="ol_bottom_ok" v-if="goods.status==201" @click="tixingfahuo()">
				提醒发货
			</view>
			<view class="ol_bottom_ok" v-if="goods.actualPrice>0 && ( goods.status == 201||goods.status == 250)" @click="refund()">
				申请退款
			</view>
		</view>
		<!-- 悬浮固定 end-->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				orderId:0,
				goods: {
					//发货状态+物流+收货地址
					// "mai": "卖家已发货",
					// "maitime": "1562221563",
					// "wuliu": "等待揽收中等待揽收中等待揽收中等待揽收中等待揽收中等待揽收中等待揽收中等待揽收中",
					// "wuliutime": "2019-06-04 08:55:55",
					// "username":"张三",
					// "phone":"12345678910",
					// "address":"海上生明月，春潮连海平。海上生明月，春潮连海平。海上生明月，春潮连海平。"
				},
				goodslist: [
					//商品信息列表
					// {
					// 	title:"海上生明月海上生明月海上生明月海上生明月海上生明月海上生明月海上生明月海上生明月",
					// 	img:'../../static/temp/ad2.jpg',
					// 	price:'33.00',
					// 	num:1,
					// 	size:'白色',
					// 	others:"15天价保"
					// }

				],
				goodscount: {
					// //订单信息+商品信息
					// "countgoods":"36",
					// "kuaidi":"8.00",
					// "cut":"2.30",
					// "countorder":"123.00",
					// "redbag":"1.23",
					// "pay":"120",
					// "jifen":"10",
					// "ordernumber":"123456789",
					// "countnumber":"11112222444555",
					// "createtime":"2019-06-03 02:10:20",
					// "paytime":"2019-06-03 02:11:20",
					// "carttime":"2019-06-03 03:11:20",
				},
				contentTime: ''
			};
		},
		created() {
			var daoTime = this.goods.maitime - Math.round(new Date().getTime() / 1000).toString();
			var hour = Math.floor(daoTime / (3600 * 24));
			var minute = (daoTime - hour * 3600 * 24) / 3600;
			var contentTime = (minute > 23 ? hour + 1 : hour) + '天' + (minute > 23 ? 0 : Math.ceil(minute)) + '分'
			this.contentTime = contentTime;
		},
		async onLoad(options) {

			let id = options.id;
			this.orderId=id;
            this.loadData(id);
		},

		methods: {
			loadData(id){
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					params: {
						orderId: id
					},
					url: this.global.target + "/wx/order/detail" //this指data
				}).then(function(response) { //接口返回数据
					// console.log(response.data.data)
					var data = response.data.data;
					console.log(response)
					var state = data.orderInfo.status;
					that.goods = data.orderInfo;
					console.log(that.goods)
					var date = Math.round(new Date(Date.parse(that.goods.addTime.replace(/-/g, "/"))).getTime() / 1000).toString();
				
					var daoTime = Math.round(new Date().getTime() / 1000).toString() - date;
					console.log(daoTime)
					//计算30分钟的时间数
					// daoTime=30*60-daoTime;
					
					//从后台取设置的时间
					that.$http({ //调用接口
						method: 'GET',
						params: {},
						url: that.global.target + "/wx/config/unPayCancelMinutes" //this指data
					}).then(function(resMinutes) { //接口返回数据
					   let unPayCancelMinutes=resMinutes.data.data;
					   if(unPayCancelMinutes==null){
						   unPayCancelMinutes=30
					   }  
					   daoTime = Math.floor((unPayCancelMinutes * 60 - daoTime) / 60);
					   that.contentTime = daoTime;
					})
					
					// daoTime = Math.floor((60 * 60 - daoTime) / 60);
					// that.contentTime = daoTime;
				
				
					that.goodslist = data.orderGoods;
					that.goodscount = data.orderInfo2;
				
				}, function(error) {})
				
				 
				// this.shareList = await this.$api.json('shareList');
			},
			//复制
			copy(){
				wx.setClipboardData({
								data: 'www.petout.cn',
								success: function(res) {
									wx.getClipboardData({
										success: function(res) {
											wx.showToast({
												title: '已复制到剪贴板'
											});
										}
									});
								}
							});
			},
			
			//取消订单
			cancelOrder(item) {
				var that = this;
				uni.showModal({
					title: '',
					content: '确认取消订单吗？',
					success: function(res) {
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
									//成功后执行，重新加載
									that.loadData(item.id)
									
									// uni.showLoading({
									// 	title: '请稍候'
									// })
									// setTimeout(() => {
									// 	let {
									// 		stateTip,
									// 		stateTipColor
									// 	} = that.orderStateExp(9);
									// 	item = Object.assign(item, {
									// 		state: 9,
									// 		stateTip,
									// 		stateTipColor
									// 	})

									// 	//取消订单后删除待付款中该项
									// 	let list = that.navList[1].orderList;
									// 	let index = list.findIndex(val => val.id === item.id);
									// 	index !== -1 && list.splice(index, 1);
									// 	uni.hideLoading();
									// }, 600)
									this.loadData(that.orderId);
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

			//详情
			navToDetailPage(item) {
				//测试数据没有写id，用title代替
				let id = item.id;
				console.log()
				uni.navigateTo({
					url: `/pages/product/product?id=` + id
				})
			},
			orderTerminat() {
				var that = this;
				let id = this.goods.id;
				uni.showModal({
					title: '',
					content: '确认已经收货？',
					success: function(res) {
						if (res.confirm) {
							that.$http({ //调用接口
								method: 'POST',
								params: {
									orderId: id
								},
								url: that.global.target + "/wx/order/confirm" //this指data
							}).then(function(response) { //接口返回数据
								console.log(response.data)
								var result = response.data;
								that.goods.status = 401;
								that.goods.mai = "已收货";
								this.loadData(that.orderId);
							}, function(error) {})
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});

			},
			tixingfahuo() {
				this.$api.msg("已通知商家");
			},
			refund() {
				var that = this;
				let id = this.goods.id;

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
					that.loadData(id);
					
					// if (result.errno == 0) {
					// 	let data = result.data.data;
					// 	this.loadData(that.orderId);
					// } else {
					// 	that.$api.msg(result.errmsg)
					// }

				}, function(error) {})

			},
			topay(){
				let orderid = this.orderId;
				let pay = this.goodscount.pay;
				let orderSn =this.goodscount.ordernumber
				uni.redirectTo({
					url: '/pages/money/pay?orderId=' + orderid + '&pay=' + pay + '&ordersn=' + orderSn
				})
			}
		}
	}
</script>

<style lang="scss">
	.orderlist {
		background: #f2f2f2;
		height: 120vh;
	}

	.ol_top_mai {
		height: 200upx;
		background: linear-gradient(right, #ff5a7dfc, #ffb6c6bd);
		color: #fff;
		padding: 0 80upx 0 50upx;
		box-sizing: border-box;
	}

	.ol_top_mai .ol_top_img {
		width: 150upx;
		height: 150upx;
	}

	.ol_top_mai .ol_topm_top {
		font-size: 40upx;
		line-height: 60upx;
	}

	.ol_top_mai .ol_topm_time {
		font-size: 28upx;
	}

	.ol_top_wuliu {
		border-bottom: 1px solid #f2f2f2;
	}

	.ol_top_wuliu,
	.ol_top_address {
		background: #fff;

		.ol_topw_img {
			width: 100upx;
			height: 100upx;
			background: #ff5a7d;
			border-radius: 100%;
			overflow: hidden;
		}

		.icon_wuliu {
			width: 80upx;
			height: 80upx;
			margin: 10upx;
		}

		.ol_topw_status {
			padding: 0 30upx;
			box-sizing: border-box;
			position: relative;
		}
	}

	.ol_top_wuliu .ol_top_wuliu .ol_topw_status::after {
		position: absolute;
		content: '';
		width: 30upx;
		height: 30upx;
		background: url('../../static/right.png');
		background-size: 100% 100%;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
	}

	.ol_top_wuliu .ol_topw_status .ol_topw_text {
		font-size: 32upx;
		color: #ff5a7d;
		line-height: 50upx;
	}

	.ol_top_wuliu .ol_topw_status .ol_topw_time {
		font-size: 28upx;
		color: #999;
	}

	.ol_top_address .ol_topw_img {
		background: #007aff;
	}

	.ol_top_address .ol_topw_status {
		padding: 0 0 0 30upx;
	}

	.ol_top_address .ol_topw_status .ol_topa_user {
		font-size: 28upx;
		color: #999;
		line-height: 50upx;
	}

	.ol_top_address .ol_topw_status .ol_topa_user .name {
		font-size: 32upx;
		color: #000;
		margin: 0 30upx 0 0;
	}

	.ol_top_address .ol_topw_status .ol_topa_text {
		font-size: 32upx;
		line-height: 50upx;
	}

	// 商品信息
	.ol_goods {
		margin: 20upx 0 0;
		background: #fff;
	}

	.ol_goods .ol_goods_li {
		padding: 30upx;
		box-sizing: border-box;
		margin: 10upx 0 0;
	}

	.ol_goods .ol_goods_li .ol_goodsimg {
		width: 200upx;
		height: 200upx;
		border-radius: 10upx;
	}

	.ol_goods .ol_goods_li .ol_goods_right {
		margin: 0 0 0 20upx;
		line-height: 50upx;

		.ol_goods_title {
			font-size: 34upx;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
		}

		.ol_roogs_num {
			text-align: right;
		}

		.price {
			font-size: 32upx;
		}

		.num,
		.ol_goods_size {
			font-size: 28upx;
			color: #999;
		}

		.ol_goods_other {
			font-size: 24upx;
			color: #ff5a7d;
			background: #ffb6c6;
			padding: 4upx;
		}

		.ol_goods_back {
			width: 166upx;
			height: 56upx;
			font-size: 30upx;
			color: #333;
			border-radius: 50upx;
			line-height: 56upx;
			text-align: center;
			border: 1px solid #666;
			float: right;
		}
	}

	.ol_goods_count,
	.ol_goods_pay {
		padding: 20upx 30upx;
		box-sizing: border-box;

		.ol_goodsc_gray,
		.ol_goodsp_top,
		.ol_goods_hong {
			font-size: 28upx;
			color: #999;
			line-height: 40upx;
		}

		.right {
			float: right;
		}

		.ol_goodsc_black,
		.ol_goods_paytrue {
			font-size: 32upx;
			color: #333;
			line-height: 50upx;
		}

		.ol_goods_hong,
		.ol_goodsp_top {
			height: 40upx;
		}

		.ol_goods_hong {
			color: #333;
		}

		.red {
			color: #ff5a7d;
			font-size: 40upx;
		}
	}

	.ol_goods_pay {
		background: #f9f9f9;
		position: relative;
		margin: 10upx 0 0;
	}

	.ol_goods_pay::after {
		position: absolute;
		content: '';
		width: 0upx;
		border-bottom: 20upx solid #f9f9f9;
		border-left: 20upx solid transparent;
		border-right: 20upx solid transparent;
		top: -20upx;
		right: 60upx;
	}

	// 订单信息
	.ol_goods_infos {
		margin: 20upx 0 100upx 0;
		background: #fff;
		padding: 20upx 30upx;
		box-sizing: box-sizing;

		.ol_goodsi_title {
			font-size: 38upx;
			line-height: 50upx;
			position: relative;
			text-indent: 20upx;
		}

		.ol_goodsi_title::after {
			position: absolute;
			content: "";
			width: 6upx;
			height: 36upx;
			background: #ff5a7d;
			left: 0;
			top: 7upx;
		}

		.ol_goods_orderinfo {
			margin: 10upx;
			line-height: 60upx;
			font-size: 28upx;
		}

		.copy {
			float: right;
			color: #ff5a7d;
		}
	}

	// 
	.ol_bottom {
		width: 100vw;
	height: 100upx;
		background: #fff;
		border-top: 1px solid #f2f2f2;
		position: fixed;
		z-index: 999;
		bottom: 0;
		justify-content: flex-end;

		.ol_bottom_wuliu,
		.ol_bottom_ok {
			width: 200upx;
			height: 58upx;
			font-size: 28upx;
			line-height: 58upx;
			margin: 25upx 20upx;
			border-radius: 50upx;
			text-align: center;
		}

		.ol_bottom_wuliu {
			border: 1px solid #666;
		}

		.ol_bottom_ok {
			color: #ff5a7d;
			border: 1px solid #ff5a7d;
		}
	}

	// common
	.ol_top_mai,
	.ol_top_wuliu,
	.ol_top_address,
	.ol_goods .ol_goods_li,
	.ol_goods_right .ol_goods_info,
	.ol_bottom {
		display: flex;
	}

	.ol_top_mai {
		justify-content: space-between;
	}

	.ol_top_mai,
	.ol_top_wuliu,
	.ol_top_address {
		align-items: center;
	}

	.ol_top_wuliu .ol_topw_img,
	.ol_top_address .ol_topw_img,
	.ol_goods .ol_goods_li .ol_goodsimg {
		flex-shrink: 0;
	}

	.ol_top_wuliu,
	.ol_top_address {
		padding: 20upx 30upx;
		box-sizing: border-box;
	}
</style>
