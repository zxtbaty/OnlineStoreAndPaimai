<template>
	<view class="container">
		<view class="carousel">
			<swiper indicator-dots circular=true duration="400" v-if="zhuanchangRule!=undefined">
				<swiper-item  class="swiper-item" v-for="(item,index) in zhuanchangRule.auctionGallery" :key="index">
					<view class="image-wrapper">
						<image
							:src="item" 
							class="loaded" 
							mode="aspectFill"
						></image>
					</view>
				</swiper-item>
			</swiper>
		</view>
		
		<view class="introduce-section c-list" v-if="zhuanchangRule!=undefined">
			<!-- <text class="title">刘逊国画作品专场</text> -->
	        <text class="title ">{{zhuanchangRule.zhuanchangName}}</text>
			<view class="bot-row c-row b-b">
				<text>拍品数: {{zhuanchangRule.goodsCount}}</text>
				<text>围观数: {{zhuanchangRule.visitCount}}</text>
				<text>出价数: {{zhuanchangRule.auctionCount}}</text>
			</view>
			<view class="bot-time c-row b-b">
				<text>竞拍时间:{{zhuanchangRule.beginTime}} 至 {{zhuanchangRule.endTime}}</text>
			</view>
			<view class="countdown c-row b-b" v-if="zhuanchangRule!=undefined&&filterIndex!=2">
				 <uni-countdown
					 :showText="timeHint"
					 :displayOneRow="true"
					 :duration="paimaiTimes[0]">
				 </uni-countdown>
			</view>
			<view class="bot-row c-row b-b">
				 作者简介: 
			     
			</view>
			<view  class="bot-author c-row b-b" >
			    {{zhuanchangRule.authorDesc}} 
			</view>
			<view class="bot-row c-row b-b">
				 拍品简介:
			     
			</view>
			<view  class="bot-author c-row b-b" >
				 
			    {{zhuanchangRule.auctionDesc}} 
			</view>
<!-- 			<sunui-grand 
			:content="'这是一个测试例子rqwrwrqwerwerqwerqwerqwerwerqwerwrqwerwrewer2323rwerwerwre'"
			:expandText="'这是一个测试例子rqwrwrqwerwerqwerqwerqwerwerqwerwrqwerwrewer2323rwerwerwre'"
			 clamp=4>
				
			</sunui-grand>
			</view>
			<view class="category" >
				这是一个测试例子rqwrwrqwerwerqwerqwerqwerwerqwerwrqwerwrewer2323rwerwerwre
				<uni-collapse >
					<uni-collapse-item :title="'这是一个测试例子'" >
						<view style="padding: 45upx;font-size: 28upx;"> 这是一个测试例子rqwrwrqwerwerqwerqwerqwerwerqwerwrqwerwrewer2323rwerwerwre</view>
					</uni-collapse-item>
				</uni-collapse>
			</view> -->
<!-- 			<view class="bot-desc">
				<sunui-grand :content="'这是一个测试例子rqwrwrqwerwerqwerqwerqwerwerqwerwrqwerwrewer2323rwerwerwre'" clamp=4>
					
				</sunui-grand>
				
			</view> -->
			
		</view>
		

		
 		<view class="goods-list">
 			<view style="position:relative;"
 				v-for="(item, index) in zhuanchangGoodsList" :key="index"
 				class="goods-item"
 				@click="navToDetailPage(item)">
 				<view class="image-wrapper">
 					<image :src="item.auctionPicHead" mode="aspectFill">	
 					</image>
 				</view>
 				<text class="title clamp b-b">{{item.goodsName}}</text>
 				<view class="paimai-box b-b">
 					<text>围观:{{item.visitCount}}</text>
 					<text>出价:{{item.auctionCount}}</text>
 				</view>
 			</view>
 		</view>
 		 

	</view>
</template>

