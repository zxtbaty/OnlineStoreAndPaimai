<template>
	<view class="container">
		<view class="content">
		    <!-- 小程序头部兼容 -->
			<!-- #ifdef MP -->
			<view class="mp-search-box">
				<input class="ser-input" type="text" value="输入关键字搜索" disabled />
			</view>
			<!-- #endif -->
			<view class="navbar" :style="{ position: headerPosition, top: headerTop }">
				<view :key="cate.code" class="nav-item" :class="{ current: filterCateName === cate.name }" 
				@click="tabClick(cate.name)" v-for="(cate,index) in cateList" >
					{{cate.name}}
				</view>
<!-- 				<view class="nav-item" :class="{ current: filterIndex === 0 }" @click="tabClick(0)">推荐</view>
				<view class="nav-item" :class="{ current: filterIndex === 1 }" @click="tabClick(1)">手工艺品</view>
				<view class="nav-item" :class="{ current: filterIndex === 2 }" @click="tabClick(2)">饰品</view>
				<view class="nav-item" :class="{ current: filterIndex === 3 }" @click="tabClick(3)">摆件</view>
				<view class="nav-item" :class="{ current: filterIndex === 4 }" @click="tabClick(4)">玉石</view> -->
			 
			</view>
 
			<view class="goods-list">
				<view v-for="(item, index) in paimaiList" :key="index" class="goods-item" @click="navToDetailPage(item)">
					<view class="image-wrapper">
						<image :src="item.auctionPicHead" mode="aspectFill"></image>
					</view>
					<view class="overlay" >
						<view class="countdown" v-if="paimaiListTimes!=null&&paimaiListTimes.length>0" >
<!-- 							 <uni-countdown
								 :showText="countDownText"
								 :displayOneRow="true"
								 :duration="paimaiListTimes[index][0]">
							 </uni-countdown> -->
						</view>
					</view>
					<text class="title clamp">{{ item.goodsName }}</text>
					
					<view class="paimai-box" >
						<text class="title pmdate" >开拍：{{ item.beginTime }}</text>
						<text class="title pmdate">结拍：{{ item.endTime }}</text>
					</view>
					<view class="countdown c-row" v-if="paimaiList!=undefined">
						 <uni-countdown
							 :showText="'距离结束：'"
							 :displayOneRow="true"
							 :duration="paimaiListTimes[index][0]">
						 </uni-countdown>
					</view>
					<view class="paimai-box">
						<text>围观:{{item.visitCount}}</text>
						<text>出价次数:{{item.auctionCount}}</text>
						<text>出价人数:{{item.auctionPersonCount}}</text>
					</view>

				</view>
			</view>
			 
		</view>
	</view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import UniCountdown from '@/components/uni-countdown/uni-countdown'
 
export default {
	components: {
		uniLoadMore,UniCountdown
	},
	data() {
		return {
			filterCateName:'全部',
			headerPosition: 'fixed',
			headerTop: '0px',
            cateList: [],
			paimaiList: [],
			countDownText:undefined,
			page:1,
			size:4,
			paimaiListTimes: [[]], //定时器列表
		};
	},

	onLoad(options) {
		this.loadCateList();
	},
	onShow(){
		this.page=1;
		
		this.loadData('refresh');
	},
	onPageScroll(e) {
		//兼容iOS端下拉时顶部漂移
		if (e.scrollTop >= 0) {
			this.headerPosition = 'fixed';
		} else {
			this.headerPosition = 'absolute';
		}
	},
	//下拉刷新
	onPullDownRefresh() {
		this.loadData('refresh');
	},
	//加载更多
	onReachBottom() {
		uni.showLoading({
			title: '正在加载'
		}) 
		this.page++;
		this.loadData();
	},
	methods: {
		//加载分类
		async loadCateList() {
			var that=this;
			that.$http({           //调用接口
			    method:'GET',
				params:{
				},
				 url:this.global.target+"/wx/dajiaPai/dajiaPaiClass"  //this指data
			}).then(function(response){  //接口返回数据
				// console.log( response);
				var total = response.data.data.total;
				if(total==0){
					uni.hideLoading()
					that.$api.msg("无数据");
					return
				}		 
				that.cateList = response.data.data.list;
				
				//uni.hideLoading()
			},function(error){
				//uni.hideLoading()
			}) 
		},
		//加载商品 ，带下拉刷新和上滑加载
		async loadData(type) {//type 标识是否需要重新计算分页

			var that=this
	 
			var page=that.page;
			var size = that.size;
			that.$http({           //调用接口
				method:'GET',
				params:{
					className:that.filterCateName,
					page:page,
					size:size
				},
				 url:this.global.target+"/wx/dajiaPai/dajiaPaiList"  //this指data
			}).then(function(response){  //接口返回数据

				var total = response.data.data.total;
				if(total==0){
					uni.hideLoading()
					that.$api.msg("无数据");
					return
				}
				 
				var glist = response.data.data.list;
				if(type){
					that.paimaiList=glist;
					document.documentElement.scrollTop = 0;
				}else{
					glist.forEach(item=>{
						that.paimaiList.push(item);
					})
				}
				 
				that.daotime();
				// this.loadingType="noMore"
				uni.hideLoading()
			},function(error){
				uni.hideLoading()
				// this.loadingType="noMore"
			}) 
		},
		async daotime() {
			 
			//设置倒计时
			var that = this;
			if(that.paimaiList==null||that.paimaiList.length==0){
				return;
			}
			var myDate = Math.round(new Date().getTime() / 1000).toString();
			that.currenttime = myDate;
			var paimaiList = that.paimaiList;
			that.paimaiListTimes=[{}];
			var duration = 0;
			for (var i = 0; i < that.paimaiList.length; i++) {
				let times = new Array();
				let endTimeSource=undefined
			 
				 endTimeSource=that.paimaiList[i].endTime;
				 
				 
				if(endTimeSource==null){
					return;
				}
				let endTimeReplace = endTimeSource.replace(/-/g,'/');
				
				var endtime = (Date.parse(new Date(endTimeSource))||Date.parse(new Date(endTimeReplace))) / 1000 - myDate - duration;
				
				endtime = endtime > 0 ? endtime : 0;
				
				that.duration=endtime;
				times.push(endtime);
			  
				that.paimaiListTimes[i] = times;
				that.$forceUpdate();
			}
		 
		},
		//筛选点击
		tabClick(cate) {
			this.filterCateName=cate;
            this.paimaiList=[];
			uni.pageScrollTo({
				duration: 300,
				scrollTop: 0
			});
			this.page=1;
			this.loadData('refresh', 1);
			uni.showLoading({
				title: '正在加载'
			});
		},
	 
 
		//详情
		navToDetailPage(item) {
			//拍卖明细ID
			let id = item.id;
			uni.navigateTo({
				url: `paimaidetail?type=dajia&id=`+id
			});
		},
		stopPrevent() {}
	}
};
</script>

