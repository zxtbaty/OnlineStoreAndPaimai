<template>
	<view class="waitpay">
		<!-- top -->
		<view class="wp-top">
			<view class="wx-top-p1">
				<image src="/static/newimg/wait.png" mode=""></image>
				<text>等待支付</text>
			</view>
			<view class="wp-top-p2">
				需付款：<text class="red">{{goods.price}}</text>
			</view>
			<view class="wp-top-p3">
				请在<text class="red">30分钟内</text>完成付款，过时订单将会取消
			</view>
			<view class="wp-top-btn">
				<view class="btn1">
					取消订单
				</view>
				<view class="btn2">
					立即支付
				</view>
			</view>
		</view>
		<!-- top end-->
		<!-- center -->
		<view class="wp-center">
			<view class="">
				<text>订单号:</text>
				<text class="right">{{goods.ordernum}}</text>
			</view>
			<view class="">
				<text>订单状态：</text>
				<text class="right">{{goods.orderstatus}}</text>
			</view>
		</view>
		<!-- center end-->
		<!-- bottom -->
		<view class="wp-bottom">
			<image :src="goods.picurl" mode="" class="goods-img"></image>
			<view class="wp-bottom-right">
				<view class="title">
					{{goods.title}}
				</view>
				<view class="other">
					<text>数量：{{goods.num}}</text>
					<text>{{goods.spail}}</text>
				</view>
				<view class="price">
					{{goods.price}}
				</view>
			</view>
		</view>
		<!-- bottom end -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				goods:{
					price:123,
					ordernum:201806093233,
					orderstatus:'未支付',
					picurl:'http://f.hiphotos.baidu.com/image/pic/item/8d5494eef01f3a29f863534d9725bc315d607c8e.jpg',
					title:'稻香村  真空包装真空包装真空包装真空包装 美食熟食（套装3只装）...',
					num:1,
					spail:'规格：套装3袋装（单只约500g）',
					price:123.03
				}
			};
		}
	}
</script>

<style lang="scss">
	page {
		background: #f7f7f7;
	}

	.wp-top {
		text-align: center;
		background: #fff;
		padding: 53upx 0;
		.wx-top-p1 {
			color: #FF0000;
			font-size: 31upx;
			display: flex;
			align-items: center;
			justify-content: center;
			image {
				width: 33upx;
				height: 34upx;
				margin: 0 9upx 0 0;
			}
		}
		.wp-top-p2{
			font-size: 24upx;
			margin: 24upx 0 10upx 0;
			font-weight: bold;
			.red{
				&:before{
					content:'￥'
				}
			}
		}
		.wp-top-p3{
			font-size: 24upx;
		}
		.wp-top-btn{
			display: flex;
			margin: 53upx 0 0 0;
			padding: 0 81upx;
			justify-content: space-between;
			view{
				width: 267upx;
				height: 73upx;
				font-size: 27upx;
				border-radius: 33upx;
				line-height: 73upx;
			}
			.btn1{
				color: #FF0000;
				border: 1px solid #E93A27;
			}
			.btn2{
				color: #fff;
				background:linear-gradient(239deg,rgba(242,81,33,1),rgba(233,58,39,1));
			}
		}
	}
	.red{
		color: #FF0000;
	}
	.wp-center{
		margin: 27upx 0;
		background: #fff;
		padding: 20upx 27upx;
		view{
			line-height: 40upx;
			font-size: 24upx;
			color: #909090;
		}
		.right{
			float: right;
		}
	}
	.wp-bottom{
		background: #fff;
		width: 100vw;
		padding: 27upx;
		display: flex;
		.goods-img{
			width: 154upx;
			height: 146upx;
			flex-shrink: 0;
			margin: 0 27upx 0 0;
		}
		.wp-bottom-right{
			font-size: 24upx;
			flex: 1;
		position: relative;
			.other{
				font-size: 20upx;
				color: #909090;
				margin: 10upx 0 0;
			}
			.price{
				color: #FF3B30;
				font-size: 29upx;
				font-weight: bold;
				position: absolute;
				bottom: 0;
				&:before{
					content:'￥';
					font-size: 24upx;
				}
			}
			
		}
	}
</style>
