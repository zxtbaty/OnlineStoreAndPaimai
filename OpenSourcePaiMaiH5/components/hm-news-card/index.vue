<template>
  <div class="hm-news-card">
    <div class="container">
      <div class="box" @click="onClick">
        <img class="img" :src="options.img" />
        <span class="title">{{ options.title }}</span>
        <div class="submain">
          <span class="author">{{ options.author }}</span>
          <span class="date">{{ options.date }}</span>
        </div>
        <span class="summary">{{ options.summary }}</span>
        <div class="row_2" />
        <div class="ft">
          <span class="comments" v-show="options['showComments']">{{ options.comments }}</span>
          <span class="likes" v-show="options['showLikes']">{{ options.likes }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'HmNewsCard',
  props: {
    dataId: {
      type: String,
      default: 'hm-news-card'
    },
    options: {
      type: Object,
      default: function() {
        return {
          img: '/static/hm-news-card/images/img_22726_0_0.png',
          title: '新闻标题',
          author: '作者',
          date: '16 MAY 2016',
          summary:
            '新闻概要。新闻概要长度最好控制在50字符以内',
          comments: "14 评论",
          likes: "254 喜欢",
          url: '',
          showComments: true,
          showLikes: true
        };
      }
    }
  },
  data() {
    return {};
  },
  methods: {
    onClick() {
      uni.navigateTo({
        url: this.options.url
      });
    }
  }
};
</script>
<style>
@import './index.response.css';
</style>
