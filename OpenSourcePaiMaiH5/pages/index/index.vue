 
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
			<view class="nav-item" :class="{current: filterIndex === 0}" @click="tabClick(0)">
				拍卖中
			</view>
			<view class="nav-item" :class="{current: filterIndex === 1}" @click="tabClick(1)">
				预展中
			</view>
			<view class="nav-item" :class="{current: filterIndex === 2}" @click="tabClick(2)">
				已结束
			</view>
			 
		</view>
 
		<view class="goods-list">
			<view style="position:relative;"
				v-for="(item, index) in paimaiList" :key="index"
				class="goods-item"
				@click="navToDetailPage(item)">
				<view class="image-wrapper">	
					<image :src="item.auctionPicHead" mode="aspectFill">	
					</image>
				</view>
 
				<view class="overlay" v-if="filterIndex!=2">
					<view class="countdown" >
<!-- 						<count-down v-on:start_callback="countDownS_cb(1)" v-on:end_callback="countDownE_cb(1)" :currentTime="1481450106"
						:startTime="1481450110" :endTime="1481561115" :tipText="'距开始'"  :tipTextEnd="'距结束'" 
						:endText="'拍卖已结束'" :dayTxt="'天 '" :hourTxt="':'" :minutesTxt="':'" :secondsTxt="''">
						 </count-down>		 -->
						 <uni-countdown
						     :showText="countDownText"
							 :displayOneRow="true"
							 :duration="paimaiListTimes[index][0]">
						 </uni-countdown>
					</view>

				</view>
				<text class="title clamp">{{item.zhuanchangName}}</text>
				<!-- <text class="title clamp">陈丹青作品专场</text> -->
				<view class="paimai-box">
					<text>开拍：{{item.beginTime}}</text>
					<text>结拍：{{item.endTime}}</text>
				</view>
<!-- 				<view class="endtime" >
					<view class="countdown" >
						<count-down v-on:start_callback="countDownS_cb(1)" v-on:end_callback="countDownE_cb(1)" :currentTime="1481450106"
						:startTime="1481450110" :endTime="1481561115" :tipText="'距开始'"  :tipTextEnd="'距结束'" 
						:endText="'拍卖已结束'" :dayTxt="'天'" :hourTxt="'小时'" :minutesTxt="'分'" :secondsTxt="'秒'">
						 </count-down>			
					</view>

				</view> -->
		
				<view class="paimai-box">
					<text>围观:{{item.visitCount}}</text>
					<text>出价:{{item.auctionCount}}</text>
					<text>拍品数量:{{item.goodsCount}}</text>
					
				</view>
				
			</view>
		</view>
		<!-- <uni-load-more :status="loadingType"></uni-load-more> -->
		
		<view class="cate-mask" :class="cateMaskState===0 ? 'none' : cateMaskState===1 ? 'show' : ''" @click="toggleCateMask">
			<view class="cate-content" @click.stop.prevent="stopPrevent" @touchmove.stop.prevent="stopPrevent">
				<scroll-view scroll-y class="cate-list">
					<view v-for="item in cateList" :key="item.id">
						<view class="cate-item b-b two">{{item.name}}</view>
						<view 
							v-for="tItem in item.child" :key="tItem.id" 
							class="cate-item b-b" 
							:class="{active: tItem.id==cateId}"
							@click="changeCate(tItem)">
							{{tItem.name}}
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
	</view>
</template>

