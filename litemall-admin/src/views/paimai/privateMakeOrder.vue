<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userPhone" clearable class="filter-item" style="width: 150px;" placeholder="请输入手机号码"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入订单编号"/>
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <!--<el-input v-model="listQuery.orderStatus" clearable class="filter-item" style="width: 150px;" placeholder="请输入订单状态"/>-->
      <el-input v-model="listQuery.username" clearable class="filter-item" style="width: 150px;" placeholder="请输入用户名称"/>
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
      <el-table-column align="center" label="订单编号" prop="orderSn"/>
      <el-table-column align="center" label="订单状态" prop="orderStatusName">
      </el-table-column>
      <el-table-column align="center" label="品项名称" prop="privateItemName">
      </el-table-column>
      <el-table-column align="center" label="交付时间" prop="privateDeliverDate">
      </el-table-column>
      <el-table-column align="center" label="内容描述" prop="privateContentDesc">
      </el-table-column>
      <el-table-column align="center" label="特别备注" prop="privateRemark">
      </el-table-column>
      <el-table-column align="center" label="订单时间" prop="addTime"/>
      <el-table-column align="center" label="保证金" prop="dingjinPrice"/>
      <el-table-column align="center" label="运费金额" prop="shipCost"/>
      <el-table-column align="center" label="开始时间" prop="beginTime"/>
      <el-table-column align="center" label="结束时间" prop="endTime"/>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
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
import { listPrivateMakeOrder } from '@/api/privateMakeOrder'

import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import * as constIndex from '@/utils/index.js'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'

export default {

  name: 'PrivateMakeOrder',
  components: { BackToTop, Pagination,ViewOrderInfo },
  props:{
    ruleId:{
      type:Number,
      default:null
    }
  },
  watch:{
    ruleId:{
      immediate:true,
      handler(val){
        this.list=[];
        this.total=0;
        this.listQuery.page=1;
        this.listQuery.rulesId=val;
        this.getList();
      }
    }
  },
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
        userPhone:undefined,
        goodsSn: undefined,
        goodsName: undefined,
        orderSn: undefined,
        rulesId:undefined,
        orderStatusArray: undefined,
        username: undefined,
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
      listPrivateMakeOrder(this.listQuery).then(response => {
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
      listPrivateMakeOrder(this.listQuery)
        .then(response => {
          var exportList = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['订单编号', '订单状态', '品项名称', '交付时间', '内容描述', '特别备注',
              '订单时间', '保证金', '运费金额', '开始时间', '结束时间']
            const filterVal = ['orderSn', 'orderStatusName', 'privateItemName', 'privateDeliverDate',
              'privateContentDesc', 'privateRemark', 'addTime', 'dingjinPrice', 'shipCost',
              'beginTime', 'endTime']
            excel.export_json_to_excel2(
              tHeader,
              exportList,
              filterVal,
              '私人定制订单信息'
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