<script>
	import share from '@/components/share';
	import UniCountdown from '@/components/uni-countdown/uni-countdown';
	import {uniCollapse,uniCollapseItem} from '@dcloudio/uni-ui';
	import sunuiGrand from '@/components/sunui-grand/sunui-grand'
	
	export default{
		components: {
			share,UniCountdown,sunuiGrand,uniCollapse,uniCollapseItem
		},
		data() {
			return {
				filterIndex: 0, 
				// zhuanchangRule: {
				// 	authorZuopin:[],
				// 	zhuanchangName:undefined,
				// 	goodsCount:undefined,
				// 	visitCount:undefined,
				// 	auctionCount:undefined,
				// 	beginTime:undefined,
				// 	endTime:undefined,
				// 	authorDesc:undefined,
				// 	auctionDesc:undefined
				// },
				timeHint:"距离结束：",
				zhuanchangRule:undefined,
				zhuanchangGoodsList:[],
				shareList: [],
				paimaiTimes: [], //定时器列表
				currenttime: '', //当前时间
			};
		},
		async onLoad(options){
		
			//接收传值,id里面放的是标题，因为测试数据并没写id 
			let id = options.id;
			// if(id){
			// 	this.$api.msg(`点击了${id}`);
			// }
			this.filterIndex=options.filterIndex;
			if(this.filterIndex==0){
				this.timeHint="距离结束："
			} else if(this.filterIndex==1){
				this.timeHint="距离开始："
			 }
			this.shareList = await this.$api.json('shareList');
			
			this.loadData(id)
			
		},
		onShow(){
			// this.daotime()
		},
	
		methods:{
		 			 countDownS_cb: function (x) {
		 			 					console.log(x)
		 			 },
		 			 countDownE_cb: function (x) {
		 			 				console.log(x)
		 			 },
			//分享
			share(){
				this.$refs.share.toggleMask();	
			},
		//加载商品 ，带下拉刷新和上滑加载
		async loadData(id) {//type 标识是否需要重新计算分页	  
			var that=this;
			that.$http({           //调用接口
				method:'GET',
				params:{
					id:id
				},
				 url:that.global.target+"/wx/zhuanchang/zhuanChangPaimaiDetail"  //this指data
			}).then(function(response){  //接口返回数据
				// console.log( response); 
				that.zhuanchangRule = response.data.data.zhuanchangRule;
				that.zhuanchangGoodsList = response.data.data.zhuanchangGoodsList;
				that.daotime();
				//uni.hideLoading()
			},function(error){
				//uni.hideLoading()
			}) 
		},
		 daotime() {
			var that=this;
			if(that.filterIndex==2){
				return 
			}
			//设置倒计时
			
			if(that.zhuanchangRule==null){
				return;
			}
			var myDate = Math.round(new Date().getTime() / 1000).toString();
			that.currenttime = myDate;
		
			var duration = 0;
 
			let times = new Array();
			let endTimeSource=undefined
			if(that.filterIndex==0)
			{
				endTimeSource=that.zhuanchangRule.endTime;
			} else if(that.filterIndex==1)
			{
				endTimeSource=that.zhuanchangRule.beginTime;
			}
			 
			if(endTimeSource==null){
				return;
			}
			let endTimeReplace = endTimeSource.replace(/-/g,'/');
			
			var endtime = (Date.parse(new Date(endTimeSource))||Date.parse(new Date(endTimeReplace))) / 1000 - myDate - duration;
			
			endtime = endtime > 0 ? endtime : 0;
			
			//that.duration=endtime;
			times.push(endtime);
  
			that.paimaiTimes = times;
			that.$forceUpdate();
  
		 },
		  
			
			stopPrevent(){
				
			},
			//详情
			navToDetailPage(item) {
				//测试数据没有写id，用title代替
				let zhuanchangId = this.zhuanchangRule.id;
				let ruleMxId=item.id
				uni.navigateTo({
					url: `/pages/everyone/paimaidetail?type=zhuanchang&zhuanchangId=`+zhuanchangId+
					`&id=`+ruleMxId
				});
			},
		},

	}
</script>

<style lang='scss'>
	page{
		background: $page-color-base;
		padding-bottom: 160upx;
	}
	.category{
		width:100%;
		margin-top: 100upx;
		display: -webkit-box;
		overflow: hidden;
		text-overflow: ellipsis;
		word-wrap: break-word;
		white-space: normal !important;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
	.icon-you{
		font-size: $font-base + 2upx;
		color: #888;
	}
	.countdown{
		font-size: $font-base ;
		padding-left: 0upx;
		padding-top: 0upx;
	}
	.carousel {
		height: 722upx;
		position:relative;
		swiper{
			height: 100%;
		}
		.image-wrapper{
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
	.introduce-section{
		background: #fff;
		padding: 20upx 30upx;	
		.title{
			display: flex;
			justify-content:center;
			font-size: 32upx;
			color: $font-color-dark;
			height: 50upx;
			line-height: 50upx;
		}
  
		.coupon-tip{
			align-items: center;
			padding: 4upx 10upx;
			background: $uni-color-primary;
			font-size: $font-sm;
			color: #fff;
			border-radius: 6upx;
			line-height: 1;
			transform: translateY(-4upx); 
		}
		.bot-row{
			display:flex;
			align-items:center;
			height: 50upx;
			font-size: $font-sm;
			color: $font-color-light;
			text{
				flex: 1;
			}
		}
		.bot-author{
		 
			font-size: $font-sm;
			color: $font-color-light;
			 
		}
		.bot-time{
			padding-top: 10upx;
			padding-bottom: 10upx;
			display:flex;
			align-items:center;
			 
			font-size: $font-sm;
			color: $uni-color-primary;
			text{
				flex: 1;
			}
		}
		.bot-desc{
			padding-top: 30upx;
			padding-bottom: 30upx;
			display:flex;
			align-items:center;
			height: 50upx;
			font-size: $font-sm;
			color: $font-color-light;
			text{
				flex: 1;
			}
		}
	}
	
	.c-list{
		font-size: $font-sm + 2upx;
		color: $font-color-base;
		background: #fff;
		.c-row{
			display:flex;
			align-items:center;
			padding: 20upx 30upx;
			position:relative;
		}
		.tit{
			width: 140upx;
		}
		.con{
			flex: 1;
			color: $font-color-dark;
			.selected-text{
				margin-right: 10upx;
			}
		}
		.bz-list{
			height: 40upx;
			font-size: $font-sm+2upx;
			color: $font-color-dark;
			text{
				display: inline-block;
				margin-right: 30upx;
			}
		}
		.con-list{
			flex: 1;
			display:flex;
			flex-direction: column;
			color: $font-color-dark;
			line-height: 40upx;
		}
		.red{
			color: $uni-color-primary;
		}
	}
	
 	/* 商品列表 */
 .goods-list{
 	display:flex;
 	flex-wrap:wrap;
 	padding: 0 30upx;
	margin-top: 10px;
 	/* background: #fff; */
	background:$page-color-base;
 	.goods-item{
		background: #fff;
 		position:relative;
 		display:flex;
 		flex-direction: column;
 		width: 48%;
		margin-top: 20upx;
 		padding-bottom: 40upx;
 		&:nth-child(2n+1){
 			margin-right: 4%;
 		}
 		.overlay{
 			position:absolute;
 			width:200px;
 			font-size: $font-sm;
 			color: $uni-color-primary;
 			height:100px;
 			z-indent:2;
 			left:0;
 			top:0;
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
 	.title{
 		font-size: $font-lg;
 		color: $font-color-dark;
 		line-height: 60upx;
 	}
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

}
 
</style>
