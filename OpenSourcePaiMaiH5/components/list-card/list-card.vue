<template class="box-body">
	<view class="box">
		<view class="father-box">
			<view class="header">
				<image class="img" :src="cardinfo.authImg"></image>
				<view class="auth">{{cardinfo.authName}}</view>
				<view class="leave">LV{{cardinfo.leave}}</view>
				<view class="tag">{{cardinfo.tag}}</view>
				<view class="time">{{cardinfo.createTime}}</view>

			</view>
			
			<view class="content">{{cardinfo.content}}....<view class="show">点击阅读详细</view>
			</view>

			<view class="hr"></view>
			<view class="function">
				<view class="good">
					<image class="icon-like" src="../../static/hm-information-card/images/img_24030_0_0.png"></image>
					<view>{{cardinfo.goodNum}}</view>
				</view>
				<view class="good">
					<image class="icon-like" src="../../static/hm-information-card/images/img_24030_0_0.png"></image>
					<view>{{cardinfo.likeNum}}</view>
				</view>
				<view class="good">
					<image class="icon-like" src="../../static/hm-information-card/images/img_24030_0_0.png"></image>
					<view>{{cardinfo.commentNum}}</view>
				</view>
			</view>
		</view>
	</view>

</template>

<script>
	export default {
		name: 'card',
		props: {
			cardinfo: {
				type: Object
			}
		},
		data() {
			return {

			};
		}
	}
</script>

<style>
	
	.low{
		margin-left: 5px;
	}

	.father-box {
		background-color: #FFFFFF;
		box-shadow: #C8C7CC;
		height: 500upx;
		width: 93%;
		margin: 20upx auto;
		border: 1px dashed #999999;
		border-radius: 20upx;
	}

	.img {
		margin: 20upx 0px 20upx 50upx;
		height: 90upx;
		width: 90upx;
		border-radius: 90upx;
		border: 1px solid #007AFF;
		display: inline-flex;
	}

	.auth {
		font-weight: bolder;
		font-family: Arial, Helvetica, sans-serif;
		font-size: 15px;
		margin-top: -100upx;
		margin-left: 170upx;
	}

	.time {
		font-family: Arial, Helvetica, sans-serif;
		font-size: 12px;
		display: inline-flex;
		margin-left: 170upx;
		margin-top: -20upx;
		color: #808080;
	}

	.leave {
		font-size: 12px;
		text-align: center;
		background: #4CD964;
		width: 75upx;
		text-align: center;
		height: 35upx;
		border-radius: 20upx;
		color: #F2F2F2;
		margin-left: 250upx;
		margin-top: -35upx;
	}

	.content {
		display: block;
		font-size: 14px;
		color: #24292E;
		font-style: italic;
		margin-left: 60upx;
		margin-right: 60upx;
		margin-top: 18upx;
		height: 250upx;
		text-indent: 1.5em;
	}

	.nbsp {
		margin-left: 25upx;
	}

	.icon-like {
		height: 32upx;
		width: 32upx;
	}

	.good {
		display: inline-flex;
		margin-top: 30upx;
		margin-left: 95upx;
	}

	.good view {
		font-size: 12px;
		margin-left: 20upx;
		color: #6D6D72;
		margin-top: -2upx;
		text-align: center;
	}

	.hr {
		width: 100%;
		margin: 25upx auto;
		margin-bottom: 0upx;
		color: #AAAAAA;
		border-color: #ececec;
		border: 0.2rpx dashed #999999;
		height: 0.2px;
	}

	.show {
		color: #0081FF;
		margin-top: 12upx;
	}

	.tag {
		font-size: 12px;
		text-align: center;
		background: #36BBFF;
		width: 120upx;
		text-align: center;
		height: 35upx;
		border-radius: 10upx;
		color: #F2F2F2;
		margin-left: 340upx;
		margin-top: -33upx;
	}
</style>
