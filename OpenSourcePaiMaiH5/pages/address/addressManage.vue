<template>
	<view class="content">
		<view class="row b-b">
			<text style="color: red;">*</text>
			<text class="tit">联系人</text>
			<text class="address" hidden="true">{{addressData.id}}</text>
			<input class="input" type="text" v-model="addressData.name" placeholder="收货人姓名" placeholder-class="placeholder" />
		</view>
		<view class="row b-b">
			<text style="color: red;">*</text>
			<text class="tit">手机号</text>
			<input class="input" type="number" v-model="addressData.tel" placeholder="收货人手机号码" placeholder-class="placeholder" />
		</view>

		<view class="row b-b">
			<text style="color: red;">*</text>
			<view class="tit">
				请选择省
			</view>
			<select @change="getArea(addressData.province,2,false)" v-model="addressData.province"  class='address_select'>
				<option value="-1" selected class="address_option">请选择省</option>
				<option v-for="item in addressData.provinces" :value="item.id"  class="address_option">{{item.name}}</option>
			</select>
		</view>
		<view class="row b-b">
			<text style="color: red;">*</text>
			<view class="tit">
				请选择市
			</view>
			<select @change="getArea(addressData.city,3,false)" v-model="addressData.city" class='address_select'>
				<option value="-1" selected  class="address_option">请选择市</option>
				<option v-for="item in addressData.citys" :value="item.id"  class="address_option">{{item.name}}</option>
			</select>
		</view>
		<view class="row b-b">
			<text style="color: red;">*</text>
			<view class="tit">
				请选择区/县
			</view>
			<select v-model="addressData.county"  class='address_select'>
				<option value="-1" selected  class="address_option">请选择区/县</option>
				<option v-for="item in addressData.countrys" :value="item.id"  class="address_option">{{item.name}}</option>
			</select>
		</view>
		<view class="row b-b">
            <text style="color: red;">*</text>
			<text class="tit">地址</text>
			<input  type="text" class="input" v-model="addressData.addressDetail" placeholder="请输入详细地址" placeholder-class="placeholder">
			<!-- <text class="yticon icon-shouhuodizhi" @click="chooseLocation"></text> -->
		</view>

		<view class="row b-b">
			<text style="color: red;">*</text>
			<text class="tit">门牌号</text>
			<input class="input" type="text" v-model="addressData.area" placeholder="楼号、门牌" placeholder-class="placeholder" />
		</view>

		<view class="row default-row">
			<text class="tit">设为默认</text>
			<switch :checked="addressData.default" color="#ff0000" @change="switchChange" />
		</view>
		<button class="add-btn" @click="confirm">提交</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				addressData: {
					id: '',
					name: '',
					tel: '',
					addressDetail: '',
					address: '',
					area: '',
					default: false,
					provinces: [],
					citys: [],
					countrys: [],
					province: "-1",
					city: "-1",
					county: "-1"
				}
			}
		},
		onLoad(option) {
			var that = this;
			let title = '新增收货地址';
			if (option.type === 'edit') {
				title = '编辑收货地址'


				this.addressData.id = JSON.parse(option.data).id;
				this.addressData.name = JSON.parse(option.data).name;
				this.addressData.tel = JSON.parse(option.data).tel;
				this.addressData.addressDetail = JSON.parse(option.data).addressDetail;
				this.addressData.address = JSON.parse(option.data).addressDetail;
				this.addressData.area = JSON.parse(option.data).area;

				this.addressData.default = JSON.parse(option.data).default;


				that.getArea(0, 1, true);
				this.addressData.province = JSON.parse(option.data).province;

				that.getArea(JSON.parse(option.data).province, 2, true);
				this.addressData.city = JSON.parse(option.data).city;

				that.getArea(JSON.parse(option.data).city, 3, true);
				this.addressData.county = JSON.parse(option.data).county;



			} else {

				that.getArea(0, 1, true);
			}
			this.manageType = option.type;
			uni.setNavigationBarTitle({
				title
			})

		},
		methods: {
			switchChange(e) {

				this.addressData.default = e.detail.value;
			},

			getArea(id, type, isOnload) {

				var that = this;
				that.$http({ //调用接口
					method: 'get',
					data: "",
					url: this.global.target + "/wx/address/getRegionList?pid=" + id + "&type=" + type
				}).then(function(response) { //接口返回数据
					const result = response.data;
					if (type == 1) {

						that.addressData.provinces = result.data;


					} else if (type == 2) {
						if (!isOnload) {
							that.addressData.citys = result.data;
							that.addressData.city = "-1";

							that.addressData.countrys = [];
							that.addressData.county = "-1";

						} else {
							that.addressData.citys = result.data;
						}

					} else if (type == 3) {
						if (!isOnload) {
							that.addressData.countrys = result.data;
							that.addressData.county = "-1";
						} else {
							that.addressData.countrys = result.data;
						}
					}


				}, function(error) {})


				//this.$api.prePage()获取上一页实例，可直接调用上页所有数据和方法，在App.vue定义
				/* this.$api.prePage().refreshList(data, this.manageType);
				this.$api.msg(`地址${this.manageType=='edit' ? '修改': '添加'}成功`);
				setTimeout(()=>{
					uni.navigateBack()
				}, 800) */
			},

			//地图选择地址
			chooseLocation() {
				uni.chooseLocation({
					success: (data) => {

						this.addressData.addressDetail = data.name;
						this.addressData.address = data.name;
					}
				})
			},

			//提交
			confirm() {
				let data = this.addressData;
				if (!data.name) {
					this.$api.msg('请填写收货人姓名');
					return;
				}
				if (!/(^1[3|4|5|7|8][0-9]{9}$)/.test(data.tel)) {
					this.$api.msg('请输入正确的手机号码');
					return;
				}
				if (!data.addressDetail) {
					this.$api.msg('请填写详细地址');
					return;
				}
				if (!data.area) {
					this.$api.msg('请填写门牌号信息');
					return;
				}
				if (!data.province) {
					this.$api.msg('请选择省');
					return;
				} else {
					if (data.province == "-1") {
						this.$api.msg('请选择省');
						return;
					}
				}
				if (!data.city) {
					this.$api.msg('请选择市');
					return;
				} else {
					if (data.city == "-1") {
						this.$api.msg('请选择市');
						return;
					}
				}
				if (!data.county) {
					this.$api.msg('请选择区/县');
					return;
				} else {
					if (data.county == "-1") {
						this.$api.msg('请选择区/县');
						return;
					}
				}
				var isde = "false";

				if (data.default) {

					isde = "true";
				}
				var that = this;
				that.$http({ //调用接口
					method: 'POST',
					data: data,
					url: this.global.target + "/wx/address/save?userId=" + uni.getStorageSync('userInfo').id + "&isDefault=" + isde //this指data
					// url: this.global.target + "/wx/address/save?isDefault=" + isde //this指data
				}).then(function(response) { //接口返回数据
					const result = response.data;

					if (result.errno == 0) {

						that.$api.msg(`地址${that.manageType=='edit' ? '修改': '添加'}成功`);
						setTimeout(() => {
							uni.navigateBack()
						}, 800)
					} else {
						that.$api.msg(result.errmsg);
						that.logining = false;
					}

				}, function(error) {})


				//this.$api.prePage()获取上一页实例，可直接调用上页所有数据和方法，在App.vue定义
				/* this.$api.prePage().refreshList(data, this.manageType);
				this.$api.msg(`地址${this.manageType=='edit' ? '修改': '添加'}成功`);
				setTimeout(()=>{
					uni.navigateBack()
				}, 800) */
			},
		}
	}
