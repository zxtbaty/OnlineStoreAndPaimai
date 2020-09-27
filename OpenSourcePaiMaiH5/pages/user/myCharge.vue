<template>
	<view class="container" >
		<view class="eva-section e-header">
			<view style="display: flex;">
				<div style="width: 90px;">
					<text class="tit" >可用余额:</text>
				</div>
				<div style="width: 80px;">
					<text >{{userInfo.chargeRemainMoney}}元</text>
				</div>
				<div style="width: 90px;margin-left: 10px;">
					<text class="tip" >充值金额:</text>	
				</div>
				<div>
					<text>{{userInfo.chargeSumMoney}}元</text>
				</div>    
			</view>
			<view style="display: flex;margin-top: 10px;">
				<div style="width: 90px;">
					<text class="tit">已退金额:</text>
				</div>
				<div style="width: 80px;">
					<text >{{userInfo.chargeReturnMoney}}元</text>
				</div>
				<div style="width: 90px;margin-left: 10px;">
					<text class="tip" >锁定金额:</text>	
				</div>
				<div >
					<text>{{userInfo.chargeLockMoney}}元</text>
				</div>
			</view>
			<!-- <view style="display: flex;margin-top: 10px;">
				<div style="width: 90px;">
					<text class="tit">拍卖抵现:</text>
				</div>
				<div style="width: 80px;">
					<text>{{userInfo.chargeLockToOrderMoney}}元</text>
				 </div>
			</view> -->
			<view style="display: flex;margin-top: 10px;">
			  <button type="primary" class=" action-btn"
			   @click="shenQingTuiKuan(charge.id)">退款</button>
			  </view>
			
		</view>
		 
		<!-- 出价记录 -->
		<view class="eva-section">
			<view class="e-header" style="display: flex;justify-content: space-between;">
<!-- 				<div>
					<text class="tip">申请退款</text>
				</div> -->
				<div>
					<text class="tip">充值时间</text>
				</div>
				<div>
					<text class="tit">金额</text>
				</div>
				<div>
					<text class="tip">渠道</text>
				</div>
				<!-- <div>
					<text class="tip">退还</text>
				</div> -->
				<!-- <div>
					<text class="tip">出价状态</text>
				</div> -->
			</view> 
			<view class="e-header" style="display: flex;justify-content: space-between;margin-top: 15px;"
			 v-for="(charge,index) in chargeList" :key="index">
			 
					<!-- <div style="width: 80px;" v-if="charge.paySuccess==true&&charge.refundTime==null">
						 <button type="primary" class=" action-btn"
						 
						  @click="shenQingTuiKuan(charge.id)">退款</button>
					</div> -->
					<div style="width: 80px;margin-right: 10px;" 
					v-if="charge.paySuccess==false||charge.refundTime!=null" >
						  
					</div>
					<div style="width: 100px;">
						<text class="tip" >{{charge.chargeTime}}</text>
					</div>
					<div style="width: 50px;">
						<text class="tit" >{{charge.chargeMoney}}</text>
					</div>
					<div  style="width: 70px;">
						<text class="tip">{{charge.payMethodName}}</text>
					</div>
<!-- 					<div>
						<text class="tip" v-if="charge.payReturn==null||charge.payReturn==false">否</text>
						<text class="tip" v-if="charge.payReturn==true">是</text>
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
				userInfo:[],//会员储值卡信息
				chargeList:[],//充值记录
				 
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
					 url:that.global.target+"/wx/user/getUserChargeList"  //this指data
				}).then(function(response){  //接口返回数据
				    var result=response.data;
					if(result.errno!=0){
						that.$api.msg(result.errmsg)
						return;
					}
					that.userInfo=result.data.userInfo;
					that.chargeList=result.data.chargeList;
					 
				},function(error){
					//uni.hideLoading()
				}) 
			},
			
			shenQingTuiKuan(id) {//type 标识是否需要重新计算分页	 
			   if(confirm("确定要申请退款吗?")==false){
				   return;
			   }
				var that=this;
			 
				that.$http({           //调用接口
					method:'GET',
					params:{
						chargeId:id
					},
					 url:that.global.target+"/wx/user/refundCharge"  //this指data
				}).then(function(response){  //接口返回数据
				    var result=response.data;
					if(result.errno!=0){
						that.$api.msg(result.errmsg)
						return;
					}else
					{
						that.loadUserOffer();
						that.$api.msg("申请退款成功，请耐心等待后台人员处理")
						return;
					}
					 
					 
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
