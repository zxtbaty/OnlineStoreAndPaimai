<template>
	<view class="app">
		<view class="price-box">
			<!-- <text>支付金额</text> -->
			<text class="price">{{pay}}</text>
			<text class="p1">请选择付款方式</text>
			<text class="p2" v-if="contentTime>0">请在创建订单<text class="red">{{contentTime}}分钟内</text>完成付款，过时订单将被取消</text>
			<text class="p2" v-if="contentTime<=0">订单已超时</text>
		</view>

		<view class="pay-type-list">

			<view class="type-item b-b" @click="changePayType(1)">
				<image src="/static/newimg/pay-w.png" class="wei-img" mode=""></image>
				<!-- <text class="icon yticon icon-weixinzhifu"></text> -->
				<view class="con">
					<text class="tit">微信支付</text>
					<!-- <text>推荐使用微信支付</text> -->
				</view>
				<label class="radio">
					<radio value="" color="#ff0000" :checked='payType == 1' />
				</radio>
				</label>
			</view>
			<view class="type-item b-b" @click="changePayType(2)">
				<image src="/static/newimg/pay_z.png" class="zhi-img" mode=""></image>
				<view class="con">
					<text class="tit">支付宝支付</text>
				</view>
				<label class="radio">
					<radio value="" color="#ff0000" :checked='payType == 2' />
					</radio>
				</label>
			</view>
			<!-- <view class="type-item" @click="changePayType(3)">
				<text class="icon yticon icon-erjiye-yucunkuan"></text>
				<view class="con">
					<text class="tit">预存款支付</text>
					<text>可用余额 ¥198.5</text>
				</view>
				<label class="radio">
					<radio value="" color="#fa436a" :checked='payType == 3' />
					</radio>
				</label>
			</view> -->
		</view>
		
		<text class="mix-btn" @click="confirm" v-if="contentTime>0">确认支付</text>
	</view>
</template>

<script>

	export default {
		data() {
			return {
				payType: 1,
				orderInfo: {},
				ordersn:"",
				orderid:0,
				pay:0,
				unPayCancelMinutes:30,
				contentTime:30,
			};
		},
		computed: {
		
		},
		onLoad(options) {
			let ordersn=options.ordersn;
			let pay = options.pay;
			this.ordersn=ordersn;
			this.pay=pay;
			this.orderid=options.orderId;
			this.getUnPayCancelMinutes();
			// this.loadData(pay,ordersn);
		},

		methods: {
			getUnPayCancelMinutes(){
				let ordersn=this.ordersn;
				var that=this;
				that.$http({ //调用接口
					method: 'GET',
					params: {
						orderSn: ordersn
					},
					url: this.global.target + "/wx/order/detailbysn" //this指data
				}).then(function(response) { //接口返回数据
					// console.log(response.data.data)
					var data = response.data.data;
					console.log(response)
					var state = data.orderInfo.status;
					let goods = data.orderInfo;
					console.log(that.goods)
					var date = Math.round(new Date(Date.parse(goods.addTime.replace(/-/g, "/"))).getTime() / 1000).toString();
				
					var daoTime = Math.round(new Date().getTime() / 1000).toString() - date;
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
					   
					   that.contentTime = daoTime>0?daoTime:0;
					})
					
					// daoTime = Math.floor((60 * 60 - daoTime) / 60);
					// that.contentTime = daoTime;
				
				
				}, function(error) {})
				
			},
			
			async loadData(pay,ordersn){
				that.ordersn=ordersn;
				that.pay=pay;
				
			},
			//选择支付方式
			changePayType(type) {
				this.payType = type;
			},
			//	微信支付
			onBridgeReady(params) {
				var that=this;
				WeixinJSBridge.invoke(
					'getBrandWCPayRequest', {
						"appId": params.appid,  //公众号名称，由商户传入     
						"timeStamp": params.timeStamp,  //时间戳，自1970年以来的秒数     
						"nonceStr": params.nonceStr,  //随机串     
						"package": params.package,     
						"signType": "MD5",  //微信签名方式
						"paySign": params.sign  //微信签名 
					},
					function(res){
						if(res.err_msg == "get_brand_wcpay_request:ok" ){
							uni.navigateTo({
								url: '/pages/money/paySuccess?price='+that.pay+'&orderid='+that.orderid
							})
						} 
				}); 
			},
			//确认支付
			confirm: async function() {
				
				var that = this;
				if(this.payType==1){
					that.$http({ //调用接口
						method: 'POST',
						params: {
							orderId:this.orderid
						},
						url: that.global.target + "/wx/order/prepay" //this指data
					}).then(function(response) { //接口返回数据
						console.log(response)
						if(response.data.errno!=0){
							const pay_params = response.data;
							if (typeof WeixinJSBridge == "undefined"){
							    if( document.addEventListener ){
							        document.addEventListener('WeixinJSBridgeReady', function () { that.onBridgeReady(pay_params) }, false);
							    }else if (document.attachEvent){
							        document.attachEvent('WeixinJSBridgeReady', function () { that.onBridgeReady(pay_params) }); 
							        document.attachEvent('onWeixinJSBridgeReady', function () { that.onBridgeReady(pay_params) });
							    }
							}else{
							    that.onBridgeReady(pay_params);
							}
						}else{
							that.$api.msg('微信支付失败');
						}
					}, function(error) {
						that.$api.msg('支付失败');
					})
				}else{//支付宝
					
					 var ua = window.navigator.userAgent.toLowerCase(); 
					if (ua.match(/MicroMessenger/i) == 'micromessenger') { 
						// that.$api.msg('请在其他浏览器中打开本页');
						// uni.navigateTo({
						// 	url: '/pages/money/alipay?userid='+ uni.getStorageSync('userInfo').id+'&orderid='+that.orderid
						// })
						location.href=that.global.website+'/'+'pages/money/alipay?userid='+ uni.getStorageSync('userInfo').id+'&orderid='+that.orderid;
					} else {
						var userInfo = uni.getStorageSync('userInfo');
						location.href=that.global.website +"/"+ "/wx/order/aliprepay?orderId="+this.orderid+"&userId="+userInfo.id;
					}
					
					
				}
				
								

				
				// uni.redirectTo({
				// 	url: '/pages/money/paySuccess'
				// })
			},
		}
	}
