<template>
	<view class="content">
<!-- 		<view class="ps_top">
			<image src="/static/newimg/redquan.png" mode=""></image>
			<text>请在其他浏览器中打开本页</text>
		</view> -->
		<view class="h_window">
		   <view class="hw_content">
 		    <view class="text">
		      <img style="width:100%;" src="/static/newimg/timg.png" >
		    </view>
		   <!-- <view class="text top66">2.选择在浏览器中打开</view> -->
		    <image style="width:100%;" src="/static/newimg/timg.png" ></image>
		   <!-- <img :src="phonetype=='ios'?ios:android" alt class="hw_share" /> -->
		  </view>
		</view>
		
		<view class="ps-goods">
			<!-- <view class="price">
				{{goods.price}}
			</view>
			<view class="p">
				订单编号<text class="num">{{goods.num}}</text>
			</view>
			<view class="p">
				支付方式<text>{{goods.express}}</text>
			</view> -->
		</view>
		 <view class="ps-bottom">
			<!-- <view class="order" @click='navTo("/pages/order/order?state=0")'>
				订单中心
			</view> -->
			<!-- <view class="buy" @click='switchTo("/pages/category/category")'>
				返回商城
			</view> -->
		</view>
	</view>
</template>

<script>
	import ios from "@/static/ios.png";
	import android from "@/static/android.png";
	export default {
		data() {
			return {
				phonetype: "",
				android,
				ios,
				goods:{
					price:0,
					num:0,
					express:'特产'
				},
				userid:0,
				orderid:0,
			}
		},
		onLoad(option){
			if(option){
				// this.getphonetype();
				this.userid = option.userid;
				this.orderid=option.orderid;
				 var ua = window.navigator.userAgent.toLowerCase(); 
				if (ua.match(/MicroMessenger/i) == 'micromessenger') { 
					
				} else {
					location.href=this.global.target + "/wx/order/aliprepay?orderId="+this.orderid+"&userId="+this.userid;
				}
				
			}
		},
		methods: {
			navTo(url){
				uni.navigateTo({
					url
				})
			},
			switchTo(url){
				uni.switchTab({
					url
				})
			},
			getphonetype() {
			  var u = navigator.userAgent,
			    that = this,
			    app = navigator.appVersion;
			  var isAndroid = u.indexOf("Android") > -1 || u.indexOf("Linux") > -1; //g
			  var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
			  if (isAndroid) {
			    console.log("安卓机！");
			    that.phonetype = "android";
			  }
			  if (isIOS) {
			    console.log("苹果果机！");
			    that.phonetype = "ios";
			  }
			},
			
		}
	}
</script>

<style lang='scss'>
	.content{
		text-align: center;
	}
	.ps_top{
		padding: 53px 0 0;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 40upx;
		image{
			width: 45upx;
			height: 45upx;
			margin: 0 20upx 0 0;
		}
	}
	.ps-goods{
		font-size: 24upx;
		.price{
			color: #ff0000;
			font-size: 40upx;
			font-weight: bold;
			position: relative;
			margin: 30upx 0 34upx 0;
			&:before{
				content: '￥';
				font-size: 27upx;
			}
		}
		.p{
			line-height: 40upx;
		}
	}
	.ps-bottom{
		margin: 80upx 0 0;
		display: flex;
		justify-content: space-around;
		padding: 0 40upx;
		view{
			width: 267upx;
			height: 73upx;
			border-radius: 33upx;
			font-size: 27upx;
			display: flex;
			align-items: center;
			justify-content: center;
			border: 1px solid #ff0000;
		}
		.order{
			color: #ff0000;
		}
		.buy{
			background: #ff0000;
			color: #fff;
		}
	}
	.h_window {
	  position: fixed;
	  width: 100vw;
	  height: 100vh;
	  z-index: 2;
	  background: rgba(0, 0, 0, 0.6);
	  top: 0;
	  left: 0;
	  color: #fff;
	  font-size: 0.32rem;
	  padding: 0 0 0 0.3rem;
	  box-sizing: border-box;
	}
	.h_window .text {
	  display: flex;
	  display: -webkit-flex;
	  align-items: center;
	  -webkit-align-items: center;
	}
	.h_window .text > img {
	  width: 0.48rem;
	  height: 0.12rem;
	  flex-shrink: 0;
	  -webkit-flex-shrink: 0;
	  margin: 0 0 0 0.2rem;
	}
	.h_window .hw_share {
	  width: 100%;
	}
	.h_window .hw_content {
	  position: absolute;
	  top: 25%;
	  transform: translateY(-50%);
	}
</style>
