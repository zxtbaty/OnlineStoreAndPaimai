> 官网: [https://haomo-tech.com](https://haomo-tech.com)

> 作者: 毫末科技

> 邮箱: hxg@haomo-studio.com

## 预览

![预览图片](http://downloads.haomo-tech.com/uniapp/hm-news-detail.png)

[在线效果预览](http://template.uniapp.haomo-tech.com/pages/haomo/test-component/hm-news-detail)

更多毫末科技的uni-app跨端模板，请见[毫末科技uni-app跨端模板](https://haomo-tech.com/sale.html)

## 技术支持

* [uni-app插件市场](https://ext.dcloud.net.cn/plugin?id=1384)

* [npm包](https://www.npmjs.com/package/hm-uniapp-news-detail)

* [github地址](https://github.com/haomo-studio/hm-uniapp-news-detail)

* [gitee地址](https://gitee.com/haomo/hm-uniapp-news-detail)

毫末科技将长期迭代本组件。需要技术支持，请进钉钉群：

<img width="250" src="http://downloads.haomo-tech.com/%E6%AF%AB%E6%9C%ABuniapp%E7%BB%84%E4%BB%B6%E6%8A%80%E6%9C%AF%E6%94%AF%E6%8C%81.jpg">

## 概述

毫末uni-app新闻卡片组件。支持H5/小程序(微信、支付宝、头条、百度、QQ)/App；组件可自适应各种屏幕大小的手机。

## 使用

请使用HBuilderX导入组件。

在script中引用：

```javascript
import HmNewsCard from '@/components/hm-news-detail/index.vue'
export default {
    components: {HmNewsDetail}
}
```

在template中使用：

```html
<template>
  <div class="test-component">
    <hm-news-detail :options="options"></hm-news-detail>
  </div>
</template>
<script>
import HmNewsDetail from '@/components/hm-components/hm-news-detail/index.vue'

export default {
 components: {HmNewsDetail},
  data() {
    return {
      options: {
          yunshu:
            '/static/hm-news-detail/images/img_22946_0_0.png',
          author: '毫末科技',
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
        }
    };
  },
  methods: {
    
  }
};
</script>
<style>
</style>
```

## 属性说明

| 属性名        | 类型     | 默认值 | 说明                                                                       |
|-----------   |---------|--------|----------------------------------------------------------------------------|
| options        | Object  | -      | 卡片属性                                                                   |

options对象各个属性说明如下：

| 属性名        | 类型     | 默认值 | 说明                                                                       |
|-----------   |---------|--------|----------------------------------------------------------------------------|
| yunshu        | String  | -      | 头像图片                                                                   |
| author       | String  | -      | 作者                                                               |
| share         | String  | -      | 收藏图片                                                             |
| like          | String  | -      | 分享图片                                                             |
| time          | String  | -  | 新闻时间                                                     |
| title        | String  | -      | 标题文字                                                                   |
| img   | String  | -   | 新闻内容图片                                                     |
| content     | String | -  | 内容                                  |

## 事件说明


## 更新日志

### 0.0.1(2020-03-08)

* 完成第一个版本
