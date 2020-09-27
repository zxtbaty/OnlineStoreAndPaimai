<template>
	<view class="container">
		<view class="carousel">
			<swiper indicator-dots circular="true" duration="400">
				<swiper-item class="swiper-item" v-for="(item, index) in imgList" :key="index">
					<view class="image-wrapper">
						<image :src="item" class="loaded" mode="aspectFill"></image>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<view class="introduce-section">
			
<!-- 			<view v-if="product.priceDesc!=null&&product.priceDesc!=''">
				<text class="redprice">¥{{ product.priceDesc }}</text>
			</view>
			<view v-if="product.priceDesc==null||product.priceDesc==''"> -->
				<view v-if="product.retailPrice!=product.counterPrice">
					<text class="redprice">¥{{ product.retailPrice }}</text>
					<text class="m-price">¥{{ product.counterPrice }}</text>
				</view>
				<view v-if="product.retailPrice==product.counterPrice">
					<text class="redprice">¥{{ product.retailPrice }}</text>
				</view>
			<!-- </view> -->
			
			<view style="float:right;" v-if="product.returnType=='秒杀商品'">
				<text class="redprice">剩余时间：</text>
				<!-- <text class="time hour">{{product.seckilltimes[0]}}</text>
				<text class="redprice">{{product.returnType}}：</text> -->
				<text>
					<uni-countdown :displayOneRow="true" :duration="product.seckilltimes[0]"></uni-countdown>
				</text>
			</view>
			<text class="title">
				</br>{{ product.name }}
			</text>
			<!-- <view class="price-boxs">
				<text class="price-tip">{{ product.brief }}</text>
			</view> -->
			<text style="font-size: 30upx;">
				</br>{{product.brief }}
			</text>
			<view class="price-box">
				<view class="now" style="display: flex;justify-content: space-between;">
					<text>
						<text class="Kucun">库存:</text>
						<text class="store-price">{{ normStock }}</text>
					</text>
					<text v-if="product.returnType!='正常商品'">{{product.returnType}}</text>
				</view>
				<view class="old" style="padding-left:0;margin-left:0">
					<text v-if="product.returnType=='团购商品'">
						<text class="store-number">已参团人数:</text>
						<text class="store-price">{{groupOnPersonCount}}</text>
					</text>
					<text v-if="product.returnType=='团购商品'">
						<text class="store-number" style="padding: 0upx 40upx;">最低成团人数:</text>
						<text class="store-price">{{groupOnMinPersonCount}}</text>
					</text>
				</view>
			</view>
		</view>


		<view class="c-list">
			<view class="c-row" @click="toggleSpec">
				<text class="tit">选择规格</text>
				<view class="con">
					<!-- <text>{{ goods_num }}{{ product.unit }}</text> -->
					<text>请选择</text>
					<text class="selected-text" v-for="(sItem, sIndex) in specSelected" :key="sIndex">
						<text class="red" style="margin-right:10upx;">{{sItem.specification}}</text>
						{{ sItem.value }} 
					</text>
				</view>
				<text class="yticon icon-you"></text>
			</view>
			
			<view class="c-row" v-for="(attr,index) in attributeList" :key="index">
				<text class="tit">{{attr.attribute}}</text>
				<view class="con">
					<text>{{attr.value}}</text>
			
				</view>
			</view>

			<view class="c-row" v-if="footflag">
				<text class="tit">质量保证</text>
				<view class=" con">
					<!-- <text>七天无理由退换货 ·</text> -->
					<text>· 正品保证 ·</text>
				</view>
			</view>

			<view class="c-row" v-if="footflag">
				<text class="tit">优质服务</text>
				<view class=" con">
				 
					<text>全国配送 ·</text>
				</view>
			</view>


		</view>


		<view class="detail-desc">
			<view class="d-header"><text>图文详情</text></view>
			<rich-text :nodes="desc"></rich-text>
			<view class="d-header"><text>没有更多了</text></view>
		</view>

		<!-- 底部操作菜单 -->
		<view class="page-bottom" v-if="footflag">
			<navigator url="/pages/index/index" open-type="switchTab" class="p-b-btn">
				<image src="/static/newimg/good_index.png" class="index-img" mode=""></image>
				<!-- <text>首页</text> -->
			</navigator>
			<!--  -->
			<navigator open-type="switchTab" class="p-b-btn">
				<image src="/static/newimg/good_service.png" class="cart-img" mode="" @click="kefu()"></image>
				<!-- <text>客服</text> -->
			</navigator>
			<navigator url="/pages/cart/cart" open-type="switchTab" class="p-b-btn">
				<min-badge :count="cartNumberTotal" :maxCount="99">
					<image src="/static/newimg/foot_cart2.png" class="cart-img" mode=""></image>
				</min-badge>
				<!-- <text>购物车</text> -->
			</navigator>


			<view class="action-btn-group" v-if="showButton">
				<button type="primary" v-if="product.returnType!='秒杀商品'&&product.returnType!='团购商品'&&product.ifXuni!=true&&product.ifXuni!='true'&&product.returnIfOnlyUserBonus==false"
				 class=" action-btn no-border add-cart-btn" @click="addtocart"><text>加入购物车</text></button>
				<button type="primary"  class=" action-btn no-border buy-now-btn" @click="buyClick">立即购买</button>
			</view>
			<view class="action-btn-group" v-if="isOnSale">
				<button type="primary" class=" action-btn no-border not-sale-btn"><text>已下架</text></button>
			</view>
		</view>
		<!-- 底部操作菜单 end  -->
		<!-- 底部操作菜单 预定 -->
		<view class="page-bottom" v-else>
			<navigator url="/pages/index/index" open-type="switchTab" class="p-b-btn">
				<image src="/static/newimg/good_index.png" class="index-img" mode=""></image>
				<!-- <text>首页</text> -->
			</navigator>
			<!-- open-type="navigateBack" -->
			<navigator class="p-b-btn" style="width:120upx">
				<image src="/static/newimg/good_service.png" class="cart-img" mode="" @click="kefu()"></image>

			</navigator>
			<navigator url="/pages/cart/cart" open-type="switchTab" class="p-b-btn">
				<min-badge :count="cartNumberTotal" :maxCount="99">
					<image src="/static/newimg/foot_cart2.png" class="cart-img" mode=""></image>
				</min-badge>
				<!-- <text>购物车</text> -->
			</navigator>
			<view class="action-btn-group" v-if="showButton">
				<button type="primary" class=" action-btn no-border add-cart-btn" @click="addtocart"><text>加入购物车</text></button>
				
			</view>
			<view class="action-btn-group" v-if="isOnSale">
				<button type="primary" class=" action-btn no-border not-sale-btn"><text>已下架</text></button>
			</view>
		</view>
		<!-- 底部操作菜单 预定 end-->

		<!-- 规格-模态层弹窗 -->
		<!-- @touchmove.stop.prevent="stopPrevent" -->
		<view class="popup spec" :class="specClass" @click="toggleSpec">
			<!-- 遮罩层 -->
			<view class="mask"></view>
			<view class="layer attr-content" @click.stop="stopPrevent">
				<view class="a-t">
					<image :src="product.picUrl"></image>
					<view class="right">
						<text class="price">¥{{ normPrice }}</text>
						<text class="stock">库存：{{ normStock }}{{ product.unit }}</text>
						<view class="selected">
							已选：
							<text class="selected-text" v-for="(sItem, sIndex) in specSelected" :key="sIndex">{{ sItem.value }}</text>
						</view>
					</view>
				</view>
				<view v-for="(item, index) in specList" :key="index" class="attr-list">
					<text>{{ item.name }}</text>
					<view class="item-list">
						<text v-for="(childItem, childIndex) in item.valueList" :key="childIndex" class="tit" :class="{ selected: childItem.selected }"
						 @click="selectSpec(index, childItem.value)">
							{{ childItem.value }}
						</text>
					</view>

					<!-- <view class="item-list">
						<text 
							v-for="(childItem, childIndex) in productList" 
							v-if="childItem.pid === item.id"
							:key="childIndex" class="tit"
							:class="{selected: childItem.selected}"
							@click="selectSpec(childIndex, childItem.pid)"
						>
							{{childItem.name}}
						</text>
					</view> -->
				</view>
				<view class="attr-list flex_between">
					<text class="gou_text">购买数量</text>
					<view class="add_dis">
						<view v-if="product.returnType!='团购商品' && 
						( product.returnType=='正常商品' || (product.returnType!='正常商品'&&
						(product.returnAllowBuyGoods==false||product.returnAllowBuyGoods=='false')))"
						 class="dis">
							<image :src="num_icon_dis" mode="" class="disimg" @click="disgood_num"></image>
						</view>
						<view v-if="product.returnType!='团购商品' &&
						( product.returnType=='正常商品' || (product.returnType!='正常商品'&&
						(product.returnAllowBuyGoods==false||product.returnAllowBuyGoods=='false')))"
						 class="dis">
						     <input type="number" value="" v-model="goods_num" class="goods_input" />
							
						</view>
						<view v-else class="dis">
						     <input type="number" disabled="true" value="" v-model="goods_num" class="goods_input" />						
						</view>
						
						 
						<view v-if="product.returnType!='团购商品' && 
						(product.returnType=='正常商品' || (product.returnType!='正常商品'&&
						(product.returnAllowBuyGoods==false||product.returnAllowBuyGoods=='false')))"
						 class="add">
							<image :src="num_icon_add" mode="" class="addimg" @click="addgood_num"></image>
						</view>
					</view>
				</view>
				<button class="btn" @click="untoggleSpec">完成</button>
			</view>
		</view>
		<!-- 分享 -->
		<share ref="share" :contentHeight="580" :shareList="shareList"></share>
	</view>
