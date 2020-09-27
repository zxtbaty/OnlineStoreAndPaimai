<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.wxNickname" clearable class="filter-item" style="width: 200px;" placeholder="微信名称"/>
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="收货人名称"/>
      <el-input v-model="listQuery.tel" clearable class="filter-item" style="width: 200px;" placeholder="手机号"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
<!--      <el-table-column align="center" width="100px" label="地址ID" prop="id" sortable/>-->
<!--      <el-table-column align="center" min-width="100px" label="用户ID" prop="userId"/>-->

      <el-table-column align="center" min-width="100px" label="微信名称" prop="wxNickname"/>
      <el-table-column align="center" min-width="100px" label="微信OpenId" prop="weixinOpenid"/>
      <el-table-column align="center" min-width="100px" label="CRM会员编号" prop="crmId"/>

      <el-table-column align="center" min-width="100px" label="收货人名称" prop="name"/>

      <el-table-column align="center" min-width="100px" label="手机号码" prop="tel"/>

      <el-table-column align="center" min-width="200px" label="区域地址">
        <template slot-scope="scope">
          {{ scope.row.provinceName + scope.row.cityName + scope.row.countryName }}
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="200px" label="详细地址门牌" prop="addressDetail">
      <template slot-scope="scope">
        {{scope.row.addressDetail + scope.row.area }}
      </template>
      </el-table-column>
      <el-table-column align="center"   label="默认" prop="isDefault">
        <template slot-scope="scope">
          {{ scope.row.isDefault ? '是' : '否' }}
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

  </div>
</template>

<script>
import { listAddress } from '@/api/user'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

export default {
  name: 'UserAddress',
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        userId: undefined,
        wxNickname: undefined,
        tel: undefined,
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
      listAddress(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
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
      this.$set(this.listQuery, "limit", 999999)
      listAddress(this.listQuery)
        .then(response => {
          var exportList = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['地址ID', '用户ID', '收获人', '手机号', '省', '市', '区', '地址', '是否默认']
            const filterVal = ['id', 'userId', 'name', 'tel', 'provinceName', 'cityName', 'countryName', 'addressDetail', 'isDefault']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '用户地址信息'
            )
            this.downloadLoading = false
          })
          this.downloadLoading = false
          this.$set(this.listQuery, "limit", 20)
        })
        .catch(() => {
          this.downloadLoading = false
          this.$set(this.listQuery, "limit", 20)
        })
    }
  }
}
</script>
