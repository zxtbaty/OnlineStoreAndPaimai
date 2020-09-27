<template>

	<view class="content">
 	
		 <uni-page-head  >
			<div class="uni-page-head " style="background-color: #7E0076; color: rgb(0, 0, 0);">
				<div class="uni-page-head-hd">
					<div class="uni-page-head-btn" style="display: none; background-color: rgba(0, 0, 0, 0.5);">
						<i class="uni-btn-icon" style="color: rgb(255, 255, 255); font-size: 27px;"></i>
					</div>
				</div>
				<div class="uni-page-head-search" style="border-radius: 16px; background-color: rgba(251, 251, 251, 0.6);">
				   <div class="uni-page-head-search-placeholder uni-page-head-search-placeholder-left" style="color: rgb(251, 251, 251);">
				   </div>
				   <uni-input class="uni-page-head-search-input" style="color: rgb(0, 0, 0);">
						<div class="uni-input-wrapper">
							<div class="uni-input-placeholder" style="color: rgb(251, 251, 251);"></div>
							<form action="" class="uni-input-form">
							   <input maxlength="140" step="" :value="keyword" v-model="txtSearch"  placeholder="搜索商品" autocomplete="off" @confirm="navToSearch" type="search" class="uni-input-input">
							</form>
						</div>
					</uni-input>
				</div>
				<div class="uni-page-head-ft">
				   <view class="set-box">	  			
					<view class="icon"   @click="navTo('/pages/notice/notice')">
						<min-badge :count="userInfoCount" :maxCount="99">
						<image src="/static/newimg/message.png" mode="" ></image>
						</min-badge>			   
					</view>			
				  </view>
				</div>
			</div>
		 </uni-page-head>	 
		
		<view class="category">
			<!-- 商品列表 -->
			<view class="guess-section">
				<view class="guess-left">
					<block  v-for="(item, index) in goodslist" :key="index">
						<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==0">
							<view class="image-wrapper">
								<image :src="item.picUrl" mode="aspectFill"></image>
							</view>
							<view class="guess-s-bottom">
								<view class="title clamp">{{item.name}}</view>
							</view>
							<view class="guess-bug">
								<text class="price">￥{{item.retailPrice}}</text>
							</view> 
						</view>
					</block>
				</view>
				<view class="guess-right">
					<block  v-for="(item, index) in goodslist" :key="index">
						<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==1">
							<view class="image-wrapper">
								<image :src="item.picUrl" mode="aspectFill"></image>
							</view>
							<view class="guess-s-bottom">
								<view class="title clamp">{{item.name}}</view>
							</view>
							<view class="guess-bug">
								<text class="price">￥{{item.retailPrice}}</text>
							</view> 
						</view>
					</block>
				</view>
				<!-- <view v-for="(item, index) in goodslist" :key="index" class="guess-item" @click="navToDetailPage(item)">
					<view class="image-wrapper">
						<image :src="item.picUrl" mode="aspectFill"></image>
					</view>
					<view class="guess-s-bottom">
						<view class="title clamp">{{item.name}}</view>
					</view>
					<view class="guess-bug">
						<text class="price">￥{{item.retailPrice}}</text>
					</view> 
				</view> -->
			</view>
			<!-- 商品列表 end-->
			<!-- <scroll-view scroll-x class="cg_c_list">
				<block  v-for='(item,index) in fatherlist' :key='index'>
					<view class="cg_c_li"  v-if="item.pid!=''&&item.pid!=null&&item.name!=undefined&&item.pid==currentId">{{item.name}}</view>
				</block>
			</scroll-view> -->
		</view>
		<!-- 	<scroll-view scroll-y class="left-aside">
			<view v-for="item in flist" :key="item.id" class="f-item b-b" :class="{active: item.id === currentId}" @click="tabtap(item)">
				{{item.name}}
			</view>
		</scroll-view>
		<scroll-view scroll-with-animation scroll-y class="right-aside" @scroll="asideScroll" :scroll-top="tabScrollTop">
			<view v-for="item in slist" :key="item.id" class="s-list" :id="'main-'+item.id">
				<text class="s-item">{{item.name}}</text>
				<view class="t-list">
					<view @click="navToList(item.id, titem.id)" v-if="titem.pid === item.id" class="t-item" v-for="titem in tlist" :key="titem.id">
						<image :src="titem.picUrl"></image>
						<text>{{titem.name}}</text>
					</view>
				</view>
			</view>
		</scroll-view> -->
	</view>
</template>

