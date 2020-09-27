<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.seckillName" clearable class="filter-item" style="width: 200px;" placeholder="请输入秒杀活动名称"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 200px;" placeholder="请输入商品名称"/>
      <el-select v-model="listQuery.seckillExpireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/seckillrule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/seckillrule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
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
      <el-table-column align="center" label="操作" width="250px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/seckillrule/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/seckillrule/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-permission="['GET /admin/seckillorders/list']" type="primary" size="mini" @click="findOrders(scope.row.id)">订单</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="商品编号" prop="goodsSn"/>
      <el-table-column align="center" min-width="100" label="名称" prop="goodsName"/>
      <!--<el-table-column align="center" property="picUrl" label="图片">-->
        <!--<template slot-scope="scope">-->
          <!--<img :src="scope.row.picUrl" width="40">-->
        <!--</template>-->
      <!--</el-table-column>-->
      <el-table-column align="center" min-width="100" label="货品规格" prop="goodsProductSpecifications"/>

      <el-table-column align="center" label="活动名称" prop="seckillName"/>
      <el-table-column align="center" label="原价" prop="sourcePrice"/>
      <el-table-column align="center" label="秒杀价" prop="seckillPrice"/>
      <el-table-column align="center" label="活动库存" prop="seckillStoreNum"/>
      <el-table-column align="center" label="剩余库存" prop="seckillRemainNum"/>
      <el-table-column align="center" label="单人单次" prop="seckillOnlyOne">
        <template slot-scope="scope">
          <el-tag :type="scope.row.seckillOnlyOne ? 'success' : 'error' ">{{ scope.row.seckillOnlyOne ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="过期标志" prop="seckillExpireFlag">
        <template slot-scope="scope">
          <el-tag :type="scope.row.seckillExpireFlag ? 'true' : 'false' ">{{ scope.row.seckillExpireFlag ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="开始时间" prop="seckillBeginDate"/>
      <el-table-column align="center" label="结束时间" prop="seckillEndDate"/>
      <el-table-column align="center" label="执行备注" prop="remark"/>

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false"
               customClass="customWidth"  :before-close="closeAndRefresh" :visible.sync="dialogFormVisible">
      <el-card class="box-card">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="dataForm"
          status-icon
          label-width="100px"
        >
          <el-col :span="12" >
            <el-form-item label="活动名称" prop="seckillName" >
              <el-input style="width: 200px" v-model="dataForm.seckillName"  clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="商品编号" prop="goodsSn" >

              <el-input style="width: 220px" placeholder="请输入内容" v-model="dataForm.goodsSn"  clearable :readonly="true"  >
                <el-button slot="append" type="primary"  icon="el-icon-search" @click="handleSelectGoods">选择</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商品名称" prop="goodsName">
              <el-input style="width: 200px" v-model="dataForm.goodsName"  :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="商品规格">
              <el-select v-model="dataForm.goodsProductId" style="width:220px;" @change="changeGoodsProduct" >
                <el-option v-for="(item,index) in goodsProduct" :value="item.id"  :key="index" :label="item.specifications.toString()">
                </el-option>
                <!--<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>-->
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="商品图片">
              <el-upload
                :action="uploadPath"
                :headers="headers"

                :show-file-list="false"
                :on-success="uploadPicUrl"
                class="avatar-uploader"
                accept=".jpg,.jpeg,.png,.gif">
                <img style="width: 200px;height: 200px"  v-if="dataForm.picUrl" :src="dataForm.picUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"/>

              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="sourcePrice" >
              <el-input style="width: 200px" v-model="dataForm.sourcePrice" :disabled="true"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="秒杀价" prop="seckillPrice">
              <el-input style="width: 200px" v-model="dataForm.seckillPrice"/>
            </el-form-item>
          </el-col>
<!--          <el-col :span="12">-->
<!--            <el-form-item label="非活动库存" prop="goodsStoreNum">-->
<!--              <el-input style="width: 200px" v-model="goodsStoreNum" :disabled="true"/>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
          <el-col :span="12" >
            <el-form-item label="活动库存" prop="seckillStoreNum">
              <el-input style="width: 200px" :disabled="dataForm.id!=null&&dataForm.id!=undefined" v-model="dataForm.seckillStoreNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="剩余库存" prop="seckillRemainNum">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.seckillRemainNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="单人单次" prop="seckillOnlyOne">
              <el-radio-group style="width: 200px" v-model="dataForm.seckillOnlyOne">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="限购一件" prop="seckillOnlyBuyOne">
              <el-radio-group style="width: 200px" v-model="dataForm.seckillOnlyBuyOne">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="是否免邮" prop="seckillOnlyOne">
              <el-radio-group style="width: 200px" v-model="dataForm.freePost">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="可用优惠券" prop="ifUseCoupon">
              <el-radio-group style="width: 200px" v-model="dataForm.ifUseCoupon">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="可用积分" prop="ifUseBonus">
              <el-radio-group style="width: 200px" v-model="dataForm.ifUseBonus">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="开始时间" prop="seckillBeginDate">
              <el-date-picker style="width: 200px"
                              v-model="dataForm.seckillBeginDate"
                              type="datetime"
                              placeholder="选择日期"
                              value-format="yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="过期时间" prop="seckillEndDate">
              <el-date-picker style="width: 200px"
                              v-model="dataForm.seckillEndDate"
                              type="datetime"
                              placeholder="选择日期"
                              value-format="yyyy-MM-dd HH:mm:ss"/>
            </el-form-item>
          </el-col>

          <el-col :span="12"  >
            <el-form-item label="执行备注" prop="remark">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.remark"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="已销售数量" prop="havePayNum">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.havePayNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="未支付数量" prop="unPayNum">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.unPayNum"/>
            </el-form-item>
          </el-col>
        </el-form>

      </el-card>
      <div slot="footer" class="dialog-footer" style="text-align: center;">
        <el-button type="primary" :disabled="dataForm.seckillExpireFlag==false" @click="updateExpireFlagFalse">正常可用</el-button>
        <el-button type="primary" :disabled="dataForm.seckillExpireFlag==true" @click="updateExpireFlagTrue">强制过期</el-button>

        <!--        <el-button @click="dialogFormVisible = false">取消</el-button>-->
        <el-button v-if="dataForm.id!=null&&dataForm.id!=undefined" type="primary" @click="btnAddStoreNum">活动加库存</el-button>

        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 选择商品对话框 -->
    <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogSelVisible"
               width="80%">
      <find-goods-single :listGoodsQuery="goodsQuery"
                         :inComdId="goodsQuery.comId" :goodsOrProduct="'goods'"
                         @closeAndReturn="closeAndReturn"></find-goods-single>
      <!--      <find-goods-multi :listGoodsQuery="goodsQuery"-->
      <!--                         :inComdId="goodsQuery.comId" :goodsOrProduct="'goods'"-->
      <!--                         @closeAndReturn="closeAndReturn"  ></find-goods-multi>-->
    </el-dialog>


    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
      <!-- 查询和其他操作 -->
      <div class="filter-container">
        <!--<el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>-->
        <el-input v-model="listOrderQuery.orderSn" clearable class="filter-item" style="width: 120px;"
                  placeholder="订单编号"/>
        <el-input v-model="listOrderQuery.username" clearable class="filter-item" style="width: 120px;"
                  placeholder="用户名称"/>
        <el-select v-model="listOrderQuery.orderStatusArray" multiple style="width: 150px" class="filter-item"
                   placeholder="请选择订单状态">
          <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
        </el-select>


        <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleFilterSecOrder">查找
        </el-button>
      </div>
      <!-- 查询结果 -->
      <el-table v-loading="listLoading"
                :data="listOrder" element-loading-text="正在查询中。。。"
                border fit highlight-current-row>
        <el-table-column type="selection"/>

        <el-table-column align="center" min-width="200" label="订单编号" prop="orderSn"/>
        <!--          <el-table-column align="center" label="用户ID" prop="userId"/>-->
        <el-table-column align="center" min-width="90" label="订单状态" prop="orderStatusName">

        </el-table-column>
        <!--          <el-table-column align="center" label="订单金额" prop="orderPrice"/>-->
        <el-table-column align="center" label="支付金额" prop="actualPrice"/>
        <el-table-column align="center" label="支付时间" prop="payTime"/>
        <el-table-column align="center" label="物流单号" prop="shipSn"/>
        <el-table-column align="center" label="物流渠道" prop="shipChannel"/>
        <el-table-column align="left" label="操作"  class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini"
                       @click="handleDetail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="totalOrder>0" :total="totalOrder" :page.sync="listOrderQuery.page"
                  :limit.sync="listOrderQuery.limit" @pagination="handleFilterSeckillOrder"/>

    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 活动加库存 -->
    <el-dialog :title="'活动加库存'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="addStoreNumVisible">
      <el-card class="box-card">
        <el-form ref="huoDongStoreForm"
                 status-icon  label-width="200px" >
          <el-col :span="24"  >
            <el-form-item label="活动库存" prop="name">
              <el-input :disabled="true" style="width: 200px" v-model="dataForm.seckillStoreNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="剩余库存" prop="name">
              <el-input :disabled="true" style="width: 200px" v-model="dataForm.seckillRemainNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="待支付数量" prop="name">
              <el-input :disabled="true" style="width: 200px" v-model="dataForm.unPayNum"/>
            </el-form-item>
          </el-col>
          <el-col :span="24"  >
            <el-form-item label="增加库存数量(为负数为减少)" prop="name">
              <el-input  style="width: 200px" v-model="addStoreNum"/>
            </el-form-item>
          </el-col>
        </el-form>
      </el-card>
      <div class="op-container" style="text-align: center;padding-top: 10px">
        <el-button type="primary"  @click="btnConfirmAddStoreNum">增加库存</el-button>
      </div>
    </el-dialog>


  </div>
</template>
<style>
  .customWidth {
    width: 850px;
  }
</style>
<script>
import { listSeckillrule, deleteSeckillrule, addSeckillrule,
  updateSeckillrule,detailSeckillrule,addGoodsStoreNum } from '@/api/seckillrule'
import { listSeckillOrders } from '@/api/seckillorder'
import { listGoods,getGoodsProduct,listGoodsProduct,goodsMain,goodsProductById,getGoodsProductViewById  } from '@/api/goods'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'
import { detailOrder} from '@/api/order'
import { createStorage, uploadPath } from '@/api/storage'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'

import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'
import * as constIndex from '@/utils/index.js'

export default {

  name: 'SeckillRule',
  components: { BackToTop, Pagination,ViewOrderInfo,FindGoodsSingle,FindGoodsMulti },
  data() {
    return {
      statusMap:constIndex.statusMap,
      uploadPath,
      orderDetailId:undefined,
      isRouteActive:true,

      list: [],
      total: 0,
      listOrder: [],
      totalOrder: 0,

      listLoading: true,
      goodsProduct:[],
      goodsStoreNum:undefined,//当前规格型号库存
      listOrderQuery: {
          page: 1,
          limit: 10,
          id: undefined,
          name: undefined,
          username: undefined,
          consignee: undefined,
          sourceCode: undefined,
          typeCode: undefined,
          orderStatusArray: [],
          ifFapiao: undefined,
          fapiaoStatus: undefined,
          sendWay: undefined,
          orderType: "",
          userId:undefined,
          rulesId:undefined,
          sort: 'add_time desc',
      },
      orderDetail: {
          order: {},
          fapiao:{},//发票
          coupon:{},//优惠券
          user: {},
          orderGoods: []
      },
      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsName:undefined,
        seckillName:undefined,
        seckillExpireFlag:0,
        sort: 'add_time desc',

      },
      // 可推荐列表查询
      goodsQuery: {
        goodsOrProduct:"goods",
        page: 1,
        limit: 10,
        goodsSn: undefined,
        comId: 1,
        storeId: undefined,
        isOnSale:1,
        name: undefined,
        ifNotIncludeHuodong:true,
        sort: 'add_time desc',

      },
      downloadLoading: false,
      dataForm: {
        seckillName:undefined,
        comId:undefined,
        comName:undefined,
        id: undefined,
        goodsId:undefined,
        goodsSn:undefined,
        goodsName:undefined,
        goodsProductId:undefined,
        goodsProductSpecifications:[],
        sourcePrice: undefined,
        seckillPrice: undefined,
        storeNum:undefined,
        seckillStoreNum: undefined,
        seckillRemainNum: undefined,
        unPayNum: undefined,
        havePayNum: undefined,
        seckillBeginDate: undefined,
        seckillEndDate: undefined,
        seckillExpireFlag:false,
        seckillOnlyOne:false,
        seckillOnlyBuyOne:false,
        remark: undefined,
        picUrl: undefined,
        freePost:false,
        ifUseCoupon:false,
        ifUseBonus:false,
      },
      dialogFormVisible: false,
      dialogSelVisible:false,
      orderFormVisible: false, //是否显示订单窗口
      orderDialogVisible: false, //是否显示订单详情
      addStoreNumVisible: false, //是否显示增加活动库存

      addStoreNum:undefined,//本次要增加的库存数量
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        secKillGoodsId : [{ required: true, message: '商品不能为空', trigger: 'blur' }],
        seckillPrice : [{ required: true, message: '必填', trigger: 'blur' }],
        seckillStoreNum : [{ required: true, message: '必填', trigger: 'blur' }],
        seckillBeginDate : [{ required: true, message: '必填', trigger: 'blur' }],
        seckillEndDate : [{ required: true, message: '必填', trigger: 'blur' }],
      }
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
    this.goodsProduct=[];
    this.getList()
  },
  methods: {

    closeAndReturn(val){
      console.log(val);
      this.dialogSelVisible=false;
      //返回的单一会员商品进行赋值
      if(val==null){
        return
      }
      this.dataForm.goodsProductId=null
      this.dataForm.goodsId=val.goodsId
      this.dataForm.goodsSn=val.goodsSn
      this.dataForm.goodsName=val.goodsName
      this.dataForm.sourcePrice=val.retailPrice
      this.dataForm.comId=val.comId
      this.dataForm.comName=val.comName
      this.dataForm.picUrl=val.picUrl
      this.dataForm.goodsProductSpecifications=val.specifications
      this.getListGoodsProduct()
    },
    handleFilterSecOrder() {
      this.listOrderQuery.page = 1
      this.findOrders(this.listOrderQuery.rulesId)
    },
    closeAndRefresh(){
      this.dialogFormVisible=false;
      this.getList();
    },
    btnConfirmAddStoreNum(){
      if(this.addStoreNum==null||this.addStoreNum==undefined){
        this.$message.error("请输入要增加的活动库存");
        return;
      }
      if(typeof this.addStoreNum === 'number' && this.addStoreNum%1 === 0)
      {
        this.$message.error("要增加的库存必须为整数");
        return;
      }
      if( (Number(this.dataForm.seckillRemainNum)+Number(this.addStoreNum))<0){
        this.$message.error("要增加的库存合计必须大于零");
        return;
      }
      addGoodsStoreNum({
        id:this.dataForm.id,
        addStoreNum:this.addStoreNum
      }).then(
        (res)=>{
          if(res.data.errno==0){
            //返回调整后的活动规则重新加载
            this.addStoreNumVisible=false;

            detailSeckillrule(this.dataForm.id).then(
              (response)=>{
                this.dataForm=response.data.data;

              }
            ).catch((error)=>{
              this.$message.error(error)
            })
            // this.getListGoodsProduct();
            // this.getGoodsRuleDetail();
          }
        }
      );
    },
    btnAddStoreNum(){
      this.addStoreNumVisible=true;
    },
    updateExpireFlagTrue(){
      if(confirm("确实要强制更新过期吗?")==false){
        return;
      }
      this.dataForm.seckillExpireFlag=true;
      this.updateData();

    },
    updateExpireFlagFalse(){
      if(confirm("确实要启动活动吗?")==false){
        return;
      }
      this.dataForm.seckillExpireFlag=false;
      this.updateData();

    },
      handleFilterSeckillOrder() {
          this.listOrderQuery.page = 1
          this.handleDetail(this.listOrderQuery.rulesId)
      },
      handleDetail(row) {
          // detailOrder(row.orderId).then(response => {
          //     this.orderDetail = response.data.data
          //     console.log(this.orderDetail)
          // })
          this.orderDetailId=row.orderId;
          this.orderDialogVisible = true
      },
      findOrders(rulesId) {
          this.orderFormVisible=true;
          this.listLoading = true
          this.listOrderQuery.rulesId=rulesId
          listSeckillOrders(this.listOrderQuery).then(response => {
              this.listOrder = response.data.data.list
              this.totalOrder = response.data.data.total
              this.listLoading = false
          }).catch(() => {
              this.listOrder = []
              this.totalOrder = 0
              this.listLoading = false
          })
      },

    changeGoodsProduct(){
      if(this.goodsProduct==null){
        return
      }
      // 晕，搞了好长时间，重新赋值，才会刷新页面
      var a =this.goodsProduct;
      this.goodsProduct=[];
      this.goodsProduct=a;
      const obj = this.goodsProduct.find((item) => {
        return item.id == this.dataForm.goodsProductId

      })
      this.changeSpecification(obj);
    },
    getList() {
      this.listLoading = true
      listSeckillrule(this.listQuery).then(response => {
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
    resetForm() {
      this.dataForm = {
        seckillName:undefined,
        comId:undefined,
        comName:undefined,
        id: undefined,
        goodsId:undefined,
        goodsSn:undefined,
        goodsName:undefined,
        goodsProductId:undefined,
        goodsProductSpecifications:[],
        sourcePrice: undefined,
        seckillPrice: undefined,
        seckillStoreNum: undefined,
        seckillRemainNum: undefined,
        seckillBeginDate: undefined,
        seckillEndDate: undefined,
        seckillExpireFlag:0,
        seckillOnlyOne:false,
        seckillOnlyBuyOne:false,
        remark: undefined,
        picUrl: undefined,
        freePost:false,
        ifUseCoupon:false,
        ifUseBonus:false,
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          addSeckillrule(this.dataForm).then(response => {
            // this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$message.success('创建秒杀活动规则成功');

            this.goodsProduct=[];
            this.getList()
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      detailSeckillrule(this.dataForm.id).then(
        (response)=>{
          this.dataForm=response.data.data;
        }
      ).catch((error)=>{
        this.$message.error(error)
      })
      this.getListGoodsProduct();
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          updateSeckillrule(this.dataForm).then(() => {
            // for (const v of this.list) {
            //   if (v.id === this.dataForm.id) {
            //     const index = this.list.indexOf(v)
            //     this.list.splice(index, 1, this.dataForm)
            //     break
            //   }
            // }
            this.dialogFormVisible = false
            this.$message.success('更新秒杀活动规则成功');

            this.goodsProduct=[];
            this.getList();
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
    handleDelete(row) {
      if(confirm('确实要删除当前记录吗?')==false){
        return
      }
      deleteSeckillrule(row).then(response => {
        this.$message.success('删除秒杀活动规则成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleSelectGoods(){
      //this.dialogFormVisible=false;

      this.dialogSelVisible=true;

    },
    //以下处理选择商品事件

    uploadPicUrl: function(response) {
      this.dataForm.picUrl = response.data.url
    },
    changeSpecification(goodsProduct){
      this.dataForm.goodsProductId=goodsProduct.id
      this.dataForm.goodsProductSpecifications=goodsProduct.specifications
      //如果商品图片为空，则取商品基本信息表中的图片,不更新
      if(goodsProduct.url!=null&&goodsProduct.url!=""){
        this.dataForm.picUrl=goodsProduct.url
      }
      this.dataForm.sourcePrice=goodsProduct.price
      this.dataForm.storeNum=goodsProduct.remainNumber
    },
    getListGoodsProduct(){
      if(this.dataForm.goodsId!=null){
          getGoodsProduct(this.dataForm.goodsId).then(
          (response)=>{
            this.goodsProduct=response.data.data.list;
            if(this.goodsProduct!=null&&this.goodsProduct.length>0)
            {
              this.changeSpecification(this.goodsProduct[0]);
            }
          }
        ).catch(() => {

        })
      }
    }

  },

}
</script>
