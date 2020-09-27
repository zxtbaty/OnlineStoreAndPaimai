<template>
  <div class="app-container">

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog :visible.sync="shipDialogVisible" title="发货" :close-on-click-modal="false">
      <el-form ref="shipForm" :model="shipForm" status-icon  label-width="100px" >
        <el-form-item label="快递公司" prop="shipChannel">
          <el-input v-model="shipForm.shipChannel"/>
        </el-form-item>
        <el-form-item label="快递单号" prop="shipSn">
          <el-input v-model="shipForm.shipSn"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" title="退款" :close-on-click-modal="false">
      <el-form ref="refundForm" :model="refundForm" status-icon  label-width="100px" >


        <el-select v-model="refundForm.returnType" clearable class="filter-item" style="width: 80px;" placeholder="退款类型">
          <el-option :key="整单退款" :label="'整单退款'" :value="整单退款"/>
          <el-option :key="部分退款" :label="'部分退款'" :value="部分退款"/>
        </el-select>

        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="refundForm.returnType=='整单退款'"/>
        </el-form-item>

        <el-form-item label="客户退款原因" prop="refundMoney" >
          <el-input v-model="refundForm.returnFrontreason" :disabled="true" />
        </el-form-item>

        <el-form-item label="审核退款原因" prop="refundMoney">
          <el-input v-model="refundForm.returnReason"  />
        </el-form-item>

        <el-form-item label="退款操作员" prop="refundMoney">
          <el-input v-model="refundForm.returnOp"  />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>


    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <!--<el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>-->
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 200px;" placeholder="请输入订单编号"/>
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 200px;" placeholder="收货用户名称"/>
      <el-select v-model="listQuery.orderStatusArray" multiple style="width: 200px" class="filter-item" placeholder="请选择订单状态">
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">导出</el-button>
    </div>

    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>

      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>

      <el-table-column align="center" label="用户ID" prop="userId"/>

      <el-table-column align="center" label="订单状态" prop="orderStatusName">

      </el-table-column>

      <el-table-column align="center" label="订单金额" prop="orderPrice"/>

      <el-table-column align="center" label="支付金额" prop="actualPrice"/>

      <el-table-column align="center" label="支付时间" prop="payTime"/>

      <el-table-column align="center" label="物流单号" prop="shipSn"/>

      <el-table-column align="center" label="物流渠道" prop="shipChannel"/>

      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-permission="['POST /admin/order/ship']" v-if="scope.row.orderStatus==201" type="primary" size="mini" @click="handleShip(scope.row)">发货</el-button>
          <el-button v-permission="['POST /admin/order/refund']" v-if="scope.row.orderStatus==202" type="primary" size="mini" @click="handleRefund(scope.row)">退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />



  </div>
</template>

<style>

</style>

<script>
import { listOrder, shipOrder, refundOrder, detailOrder } from '@/api/order'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import checkPermission from '@/utils/permission' // 权限判断函数
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
import * as constIndex from '@/utils/index.js'

export default {

  name: 'Order',
  components: { Pagination,ViewOrderInfo },

  data() {
    return {
      statusMap:constIndex.statusMap,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        consignee:undefined,
        sourceCode:10,
        typeCode:10,
        orderStatusArray: [],
        orderType:"电商",
        sort: 'add_time desc',
      },

      orderDetailId:undefined,
      orderDialogVisible: false,
      orderDetail: {
        order: {},
        user: {},
        orderGoods: []
      },
      shipForm: {
        orderId: undefined,
        shipChannel: undefined,
        shipSn: undefined
      },
      shipDialogVisible: false,
      refundForm: {
        orderId: undefined,
        refundMoney: undefined,
        returnFrontreason:undefined,
        returnReason:undefined,
        returnType:'整单退款',
        returnOp:this.$store.getters.name,
      },
      refundDialogVisible: false,
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    checkPermission,

    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(response => {
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
    handleDetail(row) {
      // detailOrder(row.id).then(response => {
      //   this.orderDetail = response.data.data
      // })
      this.orderDetailId=row.id;
      this.orderDialogVisible = true
    },
    handleShip(row) {
      this.shipForm.orderId = row.id
      this.shipForm.shipChannel = row.shipChannel
      this.shipForm.shipSn = row.shipSn

      this.shipDialogVisible = true
      this.$nextTick(() => {
        this.$refs['shipForm'].clearValidate()
      })
    },
    confirmShip() {
      this.$refs['shipForm'].validate((valid) => {
        if (valid) {
          shipOrder(this.shipForm).then(response => {
            this.shipDialogVisible = false
            this.$message.success('确认发货成功');

            this.getList()
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
    handleRefund(row) {
      this.refundForm.orderId = row.id
      this.refundForm.refundMoney = row.actualPrice

      this.refundDialogVisible = true
      this.$nextTick(() => {
        this.$refs['refundForm'].clearValidate()
      })
    },
    confirmRefund() {
      this.$refs['refundForm'].validate((valid) => {
        if (valid) {
          refundOrder(this.refundForm).then(response => {
            this.refundDialogVisible = false
            this.$message.success('确认退款成功');

            this.getList()
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['订单ID', '订单编号', '用户ID', '订单状态', '是否删除', '收货人', '收货联系电话', '收货地址']
        const filterVal = ['id', 'orderSn', 'userId', 'orderStatus', 'isDelete', 'consignee', 'mobile', 'address']
        excel.export_json_to_excel2(tHeader, this.list, filterVal, '订单信息')
        this.downloadLoading = false
      })
    },
    createOrder(){

    }
  }
}
</script>
