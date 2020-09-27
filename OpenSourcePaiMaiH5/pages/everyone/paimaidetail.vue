<template>
	<view class="container" v-if="goods!=undefined">
		<view class="carousel">
			<swiper indicator-dots circular=true duration="400">
				<swiper-item class="swiper-item" v-for="(item,index) in goods.gallery" :key="index">
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
		
		<view class="introduce-section">
			<text class="title">{{goods.name}}</text>
 			<view class="bot-row">
				<text>类型: {{typeName}}</text>
				<text>围观数: {{auctionGoods.visitCount}}</text>
				<text>出价数: {{auctionGoods.auctionCount}}</text>
			</view>
			<view class="bot-time">
				<text>竞拍时间:{{beginTime}} 至 {{endTime}}</text>
			</view>
		    <view class="overlay" >
	
				 <view class="countdown c-row b-b" v-if="auctionGoods!=undefined">
				 	 <uni-countdown
				 		 :showText="timeHint"
				 		 :displayOneRow="true"
				 		 :duration="paimaiTimes[0]">
				 	 </uni-countdown>
				 </view>
			</view>
		</view>
		
		<!--  竞拍规则 -->
		<view class="jingpai-rule" >
			<view class="jingpai-column-rule">
				<text>{{auctionGoods.minPrice}}</text>
				<text>起拍价</text>
			</view>
			<view class="jingpai-column-rule">
				<text>{{auctionGoods.addPrice}}</text>
				<text>加价幅度</text>
			</view>
			<view class="jingpai-column-rule">
				<text>{{auctionGoods.maxPrice}}</text>
				<text>最新报价</text>
			</view>
			
		 
		</view>
 
		<view class="c-list">
			<view class="c-row b-b">
				<text class="tit">出品人</text>
				<view class="con-list">
					<text>{{goods.authorName}}</text>
				
				</view>
			</view>
			<view class="c-row b-b">
				<text class="tit">拍品简介</text>
				<view class="bz-list con">
					<text>{{auctionGoods.auctionDesc}}</text>
				
				</view>
			</view>
			
			<view class="c-row b-b" v-for="(attr,index) in attributeList" :key="index" >
				<text class="tit">{{attr.attribute}}</text>
				<view class="bz-list con">
					<text>{{attr.value}}</text>
					 
				</view>
			</view>
			
		</view>
		
		
		
		<!-- 评价 -->
		<view class="eva-section">
			<view class="e-header">
				<text class="tit">出价历史</text>
				<text>({{offerList.length}})</text>
				<text class="tip">最高价:({{auctionGoods.maxPrice}})</text>
			</view> 
			<view class="c-list">
				<view class="eva-box current" v-for="(offer,index) in offerList" :key="index">
					<view class="left">
						<image class="portrait" 
						src="http://img3.imgtn.bdimg.com/it/u=1150341365,1327279810&fm=26&gp=0.jpg" 
						mode="aspectFill"></image>
						<view class="title">
							<!-- <text  >{{offer.userName}}</text> -->
							<!-- {{offer.userName}} -->
							<text style="color: red;" v-if="offer.offerState=='领先'">{{offer.userName.substring(0,1)}}**</text>
							<text  style="color:#C0C4CC;"  v-if="offer.offerState=='出局'">{{offer.userName.substring(0,1)}}**</text>
							<!-- <text  >{{offer.offerTime}}</text> -->
							<text style="color: red;" v-if="offer.offerState=='领先'">{{offer.offerTime}}</text>
							<text  style="color:#C0C4CC;"  v-if="offer.offerState=='出局'">{{offer.offerTime}}</text>
						</view>
					</view>
					<view class="right title">
						<!-- <text>{{offer.offerMoney}}</text> -->
						<text style="color: red;" v-if="offer.offerState=='领先'">{{offer.offerMoney}}</text>
						<text  style="color:#C0C4CC;"  v-if="offer.offerState=='出局'">{{offer.offerMoney}}</text>
						
						<text class="name" style="color: red;" v-if="offer.offerState=='领先'">领先</text>
						<text class="name" style="color:#C0C4CC;"  v-if="offer.offerState=='出局'">出局</text>
					</view>
				</view>
				 
				 
			</view>
		</view>
		
	    <view class="auction-rule">
			 <uni-steps :options="[
				{title: '交保证金'}, 
				{title: '竞拍'}, 
				{title: '竞拍成功'}, 
				{title: '支付'},
				{title: '交付'}]" 
				:active="0">
			</uni-steps>
		</view>
		
		<view class="detail-desc">
			<view class="d-header">
				<text>拍品详情</text>
			</view>
			<rich-text :nodes="goods.detail"></rich-text>
		</view>
		
		<!-- 底部操作菜单 -->
		<view class="page-bottom" v-if="previeFlag==0">