</template>

<script>
	import share from '@/components/share';
	import add1 from '../../static/add1.png';
	import add2 from '../../static/add2.png';
	import dis1 from '../../static/dis1.png';
	import dis2 from '../../static/dis2.png';
	import minBadge from '@/components/min-badge/min-badge'
	import wx from 'weixin-js-sdk'
	import {
		mapState,
		mapMutations
	} from 'vuex';
	import UniCountdown from '@/components/uni-countdown/uni-countdown'
	// var wx = require('jweixin-module')
	export default {
		components: {
			...mapState(['hasLogin', 'userInfo']),
			share,
			UniCountdown,
			minBadge
		},
		data() {
			return {
				isOnSale:false,
				showButton:false,//控制加入购物车和立即购买按钮显示
				cartNumberTotal: 0, //购物车中商品数
				normPrice: 0, //选中的sku的价格
				normStock: 0, //选择的sku的库存
				returnGrouponInfo: '', //选择的团购信息
				groupOnPersonCount: '',
				groupOnMinPersonCount: '',
				goodsId: 0, //传入的商品Id
				productId: 0, //某个商品型号ID

				// infomation: '',
				infomation: {
					attribute: 'attribute',
					value: 'value'
				},
				// product:'',
				product: {
					// name: '稻香村  真空包装 美食熟食（套装3只装）真空包装 现货直',
					// brief: 'fjof',
					// retailPrice: 123,
					// counterPrice: 321
				},
				specClass: 'none',
				// specSelected:'',
				specSelected: [{
					value: 106,
					unit: 1
				}],
				footflag: true, //底部按钮为true正常显示，为false显示的是预定的btn
				favorite: true,
				shareList: [],
				// imgList:[],
				imgList: [],
				desc: '',
				specList: [{
						id: 1,
						name: '尺寸'
					},
					{
						id: 2,
						name: '颜色'
					}
				],
				productList: [{
						id: 1,
						pid: 1,
						name: 'XS'
					},

					{
						id: 9,
						pid: 2,
						name: '草木绿'
					}
				],
				num_icon_add: add1, //增加数量按钮
				num_icon_dis: dis2, //减少数量按钮
				goods_num: 1, //购买数量
				addtype: 0,
				attributeList:[]
			};
		},
		async onLoad(options) {
			if (options.from) {
				this.footflag = false;
			}
			//接收传值,这时传入的是商品ID,而不是产品规格型号ID
			let id = options.id;
			this.goodsId = id;
			let productGoodsId=  options.productid;
			// if(id){
			// 	this.$api.msg(`点击了${id}`);
			// }
			var that = this;
			that.$http({
				//调用接口
				method: 'POST',
				params: {
					userId: uni.getStorageSync('userInfo').id,
					id: id
				},
				url: this.global.target + '/wx/goods/detail' //this指data
			}).then(
				function(response) {
					//接口返回数据
					var data = response.data.data;
					console.log(data);
					//商品属性
					that.infomation = data.attribute;
					//展示图列表
					that.imgList = data.info.gallery;
					//商品信息
					that.product = data.info;

					//商品所属商贸类型
					let comId = that.product.comId;
					if (comId == 1) {
						that.footflag = true;
					} else {
						that.footflag = false;
					}
					that.desc = "<div style='width:100vw'>" + data.info.detail + '</div>';
					// that.desc[0].children[0].text=data.info.detail;
					// console.log(that.desc[0].children)
					that.specList = data.specificationList;
					that.productList = data.productList;
					that.attributeList=data.attributeList;
					
					//如果指定特定产品
					if(productGoodsId){
						for (var i = 0; i < that.productList.length; i++) {
							var product = that.productList[i];
							if(product.id==productGoodsId){
								for (var j = 0; j < product.specifications.length; j++) {
									that.specList.forEach(item => {
										var valueList = item.valueList;
										for (var i = 0; i < valueList.length; i++) {
											var v = valueList[i];
											if(v.value==product.specifications[j]){
												that.$set(v, 'selected', true);
												break;
											}
										}
									});
								}
							}
						}
					}else{
						//默认选择第一个规格型号
						that.specList.forEach(item => {
							var valueList = item.valueList;
							for (var i = 0; i < valueList.length; i++) {
								var v = valueList[i];
								that.$set(v, 'selected', true);
								break;
							}
						});
					}
					
					//刷新第一个规格型号
					that.refreshNorm();
					//分享信息
					that.getShareInfo();
					
					if(that.product.isOnSale==true || that.product.isOnSale=="true"){
						that.showButton=true;
					}else{
						that.isOnSale=true;
					}
				},
				function(error) {}
			);

			//规格 默认选中第一条
			// this.specList.forEach(item=>{
			// 	for(let cItem of this.productList){
			// 		if(cItem.pid === item.id){
			// 			this.$set(cItem, 'selected', true);
			// 			this.specSelected.push(cItem);
			// 			break; //forEach不能使用break
			// 		}
			// 	}
			// })

			this.shareList = await this.$api.json('shareList');
			this.getcartNumber();
		},
		methods: {
			...mapMutations(['login']),
			//规格弹窗开
			toggleSpec(selecttype) {
				this.specClass = 'show';
				if (selecttype == "tocart") {
					this.addtype = 1;
				} else if (selecttype == "tobuy") {
					this.addtype = 2;
				} else {
					this.addtype = 0;
				}
			},
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
					url: this.global.target + '/wx/share/info' //this指data
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
						that.wxready();
						
					},
					function(error) {
						console.log("微信config配置失败 err: ", error);
					}
				);

			},

			wxready() {
				var that =this;
			
				wx.ready(function() {
					//分享朋友圈
					wx.updateTimelineShareData({
						title: that.product.name, // 分享标题
						desc: that.product.brief, // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: that.product.picUrl, // 分享图标
						success: function() {
							// 用户确认分享后执行的回调函数
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
					
						}
					});
					// 分享好友
					wx.updateAppMessageShareData({
						title: that.product.name, // 分享标题
						desc: that.product.brief, // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: that.product.picUrl, // 分享图标
						type: "", // 分享类型,music、video或link，不填默认为link
						dataUrl: "", // 如果type是music或video，则要提供数据链接，默认为空
						success: function() {
							
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
						}
					});
				});
			},

			//规格弹窗关
			untoggleSpec() {
				this.specClass = 'hide';
				setTimeout(() => {
					this.specClass = 'none';
				}, 250);
				if (this.normStock <= 0) {
					this.$api.msg('库存数量为0');
					return;
				}

				if (this.addtype == 1) {
					this.confimaddtocart();
					this.getcartNumber();
				} else if (this.addtype == 2) {
					this.confimbuy();
				}
			},

			refund(id) {
				var that = this;

				that.$http({
					//调用接口
					method: 'POST',
					params: {
						userId: uni.getStorageSync('userInfo').id,
						orderId: id
					},
					url: this.global.target + '/wx/order/refund' //this指data
				}).then(
					function(response) {
						//接口返回数据
						// console.log(response.data.data)
						var result = response.data;
						if(result.errno==501||result.errno==502||result.errno==504||result.errno==725){
							that.$api.msg(result.errmsg)
							return;
						}
						// if (result.errno == 0) {
						// 	let data = result.data.data;
						// } else {
						// 	that.$api.msg(result.errmsg);
						// }
					},
					function(error) {}
				);
			},

			//选择规格
			selectSpec(index, value) {
				// let list = this.productList;
				let list = this.specList;

				for (var i = 0; i < list.length; i++) {
					var item = list[i];
					var valueList = item.valueList;
					valueList.forEach(v => {
						// console.log(v.value)
						// console.log(v.selected)
						if (i == index) {
							if (v.value != value) {
								this.$set(v, 'selected', false);
							} else {
								this.$set(v, 'selected', true);
							}
						}
					});
				}

				//this.$set(list[index], 'selected', true);
				//存储已选择
				this.refreshNorm();
			},

			refreshNorm() {
				// console.log(this.product);
				let productList = this.productList;
				let list = this.specList;
				/**
				 * 修复选择规格存储错误
				 * 将这几行代码替换即可
				 * 选择的规格存放在specSelected中
				 */
				this.specSelected = [];
				var selectNorm = '';
				list.forEach(item => {
					var valueList = item.valueList;
					valueList.forEach(v => {
						if (v.selected === true) {
							this.specSelected.push(v);
							selectNorm += v.value;
						}
					});
				});

				//根据选择的规格刷新库存，更新选择显示
				var selectedGood = this.specSelected;


				productList.forEach(item => {
					var specifications = item.specifications;
					var str = '';
					specifications.forEach(fication => {
						str += fication;
					});
					if (str == selectNorm) {
						if (item.returnRuleNumber <= 0) {
							this.$api.msg('库存不足');
							
						} 
							this.normPrice = item.returnRulePrice;
							if (item.returnRuleNumber != null) {
								this.normStock = item.returnRuleNumber;
							} else {
								this.normStock = item.number;
							}

							this.returnGrouponInfo = item.returnGrouponInfo;
							if (this.returnGrouponInfo != null) {
								this.groupOnPersonCount = this.returnGrouponInfo.split('/')[0];
								this.groupOnMinPersonCount = this.returnGrouponInfo.split('/')[1];
							}

							//将规格型号id赋值给变量
							this.productId = item.id;
							this.product.returnType = item.returnType;
							this.product.returnRuleId = item.returnRuleId;
							this.product.returnIfOnlyUserBonus = item.returnIfOnlyUserBonus;
							this.product.returnAllowBuyGoods = item.returnAllowBuyGoods;
							this.product.returnIfUseCoupon = item.returnIfUseCoupon;
							this.product.returnIfFreePost= item.returnIfFreePost;
							this.product.returnIfUseBonus = item.returnIfUseBonus;
							this.product.seckillEndDate = item.seckillEndDate;
							this.product.retailPrice = item.returnRulePrice;
							if (this.goods_num > item.returnRuleNumber) {
								this.goods_num = item.returnRuleNumber;
							}
						
					}
				});
				this.daotime();
			},

			//分享
			share() {
				this.$refs.share.toggleMask();
			},
			//收藏
			toFavorite() {
				this.favorite = !this.favorite;
			},
			buyClick() {
				// if(this.product.priceDesc!=null&&this.product.priceDesc!=''){
				// 	this.$api.msg('非标准定价商品不可以直接购买');
				// 	return;
				// }
				this.toggleSpec("tobuy");
			},
			confimbuy() {
				if (this.normStock <= 0) {
					this.$api.msg('库存数量为0');
					return;
				}
				if (this.productId && this.productId != 0) {
					let mallOrderTypeCode = 10;
					if (this.product.returnType == '秒杀商品') {
						mallOrderTypeCode = 20
					} else if (this.product.returnType == '团购商品') {
						mallOrderTypeCode = 30
					}
					uni.navigateTo({
						url: `/pages/order/createOrder?productId=` + this.productId +
							`&number=` + this.goods_num + `&comId=` + this.product.comId +
							`&mallOrderTypeCode=` + mallOrderTypeCode +
							`&ruleId=` + this.product.returnRuleId +
							`&price=` + this.normPrice +
							`&ifXuni=` + this.product.ifXuni +
							`&returnIfFreePost=` + this.product.returnIfFreePost +
							`&returnIfUseCoupon=` + this.product.returnIfUseCoupon +
							`&returnIfUseBonus=` + this.product.returnIfUseBonus +
							`&returnIfOnlyUserBonus=` + this.product.returnIfOnlyUserBonus
					});
				} else {
					this.$api.msg('请选择商品');
				}
			},

			addtocart() {
				// if(this.product.priceDesc!=null&&this.product.priceDesc!=''){
				// 	this.$api.msg('非标准定价商品不可以直接购买');
				// 	return;
				// }
				this.toggleSpec("tocart");
			},
			confimaddtocart() {
				uni.showLoading({
					title: '请稍候'
				})
				if (this.productId && this.productId != 0) {
					var that = this;
					// console.log("商品ID:"+that.goodsId+",产品ID:"+that.productId+",数量:"+that.goods_num+",单价:"+that.normPrice)
					that.$http({
						//调用接口
						method: 'POST',
						params: {
							gid: that.goodsId,
							pid: that.productId,
							count: that.goods_num,
							price: that.normPrice,
							ruleId:that.product.returnRuleId,
							ruleType:that.product.returnType,
						},
						url: that.global.target + '/wx/cart/add' //this指data
					}).then(
						function(response) {
							uni.hideLoading();
							//接口返回数据
							if (response.data.errno != 0) {
								that.$api.msg(response.data.errmsg);
							} else {
								that.getcartNumber();
								that.$api.msg('商品已加入购物车');
							}
						},
						function(error) {
							uni.hideLoading();
							that.$api.msg('请选择商品');
						}
					);
				} else {
					that.$api.msg('请选择商品');
				}

			},


			stopPrevent() {},

			addgood_num() {
				//增加商品数量
				if (this.goods_num < this.normStock) {
					var {
						goods_num = 1
					} = {
						goods_num: this.goods_num == '' ? undefined : parseInt(this.goods_num) + 1
					};
					this.goods_num = goods_num;
				}
			},
			disgood_num() {
				//减少商品数量
				if (this.goods_num > 1) {
					var {
						goods_num = 1
					} = {
						goods_num: this.goods_num == '' ? undefined : parseInt(this.goods_num) - 1
					};
					this.goods_num = goods_num;
				}
			},
			//验证是否登录
			tologin() {
				// var that = this;
				// that.$http({ //调用接口
				// 	method: 'POST',
				// 	url: that.global.target + "/wx/auth/loginInit" //this指data
				// }).then(function(response) { //接口返回数据
				//
				// 	window.location.href = response.data;
				//
				// }, function(error) {
				//
				// })

				uni.navigateTo({
					url: '/pages/public/tologin'
				});
			},
			kefu() {
				location.href = 'http://uclient.yunque360.com/frame.html?company_id=c38uk4hpcd36n';
			},
			daotime() {
				//设置倒计时
				var that = this;
				var myDate = Math.round(new Date().getTime() / 1000).toString();
				that.currenttime = myDate;
				// if(seckillList==null||seckillList.length==0){
				// 	return;
				// }

				var duration = 0;
				setInterval(() => {
					duration += 1;

					let times = new Array();
					let seckillEndDate=this.product.seckillEndDate;
					if(seckillEndDate==null){
						return
					}
					let seckillEndDateReplace = seckillEndDate.replace(/-/g,'/');
					
					
					var endtime = (Date.parse(new Date(seckillEndDate))||Date.parse(new Date(seckillEndDateReplace))) / 1000 - myDate - duration;
					times.push(endtime);
					endtime = endtime > 0 ? endtime : 0;

					// var daoH = Math.floor(endtime / 3600);
					// daoH=daoH<9?('0'+daoH):daoH;
					// times.push(daoH);
					// var daoM = Math.floor((endtime - daoH * 3600) / 60);
					// daoM=daoM<9?('0'+daoM):daoM;
					// times.push(daoM);
					// var daoS = endtime - daoH * 3600 - daoM * 60;
					// daoS=daoS<9?('0'+daoS):daoS;
					// times.push(daoS);
					that.product.seckilltimes = times;
					that.$forceUpdate();

				}, 1000);
			},
			/**
			 * 请求静态数据只是为了代码不那么乱
			 * 分次请求未作整合
			 */
			async checkIfLogin() {
				var that = this;
				that.$http({ //调用接口
					method: 'POST',
					url: this.global.target + "/wx/auth/checkToken" //this指data
				}).then(function(response) { //接口返回数据
					console.log(response)
					var result = response.data;
					if (result.errno == 0) {
						that.login(result.data);
						let userInfo = uni.getStorageSync('userInfo');
						that.bonus = userInfo.bonus;
						that.couponList = userInfo.ticketsList;
				  
						// if(userInfo.vipCode!=null&&userInfo.vipCode!=''){
							that.isbind=true;
							that.getInfoCount();
						// } else
						// {
						//    that.navToLogin()
						// }
			
					} else {
			
					}
				}, function(error) {
					console.log(error.data)
				})
				that.getFootprint();
			},
			//加载购物车角标
			getcartNumber() {
				var that = this;
				//验证
				that.$http({ //调用接口
					method: 'GET',
					url: that.global.target + "/wx/cart/cartNumber" //this指data
				}).then(function(response) { //接口返回数据
					var result = response.data;
					that.cartNumberTotal = result.data;
					if (result.errno == 0) {
						// console.log(result)
						uni.setTabBarBadge({
							index: 3,
							text: "" + result.data
						})

					} else {

					}
				}, function(error) {
					// that.$api.msg("系统错误");
				})
			}

		},
		watch: {
			goods_num(e) {
				var that = this;
				//console.log(e, 'es')
				if (e % 1 != 0) {
					// console.log('a')
					that.goods_num = parseInt(e);
				} else if (e == '' || e == undefined || e == ' ') {
					that.goods_num = 0;
				}
				if (e > 1) {
					that.num_icon_dis = dis1;
				} else {
					that.num_icon_dis = dis2;
				}
			}
		}
	};
