<template>
  <div class="hm-news-detail">
    <div class="hd">
		<view style="display: flex;margin-left: 15px;">
			<img class="yunshu" :src="options.yunshu" />
			<div class="container">
			  <div class="outer">
			    <view class="author">{{ options.author }}</view>
			    <view class="time">{{ options.time }}</view>
			  </div>
			</div>
		</view>
     
		  <div class="wrap"  @click="logZan(options.id)">
			  <min-badge :count="options.zanCount" :maxCount="99">
				<img class="like" style="width: 24px;height: 24px;" :src="options.like"  />
			  </min-badge>	   
			<!-- <img class="share" :src="options.share" /> -->
		  </div>
    </div>
	
    <!-- <span class="title">{{ options.title }}</span> -->
<!--    <div class="main">
      <div class="entryPicWrap"><img class="img" :src="options.img" /></div>
    </div> -->
    <div class="ft">
      <span class="content" v-html="options.content" ></span>
    </div>
  </div>
</template>
  
<script>
import HmNewsDetail from '@/components/hm-news-detail/index.vue'
 	import wx from 'weixin-js-sdk'
	
	export default {
		components: {
			HmNewsDetail
		},
		data() {
			return {
			    options: {
			          yunshu:
			            '/static/gcrdpmLogo.png',
			          author: '',
			          like:
			            '/static/hm-news-detail/images/img_22946_0_1.png',
			          share:
			            '/static/hm-news-detail/images/img_22946_0_2.png',
			          time: '',
			          title: '',
			          img:
			            '/static/hm-news-detail/images/img_22946_0_3.png',
			          content:'',
				},
				articleId:null
			};
		},
		async onLoad(options){
			this.articleId=options.id
			
            this.loadData(options.id)
			//分享信息
			this.getShareInfo();
		},
		methods:{
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
					url: that.global.target + '/wx/share/info' //this指data
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
						title: that.options.title, // 分享标题
						desc: that.options.title, // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: 'http://wenboguan.gcrdpm.com/static/gcrdpmLogo.png', // 分享图标
						success: function() {
							// 用户确认分享后执行的回调函数
							//记录浏览量+1 
							// that.$http({           //调用接口
							//     method:'GET',
							// 	params:{
							// 		articleId:that.articleId
							// 	},
							// 	url:that.global.target+"/wx/wenBoGuan/logShare"  //this指data
							// }).then(function(response){  //接口返回数据
							// 	console.log('成功') 
				                  
							// },function(error){
							// })
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
					
						}
					});
					// 分享好友
					wx.updateAppMessageShareData({
						title: that.options.title, // 分享标题
						desc: that.options.title, // 分享描述
						link: window.location.href.split('#')[0], // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
						imgUrl: 'http://wenboguan.gcrdpm.com/static/gcrdpmLogo.png', // 分享图标
						type: "", // 分享类型,music、video或link，不填默认为link
						dataUrl: "", // 如果type是music或video，则要提供数据链接，默认为空
						success: function() {
							// // 用户确认分享后执行的回调函数
							// //记录浏览量+1 
							// that.$http({           //调用接口
							//     method:'GET',
							// 	params:{
							// 		articleId:that.articleId
							// 	},
							// 	url:that.global.target+"/wx/wenBoGuan/logShare"  //this指data
							// }).then(function(response){  //接口返回数据
							// 	console.log('成功') 
							      
							// },function(error){
							// })
						},
						cancel: function() {
							// 用户取消分享后执行的回调函数
						}
					});
				});
			},
			
			loadData(id){
				let that=this
				that.$http({           //调用接口
				    method:'GET',
					params:{
						articleId:id
					},
					 url:this.global.target+"/wx/wenBoGuan/getArticleDetail"  //this指data
				}).then(function(response){  //接口返回数据
					// console.log( response);
					let data=response.data.data;
					that.options={
						  id:id,
						  yunshu: '/static/gcrdpmLogo.png',
						  author: data.author,
						  like:
						    '/static/hm-news-detail/images/img_22946_0_1.png',
						  share:
						    '/static/hm-news-detail/images/img_22946_0_2.png',
						  time: data.pubDate,
						  title: data.title,
						  zanCount:data.zanCount,
						  img:
						    '/static/hm-news-detail/images/img_22946_0_3.png',
						  content:data.content
					}
				})
			},
			imageOnLoad(key,index){
				this.$set(this.data[key][index], 'loaded', 'loaded');
			},
			changeEpd(index){
				let list = this.data.episodeList;
				let epd = list[index];
				this.$api.msg(`切换到第${epd}项`);
				this.currentEpd = epd;
			},
			//分享
			share(){
				this.$refs.share.toggleMask();	
			},
			//收藏
			favorite(){
				this.data.favorite = !this.data.favorite;
			},
			logZan(id){
				//记录浏览量+1
				var that = this;
				that.$http({           //调用接口
				    method:'GET',
					params:{
						articleId:id
					},
					url:that.global.target+"/wx/wenBoGuan/logZan"  //this指data
				}).then(function(response){  //接口返回数据
					console.log('成功') 
					 
					that.$http({           //调用接口
					    method:'GET',
						params:{
							articleId:id
						},
						 url:that.global.target+"/wx/wenBoGuan/getArticleDetail"  //this指data
					}).then(function(response){  //接口返回数据
						// console.log( response);
						let data=response.data.data;
						that.$set(that.options,'zanCount',data.zanCount);
				    })
					 
				},function(error){
				})
			}
			  
		},
		 
	}