<!-- 		    <view class="p-b-btn" :class="{active: favorite}" @click="toFavorite">
				<text class="yticon icon-shoucang"></text>
				<text>收藏</text>
			</view> -->
		    <view class="p-b-btn" >
				<text class="yticon icon-share"></text>
				<text>分享</text>
			</view>
			 
            <input class="input" type="text" placeholder="最新788,每次加价100" :value="currentPrice"  />
			
			<view class="action-btn-group" >
			   
				<button type="primary" class=" action-btn" @click="buyAdd">加价</button>
				<button type="primary"  class=" action-btn" @click="buySub">减价</button>
				<button type="primary" class=" action-btn no-border buy-now-btn" @click="buy">立即出价</button>
				
			</view>

		</view>
 
 
	</view>
</template>

<script>
	import share from '@/components/share';
	import uniSteps from '@/components/uni-steps';

	import UniCountdown from '@/components/uni-countdown/uni-countdown';
	import {mapMutations} from 'vuex';
 
	export default{
		components: {
			share,uniSteps,UniCountdown
		},
		data() {
			
			return {
				submiting:false,//提交状态
				typeCode:'dajia',
				typeName:'专场',
				timeHint:"距离结束：",
				beginTime:undefined,//开始时间
				endTime:undefined,//截至时间
				expireFlag:undefined,//过期标识
				goods:undefined,//商品信息
				attributeList:[],//商品属性
				goodsProduct:undefined,//货品
				auctionGoods:undefined,//设置的拍品信息
				offerList:[],//出价列表
				paimaiTimes: [], //定时器列表
				currenttime: '', //当前时间
				currentPrice:undefined,//拍品的最新价格
	            previeFlag:0,//是否是预展商品
				isRegist:false,//是否会员已经注册
				ruleMxId:undefined,
			};
		},
		async onLoad(options){
			
			//接收传值,id里面放的是标题，因为测试数据并没写id 
			this.ruleMxId= options.id;
			this.typeCode=options.type;
			if(this.typeCode=="zhuanchang"){
				this.typeName="专场"
				let zhuangchangId=options.zhuangchangId
				this.loadZhuanchang(this.ruleMxId)
			} else if(this.typeCode=="dajia"){
				this.typeName="大家拍"
				this.loadDajiapai(this.ruleMxId)
			} 
			// if(id){
			// 	this.$api.msg(`点击了${id}`);
			// }
			
			
		},
		methods:{
			 ...mapMutations(['login']),
			//加载商品 ，带下拉刷新和上滑加载
			async loadZhuanchang(id) {//type 标识是否需要重新计算分页	  
				var that=this;
				let userInfo = uni.getStorageSync('userInfo');
				that.$http({           //调用接口
					method:'GET',
					params:{
						id:id,
						userId:userInfo.id
					},
					 url:that.global.target+"/wx/zhuanchang/zhuanChangPaimaiGoodsDetail"  //this指data
				}).then(function(response){  //接口返回数据
					// console.log( response); 
					that.auctionGoods=response.data.data.zhuanchangGoods;
					if(that.auctionGoods.expireFlag==1){
						that.$api.msg("该商品已经过期，刷新后不再显示")
						return;
					}
					that.zhuanchangRule = response.data.data.zhuanchangRule;
					that.beginTime=that.zhuanchangRule.beginTime;
					that.endTime=that.zhuanchangRule.endTime;
					that.expireFlag=that.zhuanchangRule.expireFlag;
					that.goods=response.data.data.goods;
					var myDate = Math.round(new Date().getTime() / 1000);
					var beginTimeValue= (Date.parse(new Date(that.beginTime))||Date.parse(new Date(that.beginTime))) / 1000;
					if(that.zhuanchangRule.previewFlag==1&&
					that.zhuanchangRule.expireFlag==0&&
					beginTimeValue>myDate ){
						that.previeFlag=1
					}
					if(that.zhuanchangRule.expireFlag==1){
						that.previeFlag=1
					}
					 
					
					that.goodsProduct=response.data.data.goodsProduct;
					that.attributeList=response.data.data.attributeList;
					
					that.currentPrice=that.auctionGoods.maxPrice;
					that.offerList=response.data.data.zhuanchangOfferList;
				    
					if(that.currentPrice==0){
						that.currentPrice=that.auctionGoods.minPrice;
					} else{
						//必须新增一个加价单位
						if(that.offerList!=null&&that.offerList.length>0){
							that.currentPrice=that.currentPrice+that.auctionGoods.addPrice;
						}
						 
					}
					that.offerList=response.data.data.zhuanchangOfferList;
					if(that.offerList==null){
						that.offerList=[]
					}
					
					that.daotime();
					//uni.hideLoading()
				},function(error){
					//uni.hideLoading()
				}) 
			},
		async loadDajiapai(id) {//type 标识是否需要重新计算分页
			var that=this;
			let userInfo = uni.getStorageSync('userInfo');
			that.$http({           //调用接口
				method:'GET',
				params:{
					id:id,
					userId:userInfo.id
				},
				 url:that.global.target+"/wx/dajiaPai/dajiaPaiDetail"  //this指data
			}).then(function(response){  //接口返回数据
				// console.log( response); 
				that.goods=response.data.data.goods;
				that.goodsProduct=response.data.data.goodsProduct;
				that.attributeList=response.data.data.attributeList;
				that.auctionGoods=response.data.data.dajiaRuleCurrent;
				if(that.auctionGoods.expireFlag==1){
					that.$api.msg("该活动已经过期，刷新后不再显示")
					return;
				}
				that.currentPrice=that.auctionGoods.maxPrice;
				if(that.currentPrice==0){
					that.currentPrice=that.auctionGoods.minPrice;
				} else
				{
					that.currentPrice=that.currentPrice+that.auctionGoods.addPrice
				}
				that.beginTime=that.auctionGoods.beginTime;
				that.endTime=that.auctionGoods.endTime;
				that.expireFlag=that.auctionGoods.expireFlag;
				that.offerList=response.data.data.dajiaOfferCurrentList;
				
				that.daotime();
				//uni.hideLoading()
			},function(error){
				//uni.hideLoading()
			}) 
		},	
		daotime() {
			var that=this;
 
			//设置倒计时
			if(that.beginTime==undefined&&that.endTime==undefined){
				return;
			}
			if(that.expireFlag){
				return
			}
			var myDate = Math.round(new Date().getTime() / 1000).toString();
			var beginTimeValue= (Date.parse(new Date(that.beginTime))||Date.parse(new Date(that.beginTime))) / 1000;
			var endTimeValue= (Date.parse(new Date(that.endTime))||Date.parse(new Date(that.endTime))) / 1000;			
			
			that.currenttime = myDate;
		
			var duration = 0;
 
			let times = new Array();
			let endTimeSource=undefined
			if(myDate<beginTimeValue) //说明处于预展阶段，项目还没有开始
			{
				endTimeSource=that.beginTime;
				that.timeHint="距离开始："
			} else if(myDate>=beginTimeValue&&myDate<=endTimeValue)
			{
				endTimeSource=that.endTime;
				that.timeHint="距离结束："
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
		
			buy(){
				if(this.submiting){
					this.$api.msg("已经提交,正在处理,不能重复提交!");
					return;
				}
				//判断是不是会员 如果不是会员，则引导用户注册，并且充值保证金
				//判断是否为会员，如果不是会员，则需要注册成为会员
				uni.showLoading()
				var that = this;
				that.submiting=true;
				that.$http({ //调用接口
					method: 'POST',
					url: that.global.target + "/wx/auth/checkToken" //this指data
				}).then(function(response) { //接口返回数据
					 console.log(response)
					var result = response.data;
					if (result.errno != 0){
						that.submiting=false;
						uni.hideLoading()
						that.$api.msg(result.errmsg);
						return;
					}
					that.login(result.data);
					let userInfo = uni.getStorageSync('userInfo');
					//如果没有获取到用户手机号，则要求用户进行注册
					if(userInfo.mobile==null||userInfo.mobile==''){
						that.submiting=false;
						uni.hideLoading()
						that.navToLogin();
						return;
					}
					that.isRegist=true;
					//先判断用户是否已经提交了拍卖保证金，并且没有退还，如果已经提交，则继续拍卖
					//如果没有提交，再判断客户的余额是否足够，不够则提醒用户去充值
					//这里直接后台提交出价记录
					that.$http({ //调用接口
						method: 'GET',
						params:{
							userId:userInfo.id,
							type:that.typeCode,
							ruleMxId:that.ruleMxId
						},
						url: that.global.target + "/wx/order/ifUserChargeMoneyLock" //this指data
					}).then(function(chargeMoneyLock) { 
						var chargeMoneyLockResult=chargeMoneyLock.data;
						if(result.errno!=0){
							that.$api.msg(result.errmsg);
						} else{
							//是否已经锁定
							let ifUserChargeMoneyLock=false;
							if(chargeMoneyLockResult.data!=null){
								ifUserChargeMoneyLock=true
							}
							if(ifUserChargeMoneyLock==false&&userInfo.chargeRemainMoney<that.auctionGoods.deposit){
								if(confirm("您的预约保证金帐户金额为【"+userInfo.chargeRemainMoney+"】"+
								",本拍品需要预约保证金【"+that.auctionGoods.deposit+"】,您要去充值吗？")==false)
								{
									that.submiting=false;
									uni.hideLoading()
									return;
								} else
								{
									//判断会员充值余额是否大等于保证金额，如果不大于则引导客户去充值
									//充值完毕后，再继续提交订单
									var chargeMoney=that.auctionGoods.deposit-userInfo.chargeRemainMoney;
									uni.redirectTo({
										// url: '/pages/money/pay?orderId=' + result.data.orderId + '&pay=' + result.data.pay + '&ordersn=' + result.data
										// 	.orderSn
										url: `/pages/money/pay?orderId=&orderSn=&pay=`+chargeMoney
									})
									// uni.navigateTo({
									// 	url: `/pages/money/pay?orderId=&orderSn=&pay=`+chargeMoney
									// })
								}	
							} else if(ifUserChargeMoneyLock==true||(ifUserChargeMoneyLock==false&&userInfo.chargeRemainMoney>=that.auctionGoods.deposit))
							{
								//转到提交出价记录界面
							　　if(that.typeCode=='dajia')
							   {
								   //这里直接后台提交出价记录
								   that.$http({ //调用接口
								   	method: 'GET',
								   	params:{
								   		ruleMxId:that.ruleMxId,
								   		offerMoney:that.currentPrice,
										ifUserChargeMoneyLock:ifUserChargeMoneyLock
								   	},
								   	url: that.global.target + "/wx/dajiaPai/submitDajiaPaiOffer" //this指data
								   }).then(function(res) { 
								   	var result=res.data;
									that.submiting=false;
									uni.hideLoading()
								   	if(result.errno!=0){
										if(result.errno==900){
											that.loadDajiapai(that.ruleMxId)
										}
								   		that.$api.msg(result.errmsg);
								   	} else{
										that.$api.msg("出价成功,已经锁住保证金【"+that.auctionGoods.deposit+"】元");
								   		//重新加载出价记录
								   		// that.offerList=result.data.list;
										that.loadDajiapai(that.ruleMxId)
								   	}
								   })
							   } else 　if(that.typeCode=='zhuanchang')
							   {
								   //这里直接后台提交出价记录
								   that.$http({ //调用接口
								   	method: 'GET',
								   	params:{
								   		ruleMxId:that.ruleMxId,
								   		offerMoney:that.currentPrice,
										ifUserChargeMoneyLock:ifUserChargeMoneyLock
								   	},
								   	url: that.global.target + "/wx/zhuanchang/submitZhuanchangOffer" //this指data
								   }).then(function(res) { 
								   	var result=res.data;
								    that.submiting=false;
								    uni.hideLoading()
								   	if(result.errno!=0){
										if(result.errno==900){
											that.loadZhuanchang(that.ruleMxId)
										}
								   		that.$api.msg(result.errmsg);
								   	} else{
								   		that.$api.msg("出价成功,已经锁住保证金【"+that.auctionGoods.deposit+"】元");
								   		//重新加载出价记录
								   		// that.offerList=result.data.list;
								   	    that.loadZhuanchang(that.ruleMxId)
								   	}
								   })
							   }
							
							}
						}
					})
					
				}) 
			},
			
 
			
			buyAdd(){
				 this.currentPrice=this.currentPrice+this.auctionGoods.addPrice;
				 
			},
		    buySub(){
				 this.currentPrice=this.currentPrice-this.auctionGoods.addPrice;
				 if(this.currentPrice<this.auctionGoods.minPrice){
					 this.currentPrice=this.auctionGoods.minPrice
				 }
				 if(this.offerList!=null&&this.offerList.length>0){
					var temp=this.auctionGoods.maxPrice+this.auctionGoods.addPrice
				 	if(this.currentPrice<temp){
						this.currentPrice=temp;
					}
				 }
				 
			},
			stopPrevent(){},
			
			navToLogin() {
				uni.navigateTo({
					url: '/pages/public/login'
				})
			},
		},

	}
</script>

<style lang='scss'>
	page{
		background: $page-color-base;
		padding-bottom: 160upx;
	}
	.icon-you{
		font-size: $font-base + 2upx;
		color: #888;
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
		
		.title {
			display: flex;
			justify-content: center;
			font-size: $font-lg;
			color: $font-color-dark;
			height: 50upx;
			line-height: 50upx;
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
		.bot-time{
			padding-top: 10upx;
			padding-bottom: 10upx;
			display:flex;
			align-items:center;
			 
			font-size: $font-sm;
			color: $font-color-light;
			text{
				flex: 1;
			}
		}
		.overlay{
			 
			width:200px;
			font-size: $font-sm;
			color: $uni-color-primary;
		 
			z-indent:2;
			left:0;
			top:0;
		}
	}
    /* 竞拍规则 */
	.jingpai-rule{
		margin-top: 16upx;
		display:flex;
		align-items:center;
		color: $font-color-base;
		background: linear-gradient(left, #fdf5f6, #fbebf6);
		padding: 12upx 30upx;
		height: 150upx;
        justify-content: space-between;
		.jingpai-column-rule{
			display: flex;
			flex-direction: column;
			align-items:center;
			font-size: $font-base;
			
		} 
	}
	
	
	/* 分享 */
	.share-section{
		display:flex;
		align-items:center;
		color: $font-color-base;
		background: linear-gradient(left, #fdf5f6, #fbebf6);
		padding: 12upx 30upx;
		.share-icon{
			display:flex;
			align-items:center;
			width: 70upx;
			height: 30upx;
			line-height: 1;
			border: 1px solid $uni-color-primary;
			border-radius: 4upx;
			position:relative;
			overflow: hidden;
			font-size: 22upx;
			color: $uni-color-primary;
			&:after{
				content: '';
				width: 50upx;
				height: 50upx;
				border-radius: 50%;
				left: -20upx;
				top: -12upx;
				position:absolute;
				background: $uni-color-primary;
			}
		}
		.icon-xingxing{
			position:relative;
			z-index: 1;
			font-size: 24upx;
			margin-left: 2upx;
			margin-right: 10upx;
			color: #fff;
			line-height: 1;
		}
		.tit{
			font-size: $font-base;
			margin-left:10upx;
		}
		.icon-bangzhu1{
			padding: 10upx;
			font-size: 30upx;
			line-height: 1;
		}
		.share-btn{
			flex: 1;
			text-align:right;
			font-size: $font-sm;
			color: $uni-color-primary;
		}
		.icon-you{
			font-size: $font-sm;
			margin-left: 4upx;
			color: $uni-color-primary;
		}
	}
	
	.c-list{
		margin-top: 16upx;
		font-size: $font-sm + 2upx;
		color: $font-color-base;
		background: #fff;
		.c-row{
			display:flex;
			align-items:center;
			padding: 15upx 30upx;
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
	
	/* 出价记录 */
	.eva-section{
		display: flex;
		flex-direction: column;
		padding: 20upx 30upx;
		background: #fff;
		margin-top: 16upx;
		.e-header{
			display: flex;
			align-items: center;
			height: 70upx;
			font-size: $font-sm + 2upx;
			color: $font-color-light;
			.tit{
				font-size: $font-base + 2upx;
				color: $font-color-dark;
				margin-right: 4upx;
			}
			.tip{
				flex: 1;
				text-align: right;
			}
			.icon-you{
				margin-left: 10upx;
			}
		}
	}
	.eva-box{
		border-bottom: 3upx;
		display: flex;
		padding: 20upx 0;
		justify-content:space-between;
		.left{
			display: flex;
			.portrait{
				flex-shrink: 0;
				width: 80upx;
				height: 80upx;
				border-radius: 100px;
			}
		}
		.title{
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
		}
		.right{
			font-size: $font-base;
			padding-left: 26upx;
			 
		}
	}
    .current{
		
		color: $uni-color-primary;
	}
	
	 /*  拍卖规则描述 */
	.auction-rule{
		font-size: $font-sm;
		background: #fff;
		margin-top: 16upx;
	}
	
	
	/*  详情 */
	.detail-desc{
		background: #fff;
		margin-top: 16upx;
		text-align: center;
		.d-header{
			display: flex;
			justify-content: center;
			align-items: center;
			height: 80upx;
			font-size: $font-base + 2upx;
			color: $font-color-dark;
			position: relative;
				
			text{
				padding: 0 20upx;
				background: #fff;
				position: relative;
				z-index: 1;
			}
			&:after{
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
	.attr-content{
		padding: 10upx 30upx;
		.a-t{
			display: flex;
			image{
				width: 170upx;
				height: 170upx;
				flex-shrink: 0;
				margin-top: -40upx;
				border-radius: 8upx;;
			}
			.right{
				display: flex;
				flex-direction: column;
				padding-left: 24upx;
				font-size: $font-sm + 2upx;
				color: $font-color-base;
				line-height: 42upx;
				.price{
					font-size: $font-lg;
					color: $uni-color-primary;
					margin-bottom: 10upx;
				}
				.selected-text{
					margin-right: 10upx;
				}
			}
		}
		.attr-list{
			display: flex;
			flex-direction: column;
			font-size: $font-base + 2upx;
			color: $font-color-base;
			padding-top: 30upx;
			padding-left: 10upx;
		}
		.item-list{
			padding: 20upx 0 0;
			display: flex;
			flex-wrap: wrap;
			text{
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
			.selected{
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
			.mask{
				animation: showPopup 0.2s linear both;
			}
			.layer {
				animation: showLayer 0.2s linear both;
			}
		}
		&.hide {
			.mask{
				animation: hidePopup 0.2s linear both;
			}
			.layer {
				animation: hideLayer 0.2s linear both;
			}
		}
		&.none {
			display: none;
		}
		.mask{
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
			.btn{
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
	.page-bottom{
		position:fixed;
		left: 30upx;
		bottom:30upx;
		padding-right:10upx;
		z-index: 95;
		display: flex;
		justify-content: space-between;
		align-items: center;
		width: 690upx;
		height: 100upx;
		background: rgba(255,255,255,.9);
		box-shadow: 0 0 20upx 0 rgba(0,0,0,.5);
		border-radius: 16upx;
		.input{
			text-align: center;
			border: 2upx;
			width:150upx;
			color: $uni-color-primary;
			font-size: font-lg;
		}
		.p-b-btn{
			display:flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			font-size: $font-sm;
			color: $font-color-base;
			width: 96upx;
			height: 80upx;
			.yticon{
				font-size: 40upx;
				line-height: 48upx;
				color: $font-color-light;
			}
			&.active, &.active .yticon{
				color: $uni-color-primary;
			}
			.icon-fenxiang2{
				font-size: 42upx;
				transform: translateY(-2upx);
			}
			.icon-shoucang{
				font-size: 46upx;
			}
		}
		.action-btn-group{
			display: flex;
			height: 76upx;
			
			overflow: hidden;
			box-shadow: 0 20upx 40upx -16upx #fa436a;
			box-shadow: 1px 2px 5px rgba(219, 63, 96, 0.4);
			background: linear-gradient(to right, #ffac30,#fa436a,#F56C6C);
			margin-left: 20upx;
			position:relative;
			&:after{
				content: '';
				position:absolute;
				top: 50%;
				right: 50%;
				transform: translateY(-50%);
				height: 28upx;
				width: 0;
				border-right: 1px solid rgba(255,255,255,.5);
			}
			.action-btn{
				display:flex;
				align-items: center;
				justify-content: center;
				width: 150upx;
				height: 100%;
				font-size: $font-base ;
				padding: 0;
				border-radius: 0;
				background: transparent;
			}
		}
	}
	
</style>