</script>
<style lang="scss">
	page {
		background: $page-color-base;
		padding-bottom: 160upx;
	}

	.icon-you {
		font-size: $font-base + 2upx;
		color: #888;
	}

	.carousel {
		height: 728upx;
		position: relative;

		swiper {
			height: 100%;
		}

		.image-wrapper {
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
	.introduce-section {
		background: #fff;
		padding: 20upx 24upx 32upx 24upx;

		.redprice {
			font-size: 40upx;
			color: #fff;
			padding: 3upx 16upx;
			background: #ff0000;
			margin: 0 10upx 0 0;
		}

		.remainingTime {
			font-size: 40upx;
			color: #fff;
			padding: 3upx 16upx;
			background: #ff0000;
			margin: 0 10upx 0 0;
		}

		.title {
			font-size: 35upx;
			line-height: 40upx;

			.red {
				font-size: 20upx;
				color: #fff;
				padding: 3upx 16upx;
				background: #ff0000;
				border-radius: 15upx;
				margin: 0 10upx 0 0;
			}
		}

		.price-boxs {
			display: flex;
			justify-content: space-between;
			align-items: baseline;
			font-size: 30upx;
			color: #909090;
		}

		.price-zhe {
			font-size: 27upx;
			padding: 5upx 21upx;
			border: 1px solid currentColor;
			border-radius: 5upx;
			margin: 0 22upx 0 0;
		}

		.Kucun {
			color: black;
			font-size: 30upx;
			// padding: 5upx 21upx;		
			padding: 5upx 21upx;
			// border: 1px solid currentColor;
			// border-radius: 5upx;
			//margin-left: 10upx;
			margin: 0 22upx 0 0upx;
		}



		.price-box {
			view {
				margin: 16upx 0 0;
			}

			.now {
				color: #ff0000;
			}

			.old {
				color: #909090;
			}

			.price-zhe {
				font-size: 27upx;
				padding: 5upx 21upx;
				border: 1px solid currentColor;
				border-radius: 5upx;
				margin: 0 22upx 0 0;
			}

			.store-number {
				color: black;
				font-size: 27upx;
				//padding: 5upx 21upx;
				padding: 5upx 10upx;
				// border: 1px solid currentColor;
				// border-radius: 5upx;
				//margin: 0 22upx 0 40upx;
				margin: 0 0upx 0 10upx;
			}

			.store-price {
				color: black;
				font-size: 27upx;
				margin: 0;
				// font-weight: bold;

			}

			.price {
				color: #fff;
				font-weight: bold;
			}
		}

		.m-price {
			font-size: 28upx;
			margin: 0 12 0 10upx;
			color: $font-color-light;
			text-decoration: line-through;
		}

		.coupon-tip {
			align-items: center;
			padding: 4upx 10upx;
			background: $uni-color-primary;
			font-size: $font-sm;
			color: #fff;
			border-radius: 6upx;
			line-height: 1;
			transform: translateY(-4upx);
		}

		.bot-row {
			display: flex;
			align-items: center;
			height: 50upx;
			font-size: $font-sm;
			color: $font-color-light;

			text {
				flex: 1;
			}
		}
	}

	/* 分享 */
	.share-section {
		display: flex;
		align-items: center;
		color: $font-color-base;
		background: linear-gradient(left, #fdf5f6, #fbebf6);
		padding: 12upx 30upx;

		.share-icon {
			display: flex;
			align-items: center;
			width: 70upx;
			height: 30upx;
			line-height: 1;
			border: 1px solid $uni-color-primary;
			border-radius: 4upx;
			position: relative;
			overflow: hidden;
			font-size: 22upx;
			color: $uni-color-primary;

			&:after {
				content: '';
				width: 50upx;
				height: 50upx;
				border-radius: 50%;
				left: -20upx;
				top: -12upx;
				position: absolute;
				background: $uni-color-primary;
			}
		}

		.icon-xingxing {
			position: relative;
			z-index: 1;
			font-size: 24upx;
			margin-left: 2upx;
			margin-right: 10upx;
			color: #fff;
			line-height: 1;
		}

		.tit {
			font-size: $font-base;
			margin-left: 10upx;
		}

		.icon-bangzhu1 {
			padding: 10upx;
			font-size: 30upx;
			line-height: 1;
		}

		.share-btn {
			flex: 1;
			text-align: right;
			font-size: $font-sm;
			color: $uni-color-primary;
		}

		.icon-you {
			font-size: $font-sm;
			margin-left: 4upx;
			color: $uni-color-primary;
		}
	}

	.c-list {
		font-size: 27upx;
		margin: 25upx 0;

		.c-row {
			display: flex;
			align-items: center;
			padding: 0 27upx;
			position: relative;
			height: 80upx;
			line-height: 80upx;
			background: #fff;
		}

		.tit {
			width: 131upx;
			color: #909090;
		}

		.con {
			flex: 1;

			.selected-text {
				margin-right: 27upx;
				float: right;
			}
		}

		.bz-list {
			height: 40upx;
			font-size: $font-sm + 2upx;
			color: $font-color-dark;

			text {
				display: inline-block;
			}
		}

		.t-r {
			text-align: right;

			text {
				padding: 4upx 12upx;
				height: 33upx;
				border: 1px solid #ff0000;
				border-radius: 5upx;
			}
		}

		.con-list {
			flex: 1;
			display: flex;
			flex-direction: column;
			color: $font-color-dark;
			line-height: 40upx;
		}

		.top-25 {
			margin: 25upx 0;
		}

		.red {
			color: #ff0000;
		}
	}

	/* 评价 */
	.eva-section {
		display: flex;
		flex-direction: column;
		padding: 20upx 30upx;
		background: #fff;
		margin-top: 16upx;

		.e-header {
			display: flex;
			align-items: center;
			height: 70upx;
			font-size: $font-sm + 2upx;
			color: $font-color-light;

			.tit {
				font-size: $font-base + 2upx;
				color: $font-color-dark;
				margin-right: 4upx;
			}

			.tip {
				flex: 1;
				text-align: right;
			}

			.icon-you {
				margin-left: 10upx;
			}
		}
	}

	.eva-box {
		display: flex;
		padding: 20upx 0;

		.portrait {
			flex-shrink: 0;
			width: 80upx;
			height: 80upx;
			border-radius: 100px;
		}

		.right {
			flex: 1;
			display: flex;
			flex-direction: column;
			font-size: $font-base;
			color: $font-color-base;
			padding-left: 26upx;

			.con {
				font-size: $font-base;
				color: $font-color-dark;
				padding: 20upx 0;
			}

			.bot {
				display: flex;
				justify-content: space-between;
				font-size: $font-sm;
				color: $font-color-light;
			}
		}
	}

	/*  详情 */
	.detail-desc {
		background: #fff;
		margin-top: 16upx;

		.d-header {
			display: flex;
			justify-content: center;
			align-items: center;
			height: 80upx;
			font-size: $font-base + 2upx;
			color: $font-color-dark;
			position: relative;

			text {
				padding: 0 20upx;
				background: #fff;
				position: relative;
				z-index: 1;
			}

			&:after {
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
	.attr-content {
		padding: 10upx 30upx;

		.a-t {
			display: flex;

			image {
				width: 170upx;
				height: 170upx;
				flex-shrink: 0;
				margin-top: -40upx;
				border-radius: 8upx;
			}

			.right {
				display: flex;
				flex-direction: column;
				padding-left: 24upx;
				font-size: $font-sm + 2upx;
				color: $font-color-base;
				line-height: 42upx;

				.price {
					font-size: $font-lg;
					color: $uni-color-primary;
					margin-bottom: 10upx;
				}

				.selected-text {
					margin-right: 10upx;
				}
			}
		}

		.attr-list {
			display: flex;
			flex-direction: column;
			font-size: $font-base + 2upx;
			color: $font-color-base;
			padding-top: 30upx;
			padding-left: 10upx;
		}

		.item-list {
			padding: 20upx 0 0;
			display: flex;
			flex-wrap: wrap;

			text {
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

			.selected {
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

			.mask {
				animation: showPopup 0.2s linear both;
			}

			.layer {
				animation: showLayer 0.2s linear both;
			}
		}

		&.hide {
			.mask {
				animation: hidePopup 0.2s linear both;
			}

			.layer {
				animation: hideLayer 0.2s linear both;
			}
		}

		&.none {
			display: none;
		}

		.mask {
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

			.btn {
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
	.page-bottom {
		position: fixed;
		left: 0upx;
		bottom: 0upx;
		z-index: 95;
		display: flex;
		align-items: center;
		width: 100vw;
		height: 98upx;
		background: rgba(255, 255, 255, 1);

		.p-b-btn {
			width: 112upx;
			height: 53upx;
			text-align: center;
			padding: 4upx 0 0;

			&:first-child {
				border-right: 1px solid #909090;
			}

			.index-img {
				width: 41upx;
				height: 42upx;
			}

			.cart-img {
				width: 45upx;
				height: 37upx;
			}

			.icon-fenxiang2 {
				font-size: 42upx;
				transform: translateY(-2upx);
			}

			.icon-shoucang {
				font-size: 46upx;
			}
		}

		.action-btn-group {
			flex: 1;
			display: flex;
			height: 98upx;
			padding: 2upx;
			background: linear-gradient(239deg, rgba(242, 81, 33, 1), rgba(233, 58, 39, 1));
			position: relative;

			.action-btn {
				display: flex;
				align-items: center;
				justify-content: center;
				width: 50%;
				height: 94upx;
				font-size: $font-base;
				padding: 0;
				border-radius: 0;
				background: transparent;
			}

			.add-cart-btn {
				background: #fff;

				text {
					background: linear-gradient(239deg, rgba(242, 81, 33, 1) 0%, rgba(233, 58, 39, 1) 100%);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
				}
			}
			.not-sale-btn {
				background: #fff9ed;
				width: 100%;
				text {
					background: linear-gradient(239deg, rgba(242, 81, 33, 1) 0%, rgba(233, 58, 39, 1) 100%);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
				}
			}
		}
	}

	.back_img {
		width: 41upx;
		height: 42upx;
	}

	.page-bottom .action-btn-group .order-now-btn {
		width: 420upx;
	}

	.imglist_p,
	.imglist_p div p,
	.imglist_p div p img {
		width: 100vw;
	}

	.add_dis {
		width: 240upx;
		height: 60upx;
		background: #efefef;
		border-radius: 8upx;
		float: right;
		display: flex;
		display: -webkit-flex;
	}

	.flex_between {
		justify-content: space-between;
		-webkit-justify-content: space-between;
		flex-direction: row !important;
		-webkit-flex-direction: row !important;

		.gou_text {
			width: 200upx;
		}
	}

	.add_dis .disimg,
	.add_dis .addimg {
		width: 40upx;
		height: 40upx;
		margin: 10upx 10upx;
	}

	.add_dis .goods_input {
		flex: 1;
		-webkit-flex: 1;
		height: 60upx;
		line-height: 60upx;
		text-align: center;
		border-width: 0 1px;
		border-color: #fff;
		border-style: solid;
	}
</style>
