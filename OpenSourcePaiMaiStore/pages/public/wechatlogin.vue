<template>
	<view class="container">

	</view>
</template>
<script>
	import listCell from '@/components/mix-list-cell';

	import {
		mapState,
		mapMutations

	} from 'vuex';
	let startY = 0,
		moveY = 0,
		pageAtTop = true;
	export default {
		components: {
			listCell
		},
		data() {
			return {
				rurl:this.global.website+"/pages/index/index",
				crm:false
			}
		},
		async onLoad(option) {
			this.checklogin(option.state);
		},
		computed: {
			...mapState(['hasLogin', 'userInfo'])
		},
		methods: {
			...mapMutations(['login']),
			async checklogin(url){
					var that=this;
					console.log("state:"+url);
					if(url){
						let urlLink=url.split("?"); 
						that.rurl=urlLink[0];
						that.crm=urlLink[1].split("=")[1]
					}
					console.log("rurl:"+that.rurl);
					console.log("crm:"+that.crm);
						let name,value,str=location.href,num=str.indexOf("?"); //取得整个地址栏
						str=str.substr(num+1); //取得所有参数 stringvar.substr(start [, length ]
						let arr=str.split("&"); //各个参数放到数组里
						console.log(arr)
						let code;
						let crm;
						for(let i=0;i < arr.length;i++){
							num=arr[i].indexOf("=");
							if(num>0){
								name=arr[i].substring(0,num);
								value=arr[i].substr(num+1);
								console.log(value);
								if(name=="code"){
									code=value;
								}
								 
							}
						}
						
						if(code){
								//验证
								that.$http({ //调用接口
									method: 'GET',
									params:{
										code:code,
										crm:that.crm
									},
									url: that.global.target + "/wx/auth/newgetwechataccesstoken" //this指data
								}).then(function(response) { //接口返回数据
									var result = response.data;
									if(result.errno == 0){
										//登录成功
										console.log(result.data)
										that.login(result.data);
										location.href=that.rurl;
									}else{
										 // that.$api.msg(result.errmsg);
										 var url  = '/pages/index/index';
										 uni.switchTab({
										 	url
										})
									}
								}, function(error) {})
							
						}
				
				
			}
			
		}
	}
</script>

