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

      <el-table-column align="center" label="姓名" prop="name"/>

      <el-table-column align="center" min-width="100" label="人数" prop="personCount"/>

      <el-table-column align="center" min-width="100" label="销售金额" prop="saleMoney"/>

      <el-table-column align="center" min-width="100" label="利润额" prop="profitMoney"/>

      <el-table-column align="center" min-width="100" label="订单数" prop="orderCount"/>

      <el-table-column align="center" min-width="100" label="提成模板" prop="modelName"/>

      <el-table-column align="center" min-width="100" label="注册时间" prop="regTime"/>

      <el-table-column align="center" min-width="100" label="提成金额" prop="tichengMoney"/>

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
          name: 'test1',
          personCount: 5,
          saleMoney: 10000,
          profitMoney: 1000,
          orderCount: 4,
          modelName: '小伙伴',
          regTime: '2019-04-12 16:15:46',
          tichengMoney: 5
        }, {
          name: 'test2',
          personCount: 5,
          saleMoney: 10000,
          profitMoney: 1000,
          orderCount: 4,
          modelName: '小伙伴',
          regTime: '2019-04-12 16:15:46',
          tichengMoney: 5

        }, {
          name: 'test3',
          personCount: 5,
          saleMoney: 10000,
          profitMoney: 1000,
          orderCount: 4,
          modelName: '小伙伴',
          regTime: '2019-04-12 16:15:46',
          tichengMoney: 5
        }]
    }
  },
  methods: {

  }
}
</script>
