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
							   <input maxlength="140" step="" v-model="txtSearch" placeholder="搜索商品" autocomplete="off" @confirm="navToSearch" type="search" class="uni-input-input">
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
	    <view class="item_content" >
			<view class="left-aside">
				<view v-for="item in catoryList" :key="item.id"
				  class="f-item b-b" :class="{active: item.id === sel_catoryId}" 
				  @click="tabtap(item)">
					{{item.name}}
				</view>
			</view>
			<view class="right-aside">  
				<view class="guess-section">
					<view class="guess-left">
						<block  v-for="(item, index) in goodsList" :key="index">
							<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==0">
								<view class="image-wrapper">
									<image :src="item.picUrl" mode="aspectFill"></image>
								</view>
								<view class="guess-s-bottom">
									<view class="title clamp">{{item.name}}</view>
								</view>
								<view class="guess-bug">
<!-- 									<text class="price" v-if="item.priceDesc!=null&&item.priceDesc!=''">￥{{item.priceDesc}}</text>
									<text class="price" v-if="item.priceDesc==null||item.priceDesc==''" >￥{{item.retailPrice}}</text> -->
									<text class="price">￥{{item.retailPrice}}</text>
								</view> 
							</view>
						</block>
					</view>
					<view class="guess-right">
						<block  v-for="(item, index) in goodsList" :key="index">
							<view class="guess-item" @click="navToDetailPage(item)" v-if="index%2==1">
								<view class="image-wrapper">
									<image :src="item.picUrl" mode="aspectFill"></image>
								</view>
								<view class="guess-s-bottom">
									<view class="title clamp">{{item.name}}</view>
								</view>
								<view class="guess-bug">
						
<!-- 									<text class="price" v-if="item.priceDesc!=null&&item.priceDesc!=''">￥{{item.priceDesc}}</text>
								    <text class="price" v-if="item.priceDesc==null||item.priceDesc==''" >￥{{item.retailPrice}}</text>
 -->	                               
									<text class="price">￥{{item.retailPrice}}</text>
							 
									
								</view> 
							</view>
						</block>
					</view>
				</view>
			</view>
	 </view>
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
		 
				 
				catoryList: [],
				goodsList: [],
				page:1,
				size:10,
				loading:true,
				sel_catoryId: '', //选中的分类ID
			}
		},
		onLoad(){
			this.loadData();
		},
		methods: {
			navTo(url) {
				uni.navigateTo({
					url
				})
			},
			// 搜索栏
			navToSearch() {
				//console.log('输入内容为：',e.text)
				//alert(e.text);
				uni.navigateTo({
					url: `/pages/category/search?keyword=` + this.txtSearch
				});
			},
			async loadData(){
				//获取某个类下所有的子类
				var that = this;
				that.$http({ //调用接口
					method: 'GET',
					params:{
						parentClassName:'',
					},
					url: this.global.target + "/wx/catalog/getClassListAndFirstClassGoods" //this指data
				}).then(function(response) { //接口返回数据
					that.catoryList = response.data.data.categoryList;
					if(that.catoryList!=null&&that.catoryList.length>0){
						that.sel_catoryId=that.catoryList[0]["id"]
					}
					that.goodsList= response.data.data.firstCategoryGoodsList;
				}, function(error) {})
				
			},
			//加载更多
			onReachBottom(){
				this.getGoodslist();
			},
			
			async getGoodslist(type){//type 标识是否需要重新计算分页
				var loading =this.loading;
				uni.showLoading()
				if(loading==true){
					var that = this;
					var c_currentid = that.sel_catoryId;
					console.log(c_currentid)
					var page=that.page;
					var size = that.size;
					that.$http({           //调用接口
					    method:'GET',
						params:{
							categoryId:c_currentid,
							page:page,
							size:size
						},
						 url:this.global.target+"/wx/catalog/getGoodsListByCategoryId"  //this指data
					}).then(function(response){  //接口返回数据
						// console.log( response);				
						var total = response.data.data.total;
						if(total==0){
							uni.hideLoading()
							that.$api.msg("无数据");
						}
						var num = page*size;
						if(total<=num){
							that.loading=false;
						}
						var glist = response.data.data.list;
						if(type){
							that.goodsList=glist;
							document.documentElement.scrollTop = 0;
						}else{
							glist.forEach(item=>{
								that.goodsList.push(item);
							})
						}
						that.page++;
						uni.hideLoading()
					},function(error){
						uni.hideLoading()
					})
				} else{
					uni.hideLoading()
				}	
			},
			//详情页
			navToDetailPage(item) {
				let id = item.id;
				uni.navigateTo({
					url: `/pages/product/product?id=${id}`
				})
			},
			
			//一级分类点击
			tabtap(item){
				this.page=1
				this.loading=true;
				this.sel_catoryId=item.id
				this.getGoodslist(1);
				
			}
		},
	}

</script>

<style lang='scss'>
	page,
	.content {
		height: 100%;
		background-color: #f8f8f8;
	}

	.content {
		display: flex;
	}
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
	
	.item_content {
		background: #fff;
	    margin-top: 45px;
        display: flex; 
		font-size: 29upx;
		.left-aside {
	 
			/* bottom: 50px; */
			/* flex-shrink: 0; */
			width: 200upx;
			/* height: 100%; */
			background-color: #fff;
		}
		.right-aside{
			/* flex: 1; */
			overflow: auto;
			padding-left: 20upx;
			.guess-section {
				display: flex;
				justify-content: space-around;
				flex-wrap: wrap;
				padding: 0 27upx;
				width: 75vw;
				margin: 0upx 0 100upx 0;
				.guess-left,.guess-right{
					flex: 1;
					display: flex;
					flex-wrap: wrap;
					flex-direction: column;
				}
				.guess-left{
					padding-right: 10upx;
					margin-bottom: 50px; 
					width: 120px;
				}
				.guess-right{
					padding: 0 0 0 10upx;
					margin-bottom: 50px; 
					width: 120px;
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
					height: 152px;
				}
			
				.image-wrapper {
			        margin-left: 2px;
					padding-right: 2px;
					width: 100%;
					height: 327upx;
					border-radius: 3px;
					overflow: hidden;
			
					image {
						width: 95%;
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
			
		}
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
		&.active{
			color: $base-color;
			background: #f8f8f8;
			&:before{
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


</style>
