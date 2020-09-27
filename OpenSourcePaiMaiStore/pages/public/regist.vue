<template>
	<view class="container">
		<view class="left-bottom-sign"></view>
		<!-- <view class="back-btn yticon icon-zuojiantou-up" @click="navBack"></view> -->
		<view class="right-top-sign"></view>
		<!-- 设置白色背景防止软键盘把下部绝对定位元素顶上来盖住输入框等 -->
		<view class="wrapper">
			<!-- <view class="left-top-sign">regist</view>
			<view class="welcome">
				帐号注册！
			</view> -->
			<view class="login_top">
				<image src="../../static/newimg/loginbtn.png" class="airplane_img" mode=""></image>
				<view class="">
					欢迎注册会员中心！
				</view>
				<view class="Welcome">
					Welcome!
				</view>
			</view>
			<view class="input-content">
				<view class="input-item">
					<!-- <text class="tit">手机号码</text> -->
					<text class="tit">+86</text>
					<view class="arrow_img"></view>
					<input 
						type="number" 
						:value="mobile" 
						placeholder="请输入手机号码"
						maxlength="11"
						data-key="mobile"
						@input="inputChange"
					/>
				</view>
				<view class="input-item">
					<!-- <text class="tit">密码</text> -->
					<input 
						type="mobile" 
						value="" 
						placeholder="8-18位不含特殊字符的数字、字母组合"
						placeholder-class="input-empty"
						maxlength="20"
						password 
						data-key="password"
						@input="inputChange"
						@confirm="toLogin"
					/>
				</view>
				<button class="confirm-btn" @click="toRegist" :disabled="logining">立即注册</button>
			</view>
			
			<!-- <view class="forget-section">
				忘记密码?
			</view> -->
		</view>
		<!-- <view class="register-section">
			还没有账号?
			<text @click="toRegist">马上注册</text>
		</view> -->
		<view class="login_rule">
			您已同意掌上商城<text class="blue">用户协议、隐私政策</text>，并授权使用您的掌上商城账号信息（如昵称、头像、 收货地址）以便进行统一管理
		</view>
	</view>
</template>

<script>
	import {  
        mapMutations  
    } from 'vuex';
	
	export default{
		data(){
			return {
				mobile: '',
				password: '',
				logining: false
			}
		},
		onLoad(){
			
		},
		methods: {
			...mapMutations(['login']),
			inputChange(e){
				const key = e.currentTarget.dataset.key;
				this[key] = e.detail.value;
			},
			navBack(){
				uni.navigateBack();
			},
			// toRegist(){
			// 	this.$api.msg('去注册');
			// 	
			// },
			async toRegist(){
				this.logining = true;
				const {mobile, password} = this;
				/* 数据验证模块
				if(!this.$api.match({
					mobile,
					password
				})){
					this.logining = false;
					return;
				}
				*/
				const sendData = {
					mobile,
					password
				};
				var that = this;
				that.$http({           //调用接口
				    method:'POST',
					data:sendData,
				    url:this.global.target+"/wx/auth/register"  //this指data
				}).then(function(response){  //接口返回数据
					const result = response.data;
					console.log(result);
					if(result.errno == 0){
						//注册成功
						that.$api.msg('注册成功！');
					    uni.navigateBack();  
					}else{
						that.$api.msg(result.errmsg);
						that.logining = false;
					}
					
				},function(error){
				})
				
				
				
			}
		},

	}
</script>

<style lang='scss'>
	page{
		background: #fff;
	}
	.container {
		padding-top: 40upx;
		position: relative;
		width: 100vw;
		height: 100vh;
		overflow: hidden;
		background: url('../../static/newimg/login.png') no-repeat;
		background-size: 103% auto;
	}
	.wrapper{
		position:relative;
		z-index: 90;
		/* background: #fff; */
		padding-bottom: 40upx;
	}
	.login_top {
		position: relative;
		color: #fff;
		font-size: 40upx;
		padding: 69upx 0 0 27upx;
		box-sizing: border-box;
	
		.airplane_img {
			position: absolute;
			top: 0;
			right: 61upx;
			width: 417upx;
			height: 120upx;
		}
	
		.Welcome {
			margin: 23upx 0 0;
			font-weight: 100;
		}
	}
	.back-btn{
		position:absolute;
		left: 40upx;
		z-index: 9999;
		padding-top: var(--status-bar-height);
		top: 40upx;
		font-size: 40upx;
		color: $font-color-dark;
	}
	.left-top-sign{
		font-size: 120upx;
		color: $page-color-base;
		position:relative;
		left: -16upx;
	}
	.right-top-sign{
		position:absolute;
		top: 80upx;
		right: -30upx;
		z-index: 95;
		
		&:before{
			transform: rotate(50deg);
			border-radius: 0 50px 0 0;
		}
		&:after{
			position: absolute;
			right: -198upx;
			top: 0;
			transform: rotate(-50deg);
			border-radius: 50px 0 0 0;
			/* background: pink; */
		}
	}

	.welcome{
		position:relative;
		left: 50upx;
		top: -90upx;
		font-size: 46upx;
		color: #555;
		text-shadow: 1px 0px 1px rgba(0,0,0,.3);
	}
	.input-content{
		margin: 199upx auto 0;
		background: rgba(255, 255, 255, 1);
		box-shadow: 0px 0px 21upx 0px rgba(0, 0, 0, 0.1);
		border-radius: 5upx;
		padding: 60upx 92upx;
		width: 92.8%;
	}
	.input-item{
		display: flex;
		/* flex-direction: column; */
		align-items: center;
		justify-content: center;
		/* padding: 0 30upx; */
		/* background: $page-color-light; */
		height: 64upx;
		/* border-radius: 4px; */
		margin-bottom: 24upx;
		border-bottom: 1px solid #B4B4B4;
		&:last-child{
			margin-bottom: 0;
		}
		.tit{
			height: 50upx;
			line-height: 56upx;
			/* font-size: $font-sm+2upx; */
			font-size:44upx;
			/* margin:0 10upx 0 0; */
			/* color: $font-color-base; */
		}
		input{
			height: 60upx;
			font-size: 27upx;
			color: $font-color-dark;
			/* width: 100%; */
			flex:1;
		}	
		.arrow_img{
			width:25upx;
			height:25upx;
			background:url("../../static/newimg/arrow.png");
			background-size:100% 100%;
			margin:0 10upx 0 0;
		}
	}
	.confirm-btn{
		width: 100%;
		height: 73upx;
		line-height: 73upx;
		border-radius: 50px;
		margin-top: 53upx;
		background: linear-gradient(to right, #F3780E, #FDB64A);
		color: #fff;
		font-size: 27upx;
		&:after{
			border-radius: 100px;
		}
	}
	.forget-section{
		font-size: $font-sm+2upx;
		color: $font-color-spec;
		text-align: center;
		margin-top: 40upx;
	}
	.register-section{
		/* position:absolute;
		left: 0;
		bottom: 50upx; */
		width: 100%;
		font-size: $font-sm+2upx;
		color: $font-color-base;
		overflow: hidden;
		margin: 30upx 0 0;
		text{
			color: $font-color-spec;
			margin-left: 10upx;
		}
	}
	.login_rule {
		position: absolute;
		padding:0 27upx 65upx;
		bottom: 0;
		color: #B4B4B4;
		font-size: 17upx;
		.blue{
			color:#3ACEBF;
		}
	}
</style>
