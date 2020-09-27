<template>
	<view class="container">
		<!-- 空白页 -->
		<view v-if="!hasLogin || (cartList.length==0 &&daxingList.length==0 &&shouduList.length==0) " class="empty">
			<image src="/static/emptyCart.jpg" mode="aspectFit"></image>
			<view v-if="hasLogin" class="empty-tips">
				空空如也
				<navigator class="navigator" v-if="hasLogin" url="../index/index" open-type="switchTab">随便逛逛></navigator>
			</view>
			<view v-else class="empty-tips">
				空空如也
				<view class="navigator" @click="navToLogin">去登录></view>
			</view>
		</view>
		<view v-else>
			<!-- 列表 -->
			<view class="cart-list">
				<view class="cart-all">
					全部({{cartList.length+shouduList.length+daxingList.length}})
				</view>
				<block >
					<view class="cart-li" >
						<view v-for="(item, index) in cartList" :key="index">
						 <view class="cart-title">
							<image :src="titleimg[index]?'/static/newimg/redquan.png':'/static/newimg/grayquan.png'" mode="" @click="checkshop(index)" v-if="item.stock>0"></image>
							<image :src="titleimg[index]?'/static/newimg/redquan.png':'/static/newimg/grayquan.png'" mode="" @click="nostock()" v-else></image>
							<text>在线商城</text>
						</view> 
						<view class="cart-item" >
							<view class="image-wrapper" @click="navToDetailPage(item)">
								<image :src="item.picUrl" :class="[item.loaded]" style="opacity: 1;" mode="aspectFill" lazy-load @load="onImageLoad('cartList', index)"
								 @error="onImageError('cartList', index)"></image>
								<!-- <view class="yticon icon-xuanzhong2 checkbox" :class="{checked: item.checked}" @click="check('cartList','item', index)"></view> -->
							</view>
							<view class="item-right">
								<text class="clamp title"><!-- <text class="red">美食</text> -->{{item.goodsName}}</text>
								<text class="attr"><text v-for="(it, index) in item.specifications">{{it}}</text></text>
								<view class="price-dis">
									<text class="price">¥{{item.price}}</text>
									<uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
									 :isMin="item.number===1" :index="index" @eventChange="numberChange($event,'cartList')" v-if="item.stock>0" ></uni-number-box>
									 <uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
									  :isMin="item.number===1" :index="index" v-else></uni-number-box> 
								</view>
							</view>
							<text class="del-btn yticon icon-fork" @click="deleteCartItem(index,'cartList')"></text>
						</view>
						</view>
						<!-- 底部菜单栏 -->
						<view class="action-section" v-if="cartList.length>0">
							<view class="checkbox">
								<!-- <image :src="allChecked?'/static/selected.png':'/static/select.png'" mode="aspectFit" @click="check('all')"></image>
									<view class="clear-btn" :class="{show: allChecked}" @click="clearCart">
										清空
									</view> -->
								<text class="coupon">
									在线商城
								</text>
							</view>
							<view class="total-box">
								<text class="price">¥{{total}}</text>
								<!-- <text class="coupon">已优惠<text>74.35</text>元</text> -->
							</view>
							<button type="primary" class="no-border confirm-btn" @click="createOrder('cartList')">去结算</button>
						</view>
						<!-- 底部菜单栏 end-->
					</view>
					
				</block>
				
			</view>
			<!-- 首都 -->
			<view class="cart-list">
				<block v-for="(item, index) in shouduList" :key="index">
					<view class="cart-item" :class="{'b-b': index!==cartList.length-1}">
						<view class="image-wrapper">
							<image :src="item.picUrl" :class="[item.loaded]" style="opacity: 1;" mode="aspectFill" lazy-load @load="onImageLoad('shouduList', index)"
							 @error="onImageError('cartList', index)" @click="navToDetailPage(item)"></image>
							<view class="yticon icon-xuanzhong2 checkbox" :class="{checked: item.checked}" @click="check('shouduList','item', index)" v-if="item.stock>0"></view>
							<view class="yticon icon-xuanzhong2 checkbox" :class="{checked: item.checked}" @click="nostock()" v-else></view>
						</view>
						<view class="item-right">
							<text class="clamp title">{{item.goodsName}}</text>
							<text class="attr"><text v-for="(it, index) in item.specifications">{{it}}</text></text>
							<view class="price-dis">
								<text class="price">¥{{item.price}}</text>
								<!-- <uni-number-box class="step" :min="1" :max="item.stock" :value="item.number>item.stock?item.stock:item.number"
								 :isMax="item.number>=item.stock?true:false" :isMin="item.number===1" :index="index" @eventChange="numberChange"></uni-number-box> -->
								<uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
								 :isMin="item.number===1" :index="index" @eventChange="numberChange($event,'shouduList')" v-if="item.stock>0"></uni-number-box>
								 <uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
								  :isMin="item.number===1" :index="index" v-else></uni-number-box>
							 </view>
						</view>
						<text class="del-btn yticon icon-fork" @click="deleteCartItem(index,'shouduList')"></text>
					</view>
				</block>
				<!-- 底部菜单栏 -->
				<view class="action-section" v-if="shouduList.length>0">
					<view class="checkbox">
						<!-- <image :src="allChecked?'/static/selected.png':'/static/select.png'" mode="aspectFit" @click="check('all')"></image>
							<view class="clear-btn" :class="{show: allChecked}" @click="clearCart">
								清空
							</view> -->
						<text class="coupon">
							首都机场
						</text>
					</view>
					<view class="total-box">
						<text class="price">¥{{shoudutotal}}</text>
						<!-- <text class="coupon">
								已优惠
								<text>74.35</text>
								元
							</text> -->
					</view>
					<button type="primary" class="no-border confirm-btn" @click="createOrder('shouduList')">去预约</button>
				</view>
				<!-- 底部菜单栏 end-->
			</view>
			<!-- 大兴 -->


			<view class="cart-list">
				<block v-for="(item, index) in daxingList" :key="index">
					<view class="cart-item" :class="{'b-b': index!==cartList.length-1}">
						<view class="image-wrapper">
							<image :src="item.picUrl" :class="[item.loaded]" style="opacity: 1;" mode="aspectFill" lazy-load @load="onImageLoad('daxingList', index)"
							 @error="onImageError('cartList', index)" @click="navToDetailPage(item)"></image>
							<view class="yticon icon-xuanzhong2 checkbox" :class="{checked: item.checked}" @click="check('daxingList','item', index)" v-if="item.stock>0"></view>
							<view class="yticon icon-xuanzhong2 checkbox" :class="{checked: item.checked}" @click="nostock()" v-else></view>
						</view>
						<view class="item-right">
							<text class="clamp title">{{item.goodsName}}</text>
							<text class="attr"><text v-for="(it, index) in item.specifications">{{it}}</text></text>
							<view class="price-dis">
								<text class="price">¥{{item.price}}</text>
								<!-- <uni-number-box class="step" :min="1" :max="item.stock" :value="item.number>item.stock?item.stock:item.number"
								 :isMax="item.number>=item.stock?true:false" :isMin="item.number===1" :index="index" @eventChange="numberChange"></uni-number-box> -->
								<uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
								 :isMin="item.number===1" :index="index" @eventChange="numberChange($event,'daxingList')" v-if="item.stock>0"></uni-number-box>
								<uni-number-box class="step" :min="1" :max="item.stock" :value="item.number" :isMax="item.number>=item.stock?true:false"
								 :isMin="item.number===1" :index="index" v-else></uni-number-box>
							</view>
						</view>
						<text class="del-btn yticon icon-fork" @click="deleteCartItem(index,'daxingList')"></text>
					</view>
				</block>
				<!-- 底部菜单栏 -->
				<view class="action-section" v-if="daxingList.length>0">
					<view class="checkbox">
						<!-- <image :src="daxingallChecked?'/static/selected.png':'/static/select.png'" mode="aspectFit" @click="check('all')"></image>
							<view class="clear-btn" :class="{show: allChecked}" @click="clearCart">
								清空
							</view> -->
						<text class="coupon">
							大兴机场
						</text>
					</view>
					<view class="total-box">
						<text class="price">¥{{daxingtotal}}</text>
						<!-- <text class="coupon">
								已优惠
								<text>74.35</text>
								元
							</text> -->
					</view>
					<button type="primary" class="no-border confirm-btn" @click="createOrder('daxingList')">去预约</button>
				</view>
				<!-- 底部菜单栏 end-->
			</view>

		</view>
	</view>