<style lang="scss">
/* #ifdef MP */
.mp-search-box {
	position: absolute;
	left: 0;
	top: 30upx;
	z-index: 9999;
	width: 100%;
	padding: 0 80upx;
	.ser-input {
		flex: 1;
		height: 56upx;
		line-height: 56upx;
		text-align: center;
		font-size: 28upx;
		color: $font-color-base;
		border-radius: 20px;
		background: rgba(255, 255, 255, 0.6);
	}
}
/* #endif  */

page,
.content {
	background: $page-color-base;
}
.content {
	padding-top: 86upx;
}
.countdown{
	font-size: $font-base ;
	padding-left: 0upx;
	padding-top: 0upx;
}

.navbar {
	position: relative;
	left: 0;

	display: flex;
	width: 100%;
	height: 80upx;
	background: #fff;
	box-shadow: 0 2upx 10upx rgba(0, 0, 0, 0.06);
	z-index: 10;
	.nav-item {
		flex: 1;
		display: flex;
		justify-content: center;
		align-items: center;
		height: 80upx;
		font-size: 30upx;
		color: $font-color-dark;
		position: relative;
		&.current {
			color: $base-color;
			&:after {
				content: '';
				position: absolute;
				left: 50%;
				bottom: 0;
				transform: translateX(-50%);
				width: 120upx;
				height: 0;
				border-bottom: 4upx solid $base-color;
			}
		}
	}
	.p-box {
		display: flex;
		flex-direction: column;
		.yticon {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 30upx;
			height: 14upx;
			line-height: 1;
			margin-left: 4upx;
			font-size: 26upx;
			color: #888;
			&.active {
				color: $base-color;
			}
		}
		.xia {
			transform: scaleY(-1);
		}
	}
	.cate-item {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 80upx;
		width: 80upx;
		position: relative;
		font-size: 44upx;
		&:after {
			content: '';
			position: absolute;
			left: 0;
			top: 50%;
			transform: translateY(-50%);
			border-left: 1px solid #ddd;
			width: 0;
			height: 36upx;
		}
	}
}

/* 分类 */
.cate-mask {
	position: fixed;
	left: 0;
	top: var(--window-top);
	bottom: 0;
	width: 100%;
	background: rgba(0, 0, 0, 0);
	z-index: 95;
	transition: 0.3s;

	.cate-content {
		width: 630upx;
		height: 100%;
		background: #fff;
		float: right;
		transform: translateX(100%);
		transition: 0.3s;
	}
	&.none {
		display: none;
	}
	&.show {
		background: rgba(0, 0, 0, 0.4);

		.cate-content {
			transform: translateX(0);
		}
	}
}
.cate-list {
	display: flex;
	flex-direction: column;
	height: 100%;
	.cate-item {
		display: flex;
		align-items: center;
		height: 90upx;
		padding-left: 30upx;
		font-size: 28upx;
		color: #555;
		position: relative;
	}
	.two {
		height: 64upx;
		color: #303133;
		font-size: 30upx;
		background: #f8f8f8;
	}
	.active {
		color: $base-color;
	}
}

/* 商品列表 */
.goods-list {
	display: flex;
	flex-wrap: wrap;
	padding: 0 10upx 0 20upx;
	// background: #fff;
	background:$page-color-base;
	.goods-item {
		margin-top: 10px;
		background: #fff;
		display: flex;
		flex-direction: column;
		width: 100%;
		padding-bottom: 40upx;
		// &:nth-child(2n + 1) {
		// 	margin-right: 4%;
		// }
	}
	.image-wrapper {
		width: 100%;
		height: 330upx;
		border-radius: 3px;
		overflow: hidden;
		image {
			width: 100%;
			height: 100%;
			opacity: 1;
		}
	}
	.title {
		display: flex;
		justify-content: center;
		font-size: $font-lg;
		color: $font-color-dark;
		height: 50upx;
		line-height: 50upx;
	}
	// .title {
	// 	font-size: $font-lg;
	// 	color: $font-color-dark;
	// 	line-height: 80upx;
	// }
	.pmdate{
		font-size: $font-sm;
		color: $font-color-light;
		line-height: 40upx;
	}
	.paimai-box{
		padding-top: 10upx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-right: 10upx;
		font-size: 24upx;
		color: $font-color-base;
	}
	.price-box {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-right: 10upx;
		font-size: 24upx;
		color: $font-color-light;
	}
	.price {
		font-size: $font-lg;
		color: $uni-color-primary;
		line-height: 1;
		&:before {
			content: '￥';
			font-size: 26upx;
		}
	}
}
</style>
