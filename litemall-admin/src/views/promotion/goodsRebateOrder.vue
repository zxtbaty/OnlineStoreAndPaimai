<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 110px;" placeholder="商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 110px;" placeholder="商品名称"/>
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 110px;" placeholder="订单编号"/>
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 150px" class="filter-item" placeholder="订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <!--<el-input v-model="listQuery.orderStatus" clearable class="filter-item" style="width: 150px;" placeholder="请输入订单状态"/>-->
      <el-input v-model="listQuery.userNickname" clearable class="filter-item" style="width: 110px;" placeholder="用户名称"/>
      <el-input v-model="listQuery.userPhone" clearable class="filter-item" style="width: 110px;" placeholder="订购电话"/>
      <el-button v-permission="['GET /admin/seckillorders/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload">导出
      </el-button>
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      size="small"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row>
      <el-table-column align="left" label="操作"  class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini"
                     @click="handleViewDetail(scope.row.orderId)">详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="活动名称" prop="name"/>
      <el-table-column align="center" label="用户名称" prop="userNickname"/>
      <el-table-column align="center" label="订单ID" prop="orderId"/>
      <el-table-column align="center" label="订单编号" prop="orderSn"/>
      <el-table-column align="center" label="订单状态" prop="orderStatusName">
      </el-table-column>
      <el-table-column align="center" label="订单时间" prop="addTime"/>
      <el-table-column align="center" label="商品编号" prop="goodsSn"/>
      <el-table-column align="center" label="商品名称" prop="goodsName"/>
      <el-table-column align="center" label="商品规格" prop="goodsProductSpecifications"/>
      <el-table-column align="center" label="原价" prop="sourcePrice"/>
      <el-table-column align="center" label="折后价" prop="rebatePrice"/>
      <el-table-column align="center" label="开始时间" prop="beginDate"/>
      <el-table-column align="center" label="结束时间" prop="endDate"/>
    </el-table>



    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


    <!-- 查询订单 -->
    <el-dialog :title="'订单详情'" customClass="customWidth" :close-on-click-modal="false"
               :visible.sync="orderFormVisible">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

  </div>
</template>

<style>
  .table-expand {
    font-size: 0;
  }

  .table-expand label {
    width: 100px;
    color: #99a9bf;
  }

  .table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
  }

  .gallery {
    width: 80px;
    margin-right: 10px;
  }
</style>

<script>
import { listGoodsRebateOrder,createGoodsRebateOrder,editGoodsRebateOrder,deleteGoodsRebateOrder } from '@/api/goodsRebateOrder'
import * as constIndex from '@/utils/index.js'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'

export default {
  name: 'GoodsRebateOrder',
  components: { BackToTop, Pagination,ViewOrderInfo },
  data() {
    return {
      statusMap:constIndex.statusMap,
      orderDetailId:undefined,
      orderFormVisible:false,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        goodsSn: undefined,
        goodsName: undefined,
        orderSn: undefined,
        orderStatusArray: undefined,
        username: undefined,
        userNickname: undefined,
        userPhone: undefined,
        sort: 'add_time desc',

      },
      goodsDetail: '',
      detailDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    handleViewDetail(orderId){
      this.orderDetailId=orderId;
      this.orderFormVisible=true;
    },
    getList() {
      this.listLoading = true
        listGoodsRebateOrder(this.listQuery).then(response => {
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
      listGoodsRebateOrder(this.listQuery)
        .then(response => {
          var exportList = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['活动名称', '用户名称', '订单ID', '订单编号','订购电话', '订单时间', '商品编号', '商品名称','商品规格',
              '原价', '团购价', '开始时间', '结束时间']
            const filterVal = ['name', 'userNickname', 'orderId', 'orderSn','userPhone', 'addTime', 'goodsSn', 'goodsName', 'goodsProductSpecifications',
              'sourcePrice','rebatePrice', 'beginDate', 'endDate']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '品项折扣订单信息'
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
