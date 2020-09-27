<template>
	<view class="container">

	</view>
</template>
<script>

	export default {

		data() {
			return {
				
			}
		},
		async onLoad(rurl) {
			
		},
		onLoad(option) {
			
				let rurl = option.rurl;
				// console.log("tologin1:"+rurl);
				var that = this;
				var url = encodeURIComponent(rurl);
				// console.log("tologin2:"+url);
				that.$http({ //调用接口
					method: 'POST',
					params:{
						rurl:url
					},
					url: that.global.target + "/wx/auth/loginInit" //this指data
				}).then(function(response) { //接口返回数据
				
					window.location.href = response.data;
					
				}, function(error) {
				
				})
			
			
			
		},

	}
</script>

