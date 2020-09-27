<template>
	<view class="Contactus">
		<!-- top -->
		<view class="cu-top">
			<view class="cu-top-li b-b" v-for="(item,index) in culist" :key="index">
				<view class="cu-topli-left">
					<image :src="item.icon" mode="" :class="index==1?'email-icon':''"></image>
					<text>{{item.text}}</text>
				</view>
				<view class="cu-topli-right">
					{{item.right}}
				</view>
			</view>
		</view>
		<!-- top -->
		<!-- center -->
<!-- 		<view class="cu-center">
			<image src="/static/newimg/logo.png" mode=""></image>
		</view> -->
		<!-- center end -->
		<!-- bottom -->
		<view class="cu-bottom">
			<view class="title">
				北京劲远科技有限公司简介
			</view>
			<view class="text">

				北京劲远科技有限公司主要从事软件定制开发及销售推广业务,主要有微商系统、工单绩效低代码平台、
				电子书管理系统、版权系统、数据中心。
			</view>
 
			<view class="text">
				公司始终以诚信第一，广交天下朋友，为软件需求方提供在线上线下一体解决方案。

			</view>
		</view>
		<!-- bottom end -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				culist: [{
						icon: '/static/newimg/us-phone.png',
						text: '联系电话',
						right: '13521612468'
					},
					{
						icon: '/static/newimg/us-email.png',
						text: '邮箱',
						right: 'zxtbaty@163.com'
					},
					{
						icon: '/static/newimg/us-earth.png',
						text: '微信号(加手机)',
						right: '13521612468'
					},
					{
						icon: '/static/newimg/us-earth.png',
						text: 'QQ号',
						right: '85899161'
					}
				]
			};
		}
	}
</script>

<style lang="scss">
	page {
		background: #f7f7f7;
	}

	.cu-top {
		background: #fff;

		image {
			width: 37upx;
			height: 37upx;
			margin: 0 26upx 0 0;
		}

		.email-icon {
			width: 38upx;
			height: 25upx;
		}

		.cu-top-li {
			padding: 0 27upx;
			height: 94upx;
			position: relative;
			display: flex;
			align-items: center;
			justify-content: space-between;

			.cu-topli-left {
				display: flex;
				align-items: center;
			}

			text {
				font-size: 24upx;
			}

			.cu-topli-right {
				color: #909090;
				font-size: 20upx;
			}
		}
	}

	.cu-center {
		background: #fff;
		width: 100vw;
		height: 267upx;
		margin: 27upx 0 0;
		text-align: center;

		image {
			width: 335upx;
			height: 115upx;
			margin: 58upx auto 0;
		}
	}

	.cu-bottom {
		padding: 10upx 27upx 0;

		.title {
			position: relative;
			text-align: center;
			font-size: 27upx;
			line-height: 70upx;

			&:after,
			&:before {
				position: absolute;
				content: '';
				width: 67upx;
				height: 1px;
				background: #000;
				top: 50%;
				right: 80upx;
			}

			&:before {
				left: 80upx;
			}
		}

		.text {
			font-size: 23upx;
			line-height: 31upx;
			margin: 0 0 40upx 0;
		}
	}
</style>
