<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输活动名称"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/goodsRebateRule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/goodsRebateRule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
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
          <el-button v-permission="['POST /admin/goodsRebateRule/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/goodsRebateRule/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
          <el-button type="primary" size="mini" @click="findOrders(scope.row.id)">订单</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="活动名称" prop="name"/>
      <!--<el-table-column align="center" label="商品品类" prop="categoryName"/>-->

      <el-table-column align="center" label="过期标志" prop="expireFlag">
        <template slot-scope="scope">
          <el-tag :type="scope.row.expireFlag ? 'true' : 'false' ">{{ scope.row.expireFlag ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="折扣比例" prop="rebate"/>
      <el-table-column align="center" label="开始时间" prop="beginDate"/>
      <el-table-column align="center" label="结束时间" prop="endDate"/>
      <el-table-column align="center" label="执行备注" prop="remark"/>

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :rules="rules" :close-on-click-modal="false" customClass="customWidth" :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm"  :rules="rules" :model="dataForm"
                   status-icon  label-width="100px" >
            <el-col :span="12"  >
              <el-form-item label="活动名称" prop="name">
                <el-input style="width: 200px" v-model="dataForm.name"/>
              </el-form-item>
            </el-col>

            <el-col :span="12" >
              <el-form-item label="折扣(0-1)" prop="rebate">
                <el-input style="width: 200px" v-model="dataForm.rebate" />
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="开始时间" prop="beginDate">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.beginDate"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="过期时间" prop="endDate">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.endDate"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="备注" prop="remark">
                <el-input style="width: 200px" v-model="dataForm.remark"/>
              </el-form-item>
            </el-col>
          </el-form>
        </el-card>
        <el-card class="box-card">
          <div class="hot-header">
            <h3 class="title common-title left" >参与活动商品</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini" type="primary" @click="handleSelectGoods">添加</el-button>
            </div>
          </div>
          <el-table :data="goodsRebateGoodsList">
            <el-table-column property="goodsSn" label="商品编号">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsSn}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsSn}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="goodsName" label="商品名称">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsName}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsName}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="goodsProductSpecifications" label="规格型号">

              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsProductSpecifications}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsProductSpecifications}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="sourcePrice" label="现价">
              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.sourcePrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.sourcePrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="rebatePrice" label="折后价">

              <template slot-scope="scope"  >
                <!--<slot :class="{'deletedStyle':scope.row.deleted==0||scope.row.deleted==null}">-->
                <!--{{scope.row.brandName}}-->
                <!--</slot>-->
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    <el-input v-model="scope.row.rebatePrice"></el-input>
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  <el-input v-model="scope.row.rebatePrice"></el-input>
                  <!--                  {{scope.row.rebatePrice}}-->
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="storeNum" label="库存数">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    {{scope.row.storeNum}}
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.storeNum}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="havPayNum" label="已支付">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    {{scope.row.havPayNum}}
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.havPayNum}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="unPayNum" label="未支付">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">
                    {{scope.row.unPayNum}}
                  </s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.unPayNum}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="144" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleGoodsRebateGoodsDelete(scope.row)">删除</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <div class="op-container" style="text-align: center;padding-top: 10px">
          <el-button type="primary" :disabled="dataForm.expireFlag==false" @click="updateExpireFlagFalse">正常可用</el-button>

          <el-button type="primary" :disabled="dataForm.expireFlag==true" @click="updateExpireFlagTrue">强制过期</el-button>

          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
          <el-button v-else type="primary" @click="updateData">确定</el-button>


        </div>
      </div>
    </el-dialog>


    <!-- 选择商品对话框 -->
    <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogGoodsVisible" width="80%">
      <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container">
          <el-input v-model="goodsQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
          <el-input v-model="goodsQuery.name" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
          <el-select v-model="goodsQuery.brandId" clearable class="filter-item" style="width: 150px;" placeholder="选择品牌">
            <el-option v-for="item in brandList" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
          <el-cascader class="filter-item" ref="belongClass"  style="width: 150px;"  placeholder="分类" :options="categoryList"
                       v-model="categoryIds" expand-trigger="hover" @change="handleCategoryChange"/>

          <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>
          <el-button type="primary" class="filter-item" @click="addCheckSelGoodsData">确定添加</el-button>
        </div>
        <!-- 查询结果 -->
        <el-table
          v-loading="listLoading"
          :data="goodsList"
          element-loading-text="正在查询中。。。"
          border
          fit
          highlight-current-row
          @selection-change="changeSelGoodsList">
          <!--<el-table-column type="selection"/>-->
          <el-table-column type="selection"/>

