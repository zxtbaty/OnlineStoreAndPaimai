<template>
  <div class="hm-news-detail">
    <div class="hd">
      <img class="yunshu" :src="options.yunshu" />
      <div class="container">
        <div class="outer">
          <span class="author">{{ options.author }}</span>
          <div class="wrap"  @click="logZan(options.id)">
			  <min-badge :count="options.zanCount" :maxCount="99">
			    <img class="like" style="width: 32px;height: 32px;" :src="options.like"  />
			  </min-badge>	   
            <!-- <img class="share" :src="options.share" /> -->
          </div>
        </div>
        <span class="time">{{ options.time }}</span>
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
import minBadge from '@/components/min-badge/min-badge'

export default {
  name: 'HmNewsDetail',
  components: {
  	minBadge
  },
  props: {
    dataId: {
      type: String,
      default: 'hm-news-detail'
    },
    options: {
      type: Object,
      default: function() {
        return {
		  id:'',
          yunshu:
            '/static/hm-news-detail/images/img_22946_0_0.png',
          author: '张三',
          like:
            '/static/hm-news-detail/images/img_22946_0_1.png',
          share:
            '/static/hm-news-detail/images/img_22946_0_2.png',
          time: '2 minutes ago',
          title: '如何用 Python 在笔记本上分析100GB 数据',
          img:
            '/static/hm-news-detail/images/img_22946_0_3.png',
          content:
            '第 1 种是对数据进行子抽样，但它有一个明显缺点：可能因忽略部分数据而错失关键信息，甚至误解数据表达的含义。第 2 种是使用分布式计算。虽然在某些情况下这是一种有效的方法，但是管理和维护集群会带来巨大开销。想象一下，要为一个刚超出内存大小、大概 30-50GB 的数据集就建立一套集群，对我来说，这似乎有点“用力过猛”。'
        };
      }
    }
  },
  data() {
    return {};
  },
  methods: {
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
  }
};
</script>
<style>
@import './index.response.css';
</style>