</template>

<script>
	import {
		mapState
	} from 'vuex';
	import uniNumberBox from '@/components/uni-number-box.vue'
	export default {
		components: {
			uniNumberBox
		},
		data() {
			return {
				total: 0, //在线商城总价格
				shoudutotal: 0, //在线商城总价格
				daxingtotal: 0, //在线商城总价格
				allChecked: true, //全选状态  true|false
				shouduallChecked: true, //全选状态  true|false
				daxingallChecked: true, //全选状态  true|false
				empty: true, //空白页现实  true|false
				cartList:{},
				daxingList: {},
				shouduList: {},
				titleimg:[],
			};
		},
		// onLoad() {
		// 	this.loadData();
		// },
		watch: {
			//显示空白页
			cartList(e) {
				let empty = e.length === 0 ? true : false;
				if (this.empty !== empty) {
					this.empty = empty;
				}
			}
		},
		onShow() {
			this.loadData();
		},
		computed: {
			...mapState(['hasLogin'])
		},
		mounted() {
			console.log(this.hasLogin, 'hasLogin')
		},
		methods: {
			//详情页
			navToDetailPage(item) {
				//测试数据没有写id，用title代替
				let id = item.goodsId;
				uni.navigateTo({
					url: `/pages/product/product?id=${id}&from=appointment`
				})
			},
			//请求数据

			async loadData() {
				this.cartList=[]
				this.total=0
				this.shouduList=[]
				this.shoudutotal=0
				this.daxingList=[]
				this.daxingtotal=0
				var that = this;
				uni.setTabBarBadge({
				  index: 3,
				  text: "0"
				})
				that.$http({ //调用接口
					method: 'GET',
					url: this.global.target + "/wx/cart/index" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {
						if(result.data==null){
							return;
						}
						let cartList = result.data.cartList;
						// let cartList = list.map(item => {
						// 		item.checked = true;
						// 		return item;
						// });
						that.cartList = cartList;
						console.log(result);
						for(var i=0;i<cartList.length;i++){
							console.log(cartList[i])
							that.titleimg[i]=cartList[i].checked;
						}
						that.total = result.data.cartTotal.checkedGoodsAmount;

						let shouduList = result.data.shouduList;
						// let shouduList = slist.map(item => {
						// 		item.checked = true;
						// 		return item;
						// });
						that.shouduList = shouduList;
						that.shoudutotal = result.data.shouduTotal.checkedGoodsAmount;

						let daxingList = result.data.daxingList;
						// let daxingList = dlist.map(item => {
						// 		item.checked = true;
						// 		return item;
						// });
						that.daxingList = daxingList;
						that.daxingtotal = result.data.daxingTotal.checkedGoodsAmount;
						that.getcartNumber();
					} else {

					}
				}, function(error) {})

				// this.calcTotal(); //计算总价
			},
			// 点击店铺前的icon
			checkshop(e){
				
				var that = this;
				let list = that.cartList;
				
				let product = list[e];
				that.$http({ //调用接口
					method: 'POST',
					params: {
						ids: product.productId,
						checkValue: product.checked == true ? 0 : 1
					},
					url: that.global.target + "/wx/cart/checked" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {
						that.titleimg[e]=!that.titleimg[e];
						console.log(that.titleimg)
						that.$forceUpdate();
						list[e].checked = !list[e].checked;
						that.calcTotal("cartList", "item");
					} else {
				
					}
				}, function(error) {})
				
				
			},
			//监听image加载完成
			onImageLoad(key, index) {
				this.$set(this[key][index], 'loaded', 'loaded');
			},
			//监听image加载失败
			onImageError(key, index) {
				this[key][index].image = '/static/errorImage.jpg';
			},
			navToLogin() {
				uni.navigateTo({
					url: '/pages/public/login'
				})
			},
			//选中状态处理
			check(key, type, index) {
				var that = this;
				let list;
				if (key == "cartList") {
					list = that.cartList;
				} else if (key == "shouduList") {
					list = that.shouduList;
				} else if (key == "daxingList") {
					list = that.daxingList;
				}
				let product = list[index];
				that.$http({ //调用接口
					method: 'POST',
					params: {
						ids: product.productId,
						checkValue: product.checked == true ? 0 : 1
					},
					url: that.global.target + "/wx/cart/checked" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {

						if (type === 'item') {
							list[index].checked = !list[index].checked;
						} else {
							const checked = !that.allChecked;
							const plist = list;
							plist.forEach(item => {
								item.checked = checked;
							})
							that.allChecked = checked;
						}
						that.calcTotal(key, type);
					} else {

					}
				}, function(error) {})

			},
			//数量
			numberChange(data, key) {
				let list;
				if (key == "cartList") {
					list = this.cartList;
				} else if (key == "shouduList") {
					list = this.shouduList;
				} else if (key == "daxingList") {
					list = this.daxingList;
				}
				var that = this;
				let product = list[data.index];
				that.$http({ //调用接口
					method: 'POST',
					params: {
						id: product.id,
						number: data.number
					},
					url: that.global.target + "/wx/cart/update" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {

						list[data.index].number = data.number;
						that.calcTotal(key);
					} else {
						that.$api.msg(result.errmsg);
						
					}
				}, function(error) {

				})




			},
			//删除
			deleteCartItem(index, key) {
				let list;
				if (key == "cartList") {
					list = this.cartList;
				} else if (key == "shouduList") {
					list = this.shouduList;
				} else if (key == "daxingList") {
					list = this.daxingList;
				}
				let row = list[index];
				let id = row.productId;

				var that = this;
				that.$http({ //调用接口
					method: 'POST',
					params: {
						productIds: id
					},
					url: this.global.target + "/wx/cart/delete" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {
						list.splice(index, 1);
						that.calcTotal(key);
						that.getcartNumber();
					} else {

					}
				}, function(error) {})



				uni.hideLoading();
			},
			//清空
			clearCart() {
				uni.showModal({
					content: '清空购物车？',
					success: (e) => {
						if (e.confirm) {
							let list = this.cartList;
							let ids = "";
							list.forEach(item => {
								ids += item.id + ",";
							})
							var that = this;
							that.$http({ //调用接口
								method: 'POST',
								url: this.global.target + "/wx/cart/clear" //this指data
							}).then(function(response) { //接口返回数据
								console.log(response)
								var result = response.data;
								if (result.errno == 0) {
									that.cartList = [];
									that.calcTotal();
									that.getcartNumber();
								} else {

								}
							}, function(error) {})

						}
					}
				})
				
				
			},
			//计算总价
			calcTotal(key) {
				let list;
				if (key == "cartList") {
					list = this.cartList;
					if (list.length == 0) {
						this.empty = true;
						return;
					}
					let total = 0;
					let checked = true;
					list.forEach(item => {
						if (item.checked === true) {
							total += item.price * item.number;
						} else if (checked === true) {
							checked = false;
						}
					})
					this.allChecked = checked;
					this.total = Number(total.toFixed(2));
				} else if (key == "shouduList") {
					list = this.shouduList;
					if (list.length == 0) {
						this.shouduempty = true;
						return;
					}

					let shoudutotal = 0;
					let checked = true;
					list.forEach(item => {
						if (item.checked === true) {
							console.log(item.price)
							console.log(item.number)
							shoudutotal += item.price * item.number;
						} else if (checked === true) {
							checked = false;
						}
					})

					this.allChecked = checked;
					this.shoudutotal = Number(shoudutotal.toFixed(2));
				} else if (key == "daxingList") {
					list = this.daxingList;
					if (list.length == 0) {
						this.shouduempty = true;
						return;
					}
					let daxingtotal = 0;
					let checked = true;
					list.forEach(item => {
						if (item.checked === true) {
							daxingtotal += item.price * item.number;
						} else if (checked === true) {
							checked = false;
						}
					})
					this.allChecked = checked;
					this.daxingtotal = Number(daxingtotal.toFixed(2));
				}


			},
			//创建订单
			createOrder(key) {

				let list;
				if (key == "cartList") {
					list = this.cartList;
				} else if (key == "shouduList") {
					list = this.shouduList;
				} else if (key == "daxingList") {
					list = this.daxingList;
				}
				// let goodsData = [];
				let cartIds = "";
				let numbers = "";
				let comId = "";
				list.forEach(item => {
					if (item.checked) {
						cartIds += item.id + ",";
						numbers += item.number + ",";
						comId = item.comId;
					}
				})

				if (cartIds == "" || numbers == "") {
					this.$api.msg("请选择商品");
					return;
				}
				if (key == "cartList") {
					uni.navigateTo({
						url: `/pages/order/createOrder?cartIds=` + cartIds + `&numbers=` + numbers + `&comId=` + comId
					})
				} 

			},
			//加载购物车角标
			getcartNumber(){
				var that = this;
				//验证
				that.$http({ //调用接口
					method: 'GET',
					url: that.global.target + "/wx/cart/cartNumber" //this指data
				}).then(function(response) { //接口返回数据
					var result = response.data;
					if (result.errno == 0) {
						console.log(result)
						uni.setTabBarBadge({
						  index: 3,
						  text: ""+result.data
						})
					} else {
						
					}
				}, function(error) {
					// that.$api.msg("系统错误");
				})
			},
			
			nostock(){
				this.$api.msg("商品已失效");
			}
			
		}
	}
