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
          <el-select clearable  class="filter-item"  v-model="shipForm.shipChannel" style="width:300px;"   placeholder="请选择快递公司">
            <el-option  v-for=" item in kuaiDiComs" :value="item.name"  :key="item.name" :label="item.name">
            </el-option>
          </el-select>
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

    <!-- 订单备注对话框 -->
    <el-dialog :visible.sync="orderRemarkDialogVisible" title="订单备注对话框" :close-on-click-modal="false">
      <el-form ref="orderRemarkForm" :model="orderRemarkForm"  status-icon  label-width="100px" >
        <el-form-item label="订单处理备注" prop="logContent">
          <el-input type="textarea"
                    :rows="5" style="width:550px" v-model="orderRemarkForm.dealRemark"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="updateOrderDealRemarkExec">确定</el-button>
      </div>
    </el-dialog>

    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" title="退款" :close-on-click-modal="false">
      <el-form ref="refundForm" :rules="refundRules" :model="refundForm" status-icon  label-width="120px" >
        <el-form-item label="退款类型" prop="returnType">
          <el-select v-model="refundForm.returnType"   style="width: 200px;" placeholder="退款类型">
            <el-option :key="'整单退款'" :label="'整单退款'" :value="'整单退款'"/>
            <el-option :key="'部分退款'" :label="'部分退款'" :value="'部分退款'"/>
          </el-select>
        </el-form-item>
        <el-form-item label="退款金额" prop="returnPartRemark" v-if="refundForm.returnType=='部分退款'">
          <el-input v-model="refundForm.returnPartRemark" style="width: 200px;"  />
        </el-form-item>

        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" style="width: 200px;"  :disabled="refundForm.returnType=='整单退款'"/>
        </el-form-item>

        <el-form-item label="客户退款原因" prop="refundMoney" >
          <el-input v-model="refundForm.returnFrontreason" style="width: 200px;"  :disabled="true" />
        </el-form-item>

        <el-form-item label="审核退款原因" prop="returnReason">
          <el-select clearable    v-model="refundForm.returnReason" style="width:200px;"   placeholder="请选择退款原因">
            <el-option  v-for=" item in returnReasons" :value="item.name"  :key="item.name" :label="item.name">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="退款备注" prop="returnPartRemark">
          <el-input v-model="refundForm.returnPartRemark" style="width:200px;"  />
        </el-form-item>
        <el-form-item label="退款操作员" prop="returnOp">
          <el-input v-model="refundForm.returnOp" style="width:200px;"  :disabled="true" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="notAllowRefund">不允许退款</el-button>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>

    <!-- 开票对话框 -->
    <el-dialog :visible.sync="faPiaoFormDialogVisible" title="开票" :close-on-click-modal="false">
      <el-row>
      <el-form ref="fapiaoForm"  :model="faPiaoForm" status-icon  label-width="100px" >
        <el-col :span="12">
          <el-form-item label="发票类型" prop="fapiaoType">
            <el-input style="width: 200px" v-model="faPiaoForm.fapiaoType" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="单位名称" prop="name">
            <el-input style="width: 200px" v-model="faPiaoForm.name" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="纳税人识别号" prop="taxno">
            <el-input style="width: 200px" v-model="faPiaoForm.taxno" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="注册地址" prop="address">
            <el-input style="width: 200px" v-model="faPiaoForm.address" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="注册电话" prop="telphone">
            <el-input style="width: 200px" v-model="faPiaoForm.telphone" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="开户银行" prop="bank">
            <el-input style="width: 200px" v-model="faPiaoForm.bank" :disabled="true"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="银行帐号" prop="bankNo">
            <el-input style="width: 200px" v-model="faPiaoForm.bankNo" :disabled="true" />
          </el-form-item>
        </el-col>
        <el-col :span="12" >
          <el-form-item label="开票金额" prop="money">
            <el-input style="width: 200px" v-model="faPiaoForm.money" :disabled="true" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="开票状态" prop="status">
            <el-input style="width: 200px" v-model="faPiaoForm.status" :disabled="true" />
          </el-form-item>
        </el-col>

      </el-form>

      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="faPiaoFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFapiao">开票确定</el-button>
      </div>
    </el-dialog>

        <el-button-group  style=" margin-bottom: 10px" >
          <el-button @click="displayAllDan" :type="buttonAllDan"  :autofocus="true">全部单</el-button>
          <el-button @click="displayDaiZhiFu" :type="buttonDaiZhifu" >待支付</el-button>
          <el-button @click="displayDaiBeiHuo" :type="buttonDaiBeiHuo" >待备货</el-button>
          <el-button @click="displayDaiFaHuo" :type="buttonDaiFaHuo"  >待发货</el-button>
          <el-button @click="displayDaiShouHuo"  :type="buttonDaiShouHuo" >待收货</el-button>
          <el-button @click="displayYiShouHuo" :type="buttonYiShouHuo"  >已收货</el-button>
          <el-button @click="displayYiQuXiao" :type="buttonYiQuXiao"  >已取消</el-button>
          <el-button @click="displayDaiTuiKuan" :type="buttonDaiTuiKuan"  >待退款</el-button>
          <el-button @click="displayYiTuiKuan" :type="buttonYiTuiKuan"  >已退款</el-button>
        </el-button-group>
        <!-- 查询和其他操作 -->
        <div class="filter-container">
          <el-input v-model="listQuery.phone" clearable class="filter-item" style="width: 150px;" placeholder="电话号码"/>
          <el-input v-model="listQuery.orderSn" clearable class="filter-item" style="width: 120px;" placeholder="订单编号"/>
          <el-input v-model="listQuery.consignee" clearable class="filter-item" style="width: 120px;" placeholder="收货人名称"/>
          <el-select v-model="listQuery.orderStatusArray" multiple style="width: 100px" class="filter-item" placeholder="订单状态" @change="changeOrderStatus">
            <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
          </el-select>
          <el-select v-model="listQuery.sendWay" clearable="" style="width: 120px" class="filter-item" @change="changeSendWay" placeholder="配送方式">
            <el-option  :key="'快递'" :label="'快递'" :value="'快递'"/>
            <el-option  :key="'自提取货'" :label="'自提取货'" :value="'自提取货'"/>
          </el-select>
          <el-select v-if="listQuery.sendWay==undefined||listQuery.sendWay==''||listQuery.sendWay=='自提取货'" v-model="listQuery.pickSiteIdArray" multiple clearable style="width: 100px" class="filter-item" placeholder="提货点">
            <el-option v-for="item in listPickSite" :key="item.id" :label="item.siteName" :value="item.id"/>
          </el-select>

          <el-select v-model="listQuery.ifFapiao" clearable  style="width: 100px" class="filter-item" placeholder="是否开票">
            <el-option  :key="'true'" :label="'是'" :value="'true'"/>
            <el-option  :key="'false'" :label="'否'" :value="'false'"/>
          </el-select>
          <el-select v-model="listQuery.fapiaoStatus" clearable="" style="width: 100px" class="filter-item" placeholder="开票状态">
            <el-option  :key="'已开'" :label="'已开'" :value="'已开'"/>
            <el-option  :key="'未开'" :label="'未开'" :value="'未开'"/>
          </el-select>
          <el-date-picker class="filter-item" style="width: 200px"
                            v-model="listQuery.beginDate"
                            type="datetime"
                            placeholder="起始日期"
                            value-format="yyyy-MM-dd HH:mm:ss"/>
          <el-date-picker class="filter-item" style="width: 200px"
                          v-model="listQuery.endDate"
                          type="datetime"
                          placeholder="截止日期"
                          value-format="yyyy-MM-dd HH:mm:ss"/>

          <el-select v-model="listQuery.payMethod" clearable="" style="width: 120px" class="filter-item" placeholder="支付方式">
            <el-option  :key="'1'" :label="'支付宝'" :value="'1'"/>
            <el-option  :key="'2'" :label="'微信'" :value="'2'"/>
            <el-option  :key="'3'" :label="'0支付'" :value="'3'"/>
          </el-select>
          <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
          <el-dropdown class="filter-item">
            <el-button type="primary">
              数据导出<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-button :loading="excelDownloadLoading_All" class="filter-item" type="success" @click="handleDownloadAllMain">导出主单</el-button>
              <el-button :loading="excelDownloadLoading_Mx" class="filter-item" type="success" @click="handleDownloadAllDetail">导出明细</el-button>
              <el-button :loading="excelDownloadLoading_WaiJie" class="filter-item" type="success" @click="exportExcelListData">导出外借单</el-button>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button :loading="batchBeihuoloadLoading_All"
                     v-if="btnBatchConfirm==true" class="filter-item" type="primary"  @click="handleBatchBeihuoConfirm(null)">批量备货确认</el-button>
          <el-button :loading="pdfDownloadLoading_All" class="filter-item" type="primary" icon="el-icon-edit-outline" @click="handlePdf(null)">打印出货单</el-button>

        </div>
        <!-- 查询结果 -->
        <el-table v-loading="listLoading"
                  :data="list" element-loading-text="正在查询中。。。"
                  border fit highlight-current-row
                  @selection-change="changeSelOrderList">
          <el-table-column type="selection"/>
          <el-table-column align="center" label="操作" width="180" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button-group style="display: flex;justify-content: center" >
                <el-button type="primary" icon="el-icon-info"
                           style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                           title="详情" @click="handleDetail(scope.row)"></el-button>
                <el-button v-if="scope.row.orderStatus==201" type="primary" icon="el-icon-s-home"
                           style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                           title="已备货" @click="confirmBeihuo(scope.row.id)"></el-button>
                <el-button v-if="scope.row.orderStatus==250||scope.row.orderStatus==301" type="primary" icon="el-icon-truck"
                           style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                           title="发货" @click="handleShip(scope.row)"></el-button>

                <el-button   type="primary" v-if="scope.row.orderStatus>=201" icon="el-icon-printer"
                             style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px" :loading="pdfDownloadLoading_All"
                             title="出货单" @click="handlePdf(scope.row.id)"></el-button>
                <el-button  type="primary"   v-if="scope.row.ifFapiao==true&&scope.row.fapiaoStatus=='未开'" icon="el-icon-tickets"
                            style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                            title="开发票" @click="handleFapiao(scope.row)"></el-button>
                <el-button type="primary" v-if="scope.row.orderStatus==202||(scope.row.orderStatus!=203&&scope.row.adminAllowRefund==true)" icon="el-icon-delete-location"
                           style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                           title="退款" @click="handleRefund(scope.row)"></el-button>
                <el-button v-if="scope.row.orderStatus>=250&&scope.row.adminAllowRefund==false" type="primary"  icon="el-icon-sunny"
                           style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                           title="强制退款"  @click="handleSetRefundStatus(scope.row)"></el-button>
                <el-button  type="primary"  icon="el-icon-check"
                            style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                            title="验证支付" @click="handleVerifyCheckPay(scope.row)"></el-button>
                <el-button  type="primary"  icon="el-icon-star-off"
                            style="width: 24px;height: 24px;align-content: center;padding: 4px;margin-right:5px"
                            title="订单备注" @click="updateOrderDealRemark(scope.row)"></el-button>
              </el-button-group>
            </template>
          </el-table-column>
          <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>
          <el-table-column align="center" min-width="90" label="订单状态" prop="orderStatusName">
          </el-table-column>
          <el-table-column align="center" label="配送方式" prop="sendWay"/>
          <el-table-column align="center" label="支付方式" prop="payMethodName"/>
          <el-table-column align="center" label="支付金额" prop="actualPrice"/>
          <el-table-column align="center" label="支付时间" :formatter="dateForma"   prop="payTime"/>
          <el-table-column align="center" label="申请退款时间" :formatter="dateForma" prop="returnTime"/>
          <el-table-column align="center" label="物流单号" prop="shipSn"/>
          <el-table-column align="center" label="物流渠道" prop="shipChannel"/>

        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


  </div>