<script>
	import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
    import UniCountdown from '@/components/uni-countdown/uni-countdown'
    import {mapMutations} from 'vuex';
	
	export default {
		components: {
			uniLoadMore,UniCountdown
		},
		data() {
			return {
				cateMaskState: 0, //分类面板展开状态
				headerPosition:"fixed",
				headerTop:"0px",
				loadingType: 'more', //加载更多状态
				filterIndex: 0, 
				cateId: 0, //已选三级分类id
				priceOrder: 0, //1 价格从低到高 2价格从高到低
				cateList: [],
				paimaiList: [],
	            countDownText:undefined,
				page:1,
				size:2,
				paimaiListTimes: [[]], //定时器列表
				currenttime: '', //当前时间
				indexJJ:0,
			};
		},
		
		onLoad(options){
           var userInfo = uni.getStorageSync('userInfo');
             // console.log(userInfo)
           if(userInfo==undefined || userInfo==null || userInfo==""){
			   var code=options.code;
			   var crm=false;
			   if(code==null){
				   this.loginInit()
			   } else
			   {
				   this.getUserInfo(code,crm)
			   }  
			} else
			{
				this.loadData(1);
			}
		},
		onShow(){
			// this.filterIndex=0;
			// this.page=1;
			
			// this.tabClick(0)
			
			//this.loadData(1);
			//这里主要是为了启动时能够加载会员信息
			this.getUserGoods();
		},
		
	
 
		onPageScroll(e){
			//兼容iOS端下拉时顶部漂移
			if(e.scrollTop>=0){
				this.headerPosition = "fixed";
			}else{
				this.headerPosition = "absolute";
			}
		},

		//加载更多
		onReachBottom(){
			uni.showLoading({
				title: '正在加载'
			}) 
			this.page++;
			this.loadData();
		},
		methods: {
			...mapMutations(['login']),
		//根据用户的ID，返回用户商品
		getUserGoods(){
			
			var that = this;
			that.$http({ //调用接口
				method: 'POST',
				url: that.global.target + "/wx/auth/checkToken" //this指data
			}).then(function(response) { //接口返回数据
				// console.log(response)
				var result = response.data;
				if (result.errno == 0) {
					that.login(result.data);
					let userInfo = uni.getStorageSync('userInfo');
					that.$http({ //调用接口
						method: 'GET',
						url: that.global.target + "/wx/home/getUserGoods" //this指data
					}).then(function(resUserGoods) { //接口返回数据
							 // console.log( response.data.data)			 
						that.userGoodsHome = resUserGoods.data.data.list;
						console.log(that.userGoodsHome)
					}, function(error) {})
					}else{
						that.errorCount++;
						that.userGoodsHome =[];
						if(that.errorCount<3){
							setTimeout(function(){
							    that.getUserGoods();
							}, 500);
						}	
					}
				},
				function(error) {
					that.errorCount++;
					that.userGoodsHome =[];
					if(that.errorCount<3){
						 setTimeout(function(){
						     that.getUserGoods();
						 }, 500);
						
					}
				})
		
			
		},
			
		isWeiXin() {
			var ua = window.navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				return true; // 是微信端
			} else {
				return false;
			}
		},
		loginInit(){
			var rurl='/pages/index/index'
			var redirectUrl = this.global.website+rurl;
			var url = encodeURIComponent(redirectUrl);
			uni.removeStorage({  
			    key: 'userInfo'  
			})
			var that = this;
			that.$http({ //调用接口
				method: 'POST',
				params:{
					rurl:url,
				},
				url: that.global.target + "/wx/auth/loginInit" //this指data
			}).then(function(response) { //接口返回数据
			    console.log(response.data)
			    window.location.href = response.data;
			}, function(error) {
			
			})
		},
		getUserInfo(code,state){
			var that=this;
			//验证
			that.$http({ //调用接口
				method: 'GET',
				params: {
					code: code,
					crm: false
				},
				url: that.global.target + "/wx/auth/newgetwechataccesstoken" //this指data
			}).then(function(response) { //接口返回数据
				var result = response.data;
				if (result.errno == 0) {
					//登录成功
					that.login(result.data);
					//加载数据首页
					that.loadData(1);
				 }
			})
		
		},
		
		
			  countDownS_cb: function (x) {
					console.log(x)
			  },
			  countDownE_cb: function (x) {
				console.log(x)
			  },

			//加载商品 ，带下拉刷新和上滑加载
			async loadData(type) {//type 标识是否需要重新计算分页
  
				var that=this
 			     
				let filterType="zhuanChangPaimaiIng";
				if(this.filterIndex === 0)
				 
					{
						filterType="zhuanChangPaimaiIng"
						that.countDownText="距离结束："
					}
				else if(this.filterIndex === 1)
					{
						filterType="zhuanChangPaimaiPreview"
						that.countDownText="距离开始："
					}
				else if(this.filterIndex === 2)
					{filterType="zhuanChangPaimaiEnding"}
				var page=that.page;
				var size = that.size;
				that.$http({           //调用接口
				    method:'GET',
					params:{
						page:page,
						size:size
					},
					 url:this.global.target+"/wx/zhuanchang/"+filterType  //this指data
				}).then(function(response){  //接口返回数据
					// console.log( response);
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
				if(this.filterIndex==2){
					return 
				}
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
					if(this.filterIndex==0)
					{
						endTimeSource=that.paimaiList[i].endTime;
					} else if(this.filterIndex==1)
					{
						endTimeSource=that.paimaiList[i].beginTime;
					}
					 
					if(endTimeSource==null){
						return;
					}
					let endTimeReplace = endTimeSource.replace(/-/g,'/');
					
					var endtime = (Date.parse(new Date(endTimeSource))||Date.parse(new Date(endTimeReplace))) / 1000 - myDate - duration;
					
					endtime = endtime > 0 ? endtime : 0;
					
					times.push(endtime);
				  
					that.paimaiListTimes[i] = times;
					that.$forceUpdate();
				}
			 
			},
			//筛选点击
			tabClick(index){
				this.page=1;
				this.filterIndex = index;
				this.paimaiList=[];
				uni.pageScrollTo({
					duration: 300,
					scrollTop: 0
				})
				uni.showLoading({
					title: '正在加载'
				}) 
				this.loadData(1);
				
			},

			//详情
			navToDetailPage(item){
				//专场商品明细ID
				console.log(item)
				let id = item.id;
			 
				uni.navigateTo({
					url: `/pages/index/zhuanchangdetail?id=`+id+`&filterIndex=`+this.filterIndex
				})
			},
			stopPrevent(){}
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
		    padding-left: 20upx;
			padding-top: 60upx;
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