</script>

<style lang="scss">
	page {
		background: $page-color-base;
		padding-top: 16upx;
	}

	.row {
		display: flex;
		align-items: center;
		position: relative;
		padding: 0 30upx;
		height: 110upx;
		background: #fff;

		.tit {
			flex-shrink: 0;
			width: 170upx;
			font-size: 30upx;
			color: $font-color-dark;
		}

		.input {
			flex: 1;
			font-size: 30upx;
			color: $font-color-dark;
		}

		.icon-shouhuodizhi {
			font-size: 36upx;
			color: $font-color-light;
		}
	}

	.default-row {
		margin-top: 16upx;

		.tit {
			flex: 1;
		}

		switch {
			transform: translateX(16upx) scale(.9);
		}
	}

	.add-btn {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 690upx;
		height: 80upx;
		margin: 60upx auto;
		font-size: $font-lg;
		color: #fff;
		background:linear-gradient(239deg,rgba(242,81,33,1),rgba(233,58,39,1));
		border-radius: 10upx;
		box-shadow: 1px 2px 5px rgba(219, 63, 96, 0.4);
	}
	.address_select{
		width: 100%;
		border: none;
		height: 110upx;
		line-height: 110upx;
		-webkit-appearance: none;
		background-color: #fff !important;
	}
	.address_option{
		background: #fff;
	}
</style>
