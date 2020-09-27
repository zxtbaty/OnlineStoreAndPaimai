<template>
	<view class="container" >
		<view class="eva-section e-header">
			<view>
				<text class="tit">出价历史:</text>
				<text>{{offerList.length}}次</text>
				<text class="tip" style="margin-left: 50px;">最高价(元):</text>
				 
				<text v-if="sumInfo!=null&&sumInfo.length>0&&sumInfo[0]!=null">{{sumInfo[0].maxPrice}}</text>	
				 
			</view>
			<view style="margin-top: 10px;">
				<text class="tit">拍中次数:</text>
				<text v-if="lingXianInfo.length>0">{{lingXianInfo[0].sumCount}}次</text>
				<text class="tip" style="margin-left: 50px;">中高价(元):</text>
				<text v-if="lingXianInfo!=null&&lingXianInfo.length>0&&lingXianInfo[0]!=null">{{lingXianInfo[0].maxPrice}}</text>		
			</view>
		</view>
		 
		<!-- 出价记录 -->
		<view class="eva-section">
			<view class="e-header" style="display: flex;justify-content: space-between;">
				<div>
					<text class="tip">商品</text>
				</div>
				<div>
					<text class="tit">时间</text>
				</div>
				<div>
					<text class="tip">金额</text>
				</div>
				<!-- <div>
					<text class="tip">出价状态</text>
				</div> -->
			</view> 
			<view class="e-header" style="display: flex;justify-content: space-between;margin-top: 15px;" v-for="(offer,index) in offerList" :key="index">
			 
				<!-- <view class="eva-box current" v-for="(offer,index) in offerList" :key="index"> -->
					<div style="width: 120px; margin-right: 10px;">
						<text class="tip" >{{offer.goods_name}}</text>
					</div>
					<div style="width: 150px;margin-right: 10px;">
						<text class="tit" >{{offer.offer_time}}</text>
					</div>
					<div>
						<text class="tip">{{offer.offer_money}}</text>
					</div>
					<!-- <div>
						<text class="tip">{{offer_state}}</text>
					</div> -->

			</view>
		</view>
		
	</view>
</template>

<script>

	import {mapMutations} from 'vuex';
 
	export default{
		components: {
		},
		data() {
			
			return {
				offerList:[],//出价列表
				sumInfo:[],//出价次数和最高
				lingXianInfo:[],//领先次数和最高
			};
		},
		async onLoad(){
			this.loadUserOffer()
		},
		methods:{
			 ...mapMutations(['login']),
			//加载商品 ，带下拉刷新和上滑加载
			async loadUserOffer() {//type 标识是否需要重新计算分页	  
				var that=this;
				let userInfo = uni.getStorageSync('userInfo');
				that.$http({           //调用接口
					method:'GET',
					params:{
						userId:userInfo.id
					},
					 url:that.global.target+"/wx/user/getUserOfferList"  //this指data
				}).then(function(response){  //接口返回数据
				    var result=response.data;
					if(result.errno!=0){
						that.$api.msg(result.errmsg)
						return;
					}
					that.offerList=result.data.offerList;
					that.sumInfo=result.data.sumInfo;
					that.lingXianInfo=result.data.lingXianInfo;
				},function(error){
					//uni.hideLoading()
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
 
 	
</style>
