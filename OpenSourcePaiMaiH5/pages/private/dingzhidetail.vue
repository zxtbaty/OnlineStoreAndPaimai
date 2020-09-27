<template>
	<view class="container" v-if="goods!=undefined">
		<view class="carousel">
			<swiper indicator-dots circular="true" duration="400">
				<swiper-item class="swiper-item" v-for="(item, index) in goods.gallery" :key="index">
					<view class="image-wrapper">
						<image :src="item" class="loaded" mode="aspectFill"></image>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<view class="introduce-section"><text class="title">{{goods.name}}</text></view>

		<view class="c-list">
			<view class="c-row b-b">
				<text class="tit">出品人</text>
				<view class="con-list"><text>{{goods.authorName}}</text></view>
			</view>

			<view class="c-row b-b" v-for="(attr,index) in attributeList" :key="index">
				<text class="tit">{{attr.attribute}}</text>
				<view class="bz-list con">
					<text>{{attr.value}}</text>

				</view>
			</view>

			<!-- 			<view class="c-row b-b">
				<text class="tit">定制类型</text>
				<view class="bz-list con"><text>书法</text></view>
			</view>

			<view class="c-row b-b">
				<text class="tit">定制类型</text>
				<view class="bz-list con"><text>书法</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">擅长</text>
				<view class="bz-list con"><text>题斋号、行书、草书</text></view>
			</view>

			<view class="c-row b-b">
				<text class="tit">装裱情况</text>
				<view class="bz-list con"><text>带装裱</text></view>
			</view> -->

			<view class="c-row b-b">
				<text class="tit">出品人简介</text>

			</view>
			<view class="c-row b-b">

				<view class="bz-list con"><text>{{authororcorp.desc}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">定制品简介</text>

			</view>
			<view class="c-row b-b">

				<view class="bz-list con"><text>{{privateMakeRule.auctionDesc}}</text></view>
			</view>


			<view class="c-row b-b">
				<text class="tit">最低起价</text>
				<view class="bz-list con"><text>{{privateMakeRule.lowPrice}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">定价描述</text>
				<view class="bz-list con"><text>{{privateMakeRule.priceDesc}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">定金金额</text>
				<view class="bz-list con"><text>{{privateMakeRule.dingjinPrice}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">项目周期</text>
				<view class="bz-list con"><text>{{privateMakeRule.finishDays}}</text></view>
			</view>
			<view class="c-row b-b" v-if="privateMakeRule.ruleDesc!=null&&privateMakeRule.ruleDesc!=''">
				<text class="tit">预约规则</text>
				<view class="bz-list con"><text>{{privateMakeRule.ruleDesc}}</text></view>
			</view>
			<view class="c-row b-b" v-if="privateMakeRule.ruleDesc==null||privateMakeRule.ruleDesc==''">
				<text class="tit">预约规则</text>
				<view class="bz-list con"><text>由于被预约人可能存在工期安排不开情况，本平台负责线下撮合，同时本平台负责居间担保，双方以诚信为本</text></view>
			</view>
		</view>

		<!-- 预约信息 -->
		<!-- 		<view class="c-list" v-if="authororcorpItems!=null&&authororcorpItems.length>0">
			<view class="e-header"><text class="tit">其它预约信息</text></view>
			<view class="c-row b-b">
				<text class="tit">预约项目</text>
				<view class="con-list">
					<view class="uni-list-cell-db">
						<picker @change="bindItemsChange" :value="itemIndex" :range="array">
							<view class="uni-input">{{itemName}}</view>
						</picker>
					</view>
				</view>
			</view>

			<view class="c-row b-b">
				<text class="tit">最低起价</text>
				<view class="bz-list con"><text>{{itemMinPrice}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">定价描述</text>
				<view class="bz-list con"><text>{{itemPriceDesc}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">定金金额</text>
				<view class="bz-list con"><text>{{itemDingMoney}}</text></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">项目周期</text>
				<view class="bz-list con"><text>{{itemFinishDays}}</text></view>
			</view> -->
		<!-- 			<view class="c-row b-b">
				<text class="tit">预约时间</text>
				<view class="bz-list con">
					<picker mode="date" :value="date" :start="startDate" :end="endDate" @change="bindDateChange">
						<view class="uni-input">{{ date }}</view>
					</picker>
				</view>
			</view>

			<view class="c-row b-b">
				<text class="tit">内容描述</text>
				<view class="bz-list con"><input class="ser-input" type="text" placeholder="输入内容描述" /></view>
			</view>
			<view class="c-row b-b">
				<text class="tit">特别备注</text>
				<view class="bz-list con"><input class="ser-input" type="text" placeholder="输入特别备注" /></view>
			</view>
			<view class="c-row b-b">
				<view style="width: 150upx;">
					<text><a href="#">上传图片</a></text>
				</view>

				 <view class="bz-list con">
					<image class="portrait" 
					src="#" 
					mode="aspectFill"></image>
			    </view>
			</view> -->
		<!-- 			 <view class="bz-list con">
								<image class="portrait" 
								src="http://img3.imgtn.bdimg.com/it/u=1150341365,1327279810&fm=26&gp=0.jpg" 
								mode="aspectFill"></image> -->
		<!-- 			<view class="c-row b-b">
				<text class="tit">预约规则</text>
				<view class="bz-list con"><text>由于被预约人可能存在工期安排不开情况，本平台负责线下撮合，同时本平台负责居间担保，双方以诚信为本</text></view>
			</view>
		</view> -->

		<!-- 定制历史 -->
		<view class="eva-section" v-if="privateMakeOrderList!=null&&privateMakeOrderList.length>0">
			<view class="e-header">
				<text class="tit">定制历史</text>
				<!-- <text>【{{privateMakeOrderList.length}}】</text> -->
				<!-- <text class="tip">最新价:{{privateMakeOrderList[0]['orderPrice']}}</text> -->
			</view>
			<view class="c-list">
				<view class="eva-box " v-for="(order,index) in privateMakeOrderList">
					<view class="left">
						<image class="portrait" src="http://img3.imgtn.bdimg.com/it/u=1150341365,1327279810&fm=26&gp=0.jpg" mode="aspectFill">
						</image>
						<view class="title">
							<text>{{order.userName}}
								<!-- :{{order.privateItemNName}} -->
							</text>
							<!-- <text>{{order.orderCreateDate}}</text> -->
						</view>
					</view>
					<view class="right title">
						<text>{{new DateFormat(order.orderCreateDate,'yyyy-mm-dd hh:ff:ss').toString('yyyy-mm-dd')}}</text>

						<!-- <text>{{DateFormat.setTime(order.orderCreateDate).toString('yyyy-mm-dd')}}</text> -->
						<!-- <text>{{order.orderCreateDate}}</text> -->
						<!-- <text>￥:{{order.orderPrice}}</text> -->
						<!-- <text class="name">领先</text> -->
					</view>
				</view>


			</view>
		</view>

		<view class="auction-rule">
			<uni-steps :options="[{ title: '填写预约' }, { title: '撮合' }, { title: '支付定金' }, { title: '交付' }, { title: '支付尾款' }]"
			 :active="0"></uni-steps>
		</view>

		<view class="detail-desc">
			<view class="d-header"><text>定制业务介绍</text></view>
			<rich-text :nodes="goods.detail"></rich-text>
		</view>

		<!-- 底部操作菜单 -->
		<view class="page-bottom">
			<navigator url="/pages/private/private" open-type="switchTab" class="p-b-btn">
				<text class="yticon icon-zuojiantou-up"></text>
				<text>返回</text>
			</navigator>

			<!-- 		    <view class="p-b-btn" :class="{active: share}" @click="toShare">
				<text class="yticon icon-shoucang"></text>
				<text>分享</text>
			</view> -->

			<!--    <input class="input" type="text" placeholder="最新788,每次加价100" :value="currentPrice"  />
			 -->
			<view class="action-btn-group"><button type="primary" class=" action-btn no-border buy-now-btn" @click="yudingOrder">立即预定</button></view>
		</view>


	</view>
</template>

<script>
	import share from '@/components/share';
	import uniSteps from '@/components/uni-steps';
	import DateFormat from '@/components/js_sdk/xfl-DateFormat/DateFormat';
	export default {
		components: {
			share,
			uniSteps,

		},
		data() {
			return {
				DateFormat: DateFormat,
				// array: ['四字吉语', '斋号定制', '吉祥花鸟', '国画定制', '拓片定制', '篆刻定制', '对联定制', '诗文小品'],
				array: [],
				privateMakeRule: undefined,
				privateMakeOrderList: [],
				goods: undefined,
				attributeList: [],
				goodsProduct: undefined,
				authororcorp: undefined,
				authororcorpParas: [],
				authororcorpItems: [],
				itemName: undefined,
				itemMinPrice: undefined,
				itemPriceDesc: undefined,
				itemDingMoney: undefined,
				itemFinishDays: undefined,
				itemIndex: 0,
				ruleId: 0
			};
		},
		async onLoad(options) {
			//接收传值,id里面放的是标题，因为测试数据并没写id
			let id = options.id;
			this.ruleId = id;
			if (id) {
				// this.$api.msg(`点击了${id}`);
			}
			this.loadData(id)

		},
		methods: {

			//加载商品 ，带下拉刷新和上滑加载
			async loadData(id) { //type 标识是否需要重新计算分页	  
				var that = this;
				let userInfo = uni.getStorageSync('userInfo');
				that.$http({ //调用接口
					method: 'GET',
					params: {
						id: id,
						userId: userInfo.id
					},
					url: that.global.target + "/wx/privateMake/privateMakeDetail" //this指data
				}).then(function(response) { //接口返回数据
					// console.log( response); 

					that.goods = response.data.data.goods;
					that.goodsProduct = response.data.data.goodsProduct;
					that.attributeList = response.data.data.attributeList;

					that.authororcorp = response.data.data.authororcorp;
					that.authororcorpParas = response.data.data.authororcorpParas;
					that.authororcorpItems = response.data.data.authororcorpItems;
					console.log(that.authororcorpItems)

					// if(that.authororcorpItems!=null&&that.authororcorpItems.length>0){
					// 	that.itemName=that.authororcorpItems[0].itemName
					// 	that.itemMinPrice=that.authororcorpItems[0].itemMinPrice
					// 	that.itemPriceDesc=that.authororcorpItems[0].itemPriceDesc,
					// 	that.itemDingMoney=that.authororcorpItems[0].itemDingMoney
					// 	that.itemFinishDays=that.authororcorpItems[0].itemFinishDays
					// 	that.authororcorpItems.forEach(
					// 	  (item)=>{
					// 		that.array.push(item.itemName)
					// 	 }
					// 	)
					// }

					that.privateMakeRule = response.data.data.privateMakeRule;
					// 取预约商品属性
					that.itemName = that.privateMakeRule.goodsName
					that.itemMinPrice = that.privateMakeRule.lowPrice
					that.itemPriceDesc = that.privateMakeRule.itemPriceDesc
					that.itemDingMoney = that.privateMakeRule.dingjinPrice
					that.itemShipCost = that.privateMakeRule.shipCost
					that.itemFinishDays = that.privateMakeRule.finishDays

					console.log(that.privateMakeRule)

					that.privateMakeOrderList = response.data.data.privateMakeOrderList;
					//uni.hideLoading()
				}, function(error) {
					//uni.hideLoading()
				})
			},
			yudingOrder() {
				let id = this.ruleId
				uni.navigateTo({
					url: `/pages/private/yudingOrder?id=` + id
				});
			},

			stopPrevent() {},


		}
	};
</script>

<style lang="scss">
	page {
		background: $page-color-base;
		padding-bottom: 160upx;
	}

	.icon-you {
		font-size: $font-base + 2upx;
		color: #888;
	}

	.carousel {
		height: 722upx;
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
		padding: 20upx 30upx;

		.title {
			display: flex;
			justify-content: center;
			font-size: 32upx;
			color: $font-color-dark;
			height: 50upx;
			line-height: 50upx;
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

		.bot-time {
			padding-top: 10upx;
			padding-bottom: 10upx;
			display: flex;
			align-items: center;

			font-size: $font-sm;
			color: $font-color-light;

			text {
				flex: 1;
			}
		}

		.overlay {
			width: 200px;
			font-size: $font-sm;
			color: $uni-color-primary;

			z-indent: 2;
			left: 0;
			top: 0;
		}
	}

	/* 竞拍规则 */
	.jingpai-rule {
		margin-top: 16upx;
		display: flex;
		align-items: center;
		color: $font-color-base;
		background: linear-gradient(left, #fdf5f6, #fbebf6);
		padding: 12upx 30upx;
		height: 150upx;
		justify-content: space-between;

		.jingpai-column-rule {
			display: flex;
			flex-direction: column;
			align-items: center;
			font-size: $font-base;
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
		margin-top: 16upx;
		font-size: $font-sm + 2upx;
		color: $font-color-base;
		background: #fff;

		.c-row {
			display: flex;
			align-items: center;
			padding: 15upx 30upx;
			position: relative;
		}

		.e-header {
			padding: 20upx 30upx;
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

		.tit {
			width: 150upx;
		}

		.con {
			flex: 1;
			color: $font-color-dark;

			.selected-text {
				margin-right: 10upx;
			}
		}

		.bz-list {
			font-size: $font-sm + 2upx;
			color: $font-color-dark;

			text {
				display: inline-block;
				margin-right: 30upx;
			}
		}

		.con-list {
			flex: 1;
			display: flex;
			flex-direction: column;
			color: $font-color-dark;
			line-height: 40upx;
		}

		.red {
			color: $uni-color-primary;
		}
	}

	/* 出价记录 */
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
		border-bottom: 3upx;
		display: flex;
		padding: 20upx 0;
		justify-content: space-between;

		.left {
			display: flex;

			.portrait {
				flex-shrink: 0;
				width: 80upx;
				height: 80upx;
				border-radius: 100px;
			}
		}

		.title {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
		}

		.right {
			font-size: $font-base;
			padding-left: 26upx;
		}
	}

	.current {
		color: $uni-color-primary;
	}

	/*  拍卖规则描述 */
	.auction-rule {
		font-size: $font-sm;
		background: #fff;
		margin-top: 16upx;
	}

	/*  详情 */
	.detail-desc {
		background: #fff;
		margin-top: 16upx;
		text-align: center;
		.d-header {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 80upx;
			font-size: $font-base + 2upx;
			color: $font-color-dark;
			position: relative;

			text {
				padding: 0 20upx;
				background: #fff;
				position: relative;
				z-index: 1;
			}

			&:after {
				position: absolute;
				left: 50%;
				top: 50%;
				transform: translateX(-50%);
				width: 300upx;
				height: 0;
				content: '';
				border-bottom: 1px solid #ccc;
			}
		}
	}

	/* 规格选择弹窗 */
	.attr-content {
		padding: 10upx 30upx;

		.a-t {
			display: flex;

			image {
				width: 170upx;
				height: 170upx;
				flex-shrink: 0;
				margin-top: -40upx;
				border-radius: 8upx;
			}

			.right {
				display: flex;
				flex-direction: column;
				padding-left: 24upx;
				font-size: $font-sm + 2upx;
				color: $font-color-base;
				line-height: 42upx;

				.price {
					font-size: $font-lg;
					color: $uni-color-primary;
					margin-bottom: 10upx;
				}

				.selected-text {
					margin-right: 10upx;
				}
			}
		}

		.attr-list {
			display: flex;
			flex-direction: column;
			font-size: $font-base + 2upx;
			color: $font-color-base;
			padding-top: 30upx;
			padding-left: 10upx;
		}

		.item-list {
			padding: 20upx 0 0;
			display: flex;
			flex-wrap: wrap;

			text {
				display: flex;
				align-items: center;
				justify-content: center;
				background: #eee;
				margin-right: 20upx;
				margin-bottom: 20upx;
				border-radius: 100upx;
				min-width: 60upx;
				height: 60upx;
				padding: 0 20upx;
				font-size: $font-base;
				color: $font-color-dark;
			}

			.selected {
				background: #fbebee;
				color: $uni-color-primary;
			}
		}
	}

	/*  弹出层 */
	.popup {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		z-index: 99;

		&.show {
			display: block;

			.mask {
				animation: showPopup 0.2s linear both;
			}

			.layer {
				animation: showLayer 0.2s linear both;
			}
		}

		&.hide {
			.mask {
				animation: hidePopup 0.2s linear both;
			}

			.layer {
				animation: hideLayer 0.2s linear both;
			}
		}

		&.none {
			display: none;
		}

		.mask {
			position: fixed;
			top: 0;
			width: 100%;
			height: 100%;
			z-index: 1;
			background-color: rgba(0, 0, 0, 0.4);
		}

		.layer {
			position: fixed;
			z-index: 99;
			bottom: 0;
			width: 100%;
			min-height: 40vh;
			border-radius: 10upx 10upx 0 0;
			background-color: #fff;

			.btn {
				height: 66upx;
				line-height: 66upx;
				border-radius: 100upx;
				background: $uni-color-primary;
				font-size: $font-base + 2upx;
				color: #fff;
				margin: 30upx auto 20upx;
			}
		}

		@keyframes showPopup {
			0% {
				opacity: 0;
			}

			100% {
				opacity: 1;
			}
		}

		@keyframes hidePopup {
			0% {
				opacity: 1;
			}

			100% {
				opacity: 0;
			}
		}

		@keyframes showLayer {
			0% {
				transform: translateY(120%);
			}

			100% {
				transform: translateY(0%);
			}
		}

		@keyframes hideLayer {
			0% {
				transform: translateY(0);
			}

			100% {
				transform: translateY(120%);
			}
		}
	}

	/* 底部操作菜单 */
	.page-bottom {
		position: fixed;
		left: 30upx;
		bottom: 30upx;
		padding-right: 10upx;
		z-index: 95;
		display: flex;
		justify-content: space-between;
		align-items: center;
		width: 690upx;
		height: 100upx;
		background: rgba(255, 255, 255, 0.9);
		box-shadow: 0 0 20upx 0 rgba(0, 0, 0, 0.5);
		border-radius: 16upx;

		.input {
			text-align: center;
			border: 2upx;
			width: 150upx;
			color: $uni-color-primary;
			font-size: font-lg;
		}

		.p-b-btn {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			font-size: $font-sm;
			color: $font-color-base;
			width: 96upx;
			height: 80upx;

			.yticon {
				font-size: 40upx;
				line-height: 48upx;
				color: $font-color-light;
			}

			&.active,
			&.active .yticon {
				color: $uni-color-primary;
			}

			.icon-fenxiang2 {
				font-size: 42upx;
				transform: translateY(-2upx);
			}

			.icon-shoucang {
				font-size: 46upx;
			}
		}

		.action-btn-group {
			display: flex;
			height: 76upx;

			overflow: hidden;
			box-shadow: 0 20upx 40upx -16upx #fa436a;
			box-shadow: 1px 2px 5px rgba(219, 63, 96, 0.4);
			background: linear-gradient(to right, #ffac30, #fa436a, #f56c6c);
			margin-left: 20upx;
			position: relative;

			&:after {
				content: '';
				position: absolute;
				top: 50%;
				right: 50%;
				transform: translateY(-50%);
				height: 28upx;
				width: 0;
				border-right: 1px solid rgba(255, 255, 255, 0.5);
			}

			.action-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 150upx;
				height: 100%;
				font-size: $font-base;
				padding: 0;
				border-radius: 0;
				background: transparent;
			}
		}
	}
</style>