<script>
	import minBadge from '@/components/min-badge/min-badge'
	export default {
		components: {
			minBadge
		},
		data() {
			return {
				txtSearch:'',
				userInfoCount:0,
				sizeCalcState: false,
				tabScrollTop: 0,
				tabScrollLeft: 0,
				flist: [],
				slist: [],
				tlist: [],
				fatherlist: [
					{
						name:"价格"
					}
				],
				currentId: 0,
				childshow: false, //当前二级是否显示
				current_f_id: 1, //当前选中的父级id
				goodslist: [], //商品列表
				c_currentid: '', //选中的二级子集
				page:1,
				size:10,
				loading:true,
				levl:0,
				keyword:"",
			}
		},
		onLoad() {
			this.loadData();
			this.getInfoCount();
		},
		//加载更多
		onReachBottom(){
			this.loadData();
		},
		methods: {
			//获取我的预约，我的特产订单角标的数量,我的消息角标的数量
			getInfoCount(){
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					url: this.global.target + "/wx/user/userinfopubscount" //this指data
				}).then(function(response) { //接口返回数据
					// console.log( response)
				 
					that.userInfoCount = response.data.data;
				}, function(error) {})
				
			},
			async loadData() {

				var loading = this.loading;
				if (loading == true) {
					if(this.keyword==""){
						this.keyword=this.$route.query.keyword;
						this.txtSearch = this.keyword;
					}
					var that = this;
					var page = that.page;
					var size = that.size;
					that.$http({ //调用接口
						method: 'POST',
						params: {
							keyword: that.keyword,
							page: page,
							size: size,
							sort: "add_time"
						},
						url: that.global.target + "/wx/goods/search" //this指data
					}).then(function(response) { //接口返回数据

						// that.goodslist =  response.data.data.goodslist;
						var total = response.data.data.totals;
						if (total == 0) {
							that.$api.msg("无数据");
						}
						// var num = page*size;
						if (total < size) {
							that.loading = false;
						}
						var glist = response.data.data.goodslist;
						console.log(response);
						glist.forEach(item => {
							that.goodslist.push(item);
						})

						that.page++;
						uni.hideLoading()


					}, function(error) {})
				}


			},
			
			//右侧栏滚动
			asideScroll(e) {
				if (!this.sizeCalcState) {
					this.calcSize();
				}
				let scrollTop = e.detail.scrollTop;
				let tabs = this.slist.filter(item => item.top <= scrollTop).reverse();
				if (tabs.length > 0) {
					this.currentId = tabs[0].pid;
				}
			},
			//计算右侧栏每个tab的高度等信息
			calcSize() {
				let h = 0;
				this.slist.forEach(item => {
					let view = uni.createSelectorQuery().select("#main-" + item.id);
					view.fields({
						size: true
					}, data => {
						item.top = h;
						h += data.height;
						item.bottom = h;
					}).exec();
				})
				this.sizeCalcState = true;
			},
			navToList(sid, tid) {
				uni.navigateTo({
					url: `/pages/product/list?fid=${this.currentId}&sid=${sid}&tid=${tid}`
				})
			},
			// 切换父级标签
			change_currentid(id) {
				this.currentId = id;
				this.loading=true;
				this.page=1;
				this.levl=1;
				this.getGoodslist(1);
			},
			// 展示子集列表
			childshow_btn() {
				this.childshow = !this.childshow;
			},
			//详情页
			navToDetailPage(item) {
				let id = item.id;
				uni.navigateTo({
					url: `/pages/product/product?id=${id}`
				})
			},
			navTo(url) {
				uni.navigateTo({
					url
				})
			},
			// 搜索栏
			navToSearch() {
				// uni.navigateTo({
				// 	url: `/pages/category/search?keyword=` + this.txtSearch
				// });
					
				this.loading = true;
				let keyword = this.txtSearch;
				this.keyword=keyword;
				var that = this;
				var page = 1;
				var size = that.size;
				that.$http({ //调用接口
					method: 'POST',
					params: {
						keyword: keyword,
						page: page,
						size: size,
						sort: "add_time"
					},
					url: that.global.target + "/wx/goods/search" //this指data
				}).then(function(response) { //接口返回数据
				
					// that.goodslist =  response.data.data.goodslist;
					var total = response.data.data.totals;
					if (total == 0) {
						that.$api.msg("无数据");
					}
					// var num = page*size;
					if (total < size) {
						that.loading = false;
					}
					var glist = response.data.data.goodslist;
					// console.log(response);
					that.goodslist=glist;
				
					that.page=2;
					uni.hideLoading()
				
				
				}, function(error) {})
					
				
			},
		}
	}
</script>