<!--          <el-table-column-->
<!--            label="操作"-->
<!--            width="55">-->
<!--            -->
<!--            <template slot-scope="scope">-->
<!--              <el-checkbox v-model="scope.row.checked"></el-checkbox>-->
<!--            </template>-->
<!--          </el-table-column>-->

          <el-table-column align="center" label="商品编号" prop="goodsSn"/>

          <el-table-column align="center" min-width="100" label="名称" prop="name"/>

          <el-table-column align="center" min-width="100" label="规格型号" prop="specifications"/>

          <el-table-column align="center" property="iconUrl" label="图片">
            <template slot-scope="scope">
              <img :src="scope.row.picUrl" width="40">
            </template>
          </el-table-column>

          <el-table-column align="center" property="iconUrl" label="分享图">
            <template slot-scope="scope">
              <img :src="scope.row.shareUrl" width="40">
            </template>
          </el-table-column>

          <el-table-column align="center" label="原价" prop="counterPrice"/>

          <el-table-column align="center" label="当前价格" prop="retailPrice"/>

          <el-table-column align="center" label="是否上架" prop="isOnSale">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isOnSale ? 'success' : 'error' ">{{ scope.row.isOnSale ? '上架' : '上架' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="goodsTotal>0" :total="goodsTotal" :page.sync="goodsQuery.page" :limit.sync="goodsQuery.limit" @pagination="getGoodsList" />

        <el-tooltip placement="top" content="返回顶部">
          <back-to-top :visibility-height="100" />
        </el-tooltip>

      </div>

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
                   @click="handleFilterRebateOrder">查找
        </el-button>
      </div>
      <!-- 查询结果 -->
      <!-- 查询结果 -->
      <el-table v-loading="listLoading"
                :data="listOrder" element-loading-text="正在查询中。。。"
                border fit highlight-current-row>
        <el-table-column type="selection"/>
        <el-table-column align="left" label="操作"  class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['GET /admin/order/detail']" type="primary" size="mini"
                       @click="handleDetail(scope.row)">详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column align="center" min-width="200" label="订单编号" prop="orderSn"/>
        <!--          <el-table-column align="center" label="用户ID" prop="userId"/>-->
        <el-table-column align="center" min-width="90" label="订单状态" prop="orderStatusName">
        </el-table-column>
        <!--          <el-table-column align="center" label="订单金额" prop="orderPrice"/>-->
        <el-table-column align="center" label="支付金额" prop="actualPrice"/>
        <el-table-column align="center" label="支付时间" prop="payTime"/>
        <el-table-column align="center" label="物流单号" prop="shipSn"/>
        <el-table-column align="center" label="物流渠道" prop="shipChannel"/>

      </el-table>

      <pagination v-show="totalOrder>0" :total="totalOrder" :page.sync="listOrderQuery.page"
                  :limit.sync="listOrderQuery.limit" @pagination="findOrders"/>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>


  </div>
</template>
<style>
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
import { listGoodsRebaterule, deleteGoodsRebaterule, addGoodsRebaterule,
  updateGoodsRebaterule,detailGoodsRebaterule } from '@/api/goodsRebateRule'
import { listMainOrder,listGoodsRebateOrder } from '@/api/goodsRebateOrder'
import { listGoods,listCatAndBrand,getGoodsProduct,listGoodsProduct,goodsMain,goodsProductById } from '@/api/goods'

import { listDiccode } from '@/api/diccode'
import { fetchList } from '@/api/user'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'
import { createStorage, uploadPath } from '@/api/storage'
import { detailOrder} from '@/api/order'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'

import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'

import * as constIndex from '@/utils/index.js'

