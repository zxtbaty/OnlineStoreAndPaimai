<template>

	<view class="content">
 	
		 <uni-page-head  >
			<div class="uni-page-head " style="background-color: #7E0076; color: rgb(0, 0, 0);">
				<div class="uni-page-head-hd">
					<div class="uni-page-head-btn" style="display: none; background-color: rgba(0, 0, 0, 0.5);">
						<i class="uni-btn-icon" style="color: rgb(255, 255, 255); font-size: 27px;"></i>
					</div>
				</div>
				<div class="uni-page-head-search" style="border-radius: 16px; background-color: rgba(251, 251, 251, 0.6);">
				   <div class="uni-page-head-search-placeholder uni-page-head-search-placeholder-left" style="color: rgb(251, 251, 251);">
				   </div>
				   <uni-input class="uni-page-head-search-input" style="color: rgb(0, 0, 0);">
						<div class="uni-input-wrapper">
							<div class="uni-input-placeholder" style="color: rgb(251, 251, 251);"></div>
							<form action="" class="uni-input-form">
							   <input maxlength="140" step="" :value="keyword" v-model="txtSearch"  placeholder="搜索" autocomplete="off" @confirm="navToSearch" type="search" class="uni-input-input">
							</form>
						</div>
					</uni-input>
				</div>
			</div>
		 </uni-page-head>	 
		
		<view class="category" >
			<!-- 问题列表 -->
			<uni-collapse v-for="item in qlist">
			    <uni-collapse-item :title="item.question" >
			        <view style="padding: 45upx;font-size: 28upx;"> {{item.answer}} </view>
			    </uni-collapse-item>
			</uni-collapse>
			
		</view>
	</view>
	
	

</template>

<script>
	import {uniCollapse,uniCollapseItem} from '@dcloudio/uni-ui';
	export default {
		components: {
			uniCollapse,uniCollapseItem
		},
		data() {
			return {
				txtSearch:'',
				userInfoCount:0,
				sizeCalcState: false,
				tabScrollTop: 0,
				tabScrollLeft: 0,
				qlist: [],
				page:1,
				size:100,
				loading:true,
				keyword:"",
			}
		},
		onLoad() {
			this.loadData();
		},
		//加载更多
		onReachBottom(){
			// this.loadData();
		},
		methods: {
			async loadData() {

				var loading = this.loading;
				if (loading == true) {
					if(this.keyword==""){
						this.keyword=this.$route.query.keyword;
						this.txtSearch = this.keyword;
					}
					var that = this;
					var page = that.page;
					var size = that.size;
					that.$http({ //调用接口
						method: 'POST',
						params: {
							keyword: that.keyword,
							page: page,
							size: size,
							sort: "add_time"
						},
						url: that.global.target + "/wx/issue/list" //this指data
					}).then(function(response) { //接口返回数据

						console.log(response.data);
						let result = response.data;
						if(result.errno==0){
							that.qlist = result.data.data;
						}

					}, function(error) {})
				}


			},
			
			//右侧栏滚动
			asideScroll(e) {
				if (!this.sizeCalcState) {
					this.calcSize();
				}
				let scrollTop = e.detail.scrollTop;
				let tabs = this.slist.filter(item => item.top <= scrollTop).reverse();
				if (tabs.length > 0) {
					this.currentId = tabs[0].pid;
				}
			},
			//计算右侧栏每个tab的高度等信息
			calcSize() {
				let h = 0;
				this.slist.forEach(item => {
					let view = uni.createSelectorQuery().select("#main-" + item.id);
					view.fields({
						size: true
					}, data => {
						item.top = h;
						h += data.height;
						item.bottom = h;
					}).exec();
				})
				this.sizeCalcState = true;
			},

			
			
		
		
			// 搜索栏
			navToSearch() {
				// uni.navigateTo({
				// 	url: `/pages/category/search?keyword=` + this.txtSearch
				// });
				
				this.loading = true;
				let keyword = this.txtSearch;
				this.keyword=keyword;
				var that = this;
				var page = 1;
				var size = that.size;
				that.$http({ //调用接口
					method: 'POST',
					params: {
						question: keyword,
						page: page,
						size: size,
						sort: "add_time"
					},
					url: that.global.target + "/wx/issue/list" //this指data
				}).then(function(response) { //接口返回数据
				
					
					var total = response.data.data.count;
					that.qlist = response.data.data.data;
					if (total == 0) {
						that.$api.msg("无数据");
					}
					// var num = page*size;
					if (total < size) {
						that.loading = false;
					}
				
					// that.page=2;
					uni.hideLoading()
				
				
				}, function(error) {})
					
				
			},
		}
	}
</script>

<style lang='scss'>
	page,
	.set-box {
		flex: 1;
	
		.icon {
			float: right;
			margin-left: 0upx;
			margin-right: 20upx
		}
	
		image {
			width: 44upx;
			height: 40upx;
		}
	}
	.content {
		height: 100%;
		/* background-color: #f8f8f8; */
		background-color: #f9f9f9;
	}

	.content {
		display: flex;
	}
	
	.category{
		width:100%;
		margin-top: 100upx;
	}


</style>
