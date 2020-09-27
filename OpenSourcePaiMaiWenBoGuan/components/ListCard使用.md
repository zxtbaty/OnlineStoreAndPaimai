## list-card卡片

完全使用View布局的方式修饰，组件名:list-card,代码块: card

## 使用方法

在需要使用的页面中引入:
```vue
import card from '@/components/list-card/list-card.vue'
export default {
    components: {card}
}`
```

在视图中使用
```Vue
<card :cardinfo="cardinfo"></card>
```
## 属性说明

cardinfo(object)，需包含以下属性:

| 属性名     | 类型   | 说明     |
| ---------- | ------ | -------- |
| authImg    | string | 头像     |
| authName   | string | 昵称     |
| tag        | string | 标签     |
| createTime | string | 发布时间 |
| content    | string | 内容     |
| goodNum    | int    | 点赞数量 |
| likeNum    | int    | 喜欢数量 |
| commentNum | int    | 评论数量 |
| leave      | int    | 等级     |
属性示例：
```Vue
cardinfo: {
		"authImg": '图片地址', 
		"authName": "张三",
		"createTime": "2020-02-01",
		"content": " 内容",
		"goodNum": "100",
		"likeNum": "200",
		"commentNum": "500",
		"leave": 8,
        "tag":"每日随笔"
	       }
```

完整使用示例：
```Vue
<template>
	<div class="test-component">
		<card :cardinfo="cardinfo"></card>
		<card :cardinfo="cardinfo"></card>
		<card :cardinfo="cardinfo"></card>
		<card :cardinfo="cardinfo"></card>
		<card :cardinfo="cardinfo"></card>
	</div>
</template>

<script>
	import card from '@/components/list-card/list-card.vue'
	export default {
		components: {
			card
		},
		data() {
			return {
            cardinfo: {
		"authImg": ''图片地址', 
		"authName": "张三",
		"createTime": "2020-02-01",
		"content": " 内容",
		"goodNum": "100",
		"likeNum": "200",
		"commentNum": "500",
		"leave": 8,
        "tag":"每日随笔"
	       }
        };
		},
		methods: {
			onClick: function(e) {
				console.log('onClick');
			}
		},
	}
</script>

<style>

</style>

```