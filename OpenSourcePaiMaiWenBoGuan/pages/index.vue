 
<template>
  <view class="container">

	<view class="content">
	    <!-- 小程序头部兼容 -->
		<!-- #ifdef MP -->
<!-- 		<view class="mp-search-box">
			<input class="ser-input" type="text" value="输入关键字搜索" disabled />
		</view> -->
		<!-- #endif -->

		<view class="navbar" :style="{position:headerPosition,top:headerTop}">
			<view :key="index" class="nav-item" :class="{ current: currentClassId === item.id }" 
					@click="tabClick(item.id)" v-for="(item,index) in classlist" >
						{{item.name}}
			</view>		 
		</view>
 
		<view class="goods-list">
<!-- 			<view style="margin-top: 65px;position:relative;">
				<block  v-for="(article, index) in cardContentList" :key="index">
				   <NAUIcard :listData="article"  ></NAUIcard>
				</block>
			</view> -->
			
			
			<view style="position:relative;"
				v-for="(item, index) in cardContentList" :key="index"
				class="goods-item"
				@toDetail="navToDetailPage(item.id)">
				<!-- <view class="image-wrapper">	
					<image :src="item.title" mode="aspectFill">	
					</image>
				</view> -->
				
				<view class="cu-list menu menu-avatar">
					 <view class="cu-item">
						<view class="cu-avatar round lg">
						<!-- 头像，匿名时显示统一的匿名头像，若需自定义请自行修改 -->
							<image class="avatar round" style="width: 49px;height: 45px;" :src="item.anony ? 'http://img.nauzone.cn/20141118153114_aiRyY.thumb.700_0.jpeg' : item.avatarurl"></image>
							<view v-if="item.mark" class="cu-tag badge bg-blue">V</view>
						</view>
						<view class="content padding-tbl">
							<view class="henflex">
								<view>{{item.anony? '一位路过的吃瓜群众' : item.user_name}}</view>
								<view class="right" v-if="item.mark">
									<view class="cu-tag round bg-blue sm">官方</view>
								</view>
								<view class="right" v-if="item.anony">
									<view class="cu-tag round bg-orange sm">匿名</view>
								</view>
								<view class="huati text-orange text-bold">#{{ item.type }}#</view>
							</view>
							<view class="text-gray text-sm flex justify-between">
								<text class="left">{{item.creat_time}}</text>
								<text class="right">浏览:{{ item.show_times }}</text>
								<text class="right">点赞:{{ item.points }}</text>
								<!-- <text class="right">分享:{{ item.shareCount }}</text> -->
							</view>
							
						</view>
					 </view>
				</view>
			 
			   <view style="height: 270px;margin: 10px;">
			 <!-- 				<view style="text-align: center;font-size:medium;font-weight: bold;"  >
			 					<text >{{listData.title}}</text>
			 				</view> -->
							 
			 				<view class="text-content" v-html="item.fengmian" @click="navToDetailPage(item.id)">
			 				</view>
			  </view>
			 
				 
			</view>
				
 
		
		</view>
	 
 
	</view>
	</view>
</template>

