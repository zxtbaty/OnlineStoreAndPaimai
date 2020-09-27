<template>
	<view>
		<view  :style="{color:splitorColor,color:color }"
			  class="uni-countdown uni-countdown__splitor" >
			{{showText}}
		</view>
		<view class="uni-countdown" v-if="showDay&&d>0">
			<view	  
			  :style="{border:0,borderColor:borderColor, color:backgroundColor, background:color}"
			  class="uni-countdown__number">{{ d }}
			</view>
			<view
			  :style="{color:splitorColor,color:color }"
			  class="uni-countdown__splitor">天
			  </view>
		</view>
	  <view style="{line-height:35upx;padding:0;margin:0}" v-if="displayOneRow==false"></view>
	  <view class="uni-countdown">
 
		 <view
		  :style="{border:0, borderColor:borderColor, color:backgroundColor, background:color}"
		  class="uni-countdown__number">{{ h }}</view>
		<view
		  :style="{color:splitorColor}"
		  class="uni-countdown__splitor">{{ showColon ? ':' : '时' }}</view>
		<view
		  :style="{border:0,borderColor:borderColor, color:backgroundColor, background:color}"
		  class="uni-countdown__number">{{ i }}</view>
		<view
		  :style="{color:splitorColor}"
		  class="uni-countdown__splitor">{{ showColon ? ':' : '分' }}</view>
		<view
		  :style="{border:0,borderColor:borderColor, color:backgroundColor, background:color}"
		  class="uni-countdown__number">{{ s }}</view>
		<view
		  v-if="!showColon"
		  :style="{color:splitorColor}"
		  class="uni-countdown__splitor">秒
		</view>
	  </view>
  
  </view>  
</template>
<script>
export default {
  name: 'UniCountdown',
  props: {
	showText: {
      type: String,
      default: "距离结束"
    },
    showDay: {
      type: Boolean,
      default: true
    },
    showColon: {
      type: Boolean,
      default: true
    },
    backgroundColor: {
      type: String,
      default: '#FFFFFF',
	  
    },
    borderColor: {
      type: String,
      default: '#000000'
    },
    color: {
      type: String,
      // default: '#000000',
	  default:'red'
    },
    splitorColor: {
      type: String,
      // default: '#000000',
	  default:'red'
    },
    day: {
      type: Number,
      default: 0
    },
    hour: {
      type: Number,
      default: 0
    },
    minute: {
      type: Number,
      default: 0
    },
    second: {
      type: Number,
      default: 0
    },
	duration:{
		type:Number,
		default:0
	},
	displayOneRow:{
		type:Boolean,
		default:true
	},
  },
  data () {
    return {
      timer: null,
      d: '00',
      h: '00',
      i: '00',
      s: '00',
      leftTime: 0,
      seconds: 0
    }
  },
  created: function (e) {
    this.seconds = this.toSeconds(this.day, this.hour, this.minute, this.second,this.duration)
    this.countDown()
    this.timer = setInterval(() => {
      this.seconds--
      if (this.seconds < 0) {
        this.timeUp()
        return
      }
      this.countDown()
    }, 1000)
  },
  beforeDestroy () {
    clearInterval(this.timer)
  },
  methods: {
    toSeconds (day, hours, minutes, seconds,duration) {
	  if(duration>0){
		  return duration;
	  } else
	  {
		   return (day * 60 * 60 * 24) + (hours * 60 * 60) + (minutes * 60) + seconds
	  }
     
    },
    timeUp () {
      clearInterval(this.timer)
      this.$emit('timeup')
    },
    countDown () {
      let seconds = this.seconds
      let [day, hour, minute, second] = [0, 0, 0, 0]
      if (seconds > 0) {
        day = Math.floor(seconds / (60 * 60 * 24))
        hour = Math.floor(seconds / (60 * 60)) - (day * 24)
        minute = Math.floor(seconds / 60) - (day * 24 * 60) - (hour * 60)
        second = Math.floor(seconds) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60)
      } else {
        this.timeUp()
      }
      if (day < 10) {
        day = '0' + day
      }
      if (hour < 10) {
        hour = '0' + hour
      }
      if (minute < 10) {
        minute = '0' + minute
      }
      if (second < 10) {
		this.s = '0' + second
        //second = '0' + second
      } else
	  {
		  this.s =  second
	  }
      this.d = day
      this.h = hour
      this.i = minute
      // this.s = second
    }
  }
}
</script>
<style lang="scss">
	$countdown-height:35upx;

	.uni-countdown {
		margin: 0upx 0 0upx 0;
		font-size: 20upx;
		padding: 2upx 0;
		display: inline-flex;
		flex-wrap: nowrap;
		justify-content: center;

		&__splitor {
			justify-content: center;
			line-height: $countdown-height;
			padding: 0 0upx;
            font-size: $uni-font-size-base;
		}

		&__number {
			line-height: $countdown-height;
			justify-content: center;
			height: $countdown-height;
			border-radius: $uni-border-radius-base;
			margin: 0 0upx;
			font-size: $uni-font-size-base;
			border: 1px solid #000000;
			font-size: $uni-font-size-sm;
			padding: 0 10upx;
		}
	}
</style>
