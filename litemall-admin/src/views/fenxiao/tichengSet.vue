<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号"/>
      <el-input clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" >查找</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-edit" >添加</el-button>
      <el-button class="filter-item" type="primary" icon="el-icon-download" >导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table
      :data="tableData"
      row-key="id"
      size="small"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row>

      <el-table-column align="center" label="模板名" prop="name"/>

      <el-table-column align="center" min-width="100" label="说明" prop="remark"/>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" >编辑</el-button>
          <el-button type="danger" size="mini" >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>

  </div>
</template>

<script>
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'ArticleClassList',
  components: { BackToTop, Pagination },
  data() {
    return {
      tableData: [
        {
          id: 1,
          name: '小伙伴',
          remark: '商城公告'

        }, {
          id: 3,
          name: '合伙人',
          remark: '提成模板'

        }]
    }
  },
  methods: {
    load(tree, treeNode, resolve) {
      resolve([
        {
          id: 41,
          order: '1',
          name: '关于网站'
        }, {
          id: 42,
          order: '2',
          name: '购物指南'
        }, {
          id: 43,
          order: '3',
          name: '订单百事通'
        }, {
          id: 44,
          order: '4',
          name: '配送方式'
        }, {
          id: 45,
          order: '5',
          name: '支付问题'
        }, {
          id: 46,
          order: '6',
          name: '售后问题'
        }
      ])
    }
  }
}
</script>