<script>
	 
	import NAUIcard from '@/components/NAUI-card/NAUI-card.vue'
	import wx from 'weixin-js-sdk'
	export default {
		components: {
			 NAUIcard
		},
		data() {
			return {
				headerPosition:"fixed",
				headerTop:"0px",
				
				tabScrollTop: 0,
				tabScrollLeft: 0,
				
				classlist: [],
				articlelist: [], //从数据库获取的文章列表
				cardContentList:[],//加工处理的卡片内容列表
				
				currentArtilceId: null,
				currentClassId: null, //当前选中的分类id
				page:1,
				size:3,
				loading:true,
  
			}
		},
		
		
		onLoad(options) {
			
			 
			this.loadDefault(options)
			 //分享信息
			 this.getShareInfo();
		},
		onShow(){
			var that = this;
			that.classlist = [];
			that.$http({           //调用接口
			    method:'GET',
				 url:this.global.target+"/wx/wenBoGuan/getClass"  //this指data
			}).then(function(response){  //接口返回数据
				// console.log(response)
				 that.classlist =  response.data.data.list;
				 that.currentClassId=uni.getStorageSync("currentClassId")
				 // that.currentClassId=that.classlist[0].id;
			 
			},function(error){
			})
		 
		},
		//加载更多
		onReachBottom(){
			// this.page++;
			this.getArticlelist();
		},
		methods: {
			getShareInfo() {
				var that = this;
			
				var url = encodeURIComponent(window.location.href.split('#')[0]);
				if (window.__wxjs_is_wkwebview) {
			
					// return;
				} else {
					url = encodeURIComponent(window.location.href.split('#')[0]);
				}
				that.$http({
					//调用接口
					method: 'POST',
					params: {
						url: url
					},
					url: that.global.target + '/wx/share/info' //this指data
				}).then(
					function(response) {
						//接口返回数据
						let result = response.data.data;
			            console.log(result)
						wx.config({
							debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
							appId: result.appid, // 必填，公众号的唯一标识
							timestamp: result.timestamp, // 必填，生成签名的时间戳
							nonceStr: result.nonceStr, // 必填，生成签名的随机串
							signature: result.signature, // 必填，签名
							jsApiList: ['updateAppMessageShareData','updateTimelineShareData'] // 必填，需要使用的JS接口列表
						});
						 
						
					},
					function(error) {
						console.log("微信config配置失败 err: ", error);
					}
				);
			
			},
			
 
			loadDefault(options){
				var that = this;
				that.classlist = [];
				that.$http({           //调用接口
				    method:'GET',
					 url:this.global.target+"/wx/wenBoGuan/getClass"  //this指data
				}).then(function(response){  //接口返回数据
					// console.log(response)
					 that.classlist =  response.data.data.list;
					 that.currentClassId=that.classlist[0].id;
					 that.loadData();
				},function(error){
				})
			},
			//筛选点击
			tabClick(id) {
				this.currentClassId=id;
			    this.articlelist=[];
				this.cardContentList=[]
				uni.pageScrollTo({
					duration: 300,
					scrollTop: 0
				});
				this.page=1;
				this.loading=true;
				uni.showLoading({
					title: '正在加载'
				});
				this.getArticlelist(1);
				
			},
			async loadData() {
				this.getArticlelist(1);
			},
			
			async getArticlelist(type){//type 标识是否需要重新计算分页
				var loading =this.loading;
				uni.showLoading({
					title: '正在加载'
				});
				if(loading==true){
					var that = this;
					var classId=that.currentClassId;
					var page=that.page;
					var size = that.size;
				 
					that.$http({           //调用接口
					    method:'GET',
						params:{
							classId:classId,
							page:page,
							size:size
						},
						 url:this.global.target+"/wx/wenBoGuan/getAricleList"  //this指data
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
							that.articlelist=glist;
							document.documentElement.scrollTop = 0;
						}else{
							glist.forEach(item=>{
								that.articlelist.push(item);
							})
						}
						//统一转换至卡片内容组件
						that.convertToCardContentList();
						that.page++;
						uni.hideLoading()
					},function(error){
						uni.hideLoading()
					})
				} else{
					uni.hideLoading()
				}
			},
			convertToCardContentList(){
				let that=this
				that.cardContentList=[];
				that.articlelist.forEach(item=>{
					let detail={
						"id": item.articleId,
						"fengmian": item.fengmian,
						"content": item.content,
						"img_url": [
						    "http://img.nauzone.cn/78764bea5a2c8828b433d7b050bcc5a4",
						    "http://img.nauzone.cn/8cfa5e52763ec60ab9ec933aa594a1c0",
						    "http://img.nauzone.cn/778a955fdc252fb432c68f1247835b12",
						    "http://img.nauzone.cn/4068d8d16c125a2ab76089166adf0903",
						    "http://img.nauzone.cn/73eb04f0d7d426d96de6f356f181da5e"
						],
						"user_name": "国创容德",
						"mark":true,
						"type": item.className,
						"title":item.title,
						"points":  item.zanCount==null?0:item.zanCount,
						"show_times": item.browseCount==null?0:item.browseCount,
						"shareCount":item.shareCount==null?0:item.shareCount,
						"creat_time":item.pubDate,
						"anony": false,
						"avatarurl": "../static/gcrdpmLogo.png"
					}
					that.cardContentList.push(detail)
				})
			},
  
			// 切换父级标签
			change_classid(id) {
				this.currentClassId = id;
				this.loading=true;
				this.page=1;
				this.getArticlelist(1);
			},
  
			//详情页
			navToDetailPage(id) {
				uni.setStorageSync("currentClassId",this.currentClassId);
				console.log(id)
				//记录浏览量+1
				var that = this;
				that.classlist = [];
				that.$http({           //调用接口
				    method:'GET',
					params:{
						articleId:id
					},
					url:this.global.target+"/wx/wenBoGuan/logBrowse"  //this指data
				}).then(function(response){  //接口返回数据
					 
					uni.navigateTo({
						url: `/pages/article?id=${id}`
					})
				},function(error){
				})
				
			},
		 
		 
		},
		 
	}
</script>