</script>

<style>
.hm-news-detail {
/*  display: flex;
  align-items: flex-start;
  flex-direction: column;
  background-color: #ffffff;
  width: 100%; */
   
}

.hd {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 20px;
  width: 100%;
}

.yunshu {
  width: 76rpx;
  height: 76rpx;
  filter: drop-shadow(0px 0px 20px rgba(0, 0, 0, 0.09));
}

.container {
  display: flex;
  
  flex-direction: row;
  justify-content: space-between;
  margin-left: 20rpx;
  height: 76rpx;
  width: 100%;
}

.outer {
/*  display: flex;  
  align-items: center;
  flex-direction: row;
  justify-content: space-between; */
  margin-top: 2rpx;
 
  height: 46rpx;
}

.author {
  max-width: 368rpx;
  height: 44rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 44rpx;
  white-space: nowrap;
  color: #000000;
  font-size: 32rpx;
  font-weight: 400;
}

.wrap {
  display: flex;
  align-items: flex-end;
  flex-direction: row;
  margin-top: 20rpx;
  margin-right: 30px;
  height: 34rpx;
}

.like {
  opacity: 0.5;
  width: 38rpx;
  height: 34rpx;
}

.share {
  opacity: 0.5;
  margin-left: 52rpx;
  width: 30rpx;
  height: 30rpx;
}

.time {
  opacity: 0.6;
  max-width: 560rpx;
  height: 28rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 28rpx;
  white-space: pre;
  color: #000000;
  font-family: Helvetica;
  font-size: 24rpx;
  font-weight: normal;
}

.title {
  margin-top: 36rpx;
  margin-left: 68rpx;
  width: 512rpx;
  height: 100rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 50rpx;
  white-space: pre-wrap;
  color: #000000;
  font-size: 36rpx;
  font-weight: 400;
}

.main {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 16rpx;
 
}

.entryPicWrap {
  display: flex;
  position: relative;
  align-items: center;
  flex-direction: row;
  justify-content: center;
 
  height: 368rpx;
}

.img {
  position: relative;
 
  height: 368rpx;
  filter: drop-shadow(0px 0px 20px rgba(0, 0, 0, 0.05));
}

.ft {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  width: 90%;
  margin-top: 20px;
  margin-left: 5%;
  margin-right: 5%;
}

.content {

  overflow: hidden;
  resize:horizontal;
  text-overflow: ellipsis;
  line-height: 62rpx;
  letter-spacing: 0px;
  white-space: pre-wrap;
  color: #000000;
  font-size: 36rpx;
  font-weight: 400;
  text-align: center; 
  width: auto;
  height: auto;
  max-width: 100%;
  min-width: 100%;
  /* max-height: 100%; */

      
}

</style>