export default {
  name: 'GoodsRebateRule',
  components: { BackToTop, Pagination,ViewOrderInfo },
  data() {
    return {
      statusMap:constIndex.statusMap,
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
        ruleId:undefined,
        sort: 'add_time desc',
      },
      orderDetail: {
        order: {},
        fapiao:{},//发票
        coupon:{},//优惠券
        user: {},
        orderGoods: []
      },

      listOrder: [],
      totalOrder: 0,

      uploadPath,
      orderFormVisible: false, //是否显示订单窗口
      orderDialogVisible: false, //是否显示订单详情
      orderDetailId:undefined,
      modiUserVisiable:false,
      isRouteActive:true,
      goodsList: undefined, // 可参与活动商品，应该剔除那些已经参与其它活动的商品，包括团购，秒杀，会员活动中已经存在的商品，该商品信息要精确到规格型号
      goodsTotal: 0, // 可参与活动商品总计

      checkBoxSelGoodsListData:[],//选择的商品数据

      list: [], //品项折扣活动列表
      total: 0,
      goodsRebateGoodsList:[],//品项折扣活动商品列表
      listLoading: true,
      brandList: [],
      categoryIds: [],

      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsSn:undefined,
        goodsName:undefined,
        name:undefined,
        expireFlag:0,
        sort: 'add_time desc',
      },
      // 可推荐列表查询
      goodsQuery: {
        page: 1,
        limit: 10,
        goodsSn: undefined,
        name: undefined,
        brandId: undefined,
        categoryId: undefined,
        comId: 1,
        isOnSale:1,
        ifNotIncludeHuodong:true,
        sort: 'add_time desc',
      },

      downloadLoading: false,
      dataForm: {
        name:undefined,
        comId:undefined,
        comName:undefined,
        rebate: undefined,
        beginDate: undefined,
        endDate: undefined,
        expireFlag:false,
        remark: undefined,
      },

      dialogFormVisible: false,
      dialogGoodsVisible:false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
          name : [{ required: true, message: '必填', trigger: 'blur' }],
          rebate : [{ required: true, message: '必填', trigger: 'blur' }],
          beginDate : [{ required: true, message: '必填', trigger: 'blur' }],
          endDate : [{ required: true, message: '必填', trigger: 'blur' }],
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
    this.goodsRebateGoodsList=[];
    this.getList();
    this.getCatL1(1)
  },
  methods: {
    handleDetail(row) {
      // detailOrder(row.orderId).then(response => {
      //   this.orderDetail = response.data.data
      //   console.log(this.orderDetail)
      // })
      this.orderDetailId=row.orderId;
      this.orderDialogVisible = true
    },
    findOrders(rulesId) {
      this.orderFormVisible=true;
      this.listLoading = true
      this.listOrderQuery.ruleId=rulesId
      listMainOrder(this.listOrderQuery).then(response => {
        this.listOrder = response.data.data.list
        this.totalOrder = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.listOrder = []
        this.totalOrder = 0
        this.listLoading = false
      })
    },

    handleFilterRebateOrder() {
      this.listOrderQuery.page = 1
      this.findOrders(this.listOrderQuery.ruleId)
    },
    updateExpireFlagTrue(){
      if(confirm("确实要强制更新过期吗?")==false){
          return;
      }
      this.dataForm.expireFlag=true;
      this.updateData();

    },
    updateExpireFlagFalse(){
      if(confirm("确实要启动活动吗?")==false){
        return;
      }
      this.dataForm.expireFlag=false;
      this.updateData();
    },
    handleCategoryChange(value) {
        this.goodsQuery.categoryId = value[value.length - 1]
        //this.goods.categoryName= value[value.length - 1]
    },
    getCatL1(comId){
        this.categoryList=[];
        // this.goods.categoryId=null;
        this.brandList=[];
        // this.goods.brandId=null;
        listCatAndBrand(comId).then(response => {
            this.categoryList = response.data.data.categoryList
            this.brandList = response.data.data.brandList
        })
    },
    getList() {
      this.listLoading = true
        listGoodsRebaterule(this.listQuery).then(response => {
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
          name:undefined,
          comId:undefined,
          comName:undefined,
          rebate: undefined,
          beginDate: undefined,
          endDate: undefined,
          expireFlag:false,
          remark: undefined,
      }
    },
    handleCreate() {
      this.resetForm()
      this.goodsRebateGoodsList=[];
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    getRepeatVerify(){
      //提交前判断一下会员商品品项是否重复
      var repeatError='';

      if(this.goodsRebateGoodsList!=null&&this.goodsRebateGoodsList.length>0){

        for (var i = 0; i < this.goodsRebateGoodsList.length; i++) {
          var find = false;
          var findRow=i+1;
          for (var j = i + 1; j < this.goodsRebateGoodsList.length; j++) {
            if (this.goodsRebateGoodsList[i].goodsProductId == this.goodsRebateGoodsList[j].goodsProductId&&

              (this.goodsRebateGoodsList[i].deleted==0||this.goodsRebateGoodsList[i].deleted==undefined)
              &&
              (this.goodsRebateGoodsList[j].deleted==0||this.goodsRebateGoodsList[j].deleted==undefined))
            {
              find = true;
              break;
            }
          }
          if (find) {
            repeatError+="商品:【"+this.goodsRebateGoodsList[i].goodsName+"】，规格：【"+this.goodsRebateGoodsList[i].goodsProductSpecifications+"】重复添加"
          }
        }
      }
      return repeatError;
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var repeatError= this.getRepeatVerify()
          if(repeatError!=''){
            alert(repeatError);
            // this.$message.error(repeatError);
            return;
          }
          const goodsRebateRuleData = {
            goodsRebateRule: this.dataForm,
            goodsRebateRuleGoods: this.goodsRebateGoodsList
          }
            addGoodsRebaterule(goodsRebateRuleData).then(response => {
            // this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$message.success('创建品项折扣活动规则成功');

            this.getList()
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.getGoodsRuleDetail();
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var repeatError= this.getRepeatVerify()
          if(repeatError!=''){
            alert(repeatError);
            // this.$message.error(repeatError);
            return;
          }
          const goodsRebateRuleData = {
              goodsRebateRule: this.dataForm,
              goodsRebateRuleGoods: this.goodsRebateGoodsList
          }
            updateGoodsRebaterule(goodsRebateRuleData).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新品项折扣活动规则成功');
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
        deleteGoodsRebaterule(row).then(response => {
        this.$message.success('删除会员专享活动规则成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },


    //以下处理选择商品事件
    /**
     * 获取检索的商品列表
     */

    handleSelectGoods(){
        //this.dialogFormVisible=false;
        this.goodsList=[]
        this.dialogGoodsVisible=true;
        this.listLoading = true
        listGoodsProduct(this.goodsQuery).then(response => {
            this.goodsList = response.data.data.list
            console.log(this.goodsList)
            this.goodsTotal = response.data.data.total
            this.listLoading = false
        }).catch(() => {
            this.goodsList = []
            this.goodsTotal = 0
            this.listLoading = false
        })
    },

    getGoodsList() {
      this.listLoading = true
        listGoodsProduct(this.goodsQuery).then(response => {
        this.goodsList = response.data.data.list
            console.log(this.goodsList)
        this.goodsTotal = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.goodsList = []
        this.goodsTotal = 0
        this.listLoading = false
      })
    },
    /**
     * 进行检索
     */
    handleGoodsFilter() {
      this.goodsQuery.page = 1
      this.getGoodsList()
      this.dialogGoodsVisible=true;
    },

    handleGoodsRebateGoodsDelete(row) {
      const index = this.goodsRebateGoodsList.indexOf(row)
      if (row.id == null) {
        this.goodsRebateGoodsList.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },


    getGoodsRuleDetail(){
      if(this.dataForm.id==null){
          this.dataForm = { onlyOne: true, expireFlag: false }
          this.goodsRebateGoodsList = []
        } else {
          detailGoodsRebaterule({id:this.dataForm.id}).then(response => {
              this.dataForm = response.data.data.goodsRebateRule
              this.goodsRebateGoodsList = response.data.data.goodsRebateGoodsList
            })
        }
      },
      changeSelGoodsList(val){
          this.checkBoxSelGoodsListData = val
      },
    addCheckSelGoodsData() {
      // if(this.goodsRuleUserForm.userClassAttr1Code==null&&this.goodsRuleUserForm.userClassAttr2Code==null&&this.goodsRuleUserForm.userId==null){
      //   const index = this.goodsRebateGoodsList.indexOf(this.goodsRuleUserForm)
      //
      //   this.goodsRebateGoodsList.splice(index,1)
      //   this.goodsRuleUserForm={}
      // }
      for (let selItem of  this.checkBoxSelGoodsListData) {
        let boo = false;
        for (let goods of  this.goodsRebateGoodsList) {
          if (selItem.id == goods.goodsProductId) {
            boo = true;
            break;
          }
        }
        if (boo == false) {
          this.goodsRebateGoodsList.push({
            ruleId:this.dataForm.id,
            goodsProductId:selItem.id,
            goodsId: selItem.goodsId,
            storeNum:selItem.remainNumber,
            goodsSn: selItem.goodsSn,
            goodsName: selItem.name,
            goodsProductSpecifications: selItem.specifications,
            sourcePrice: selItem.price,
            rebatePrice:(selItem.price*this.dataForm.rebate).toFixed(2),
          });
        }
      }
      this.dialogGoodsVisible=false
    },

},

}
</script>
