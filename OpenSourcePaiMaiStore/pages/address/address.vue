<template>
	<view class="content b-t">
		<view class="list b-b" v-for="(item, index) in addressList" :key="index" @click="checkAddress(item)">
			<view class="firstname">
				{{item.name.slice(0,1)}}
			</view>
			<view class="wrapper">
				<view class="u-box">
					<text class="name">{{item.name}}</text>
					<text class="mobile">{{item.tel.substr(0,3)+"****"+item.tel.substr(7)}}</text>
					<text class="flag">
						{{item.flag}}
					</text>
				</view>
				<view class="address-box">
					<text v-if="item.default" class="tag">默认</text>
					<text class="address">{{item.provinceName}} {{item.cityName}} {{item.countyName}} {{item.addressDetail}}
						{{item.area}} </text>
					<text class="address" hidden="true">{{item.id}}</text>
					<text class="address" hidden="true">{{item.province}}</text>
					<text class="address" hidden="true">{{item.city}}</text>
					<text class="address" hidden="true">{{item.county}}</text>
				</view>
			</view>
			<view class="edit_delete">
				<text class="yticon icon-bianji" @click.stop="addAddress('edit', item)"></text>
				<view class="delete" @click.stop="deletes(index,item.id)"></view>
			</view>

		</view>
		<!-- <text style="display:block;padding: 16upx 30upx 10upx;lihe-height: 1.6;color: #fa436a;font-size: 24upx;">
			重要：添加和修改地址回调仅增加了一条数据做演示，实际开发中将回调改为请求后端接口刷新一下列表即可
		</text> -->

		<button class="add-btn" @click="addAddress('add')">+ 新增收货地址</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				source: 0,
				addressList: [
					// {
					// 	name: '王先生',
					// 	tel: '13939425623',
					// 	addressDetail: '北京北京',
					// 	address: '北京',
					// 	area: '通州',
					// 	default: true,
					// 	provinceName: "北京省",
					// 	cityName: "北京市",
					// 	countyName: "北京哈",
					// 	id: "12",
					// 	province: "北京再来一个",
					// 	city: "北京city",
					// 	county: "北京county",
					// 	flag: '公司'
					// },
				]
			}
		},
		onLoad(option) {
			console.log(option.source);
			this.source = option.source;
			
			this.getImg();
		},
		onShow(){
			this.getImg();
		},
		methods: {
			//选择地址
			checkAddress(item) {
				if (this.source == 1) {
					//this.$api.prePage()获取上一页实例，在App.vue定义
					var a = this.$api.prePage();
					this.$api.prePage().addressData = item;
					this.$api.prePage().loadDate();
					uni.navigateBack(a)
					console.log(a);
				}
			},
			addAddress(type, item) {
				uni.navigateTo({
					url: `/pages/address/addressManage?type=${type}&data=${JSON.stringify(item)}`
				})
			},
			//添加或修改成功之后回调
			refreshList(data, type) {
				//添加或修改后事件，这里直接在最前面添加了一条数据，实际应用中直接刷新地址列表即可
				this.addressList.unshift(data);

				console.log(data, type);
			},
			getImg: function() {


				var that = this;



				that.$http({ //调用接口
					method: 'GET',

					url: this.global.target + "wx/address/list?userId=" + uni.getStorageSync('userInfo').id
				}).then(function(response) { //接口返回数据

					that.addressList = response.data.data;
					//alert(response.data.errmsg);
				}, function(error) {

				})


				/*  that.$http({      //调用接口
	 
				method:'GET',
	 
				url:that.global.target+ "wx/address/list?userId="+uni.getStorageSync('userInfo').id,
	 
			  }).then(function(response){ //接口返回数据
				
				that.addressList=response.data;             
				alert(response.data.errmsg);
			  },function(error){
	 
			  }) */

			},
			deletes: function(index, id) { //删除地址
				var that = this;

				uni.showModal({
					content: '确定要删除此地址么？',
					success: (confirmRes) => {
						if (confirmRes.confirm) {
							that.$http({ //调用接口
								method: 'POST',
								data: "",
								url: this.global.target + "/wx/address/delete?userId=" + uni.getStorageSync('userInfo').id + "&id=" + id
							}).then(function(response) { //接口返回数据
								const result = response.data.errno;

								if (result == 0) {

									that.addressList.splice(index, 1)
									that.$api.msg(`地址删除成功`);
								} else {
									that.$api.msg(`地址删除失败` + response.errmsg);
								}


							}, function(error) {})


						}
					}
				});



			}
		}
	}
</script>

<style lang='scss'>
	page {
		background: #f2f2f2;
	}

	.content {
		/*position: relative;
		background: #f2f2f2;
		min-height: 100vh;*/
		padding-bottom: 98upx;
	}

	.list {
		display: flex;
		align-items: center;
		padding: 27upx 0 23upx 22upx;
		background: #fff;
		position: relative;
		border-bottom: 1px solid #f2f2f2;
		&:after{
			position: absolute;
			content:'';
			width: 1px;
			height: 66%;
			background: #B4B4B4;
			left: 82.533%;
			top: 17%;
		}
		.firstname {
			width: 82upx;
			height: 82upx;
			background: linear-gradient(86deg, rgba(190, 190, 190, 0.75), rgba(170, 170, 170, 1));
			border-radius: 50%;
			text-align: center;
			line-height: 82upx;
			flex-shrink: 0;
			font-size: 40upx;
			color: #fff;
			margin: 0 50upx 0 0;
		}
		.edit_delete{
			width: 131upx;
			flex-shrink: 0;
			display: flex;
			justify-content: space-around;
			align-items: center;
			padding: 0 10upx;
			margin: 0 0 0 20upx;
		}
	}

	.wrapper {
		display: flex;
		flex-direction: column;
		flex: 1;
	}

	.address-box {
		.tag {
			font-size: 27upx;
			color:#fff;
			margin-right: 10upx;
			background: #FF0000;
			border-radius: 4upx;
			padding: 5upx 17upx;
		}

		.address {
			font-size: 33upx;
			line-height: 45upx;
			color: $font-color-dark;
		}
	}

	.u-box {
		margin-top: 16upx;
		.name {
			margin-right: 39upx;
			font-size: 38upx;
		}
		.mobile{
			font-size: 32upx;
		}
		.flag{
			float: right;
			font-size: 27upx;
			color: #B4B4B4;
			border:1px solid rgba(180,180,180,1);
			border-radius:6upx;
			padding: 5upx 15upx;
		}
	}

	.icon-bianji {
		display: flex;
		align-items: center;
		height: 80upx;
		font-size: 40upx;
		color: $font-color-light;
		/* padding-left: 30upx; */
	}

	.add-btn {
		position: fixed;
		bottom: 0;
		z-index: 95;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100vw;
		height: 98upx;
		font-size: 32upx;
		color: #fff;
		border-radius: 0;
		background: linear-gradient(239deg, rgba(242, 81, 33, 1), rgba(233, 58, 39, 1));
	}

	.delete {
		width: 44upx;
		height: 44upx;
		background: url('../../static/a_delete.png');
		background-size: 100% 100%;
		margin: 0 0 0 10upx;
	}
</style>
