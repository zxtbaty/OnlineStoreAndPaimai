<template>
  <div class="app-container">

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog :visible.sync="shipDialogVisible" customClass="customWidth"  title="发货" :close-on-click-modal="false">
      <el-form ref="shipForm" :model="shipForm" status-icon  label-width="100px" style="width: 250px; margin-left:10px;">
        <el-form-item label="订单编号" prop="orderSn">
          <el-input v-model="shipForm.orderSn" style="width:200px;" :disabled="true"  />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="shipForm.address" style="width:200px;" :disabled="true"  />
        </el-form-item>
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="shipForm.consignee" style="width:200px;" :disabled="true"  />
        </el-form-item>
        <el-form-item label="快递公司" prop="shipChannel">
          <el-select clearable  class="filter-item"  v-model="shipForm.shipChannel" style="width:200px;"   placeholder="请选择快递公司">
            <el-option  v-for=" item in kuaiDiComs" :value="item.name"  :key="item.name" :label="item.name">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" prop="shipSn">
          <el-input v-model="shipForm.shipSn" style="width:200px;"   />
        </el-form-item>
      </el-form>
      <div slot="footer" >
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </div>
    </el-dialog>


    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <!--<el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>-->
      <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 120px;" placeholder="订单编号"/>
      <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 120px;" placeholder="收货人名称"/>
      <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>

    </div>
    <!-- 查询结果 -->
    <el-table v-loading="listLoading" :data="list" element-loading-text="正在查询中。。。" border fit highlight-current-row>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form  class="table-expand">

            <el-col :span="6" >
              <el-form-item label="商品总费用">
                <span>{{ props.row.goodsPrice }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="配送费用">
                <span>{{ props.row.freightPrice }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="优惠券减免">
                <span>{{ props.row.couponPrice }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="用户积分减免">
                <span>{{ props.row.integralPrice }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="订单费用">
                <span>{{ props.row.orderPrice }}</span>
              </el-form-item>
            </el-col>

            <el-col :span="6" >
              <el-form-item label="实付费用">
                <span>{{ props.row.actualPrice }}</span>
              </el-form-item>
            </el-col>

            <el-col :span="24" >
              <el-form-item label="收货具体地址">
                <span>{{ props.row.address }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="收货人名称">
                <span>{{ props.row.consignee }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="收货人手机号">
                <span>{{ props.row.mobile }}</span>
              </el-form-item>
            </el-col>

            <el-col :span="24" >
              <el-form-item label="用户订单留言">
                <span>{{ props.row.message }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="下单时间">
                <span>{{ props.row.addTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="付款时间">
                <span>{{ props.row.payTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="发货时间">
                <span>{{ props.row.shipTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="收货时间">
                <span>{{ props.row.confirmTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="取消时间">
                <span>{{ props.row.cancelTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="申请退款时间">
                <span>{{ props.row.returnTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="收到退货时间">
                <span>{{ props.row.returnGoodsTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="6" >
              <el-form-item label="退款完成时间">
                <span>{{ props.row.returnPayTime }}</span>
              </el-form-item>
            </el-col>

          </el-form>
        </template>
      </el-table-column>

      <el-table-column align="left" label="操作" width="80" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/order/ship']" v-if="scope.row.orderStatus>=201" type="primary" size="mini" @click="handleShip(scope.row)">发货</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>

      <el-table-column align="center" label="地址" prop="address"/>
      <el-table-column align="center" label="收货人" prop="consignee"/>

<!--      <el-table-column align="left" label="操作" width="120" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini" @click="handleDetail(scope.row)">详情</el-button>-->
<!--        </template>-->
<!--      </el-table-column>-->

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

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
  .customWidth {
    width: 350px;
  }
</style>

<script>
  import { listOrder, beiHuoOrder,shipOrder, refundOrder,setRefundStatus, detailOrder,updateFapiaoStatus,getFapiaoDetail,printWaiJie,printOutPdf } from '@/api/order'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import checkPermission from '@/utils/permission' // 权限判断函数
  import Qs from 'qs'
  import requestblob from '@/utils/requestblob'
  import { listDiccode } from '@/api/diccode'
  import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
  import * as constIndex from '@/utils/index.js'

  export default {

    name: 'OrderMobileSendLog',
    components: { Pagination,ViewOrderInfo },

    data() {
      return {
        statusMap:constIndex.statusMap,
        checkBoxSelOrderListData:[],//选择的订单数据
        list: [],
        total: 0,
        kuaiDiComs:[],
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
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          orderType:"电商",
          sort: 'add_time desc',
        },

        orderDialogVisible: false,
        orderDetailId:undefined,
        orderDetail: {
          order: {},
          fapiao:{},//发票
          coupon:{},//优惠券
          user: {},
          orderGoods: []
        },
        shipForm: {
          orderId: undefined,
          shipChannel: "顺丰",
          shipSn: undefined,
          consignee: undefined,
          address: undefined,
          orderSn: undefined,
        },
        shipDialogVisible: false,

        downloadLoading: false,

      }
    },
    created() {

       this.getList()

      this.getKuaiDiComs()

    },

    watch: {

    },
    methods: {
      checkPermission,
      getKuaiDiComs(){
        listDiccode({dicmainName:"快递承运商"}).then(response => {
          this.kuaiDiComs = response.data.data.list
            // 晕，搞了好长时间，重新赋值，才会刷新页面
            console.log(this.shipForm.shipChannel)
            if(this.kuaiDiComs.length>0){
                this.shipForm.shipChannel=this.kuaiDiComs[0].name
            }

        }).catch(() => {
          this.kuaiDiComs = []
        })
      },


      setOrderStatusArray(){
          if(this.listQuery.orderStatusArray==null||this.listQuery.orderStatusArray.length==0){
               this.listQuery.orderStatusArray.push("250")
          }
      },

      getList() {
        this.listLoading = true
        this.setOrderStatusArray()
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
        //   console.log(this.orderDetail)
        // })
        this.orderDetailId=row.id;
        this.orderDialogVisible = true
      },

      handleShip(row) {
        this.shipForm.orderId = row.id
        this.shipForm.shipChannel = row.shipChannel
        if(this.shipForm.shipChannel==null){
            this.shipForm.shipChannel=this.kuaiDiComs[0].name
        }

        this.shipForm.shipSn = row.shipSn
        this.shipForm.orderSn = row.orderSn
        this.shipForm.consignee = row.consignee
        this.shipForm.address = row.address
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


    }
  }
</script>
