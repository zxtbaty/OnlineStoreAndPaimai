<template>
	<view class="coupon">
		<!-- tab -->
		<view class="c-tab">
			<view class="c-tabli" v-for="(item,index) in tablist" :key="index" :class="index==currenttab?'active':''" @click="changeTab(index)">
				{{item}}
			</view>
		</view>
		<!-- tab end-->
		<!-- list -->
		<swiper  :current='currenttab'  @change="changeTab">
			<swiper-item  class="c-list" v-for="(i,tabindex) in tablist" :key="tabindex">
				<view class="c-li"
				 v-for="(item,index) in couponList" 
				 :key="index" 
				 :class="currenttab==0?'bg1':'bg2'"
				 v-show="currenttab==0||currenttab==item.status"
				 >
					<view class="c-li-top">
						<view class="c-li-left">
							<view class="name">
								{{item.ticketDesci}}
							</view>
							<view class="quota">
								{{item.serialId}}
							</view>
						</view>
						<view class="c-li-right">
							{{item.maxAmt}}
						</view>
					</view>
					<view class="c-li-bottom">
						<view class="c-lib-left">
							有效期至:{{item.expiryDate}}
						</view>
						<!-- <view class="touse" v-if="item.status==1">
							去使用
						</view> -->
					</view>
				</view>
			</swiper-item>

		</swiper>
		<view class="c-list">
			<view class="c-li">

			</view>
		</view>
		<!-- list -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				tablist: ['未使用','已使用'],
				couponList:[],
				isbind:false,
				quanlist: [{
						name: '通用红包',
						quota: '满100可用',
						size: 10,
						time: "有效期至2019.05.01",
						status: 1
					},
					{
						name: '通用红包',
						quota: '满100可用',
						size: 10,
						time: "已过期",
						status: 2
					},
					{
						name: '通用红包',
						quota: '满100可用',
						size: 10,
						time: "已使用",
						status: 2
					}
				],
				currenttab:0
			};
		},
		onLoad(option) {
			this.getUserCoupon();
		},
		
		methods:{
			changeTab(e){
				console.log(e);
				var currenttab;
				if(!isNaN(e)){
					currenttab=e;
				}else{
					currenttab=e.detail.current
				}
				this.currenttab=currenttab;
				this.getUserCoupon();
			},
			getUserCoupon(){
				console.log(this.currenttab)
				if(this.currenttab==0){
					let userInfo = uni.getStorageSync('userInfo');
							 
					this.couponList = userInfo.ticketsList;
					console.log(this.couponList)
					if(userInfo.vipCode){
						this.isbind=true;
					} else
					{
						this.$api.msg("未与CRM相连");
						return;
					}
				} else
				{
					//获取所有已经使用的优惠券
					var that=this;
					that.$http({ //调用接口
						method: 'GET',
						url: that.global.target + "/wx/order/getOrderTicketsList" //this指data
					}).then(function(response) { //接口返回数据
						// console.log(response)
						var result = response.data;
						console.log(response)
						if (result.errno == 0) {
							that.couponList = result.data.list;
						} else {
					
						}
					}, function(error) {})
		 
				}
			}
		}
	}
</script>

<style lang="scss">
	page {
		background: #f7f7f7;
	}

	.c-tab {
		height: 76upx;
		background: #fff;
		padding: 20upx 27upx 0;
		display: flex;
		justify-content:space-around;
		font-size: 29upx;

		.active {
			border-bottom: 3upx solid #E50A79;
		}
	}

	.c-list {
		padding: 0 27upx;

		.c-li {
			width: 100%;
		    height: 253upx;
			margin: 29upx 0 0;
		}

		.bg1 {
			background: url('/static/newimg/quan_y.png');
			background-size: 100% 100%;
		}

		.bg2 {
			background: url('/static/newimg/quan_n.png');
			background-size: 100% 100%;
		}

		.c-li-top {
			color: #fff;
			display: flex;
			justify-content: space-between;
			height: 142upx;
			padding: 0 30upx 0 26upx;

			.name {
				font-size: 40upx;
				margin: 20upx 0 0;
			}

			.quota {
				font-size: 28upx;
				line-height: 50upx;
			}

			.c-li-right {
				font-size: 93upx;
				font-weight: bold;
				padding: 10upx 0 0;

				&:before {
					content: '￥';
					font-size: 28upx;
				}
			}
		}

		.c-li-bottom {
			width: 100%;
			font-size: 28upx;
			color: #666666;
			display: flex;
			height: 111upx;
			justify-content: space-between;
			align-items: center;
			padding: 0 30upx 0 26upx;

			.touse {
				color: #EA4242;
				font-size: 22upx;
				padding: 4upx 16upx;
				border: 1px solid #EA4242;
				border-radius: 8upx;
			}
		}

	}
	uni-swiper{
		height: 500vh;
	}
</style>