</template>


<script>
  import { listOrder,listBySql,listOrderDetail, beiHuoOrder,beiHuoBatchConfirmOrder,shipOrder, notRefundOrder,refundOrder,setRefundStatus,
      detailOrder,updateFapiaoStatus,getFapiaoDetail,printWaiJie,printOutPdf,orderIsPayCheck,orderAllArePayCheck,
      updateOrderDealRemark} from '@/api/order'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import checkPermission from '@/utils/permission' // 权限判断函数
  import Qs from 'qs'
  import requestblob from '@/utils/requestblob'
  import { listDiccode } from '@/api/diccode'
  import {
      Loading
  }
  from 'element-ui'
  import { listPickSite} from '@/api/picksite'
  import moment from "moment";
  import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
  import * as constIndex from '@/utils/index.js'

  export default {
    name: 'Order',
    components: { Pagination,ViewOrderInfo },
    data() {
      return {
        statusMap:constIndex.statusMap,
        //控制按钮样式
        buttonAllDan:'primary',
        buttonDaiZhifu:'',
        buttonDaiBeiHuo:'',
        buttonDaiFaHuo:'',
        buttonDaiShouHuo:'',
        buttonYiShouHuo:'',
        buttonYiQuXiao:'',
        buttonDaiTuiKuan:'',
        buttonYiTuiKuan:'',

        listPickSite:[],//提货点列表
        checkBoxSelOrderListData:[],//选择的订单数据
        activeTabName: '全部单', // 当前选择的Tab名
        list: [],
        total: 0,
        kuaiDiComs:[],
        returnReasons:[],
        listLoading: true,
        checkPayBtnLoading:false,
        listQuery: {
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: [],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
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
          shipChannel: undefined,
          shipSn: undefined
        },
        shipDialogVisible: false,
        orderRemarkDialogVisible: false,
        refundForm: {
          orderId: undefined,
          refundMoney: undefined,
            returnFrontreason:undefined,
          returnReason:undefined,
          returnType:'整单退款',
          returnPartRemark:undefined,
          returnOp:this.$store.getters.name,
          orderStatusRefund:undefined,
        },
        faPiaoForm: {
          orderId: undefined,
          fapiaoType: undefined,
          name:undefined,
          taxno:undefined,
          address: undefined,
          telphone:undefined,
          bank:undefined,
          bankNo: undefined,
          money:undefined,
          taxno:undefined,
          status:undefined,
        },
        orderRemarkForm: {
          orderId: undefined,
          dealRemark:undefined,
        },
        refundDialogVisible: false,
        faPiaoFormDialogVisible: false,
        downloadLoading: false,
        pdfDownloadLoading_All:false,//批量导出Pdf

        excelDownloadLoading_All:false,//导出全部主单
        excelDownloadLoading_Mx:false,//导出全部明细
        excelDownloadLoading_WaiJie:false,//导出外借单
        batchBeihuoloadLoading_All:false,//批量备货
        btnBatchConfirm:false,
        refundRules: {
          returnReason: [
            { required: true, message: '退款原因不能为空', trigger: 'blur' }
          ],
          refundMoney: [{ required: true, message: '退款金额不能为空', trigger: 'blur' }]
        },
      }
    },
    created() {
      var type=this.$route.query.type;
      var sendWay=this.$route.query.sendWay;
      console.log(sendWay)
      this.listQuery.sendWay=sendWay;
      if(type=="beihuo"){
        //'待备货'
        this.listQuery.orderStatusArray.push("201")
        this.listQuery.sort="add_time desc"
      } else if(type=="tuikuan"){
        //待退款
        this.listQuery.orderStatusArray.push("202")
        this.listQuery.sort="return_time desc"
      } else if(type=="kaipiao"){
        this.listQuery.orderStatusArray=['201','250','301','401','402'];
        this.listQuery.ifFapiao='true';
        this.listQuery.fapiaoStatus="未开";
        this.listQuery.sort="add_time desc"
      } else
      {
        this.listQuery.sort="add_time desc"
      }
      this.getList()
      this.getKuaiDiComs()
      this.getReturnReasons()
      this.getPickSite()
    },

    methods: {
      dateForma:function(row,column){
        var date = row[column.property];
        if(date == undefined||date==''){return ''};
        return moment(date).format("YYYY-MM-DD HH:mm:ss")
      },
      changeOrderStatus(val){
         console.log(val)
        if(val.length==1&&val.indexOf('201')>=0){
           this.btnBatchConfirm=true;
        } else
        {
           this.btnBatchConfirm=false;
        }
      },
      changeSendWay(){
        if(this.listQuery.sendWay=='快递'){
          this.listQuery.pickSiteIdArray=[];
        }
      },
      getPickSite() {
        this.listLoading = true
        listPickSite({page:1,limit:500})
          .then(response => {
            this.listPickSite = response.data.data.list

          })
          .catch(() => {
            this.listPickSite = []

          })
      },
      checkPermission,
      startLoading: function() {
          loading = Loading.service({
              lock: true,
              text: '加载中……',
              background: 'rgba(0, 0, 0, 0.7)'
          })
      },
      endLoading: function() {
          loading.close()
      },
      getKuaiDiComs(){
        listDiccode({dicmainName:"快递承运商"}).then(response => {
          this.kuaiDiComs = response.data.data.list
        }).catch(() => {
          this.kuaiDiComs = []
        })
      },
      getReturnReasons(){
        listDiccode({dicmainName:"退款原因"}).then(response => {
          this.returnReasons = response.data.data.list
        }).catch(() => {
          this.returnReasons = []
        })
      },

      getList() {
        this.listLoading = true

        listBySql(this.listQuery).then(response => {
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
      confirmBeihuo(orderId) {
        if(confirm("确实已经备货完成了吗?")==false){
          return ;
        }
        beiHuoOrder({orderId:orderId}).then(response => {
          this.$message.success('确认备货成功');
          this.getList()
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
      },
      handleBatchBeihuoConfirm(orderId) {
          this.batchBeihuoloadLoading_All=true;
          var orderIds=[];
          if(orderId!=null){
              orderIds.push(orderId)
          } else
          {
              for (let selItem of  this.checkBoxSelOrderListData) {
                  orderIds.push(selItem.id)
              }
              if(orderIds==null||orderIds.length==0){
                  //this.$message.error("请先选择要导出的订单");

                  if(confirm("没有勾选单据，确定要确认当前页所有备货记录吗?")==false)
                  {
                      this.batchBeihuoloadLoading_All=false;
                      return;
                  } else
                  {
                      for (let order of this.list) {
                          orderIds.push(order.id)
                      }
                  }
              }
          }
          this.batchBeihuoloadLoading_All = true
          beiHuoBatchConfirmOrder({orderIds:orderIds}).then(response => {
              this.batchBeihuoloadLoading_All=false;
              this.$message.success('确认备货成功');
              this.getList()
          }).catch(response => {
              this.batchBeihuoloadLoading_All=false;
              this.$message.error( '失败:'+response.data.errmsg);
          })
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
      updateOrderDealRemark(row) {
          this.orderRemarkForm.orderId=row.id;
          this.orderRemarkForm.dealRemark=row.dealRemark;
          this.orderRemarkDialogVisible = true
      },
      updateOrderDealRemarkExec() {
        updateOrderDealRemark({orderId:this.orderRemarkForm.orderId,dealRemark:this.orderRemarkForm.dealRemark}).then(response => {
          this.orderRemarkDialogVisible = false
          this.getList()
          this.$message.success('修改订单备注成功');
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
      },

      handleRefund(row) {
        this.refundForm={
            orderId: undefined,
            refundMoney: undefined,
            returnFrontreason:undefined,
            returnReason:undefined,
            returnType:'整单退款',
            returnPartRemark:undefined,
            returnOp:this.$store.getters.name};
        this.refundForm.orderId = row.id
        this.refundForm.orderStatusRefund = row.orderStatusRefund
        this.refundForm.refundMoney = row.actualPrice
        this.refundDialogVisible = true
        this.$nextTick(() => {
          this.$refs['refundForm'].clearValidate()
        })
      },
      handleFapiao(row) {
        this.faPiaoForm.orderId = row.id
        //加载发票信息
        getFapiaoDetail(this.faPiaoForm.orderId).then(response=>{
          console.log(response.data)
           this.faPiaoForm=response.data.data;
        })
        this.faPiaoFormDialogVisible= true
      },
      handleVerifyCheckPay(row) {
        let orderSn=row.orderSn
        //临时测试订单号
        // orderSn="20200217184718218947"
        //验证该单号是否和后台微信支付/支付付支付返回的结果一致
        orderIsPayCheck({orderSn:orderSn}).then(response=>{
          console.log(response.data)
          this.$message.success(response.data.data)
        })
      },

      handleBatchCheckPay(){
        this.checkPayBtnLoading=true;
        //验证该单号是否和后台微信支付/支付付支付返回的结果一致
        orderAllArePayCheck().then(response=>{
          let checkMessage=response.data.data;
          if(checkMessage==""){
             this.checkPayBtnLoading=false;
             this.$message.success("所有订单保持一致")

          } else
          {
            this.checkPayBtnLoading=false;
            this.$message.warning(response.data.data)
          }

        })
      },

      handleSetRefundStatus(row) {
          if(confirm("系统管理可以针对客户已经收货的单据启动强制退款,更改状态后," +
            "下单客户可以在前端操作申请退款，后台就可以正常按正常退款流程执行,只针到待收货和已收货,确定要强行修改可退款状态吗?")==false){
            return;
          }
          var sendData={
              orderId:row.id
            }
          setRefundStatus(sendData).then(response => {
            this.$message.success('状态设置成功，客户可以在前端发起退款');
            this.getList()
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })

      },
      notAllowRefund(){

        if(this.refundForm.orderStatusRefund==null||this.refundForm.orderStatusRefund==''){
          this.$message.error("申请退款时未保存订单状态,不允许撤销");
          return;
        }
        if(confirm("取消用户申请退款会回到用户申请时的订单状态，确实要取消吗?")==false){
          return;
        }
        //如果不允许客户申请退款
        notRefundOrder({orderId:this.refundForm.orderId}).then(response => {
          this.refundDialogVisible = false
          this.$message.success('取消退款申请成功');
          this.getList()
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })

      },
      confirmRefund() {
        this.$refs['refundForm'].validate((valid) => {
          if (valid) {
            if(confirm("确定要进行退款操作吗?")==false){
              return;
            }
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
      confirmFapiao(){
        if(confirm("确实已经开完发票了吗?")==false){
          return;
        }
        updateFapiaoStatus(this.faPiaoForm).then(response => {
          this.faPiaoFormDialogVisible = false
          this.$message.success('修改开票信息成功');
          this.getList()
        }).catch(response => {
          this.$message.error( '失败:'+response.data.errmsg);
        })
      },

      handleDownloadAllMain() {
        var orderIds=[];
        for (let selItem of  this.checkBoxSelOrderListData) {
          orderIds.push(selItem.id)
        }
        this.excelDownloadLoading_All = true
        this.listQuery.orderIds=orderIds;
        var listExportQuery=this.listQuery;
        listExportQuery.limit=999999
        var listExportData=[];
        listOrder(listExportQuery).then(response => {
          listExportData = response.data.data.list
          this.downloadLoading = false;
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['订单ID', '订单编号', '下单时间', '订单状态',  '收货人', '收货联系电话', '收货地址',
              '支付金额','支付方式','平台支付号','订购人','订购电话','发货方式','自提货点','预约提货时间','申请退款时间','支付退款时间']
            const filterVal = ['id', 'orderSn','addTime',  'orderStatusName',  'consignee', 'mobile', 'address',
              'actualPrice','payMethodName','payId','userNickname','userPhone','sendWay','pickSiteName','pickTime','returnTime','returnPayTime']
            excel.export_json_to_excel2(tHeader, listExportData, filterVal, '订单信息')
            this.excelDownloadLoading_All = false
            this.listQuery.limit=20;
          })
        }).catch(() => {
          this.excelDownloadLoading_All = false
          this.listQuery.limit=20;
          this.$message.error("导出失败")
        })
      },

      handleDownloadAllDetail() {
        var orderIds=[];
        for (let selItem of  this.checkBoxSelOrderListData) {
          orderIds.push(selItem.id)
        }
        this.listQuery.orderIds=orderIds;
        this.excelDownloadLoading_Mx = true
        var listExportQuery=this.listQuery;
        listExportQuery.limit=999999
        var listExportData=[];
        listOrderDetail(listExportQuery).then(response => {
          listExportData = response.data.data.list
          import('@/vendor/Export2Excel').then(excel => {
            const tHeader = ['订单ID', '订单编号','下单时间','用户ID', '订单状态',  '收货人', '收货联系电话', '收货地址',
              '支付金额','支付方式','平台支付号','订购人','订购电话','发货方式','自提货点','预约提货时间','申请退款时间','支付退款时间',
              '货品编号','品名','规格','数量','单价']
            const filterVal = ['id', 'orderSn','orderTime','userId', 'orderStatusName',  'consignee', 'mobile', 'address',
              'actualPrice','payMethodName','payId','userNickname','userPhone','sendWay','pickSiteName','pickTime','returnTime','returnPayTime',
              'goodsSn','goodsName','specifications','number','price']
            excel.export_json_to_excel2(tHeader, listExportData, filterVal, '订单信息')
            this.excelDownloadLoading_Mx = false
            this.listQuery.limit=20;
          })
        }).catch(() => {
          this.excelDownloadLoading_Mx = false
          this.listQuery.limit=20;
          this.$message.error("导出失败")
        })
      },

      exportExcelListData() {

        var orderIds=[];
        for (let selItem of  this.checkBoxSelOrderListData) {
          orderIds.push(selItem.id)
        }
        this.listQuery.orderIds=orderIds;
        this.excelDownloadLoading_WaiJie = true
        requestblob({
          url: '/order/excelList',
          method: 'get',
          params: this.listQuery,
          responseType: 'blob',
          paramsSerializer: function(params) {
            return Qs.stringify(params, { arrayFormat: 'repeat' })
          }
        }).then(res=> {
          console.log(res)
          if(res.data.size==0){
            this.$message.warning("没有要导出的数据");
            return;
          }
          var blob = res.data;
          //此处不要加扩展名称，如果加上就会报错
          var fileName="外借单";
          if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(blob, fileName);
          } else {
            var blobURL = window.URL.createObjectURL(blob);
            var tempLink = document.createElement('a');
            tempLink.style.display = 'none';
            tempLink.href = blobURL;
            tempLink.setAttribute('download', fileName);
            if (typeof tempLink.download === 'undefined') {
              tempLink.setAttribute('target', '_blank');
            }
            document.body.appendChild(tempLink);
            tempLink.click();
            document.body.removeChild(tempLink);
            window.URL.revokeObjectURL(blobURL);
            this.excelDownloadLoading_WaiJie = false
          }
        }).catch(
          error=>{
            console.log(error);
            this.excelDownloadLoading_WaiJie = false
          }
        );

      },

      handlePdf(orderId) {
          var orderIds=[];
          if(orderId!=null){
              orderIds.push(orderId)
          } else
          {
              for (let selItem of  this.checkBoxSelOrderListData) {
                  orderIds.push(selItem.id)
              }
              if(orderIds==null||orderIds.length==0){
                  //this.$message.error("请先选择要导出的订单");

                  if(confirm("没有勾选单据，确定要导出当前页的所有出库单吗?")==false)
                  {
                      return;
                  } else
                  {
                      for (let order of  this.list) {
                          orderIds.push(order.id)
                      }
                  }
              }
          }

        this.pdfDownloadLoading_All = true
        requestblob({
          url: '/order/pdf',
          method: 'get',
          params: {orderIds:orderIds} ,
          responseType: 'blob',
          paramsSerializer: function(params) {
              return Qs.stringify(params, { arrayFormat: 'repeat' })
          }
        }).then(res=> {
          console.log(res)
          if(res.data.size==0){
            this.pdfDownloadLoading_All = false
            this.$message.warning("没有要导出的数据");
            return;
          }
          var blob = res.data;
          //此处不要加扩展名称，如果加上就会报错
          var fileName="出货单.pdf";
          if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(blob, fileName);
          } else {
            var blobURL = window.URL.createObjectURL(blob);
            var tempLink = document.createElement('a');
            tempLink.style.display = 'none';
            tempLink.href = blobURL;
            tempLink.setAttribute('download', fileName);
            if (typeof tempLink.download === 'undefined') {
              tempLink.setAttribute('target', '_blank');
            }
            document.body.appendChild(tempLink);
            tempLink.click();
            document.body.removeChild(tempLink);
            window.URL.revokeObjectURL(blobURL);
           this.pdfDownloadLoading_All = false
          }
        }).catch(
          error=>{
              this.pdfDownloadLoading_All = false
            console.log(error);
          }
        );

      },

      changeSelOrderList(val){
        this.checkBoxSelOrderListData = val
      },

      displayAllDan(){
        this.btnBatchConfirm=false;
        this.buttonAllDan='primary'
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
            page: 1,
            limit: 20,
            id: undefined,
            name: undefined,
            phone:undefined,
            consignee:undefined,
            sourceCode:10,
            typeCode:10,
            orderStatusArray: [],
            ifFapiao:undefined,
            fapiaoStatus:undefined,
            sendWay:undefined,
            beginDate:undefined,
            endDate:undefined,
            orderType:"电商",
            payMethod:undefined,
            pickSiteIdArray:[],
            orderIds:[],
            sort: 'add_time desc',
        }
        this.getList();
      },
      displayDaiZhiFu(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu='primary'
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['101'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayDaiBeiHuo(){
        this.btnBatchConfirm=true;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo='primary'
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['201'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayDaiFaHuo(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo='primary'
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['250'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayDaiShouHuo(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo='primary'
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['301'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayYiShouHuo(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo='primary'
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['401','402'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayYiQuXiao(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao='primary'
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['102','103'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'add_time desc',
        }
        this.getList();
      },
      displayDaiTuiKuan(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan='primary'
        this.buttonYiTuiKuan=''
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['202'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'return_time desc',
        }
        this.getList();
      },
      displayYiTuiKuan(){
        this.btnBatchConfirm=false;
        this.buttonAllDan=''
        this.buttonDaiZhifu=''
        this.buttonDaiBeiHuo=''
        this.buttonDaiFaHuo=''
        this.buttonDaiShouHuo=''
        this.buttonYiShouHuo=''
        this.buttonYiQuXiao=''
        this.buttonDaiTuiKuan=''
        this.buttonYiTuiKuan='primary'
        this.listQuery={
          page: 1,
          limit: 20,
          id: undefined,
          name: undefined,
          phone:undefined,
          consignee:undefined,
          sourceCode:10,
          typeCode:10,
          orderStatusArray: ['203'],
          ifFapiao:undefined,
          fapiaoStatus:undefined,
          sendWay:undefined,
          beginDate:undefined,
          endDate:undefined,
          orderType:"电商",
          payMethod:undefined,
          pickSiteIdArray:[],
          orderIds:[],
          sort: 'return_pay_time desc',
        }
        this.getList();
      },

    }
  }
</script>
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