</script>

<style lang='scss'>
page{
	background: $page-color-base;
}
	.app {
		width: 100%;
	}

	.price-box {
		background-color: #fff;
		height: 225upx;
		display: flex;
		flex-direction: column;
		align-items: center;
		.price{
			font-size: 40upx;
			color: #FF0000;
			font-weight: bold;
			margin: 50upx 0 0 0;
			&:before{
				content: '￥';
				font-size: 31upx;
			}
		}
		.p1{
			font-size: 24upx;
			margin: 20upx 0 0;
		}
		.p2{
			font-size: 24upx;
			margin: 10upx 0 0;
			.red{
				color: #FF0000;
			}
		}
	}

	.pay-type-list {
		margin-top: 27upx;
		background-color: #fff;
		
		.type-item{
			height: 94upx;
			padding: 0 21upx 0 26upx;
			display: flex;
			justify-content: space-between;
			align-items: center;
			font-size: 24upx;
			position:relative;
		}
		.wei-img{
			width: 47upx;
			height: 41upx;
			margin: 0 26upx 0 0;
		}
		.zhi-img{
			width: 43upx;
			height: 43upx;
			margin: 0 29upx 0 0;
		}
	
		/*.icon{
			width: 100upx;
			font-size: 52upx;
		}
		.icon-erjiye-yucunkuan {
			color: #fe8e2e;
		}
		.icon-weixinzhifu {
			color: #36cb59;
		}
		.icon-alipay {
			color: #01aaef;
		}*/
		.tit{
			font-size: $font-lg;
			color: $font-color-dark;
			margin-bottom: 4upx;
		}
		.con{
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: $font-sm;
			color: $font-color-light;
		}
	}
	.mix-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 630upx;
		height: 80upx;
		margin: 80upx auto 30upx;
		font-size: $font-lg;
		color: #fff;
		background-color: $base-color;
		border-radius: 10upx;
		box-shadow: 1px 2px 5px rgba(219, 63, 96, 0.4);
	}

</style>
