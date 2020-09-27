> 官网: [https://haomo-tech.com](https://haomo-tech.com)

> 作者: 毫末科技

> 邮箱: hxg@haomo-studio.com

## 预览

![预览图片](http://downloads.haomo-tech.com/uniapp/hm-news-card.png)

[在线效果预览](http://template.uniapp.haomo-tech.com/pages/haomo/test-component/hm-news-card)

## 技术支持

* [uni-app插件市场]()

* [npm包](https://www.npmjs.com/package/hm-news-card)

* [github地址](https://github.com/haomo-studio/hm-uniapp-news-card)

毫末科技将长期迭代本组件。需要技术支持，请进钉钉群：

<img width="250" src="http://downloads.haomo-tech.com/%E6%AF%AB%E6%9C%ABuniapp%E7%BB%84%E4%BB%B6%E6%8A%80%E6%9C%AF%E6%94%AF%E6%8C%81.jpg">

## 概述

毫末uni-app新闻卡片组件。支持H5/小程序(微信、支付宝、头条、百度、QQ)/App；组件可自适应各种屏幕大小的手机。

## 使用

请使用HBuilderX导入组件。

在script中引用：

```javascript
import HmNewsCard from '@/components/hm-news-card/index.vue'
export default {
    components: {HmNewsCard}
}
```

在template中使用：

```html
<template>
  <div class="test-component">
    <hm-news-card :options="options" @click="onClick"></hm-news-card>
  </div>
</template>
<script>
import HmNewsCard from '@/components/hm-components/hm-news-card/index.vue'

export default {
  components: {HmNewsCard},
  data() {
    return {
      options: {
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
        }
    };
  },
  methods: {
    onClick: function(e) {
      console.log('onClick');
    }
  }
};
</script>
<style>
</style>
```

详细用法请见[组件Storyboard](http://)

## 属性说明

| 属性名        | 类型     | 默认值 | 说明                                                                       |
|-----------   |---------|--------|----------------------------------------------------------------------------|
| options        | Object  | -      | 卡片属性                                                                   |

options对象各个属性说明如下：

| 属性名        | 类型     | 默认值 | 说明                                                                       |
|-----------   |---------|--------|----------------------------------------------------------------------------|
| title        | String  | -      | 标题文字                                                                   |
| author       | String  | -      | 作者                                                               |
| summary      | String  | -      | 摘要                                                                   |
| date         | String  | -      | 日期                                                             |
| img          | String  | -  | 新闻图片                                                     |
| showComments| Boolean  | true  | 是否显示评论数                                                     |
| showLikes   | Boolean  | true  | 是否显示喜欢数                                                     |
| comments     | String | -  | 评论                                  |
| likes        | String | -  | 喜欢                                                       |

## 事件说明

| 事件称名   | 事件说明           | 返回参数 |
|----------|--------------------|----------|
| @click   | 点击 Card 触发事件 | -        |

## 更新日志

### 0.0.1(2020-03-07)

* 完成第一个版本
