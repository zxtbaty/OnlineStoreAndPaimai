<template>
  <div>
    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-date-picker class="filter-item"  style="width: 200px"
                      v-model="listQuery.beginLockTime"
                      type="datetime"
                      placeholder="锁定起始日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-date-picker class="filter-item"  style="width: 200px"
                      v-model="listQuery.endLockTime"
                      type="datetime"
                      placeholder="锁定截至日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-select v-model="listQuery.lockType" clearable class="filter-item" style="width: 100px;" placeholder="锁定类型">
        <el-option  :key="'专场拍'" :label="'专场拍'" :value="'专场拍'"/>
        <el-option  :key="'大家拍'" :label="'大家拍'" :value="'大家拍'"/>
<!--        <el-option  :key="'私人定制'" :label="'私人定制'" :value="'私人定制'"/>-->
      </el-select>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 120px;" placeholder="商品名称"/>
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 120px;" placeholder="订单号"/>
      <el-select v-model="listQuery.ifUnlockIsNull" clearable class="filter-item" style="width: 100px;" placeholder="是否解锁">
        <el-option  :key="false" :label="'是'" :value="false"/>
        <el-option  :key="true" :label="'否'" :value="true"/>
        <!--        <el-option  :key="'私人定制'" :label="'私人定制'" :value="'私人定制'"/>-->
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>

    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="chargeMoneyLockList"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row >
      <el-table-column align="left" label="操作"  class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini"
                     @click="handleDetail(scope.row)">详情
          </el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="100" label="用户名" prop="userNickname"/>
      <el-table-column align="center" min-width="100" label="手机号" prop="userPhone"/>
      <el-table-column align="center" label="锁定时间" prop="lockTime"/>
      <el-table-column align="center" label="锁定类型" prop="lockType"/>
      <el-table-column align="center" label="商品名称" prop="goodsName"/>
      <el-table-column align="center" min-width="100" label="锁定金额" prop="lockMoney"/>
      <el-table-column align="center" min-width="100" label="解锁时间" prop="unlockTime"/>
      <el-table-column align="center" min-width="100" label="锁定转订单时间" prop="lockToPayTime"/>
      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>

    </el-table>

    <pagination v-show="listTotal>0" :total="listTotal" :page.sync="listQuery.page"
                :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

  </div>

</template>
<style scoped>
  .el-dialog__body {
    padding: 0px 20px;
    color: #606266;
    font-size: 14px;
  }
  .hot-header{
    display: flex;
    flex-flow: row nowrap;
    justify-content: space-between;
  }
  .left {
    height: 30px;
  }
  .right {
    height: 30px;
    line-height: 30px;
    font-size: 14px;
    color: #9E9E9E;
  }
  .customWidth {
    width: 850px;
  }
</style>
<script>

  import { listChargeMoneyLock} from '@/api/user'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'

  export default {

    name: 'UserChargeList',
    components: {Pagination,ViewOrderInfo},

    props:{
      inUserId:{
        type:Number,
        default:null,
      },
      inUnlockIsNull:{
        type:Boolean,
        default:null
      }
    },

    watch:{
      inUserId:{
        immediate:true,
        // deep:true,
        handler(val){
          this.listQuery.userId=val;
          this.getList();
        }
      },
      inUnlockIsNull:{
        immediate:true,
        // deep:true,
        handler(val){
          this.listQuery.ifUnlockIsNull=val;
          this.getList();
        }
      }
    },

    data() {
      return {
        orderDialogVisible: false, //是否显示订单详情
        orderDetailId:undefined,

        listLoading:false,
        chargeMoneyLockList:[],
        listTotal: 0, // 可推荐的数据列表总计
        listQuery:{
          userId:undefined,
          beginLockTime:undefined,
          endLockTime:undefined,
          lockType:undefined,
          ruleId:undefined,
          ruleName:undefined,
          orderNo:undefined,
          userPhone:undefined,
          username:undefined,
          ifUnlockIsNull:null,
          page: 1,
          limit: 10,
        },

      }
    },

    computed: {
      headers() {
        return {
          'X-Litemall-Admin-Token': getToken()
        }
      }
    },
    created() {
    },
    methods: {
      handleDetail(row) {
        this.orderDetailId=row.orderId;
        this.orderDialogVisible = true
      },
      handleGoodsFilter() {
        this.listQuery.page = 1
        this.getList()
      },

      getList() {
        this.chargeMoneyLockList=[];
        this.listLoading = true
        listChargeMoneyLock(this.listQuery).then(response => {
          this.chargeMoneyLockList = response.data.data.list
          this.listTotal = response.data.data.total
          this.listLoading = false
        }).catch(() => {
          this.chargeMoneyLockList = []
          this.listTotal = 0
          this.listLoading = false
        })
      },

    },

  }
</script>