</script>

<style lang='scss'>
	page {
		background: #f2f2f2;
	}

	.container {
		padding-bottom: 134upx;

		/* 空白页 */
		.empty {
			position: fixed;
			left: 0;
			top: 0;
			width: 100%;
			height: 100vh;
			padding-bottom: 100upx;
			display: flex;
			justify-content: center;
			flex-direction: column;
			align-items: center;
			background: #fff;

			image {
				width: 240upx;
				height: 160upx;
				margin-bottom: 30upx;
			}

			.empty-tips {
				display: flex;
				font-size: $font-sm+2upx;
				color: $font-color-disabled;

				.navigator {
					color: $uni-color-primary;
					margin-left: 16upx;
				}
			}
		}
	}

	/* 购物车列表项 */
	.cart-all {
		height: 67upx;
		background: #fff;
		padding: 0 37upx;
		color: #FF0000;
		font-size: 29upx;
		line-height: 67upx;
	}
	.cart-li{
		//padding: 27upx 30upx 93upx 27upx;
		margin: 27upx 0 0;
		background: #fff;
		.cart-title {
			display: flex;
			font-size: 29upx;
			image {
				width: 38upx;
				height: 38upx;
				margin: 0 15upx 0 0;
			}
		}
	}
	.cart-list{
		margin: 27upx 0 0;
		background: #fff;
	}
	
	.cart-item {
		display: flex;
		padding: 30upx 0 0 54upx;
		position: relative;
		.image-wrapper {
			width: 246upx;
			height: 234upx;
			flex-shrink: 0;
			position: relative;
		}

		.checkbox {
			position: absolute;
			left: -16upx;
			top: -16upx;
			z-index: 8;
			font-size: 44upx;
			line-height: 1;
			padding: 4upx;
			color: $font-color-disabled;
			background: #fff;
			border-radius: 50px;
		}

		.item-right {
			display: flex;
			flex-direction: column;
			flex: 1;
			overflow: hidden;
			position: relative;
			padding-left: 46upx;
			.red{
				background: #FF0000;
				color: #fff;
				font-size: 19upx;
				padding: 5upx 15upx;
				border-radius: 20upx;
				margin: 0 6upx 0 0;
			}
			.title{
				font-size: 24upx;
				line-height: 32upx;
				white-space: normal;
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-line-clamp: 2;
				line-clamp: 2;
				-webkit-box-orient: vertical;
			}
			.price {
				font-size: $font-base + 2upx;
				color: $font-color-dark;
				height: 40upx;
				line-height: 40upx;
			}

			.attr {
				font-size:  25upx;
				color: #909090;
				height: 50upx;
				line-height: 47upx;
			}
			.price-dis{
				width: 100%;
				display: flex;
				justify-content: space-between;
			}
			.price {
				color: #FF3B30;
				font-size: 29upx;
				font-weight: bold;
				height: 50upx;
				line-height: 50upx;
			}
			.step{
				position: static;
				height: 60upx;
				font-size: 24upx;
				width: 160upx;
			}
		}
		.del-btn {
			padding: 4upx 10upx;
			font-size: 34upx;
			height: 50upx;
			color: $font-color-light;
		}
	}

	/* 底部栏 */
	.action-section {
		/* #ifdef H5 */
		margin-bottom: 100upx;
		/* #endif */
		/* position: fixed;
		left: 30upx;
		bottom: 30upx;
		z-index: 95; */
		display: flex;
		align-items: center;
		width: 690upx;
		height: 100upx;
		padding: 0 30upx;
		background: rgba(255, 255, 255, .9);
		/* box-shadow: 0 0 20upx 0 rgba(0, 0, 0, .5); */
		box-shadow: 0 0 10upx 0 rgba(0, 0, 0, .5);
		border-radius: 16upx;
		margin: 30upx auto;

		.checkbox {
			height: 52upx;
			position: relative;

			image {
				width: 52upx;
				height: 100%;
				position: relative;
				z-index: 5;
			}
		}

		.clear-btn {
			position: absolute;
			left: 26upx;
			top: 0;
			z-index: 4;
			width: 0;
			height: 52upx;
			line-height: 52upx;
			padding-left: 38upx;
			font-size: $font-base;
			color: #fff;
			background: $font-color-disabled;
			border-radius: 0 50px 50px 0;
			opacity: 0;
			transition: .2s;

			&.show {
				opacity: 1;
				width: 120upx;
			}
		}

		.total-box {
			flex: 1;
			display: flex;
			flex-direction: column;
			text-align: right;
			padding-right: 40upx;

			.price {
				font-size: $font-lg;
				color: $font-color-dark;
			}

			.coupon {
				font-size: $font-sm;
				color: $font-color-light;

				text {
					color: $font-color-dark;
				}
			}
		}

		.confirm-btn {
			padding: 0 38upx;
			margin: 0;
			border-radius: 100px;
			height: 76upx;
			line-height: 76upx;
			font-size: $font-base + 2upx;
			background: $uni-color-primary;
			box-shadow: 1px 2px 5px rgba(217, 60, 93, 0.72)
		}
	}

	/* 复选框选中状态 */
	.action-section .checkbox.checked,
	.cart-item .checkbox.checked {
		color: $uni-color-primary;
	}
</style>