<style lang='scss'>
	page,
	.set-box {
		flex: 1;
	
		.icon {
			float: right;
			margin-left: 0upx;
			margin-right: 20upx
		}
	
		image {
			width: 44upx;
			height: 40upx;
		}
	}
	.content {
		height: 100%;
		/* background-color: #f8f8f8; */
		background-color: #f9f9f9;
	}

	.content {
		display: flex;
	}

	.left-aside {
		flex-shrink: 0;
		width: 200upx;
		height: 100%;
		background-color: #fff;
	}

	.f-item {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100upx;
		font-size: 28upx;
		color: $font-color-base;
		position: relative;

		&.active {
			color: $base-color;
			background: #f8f8f8;

			&:before {
				content: '';
				position: absolute;
				left: 0;
				top: 50%;
				transform: translateY(-50%);
				height: 36upx;
				width: 8upx;
				background-color: $base-color;
				border-radius: 0 4px 4px 0;
				opacity: .8;
			}
		}
	}

	.right-aside {
		flex: 1;
		overflow: hidden;
		padding-left: 20upx;
	}

	.s-item {
		display: flex;
		align-items: center;
		height: 70upx;
		padding-top: 8upx;
		font-size: 28upx;
		color: $font-color-dark;
	}

	.t-list {
		display: flex;
		flex-wrap: wrap;
		width: 100%;
		background: #fff;
		padding-top: 12upx;

		&:after {
			content: '';
			flex: 99;
			height: 0;
		}
	}

	.t-item {
		flex-shrink: 0;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-direction: column;
		width: 176upx;
		font-size: 26upx;
		color: #666;
		padding-bottom: 20upx;

		image {
			width: 140upx;
			height: 140upx;
		}
	}

	.c_tab {
		width: 100vw;
		position: fixed;
		z-index: 999;
		height: 67upx;
		top: 44px;
		background: #fff;
		display: flex;
		display: -webkit-flex;
		font-size: 29upx;
		/* box-shadow:0px 0px 14px 0px rgba(0, 0, 0, 0.15); */
		/* border-bottom: 2upx solid #f8f8f8; */
		/* padding: 0 20upx;
		box-sizing: border-box; */

	}

	.cg_f_list {
		/* display: flex;
		display: -webkit-flex;
		width: 100vw;
		flex-wrap: wrap;
		-webkit-flex-wrap: wrap; */
		white-space: nowrap;
		overflow: auto;
		width: 750upx;
		/* width: 650upx; */
	}

	.cg_c_list {
		display: flex;
		display: -webkit-flex;
		flex-wrap: wrap;
		-webkit-flex-wrap: wrap;
		margin: 20upx 0 0;
		position: fixed;
		z-index: 99;
		top: 44px;
		margin: 110upx 0 0 0;
		background: #fff;
	}

	.cg_show_cl {
		width: 100upx;
		height: 100upx;
		/* padding: 20upx; */
		/* box-sizing: border-box; */

	}

	.cg_show_cl .cd_show_btnimg {
		width: 100%;
		height: 100%;
	}

	.cg_f_li {
		display: inline-block;
		padding: 0upx 35upx;
		box-sizing: border-box;
		line-height: 67upx;
		position: relative;
		
		&:after {
			position: absolute;
			content: '';
			width: 1px;
			height: 30upx;
			background: #E7E7E7;
			right: 0;
			top:17upx;
		}
	}
	
	.cg_c_list .active {
		border: 2upx solid rgb(250, 67, 106);
	}

	.cg_c_li {
		padding: 10upx 14upx;
		box-sizing: border-box;
		font-size: 32upx;
		border-radius: 8upx;
		border: 2upx solid #f8f8f8;
		margin: 10upx;
	}

	.active {
		color:#FF0000;

	}

/* 	.cg_f_list .active::after {
		position: absolute;
		content: '';
		width: 88%;
		height: 2upx;
		background: rgb(250, 67, 106);
		left: 6%;
		bottom: 0;
	}
 */
	.guess-section {
		display: flex;
		flex-wrap: wrap;
		padding: 0 27upx;
		width: 100vw;
		margin: 67upx 0 100upx 0;
		.guess-left,.guess-right{
			flex: 1;
			display: flex;
			flex-wrap: wrap;
			flex-direction: column;
		}
		.guess-left{
			padding-right: 10upx;
		}
		.guess-right{
			padding: 0 0 0 10upx;
		}
		.guess-item {
			display: flex;
			flex-direction: column;
			width:100%;
			flex-shrink: 0;
			padding-bottom: 22upx;
			margin: 17upx 0 0 0;
			background: #fff;
			box-shadow:0px 0px 9upx 0px rgba(47,47,47,0.1);
			
		}

		.image-wrapper {
			width: 100%;
			height: 327upx;
			border-radius: 3px;
			overflow: hidden;

			image {
				width: 100%;
				height: 100%;
				opacity: 1;
			}
		}

		.title {
			font-size:29upx;
			font-weight:bold;
			font-weight:800;
			color: $font-color-dark;
			margin: 14upx 0 0;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 2;
			-webkit-box-orient:vertical;
		}

		.price {
			font-size:24upx;
			color: #FF3B30;
			font-weight: bold;
		}
		.guess-s-bottom{
			padding: 0 27upx 0 11upx;
			display: flex;
			justify-content: left;
			.clamp{
				white-space: normal;
			}
			.clamp text{
				background: #FF0000;
				color: #fff;
				font-size: 19upx;
				padding: 6upx 15upx;
				border-radius:13upx;
				line-height: 18upx;
				display: inline-block;
				margin: 0 10upx 0 13upx;
			}
		}
		.guess-bug{
			padding: 0 27upx 0 11upx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin: 3upx 0 0 0;
			image{
				width: 48upx;
				height: 48upx;
			}
		}

	}
</style>
