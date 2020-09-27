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
				<view :key="cate.code" class="nav-item" 
				:class="{ current: filterCateName === cate.name }" 
				@click="tabClick(cate.name)" v-for="(cate,index) in cateList" >
					{{cate.name}}
				</view>
			</view>
			
			<view class="goods-list">
				 <view class="guess-left" ></view>
					<view  class="goods-item" v-for="(item, indexPrivate) in privateList"  @click="navToDetailPage(item)">
						<view class="image-wrapper">
							<image :src="item.privatePicHead" mode="aspectFill"></image>
						</view>
						<text class="title clamp">{{ item.goodsName }}</text>
						<view class="price-box">
							<text class="price">{{ item.lowPrice }}起</text>
							<text>成交单量 {{ item.orderCount }}</text>
						</view>
					</view>
				 </view>
			</view>
 
			
			
		</view>
	</view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
export default {
	components: {
		uniLoadMore
	},
	data() {
		return {
			filterCateName:'全部',
			headerPosition: 'fixed',
			headerTop: '0px',
			cateList: [],
			privateList: [],
			countDownText:undefined,
			page:1,
			size:6,
			
		};
	},

	onLoad(options) {
		this.loadCateList();
	},
	onShow(){
		this.page=1
		
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
				 url:this.global.target+"/wx/privateMake/privateMakeClass"  //this指data
			}).then(function(response){  //接口返回数据
				// console.log( response);
				var total = response.data.data.total;
				if(total==null||total==0){
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
				 url:this.global.target+"/wx/privateMake/privateMakeList"  //this指data
			}).then(function(response){  //接口返回数据
	
				var total = response.data.data.total;
				if(total==0){
					uni.hideLoading()
					that.$api.msg("无数据");
					return
				}
				 
				var glist = response.data.data.list;
				if(type){
					that.privateList=glist;
					document.documentElement.scrollTop = 0;
				}else{
					glist.forEach(item=>{
						that.privateList.push(item);
					})
				}
				 
			 
				uni.hideLoading()
			},function(error){
				uni.hideLoading()
				// this.loadingType="noMore"
			}) 
		},
 
		//筛选点击
		tabClick(cate) {
			this.filterCateName=cate;
	        this.privateList=[];
			uni.pageScrollTo({
				duration: 300,
				scrollTop: 0
			});
			this.page=1
			this.loadData('refresh', 1);
			uni.showLoading({
				title: '正在加载'
			});
		},
	 
	 
		//详情
		navToDetailPage(item) {
			//测试数据没有写id，用title代替
			let id = item.id;
			uni.navigateTo({
				url: `dingzhidetail?id=${id}`
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
	/* #endif */
page,
.content {
	background: $page-color-base;
}
.content {
	padding-top: 86upx;
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
	padding: 0 8upx 0 8px;
	width: 100vw;
	margin: 0upx 0 100upx 0;
	.guess-left {
		// flex: 1;
		display: flex;
		flex-wrap: wrap;
		justify-content: space-evenly;
		// flex-direction: column;
	}
	
	.goods-item {
		 
		background: #fff;
		// display: flex;
		// flex-direction: column;
		width: 48%;
		margin: 3px;
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
		font-size: $font-lg;
		color: $font-color-dark;
		line-height: 80upx;
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