<style lang="scss">
		/* #ifdef MP */
	.mp-search-box{
		position:absolute;
		left: 0;
		top: 30upx;
		z-index: 9999;
		width: 100%;
		padding: 0 80upx;
		.ser-input{
			flex:1;
			height: 56upx;
			line-height: 56upx;
			text-align: center;
			font-size: 28upx;
			color:$font-color-base;
			border-radius: 20px;
			background: rgba(255,255,255,.6);
		}
	}
		/* #endif */
	page, .content{
		 
		background: $page-color-base;
	}
	.content{
		padding-top: 86upx;
	}

	.navbar{
		position: relative;
		left: 0;
		display: flex;
		width: 100%;
		height: 80upx;
		background: #fff;
		box-shadow: 0 2upx 10upx rgba(0,0,0,.06);
		z-index: 10;
		.nav-item{
			flex: 1;
			display: flex;
			justify-content: center;
			align-items: center;
			height: 80upx;
			font-size: 30upx;
			color: $font-color-dark;
			position: relative;
			&.current{
				color: $base-color;
				&:after{
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
		.p-box{
			display: flex;
			flex-direction: column;
			.yticon{
				display: flex;
				align-items: center;
				justify-content: center;
				width: 30upx;
				height: 14upx;
				line-height: 1;
				margin-left: 4upx;
				font-size: 26upx;
				color: #888;
				&.active{
					color: $base-color;
				}
			}
			.xia{
				transform: scaleY(-1);
			}
		}
		.cate-item{
			display: flex;
			justify-content: center;
			align-items: center;
			height: 80upx;
			width: 80upx;
			position: relative;
			font-size: 44upx;
			&:after{
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
	.cate-mask{
		position: fixed;
		left: 0;
		top: var(--window-top);
		bottom: 0;
		width: 100%;
		background: rgba(0,0,0,0);
		z-index: 95;
		transition: .3s;
		
		.cate-content{
			width: 630upx;
			height: 100%;
			background: #fff;
			float:right;
			transform: translateX(100%);
			transition: .3s;
		}
		&.none{
			display: none;
		}
		&.show{
			background: rgba(0,0,0,.4);
			
			.cate-content{
				transform: translateX(0);
			}
		}
	}
	.cate-list{
		display: flex;
		flex-direction: column;
		height: 100%;
		.cate-item{
			display: flex;
			align-items: center;
			height: 90upx;
			padding-left: 30upx;
 			font-size: 28upx;
			color: #555;
			position: relative;
		}
		.two{
			height: 64upx;
			color: #303133;
			font-size: 30upx;
			background: #f8f8f8;
		}
		.active{
			color: $base-color;
		}
	}

	/* 商品列表 */
	.goods-list{
		display:flex;
		flex-wrap:wrap;
		background: $page-color-base;
		.goods-item{
			position:relative;
			display:flex;
			flex-direction: column;
			width: 98%;
		    padding-left: 10upx;
			padding-top: 30upx;
			padding-bottom: 60upx;
		    background:  #fff;
			margin: 10upx;
			.overlay{
				position:absolute;
				width:98%;
				font-size: $font-sm;
				color: $uni-color-primary;
				height:100px;
				z-indent:2;
				left:0;
				top:0;
				.countdown{
					padding-left: 20upx;
					padding-top: 20upx;
				}
			}
			.endtime{
				position:relative;
				width:98%;
				font-size: $font-sm;
				color: $uni-color-primary;
				z-indent:2;
				left:0;
				top:0;
				.countdown{
					padding-left: 10upx;
					padding-top: 10upx;
				}
			}
			
		}
		.image-wrapper{
			width: 100%;
			height: 330upx;
			border-radius: 3px;
			overflow: hidden;
			image{
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
			line-height: 60upx;
		}
		.pmdate{
			font-size: $font-sm;
			color: $font-color-light;
			line-height: 40upx;
		}
		
		.paimai-box{
			padding-top: 20upx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding-right: 10upx;
			font-size: 24upx;
			color: $font-color-base;
		}
 
	}
	
	
	
 
	 
</style>

<style>
 
	uni-image.div, uni-image.img {
	    width: 49px;
	    height: 45px;
	}
	.bg-orange {
	  background-color: #f37b1d;
	  color: #fff;
	}
	
	.cu-tag.sm {
	  font-size: 20rpx;
	  padding: 0rpx 12rpx;
	  height: 32rpx;
	}
	
	.padding {
	  padding: 30rpx;
	}
	
	.text-right {
	  text-align: right;
	}
	
	.bg-img {
	  background-size: cover;
	  background-position: center;
	  background-repeat: no-repeat;
	}
	
	.padding-lr {
	  padding-left: 30rpx;
	  padding-right: 30rpx;
	}
	
	.padding-tbl {
	  padding-left: 20rpx;
	  padding-top: 20rpx;
		padding-bottom: 20rpx;
	}
	
	.flex-sub {
	  flex: 1;
	}
	
	.grid {
	  display: flex;
	  flex-wrap: wrap;
	}
	
	.grid.col-1>view {
	  width: 100%;
	}
	
	.text-content {
	  line-height: 1.6;
	  height: 290px;
	  overflow: hidden;
	}
	
.justify-between {
	justify-content: space-between;
}

.flex {
	display: flex;
}

.text-sm {
	font-size: 24rpx;
}

.text-bold {
	font-weight: bold;
}

.text-gray {
	color: #aaa;
}

.text-orange {
	color: #f37b1d;
}

.huati {
	margin-left: auto;
	margin-right: 20upx;
}

.bg-blue {
	background-color: #0081ff;
	color: #fff;
}

.right {
	margin-left: 20upx;
}

.henflex {
	display: flex;
	flex-flow: row;
}

.cu-list.menu > .cu-item .content {
	line-height: 1.6em;
	flex: 1;
	font-size: 30rpx;
	
}

.bg-pink {
	background-color: #e03997;
	color: #fff;
}

.cu-tag {
	font-size: 24rpx;
	vertical-align: middle;
	position: relative;
	display: inline-flex;
	align-items: center;
	justify-content: center;
	box-sizing: border-box;
	padding: 0rpx 16rpx;
	height: 48rpx;
	font-family: Helvetica Neue, Helvetica, sans-serif;
}

.cu-tag.badge {
	border-radius: 200rpx;
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	font-size: 20rpx;
	padding: 0rpx 10rpx;
	height: 28rpx;
	color: #fff;
}

.avatar {
	height: 45px;
	width: 49px;
}

.cu-avatar.lg {
	width: 49px;
	height: 45px;
	font-size: 2em;
}

.round {
	border-radius: 5000rpx;
}

.cu-avatar {
	font-variant: small-caps;
	margin: 0;
	padding: 0;
	display: inline-flex;
	text-align: center;
	justify-content: center;
	align-items: center;
	background: #ccc;
	color: #fff;
	white-space: nowrap;
	position: relative;
	width: 64rpx;
	height: 64rpx;
	background-size: cover;
	background-position: center;
	vertical-align: middle;
	font-size: 1.5em;
}

.cu-list.menu-avatar > .cu-item {
	padding-left: 140rpx;
}



.cu-list.menu {
	display: block;
	overflow: hidden;
}

.cu-list + .cu-list {
	margin-top: 30rpx;
}

.cu-list.menu > .cu-item {
	position: relative;
	display: flex;
	justify-content: space-between;
	align-items: center;
	min-height: 100rpx;
	/* background: #fff; */
	background: #f8f8f8;
	padding: 0 30rpx;
}

.shadow {
	box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.1);
}

.cu-card {
	display: block;
	overflow: hidden;
	height: 350px;
	 
}

.cu-card > .cu-item {
	display: block;
	background-color: #fff;
	overflow: hidden;
 
	border-radius: 10rpx;
	margin-left: 30rpx;
	margin-right: 30rpx;
	 
}

.cu-card > .cu-item.shadow-blur {
	overflow: initial;
}

.cu-card.no-card > .cu-item {
	margin: 0rpx;
	border-radius: 0rpx;
}

.cu-card .grid.grid-square {
	margin-bottom: -20rpx;
}

.cu-card.case .image {
	position: relative;
}

.cu-card.case .image image {
	width: 100%;
}

.cu-card.case .image .cu-tag {
	position: absolute;
	right: 0;
	top: 0;
}

.cu-card.case .image .cu-bar {
	position: absolute;
	bottom: 0;
	width: 100%;
	background-color: transparent;
	padding: 0rpx 30rpx;
	word-wrap: normal;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.cu-card.case.no-card .image {
	margin: 30rpx 30rpx 0;
	overflow: hidden;
	border-radius: 10rpx;
}

.cu-card.dynamic {
	display: block;
}

.cu-card.dynamic > .cu-item {
	display: block;
	background-color: #fff;
	overflow: hidden;
}

.cu-card.dynamic > .cu-item > .text-content {
	padding: 0 30rpx 0;
	max-height: 6.4em;
	overflow: hidden;
	font-size: 30rpx;
	margin-bottom: 20rpx;
}

.cu-card.dynamic > .cu-item .square-img {
	width: 100%;
	height: 200rpx;
	border-radius: 6rpx;
}

.cu-card.dynamic > .cu-item .only-img {
	width: 100%;
	height: 320rpx;
	border-radius: 6rpx;
}

.margin-top-sm {
	margin-top: 20rpx;
}

.margin-bottom-sm {
	margin-bottom: 20rpx;
}
</style>