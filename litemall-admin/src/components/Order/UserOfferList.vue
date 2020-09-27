<template>
  <div>

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="offerQuery.userName" clearable class="filter-item" style="width: 120px;"
                placeholder="用户名称"/>

      <el-button v-permission="['GET /admin/dajiaPaimaiOffer/list']" class="filter-item" type="primary" icon="el-icon-search"
                 @click="handleFilterOffer">查找
      </el-button>
    </div>
    <!-- 查询结果 -->
    <el-table v-loading="listLoading"
              :data="offerList" element-loading-text="正在查询中。。。"
              border fit highlight-current-row>

      <el-table-column align="center" label="操作" width="180px" class-name="small-padding fixed-width">
        <template slot-scope="scope">

          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.orderId!=null"
                     type="primary" size="mini" style="width: 70px"  @click="handleViewDetail(scope.row.orderId)">查看订单</el-button>
          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.orderId!=null&&scope.row.expireFlag==0"
                     type="primary" size="mini" style="width: 70px"  @click="handleSetExpirtFlagTrue(scope.row.rulesId)">活动停止</el-button>
          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.orderId==null&&scope.row.offerState=='领先'"
                     type="primary" size="mini" style="width: 70px" @click="handleCreateOrder(scope.row)">生成订单</el-button>

        </template>
      </el-table-column>

      <el-table-column align="center"  label="用户名称" prop="userName"/>

      <el-table-column align="center" label="出价时间" prop="offerTime"/>
      <el-table-column align="center" label="出价金额" prop="offerMoney"/>
      <el-table-column align="center" label="出价状态" prop="offerState"/>

    </el-table>

    <pagination v-show="offerListTotal>0" :total="offerListTotal" :page.sync="offerQuery.page"
                :limit.sync="offerQuery.limit" @pagination="getList"/>

    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 添加或修改专场商品明细对话框 -->
    <el-dialog :title="'创建订单'"  :close-on-click-modal="false"  customClass="customWidth"  :visible.sync="dialogCreateOrderVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm" :model="creataOrderForm"
                   status-icon label-position="left" label-width="100px" >

            <el-col :span="12" >
              <el-form-item label="拍卖商品" prop="goodsName">
                <el-input style="width: 200px" :disabled="true" v-model="creataOrderForm.goodsName"  />
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="拍卖出价" prop="offerMoney">
                <el-input style="width: 200px" :disabled="true" v-model="creataOrderForm.offerMoney"  />
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="出价时间" prop="offerTime">
                <el-input style="width: 200px" :disabled="true" v-model="creataOrderForm.offerTime"  />
              </el-form-item>
            </el-col>

            <el-col :span="12" >
              <el-form-item label="收货人" prop="consignee">
                <el-input style="width: 200px" v-model="creataOrderForm.consignee"  />
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="收货人电话" prop="mobile">
                <el-input style="width: 200px" v-model="creataOrderForm.mobile"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="收货人地址" prop="address">
                <el-input style="width: 200px" v-model="creataOrderForm.address"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="发运方式" prop="sendWay">
                <el-select v-model="creataOrderForm.sendWay" style="width: 110px;" clearable class="filter-item" placeholder="发运方式">
                  <el-option  label="快递" :value="'快递'"/>
                  <el-option  label="自提取货"  :value="'自提取货'"/>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="快递费用" prop="freightPrice">
                <el-input  style="width: 200px" v-model="creataOrderForm.freightPrice"/>
              </el-form-item>
            </el-col>
          </el-form>
        </el-card>
        <div class="op-container" style="text-align: center;padding-top: 10px">
          <el-button  type="primary" @click="createOrderData">确定</el-button>
        </div>
      </div>
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

  import { listDajiaPaimaiOffer,detailDajiaPaimaiOffer } from '@/api/dajiaPaimaiOffer'
  import { listZhuanchangOffer,detailZhuanchangOffer } from '@/api/zhuanchangOffer'

  import { detailDajiaPaimaiRule,updateDajiaPaimaiRule } from '@/api/dajiaPaimaiRule'
  import { getZhuanchangGoodsDetail,updateZhuanchangGoodsDetail } from '@/api/zhuanchangRule'

  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
  import {createPaiMaiOrder} from '@/api/order'

  export default {

    name: 'UserOfferList',
    components: {Pagination,ViewOrderInfo},

    props:{
      inOfferQuery:{
        type:Object,
        default:{}
      },
    },

    watch:{
      inOfferQuery:{
        immediate:true,
        deep:true,
        handler(val){
          this.offerQuery=val;
          this.getList(val);
        }
      },

    },

    data() {
      return {
        orderFormVisible:false,
        orderDetailId:undefined,
        listLoading:false,
        // type:'大家拍',//传入的出价类型
        // ruleMxId:undefined,//拍品的规则ID,如果是大家怕，传入其rule_id，如果是专场拍，传入期明细的ID
        // zhuanChangId:undefined,//传入专场ID
        offerList:[],//大家拍出价记录列表
        offerListTotal:0,
        offerQuery:{
          type:'专场拍',
          page: 1,
          limit: 20,
          userId:undefined,
          rulesId:undefined,
          rulesMxId:undefined,
          userName:undefined,
          sort: 'add_time desc',
        },
        creataOrderForm:{
          goodsName:'',
          offerTime:undefined,
          offerMoney:0,
          auctionType:'大家拍',
          offerId:undefined,
          consignee:'',
          mobile:'',
          address:'',
          sendWay:'快递',
          freightPrice:0
        },
        dialogCreateOrderVisible:false,
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
      // this.getList();
    },
    methods: {
      handleSetExpirtFlagTrue(ruleMxId){
        //设置活动过期
        if(this.offerQuery.type=="大家拍"){
          detailDajiaPaimaiRule({id:ruleMxId}).then(
            (res)=>{
              var ruleData=res.data.data;
              ruleData.expireFlag=1;
              updateDajiaPaimaiRule(ruleData).then(
                (update)=>{
                   this.getList(this.offerQuery);
                   this.$message.success("更新成功")
                }
              )
            }
          )
        } else  if(this.offerQuery.type=="专场拍") {
          getZhuanchangGoodsDetail({id:ruleMxId}).then(
            (res)=>{
              var ruleData=res.data.data;
              ruleData.expireFlag=1;
              updateZhuanchangGoodsDetail(ruleData).then(
                (update)=>{
                  this.getList(this.offerQuery);
                  this.$message.success("更新成功")
                }
              )
            }
          )
        }
      },
      createOrderData(){
        createPaiMaiOrder(this.creataOrderForm).then(
          (res)=>{
            var result=res.data;
            if(result.errno!=0){
              this.$message.error("创建订单失败");
              return;
            }
            this.getList(this.offerQuery);
            this.dialogCreateOrderVisible=false;

          }
        );
      },
      handleFilterOffer() {
        this.listQuery.page = 1
        this.handleOffer()
      },
      handleViewDetail(orderId){
        this.orderDetailId=orderId;
        this.orderFormVisible=true;
      },
      handleCreateOrder(row){
        // if(confirm("通常情况下，拍卖结束才会按最高价生成订单,确实要根据现在出价创建订单吗?" )==false){
        //   return;
        // }
        var that=this;
        this.$set(that.creataOrderForm,"auctionType",this.offerQuery.type)
        this.$set(that.creataOrderForm,"offerId",row.id)
        if(this.offerQuery.type=="大家拍")
        {
          detailDajiaPaimaiOffer({id:row.id}).then(
            (res)=>{
               var data=res.data.data;
               this.$set(that.creataOrderForm,"goodsName",data.goodsName)
               this.$set(that.creataOrderForm,"offerTime",data.offerTime)
               this.$set(that.creataOrderForm,"offerMoney",data.offerMoney)

            }
          );
        }
        else  if(this.offerQuery.type=="专场拍") {
          detailZhuanchangOffer({id:row.id}).then(
            (res)=>{
              var data=res.data.data;
              this.$set(that.creataOrderForm,"goodsName",data.goodsName)
              this.$set(that.creataOrderForm,"offerTime",data.offerTime)
              this.$set(that.creataOrderForm,"offerMoney",data.offerMoney)
            }
          );
        }
        this.dialogCreateOrderVisible=true;
        //todo 根据当前拍卖的规则ID，找到最高价创建订单,也可以根据出价记录，选其中的一条进行订单创建，一个大家拍商品只能创建一个订单

      },


      getList(query) {
        this.offerQuery=query;
        this.offerList=[];
        this.listLoading = true
        if(this.offerQuery.type=="大家拍")
        {
          // this.offerQuery.rulesId=this.ruleMxId;
          listDajiaPaimaiOffer(this.offerQuery).then(response => {
            this.offerList = response.data.data.list
            this.offerListTotal = response.data.data.total
            this.listLoading = false
          }).catch(() => {
            this.offerList = []
            this.offerListTotal = 0
            this.listLoading = false
          })
        } else  if(this.offerQuery.type=="专场拍"){
          // this.offerQuery.rulesId=this.rulesId
          // this.offerQuery.rulesMxId=this.ruleMxId
          listZhuanchangOffer(this.offerQuery).then(response => {
            this.offerList = response.data.data.list
            this.offerListTotal = response.data.data.total
            this.listLoading = false
          }).catch(() => {
            this.offerList = []
            this.offerListTotal = 0
            this.listLoading = false
          })
        }

      },

    },

  }
</script>
