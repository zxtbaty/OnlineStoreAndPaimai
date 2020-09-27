<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.wxNickname" clearable class="filter-item" style="width: 200px;" placeholder="请输入微信名称"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
<!--      <el-table-column align="center" width="100px" label="足迹ID" prop="id" sortable/>-->

      <el-table-column align="center" min-width="100px" label="微信名称" prop="wxNickname"/>
      <el-table-column align="center" min-width="100px" label="微信OpenId" prop="weixinOpenid"/>
      <el-table-column align="center" min-width="100px" label="CRM会员编号" prop="crmId"/>
      <el-table-column align="center" min-width="100px" label="商品编号" prop="goodsSn"/>
      <el-table-column align="center" min-width="100px" label="商品名称" prop="goodsName"/>

      <el-table-column align="center" min-width="100px" label="浏览时间" prop="addTime"/>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listFootprint } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'FootPrint',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        userId: undefined,
        wxNickname:undefined,
        goodsId: undefined,
        goodsSn:undefined,
        goodsName:undefined,
        sort: 'add_time desc',

      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listFootprint(this.listQuery)
        .then(response => {
          this.list = response.data.data.list
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleDownload() {
      this.downloadLoading = true
      this.$set(this.listQuery,"limit",999999)
      listFootprint(this.listQuery)
        .then(response => {
          var exportList = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['微信名称','微信OpenId','CRM会员编号','商品编号','商品名称','浏览时间','用户ID', '商品ID']
            const filterVal = ['wxNickname','weixinOpenid','crmId','goodsSn','goodsName','addTime','userId', 'goodsId']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '用户浏览足迹信息'
            )
            this.downloadLoading = false
          })
          this.downloadLoading = false
          this.$set(this.listQuery,"limit",20)
        })
        .catch(() => {
          this.downloadLoading = false
          this.$set(this.listQuery,"limit",20)
        })
    }
  }
}
</script>
