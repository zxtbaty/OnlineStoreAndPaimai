<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 110px;" placeholder="商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" style="width: 110px;" placeholder="过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-select v-model="listQuery.offerFlag" style="width: 110px;" clearable class="filter-item" placeholder="出价标志">
        <el-option  label="有出价" :value="1"/>
        <el-option  label="没出价"  :value="0"/>
      </el-select>
      <el-select v-model="listQuery.enabled" style="width: 110px;" clearable class="filter-item" placeholder="启用标志">
        <el-option  label="已启用" :value="1"/>
        <el-option  label="未启用"  :value="0"/>
      </el-select>
      <el-select v-model="listQuery.orderCreateFlag" style="width: 135px;" clearable class="filter-item" placeholder="订单创建标志">
        <el-option  label="不应创建" :value="'不应创建'"/>
        <el-option  label="应建未建"  :value="'应建未建'"/>
        <el-option  label="已经创建"  :value="'已经创建'"/>
      </el-select>
      <el-button v-permission="['GET /admin/dajiaPaimaiRule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/dajiaPaimaiRule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>

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
      <el-table-column align="center" label="操作" width="400px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="primary" v-if="scope.row.offerFlag!=null" size="mini" style="width: 70px"  @click="handleReturnLockMoney(scope.row.id)">退保证金</el-button>

          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.orderId!=null"
                     type="primary" size="mini" style="width: 70px"  @click="handleViewDetail(scope.row.orderId)">查看订单</el-button>
<!--          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.orderId==null&&scope.row.auctionCount>0"-->
<!--                     type="primary" size="mini" style="width: 70px" @click="handleCreateOrder(scope.row)">生成订单</el-button>-->
          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']"
                     type="primary" size="mini" style="width: 70px" @click="handleOffer(scope.row.id)">出价记录</el-button>
          <el-button v-permission="['POST /admin/dajiaPaimaiRule/update']"
                     type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/dajiaPaimaiRule/delete']"
                     type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.auctionPicHead" width="40">
        </template>
      </el-table-column>
      <el-table-column align="center" label="商品Id" prop="goodsId"/>
      <el-table-column align="center" label="商品名称" prop="goodsName"/>
      <el-table-column align="center" label="商品规格" prop="goodsProductSpecifications"/>
      <el-table-column align="center" label="起拍价" prop="minPrice"/>
      <el-table-column align="center" label="加价单位" prop="addPrice"/>
      <el-table-column align="center" label="最高出价" prop="maxPrice"/>

      <el-table-column align="center" label="开始时间" prop="beginTime"/>
      <el-table-column align="center" label="结束时间" prop="endTime"/>
      <el-table-column align="center" label="显示排序" prop="displayOrder"/>
<!--      <el-table-column align="center" label="是否启用" prop="enabled">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改专场商品明细对话框 -->
    <el-dialog :title="textMap[dialogStatus]"  :close-on-click-modal="false"  customClass="customWidth"  :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm" :rules="ruleGoods" :model="dataForm"
                   status-icon label-position="left" label-width="100px" >
            <el-col :span="24" >
              <el-form-item label="商品编号" prop="goodsSn" >
                <el-input style="width: 290px" v-model="dataForm.goodsSn"  clearable :readonly="true"  >
                  <el-button slot="append"    type="primary"  icon="el-icon-search" @click="handleSelectGoods">选择</el-button>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="商品头图" prop="auctionPicHead">
                <img  :src="dataForm.auctionPicHead"  >
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="商品名称" prop="goodsName">
                <el-input style="width: 200px" v-model="dataForm.goodsName"  :disabled="true"/>
              </el-form-item>
            </el-col>

            <el-col :span="12" >
              <el-form-item label="商品名称" prop="goodsProductSpecifications">
                <el-input style="width: 200px" v-model="dataForm.goodsProductSpecifications"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="最低起拍价" prop="minPrice">
                <el-input style="width: 200px" v-model="dataForm.minPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="加价单位" prop="addPrice">
                <el-input style="width: 200px" v-model="dataForm.addPrice"/>
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="最低出价数" prop="minPerson">
                <el-input  style="width: 200px" v-model="dataForm.minPerson"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="预交保证金" prop="minPerson">
                <el-input  style="width: 200px" v-model="dataForm.deposit"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="活动库存" prop="storeNum">
                <el-input   style="width: 200px" v-model="dataForm.storeNum"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="剩余库存" prop="remainNum">
                <el-input :disabled="true"  style="width: 200px" v-model="dataForm.remainNum"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="开始时间" prop="beginTime">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.beginTime"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="过期时间" prop="endTime">
                <el-date-picker style="width: 200px"
                                v-model="dataForm.endTime"
                                type="datetime"
                                placeholder="选择日期"
                                value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="是否免邮" prop="freePost" >
                <el-radio-group style="width: 200px" v-model="dataForm.freePost">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="备注" prop="remark">
                <el-input  style="width: 200px" v-model="dataForm.remark"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="显示排序" prop="displayOrder">
                <el-input  style="width: 200px" v-model="dataForm.displayOrder"/>
              </el-form-item>
            </el-col>
<!--            <el-col :span="12" >-->
<!--              <el-form-item label="是否启用" prop="enabled" >-->
<!--                <el-radio-group style="width: 200px" v-model="dataForm.enabled">-->
<!--                  <el-radio :label="false">否</el-radio>-->
<!--                  <el-radio :label="true">是</el-radio>-->
<!--                </el-radio-group>-->
<!--              </el-form-item>-->
<!--            </el-col>-->
<!--            <el-col :span="12" >-->
<!--              <el-form-item label="自动创建订单" prop="orderCreateAuto" >-->
<!--                <el-radio-group style="width: 200px" v-model="dataForm.orderCreateAuto">-->
<!--                  <el-radio :label="false">否</el-radio>-->
<!--                  <el-radio :label="true">是</el-radio>-->
<!--                </el-radio-group>-->
<!--              </el-form-item>-->
<!--            </el-col>-->
            <el-col :span="12"  >
              <el-form-item label="围观数" prop="visitCount">
                <el-input style="width: 200px" v-model="dataForm.visitCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="出价数" prop="auctionCount">
                <el-input style="width: 200px" v-model="dataForm.auctionCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="出价人数" prop="auctionPersonCount">
                <el-input style="width: 200px" v-model="dataForm.auctionPersonCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="拍品描述" prop="auctionDesc">
                <el-input   type="textarea"
                            :rows="2"
                            style="width: 550px" v-model="dataForm.auctionDesc"/>
              </el-form-item>
            </el-col>
          </el-form>
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
    <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogGoodsVisible"
               width="80%">
      <find-goods-single :listGoodsQuery="goodsQuery"
                         :inComdId="goodsQuery.comId" :goodsOrProduct="'product'"
                         @closeAndReturn="closeAndReturn"  ></find-goods-single>
    </el-dialog>

    <!-- 查询大家拍卖出价记录 -->
    <el-dialog :title="'查询出价记录'" customClass="customWidth"
               :close-on-click-modal="false" :before-close="handleBeforeCloseOffer" :visible.sync="offerFormVisible">
      <user-offer-list :inType="'大家拍'" :inOfferQuery="offerQuery" ></user-offer-list>

    </el-dialog>

    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>


  </div>
</template>
<style>
  .v-modal{position: relative}
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
import { listDajiaPaimaiRule, deleteDajiaPaimaiRule,
    addDajiaPaimaiRule, updateDajiaPaimaiRule,
    detailDajiaPaimaiRule,unLockMoney } from '@/api/dajiaPaimaiRule'

import { listDajiaPaimaiOffer } from '@/api/dajiaPaimaiOffer'
import { listGoods,listCatAndBrand,getGoodsProduct,
  listGoodsProduct,goodsMain,goodsProductById } from '@/api/goods'

import { listDiccode } from '@/api/diccode'
import { fetchList } from '@/api/user'
import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'
import { createStorage, uploadPath } from '@/api/storage'
import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
import {default as UserOfferList} from '@/components/Order/UserOfferList'



export default {
  name: 'privateMakeRule',
  components: { BackToTop, Pagination,FindGoodsSingle,FindGoodsMulti,ViewOrderInfo,UserOfferList },
  data() {
    return {
      uploadPath,

      list: [], //大家拍卖活动列表
      total: 0,
      offerList:[],//大家拍出价记录列表
      offerListTotal:0,
      listLoading: true,
      sendway_select:["快递","自提取货"],
      offerQuery:{
          type:'大家拍',
          page: 1,
          limit: 20,
          userId:undefined,
          rulesId:undefined,
          userName:undefined,
          sort: 'add_time desc',
      },
      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsSn:undefined,
        goodsName:undefined,
        name:undefined,
        expireFlag:undefined,
        offerId:undefined,
        enabled:undefined,
        offerFlag:undefined,
        orderCreateFlag:undefined,
        sort: 'add_time desc',
      },
      // 可推荐列表查询
      goodsQuery: {
        goodsOrProduct:"product",
        page: 1,
        limit: 10,
        goodsSn: undefined,
        name: undefined,
        brandId: undefined,
        categoryId: undefined,
        comId: undefined,
        isOnSale:1,
        usedRange:["拍卖专用","全场通用"],
        authorId:undefined,
        sort: 'add_time desc',

      },

      dataForm: {
        minPrice:undefined,
        addPrice: undefined,
        minPerson: 1,
        beginTime: undefined,
        endTime: undefined,
        expireFlag: false,
        freePost: true,
        goodsId: undefined,
        goodsSn: undefined,
        goodsName: undefined,
        goodsProductId: undefined,
        goodsProductSpecifications: undefined,
        auctionPicHead: undefined,
        dajiapaiCategoryCode: undefined,
        dajiapaiCategoryName: undefined,
        auctionDesc: undefined,
        storeNum: undefined,
        remainNum: undefined,
        remark: undefined,
        maxPrice: undefined,
        offerId: undefined,
        offerFlag: false,
        orderCreateAuto: true,
        orderCreateFlag: '不应创建',
        orderId: undefined,
        enabled: true,
        displayOrder:undefined,
        deposit:undefined,
      },

      dialogFormVisible: false,
      dialogGoodsVisible:false,
      offerFormVisible:false,
      orderFormVisible:false,
      orderDetailId:undefined,

      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      ruleGoods: {
          addPrice : [{ required: true, message: '加价单位不能为空', trigger: 'blur' }],
          minPerson : [{ required: true, message: '最低出价人数不能为空', trigger: 'blur' }],
          beginTime : [{ required: true, message: '拍卖起始时间不能为空', trigger: 'blur' }],
          endTime : [{ required: true, message: '拍卖截止时间不能为空', trigger: 'blur' }],
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
    this.getList();

  },
  methods: {
    // 批量解锁保证金
    handleReturnLockMoney(ruleId){
      if(confirm("出价最高者的保证金在用户完成订单支付后才会自动退还，其它的保证金批量退还，确实要退还吗?")==false){
        return;
      }
      unLockMoney({ruleMxId:ruleId,ifAll:false}).then((res)=>{
        if(res.data.data==true){
          this.$message.success("退还保证金成功");
          this.getList();
        }
      })
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
    handleViewDetail(orderId){
      this.orderDetailId=orderId;
      this.orderFormVisible=true;
    },
    closeAndReturn(val){
      console.log(val);
      this.dialogGoodsVisible=false;
      //返回的单一会员商品进行赋值
      if(val==null){
        return
      }
      this.dataForm.goodsProductId=val.goodsProductId
      this.dataForm.goodsId=val.goodsId
      this.dataForm.goodsSn=val.goodsSn
      this.dataForm.goodsName=val.goodsName
      this.dataForm.auctionPicHead=val.picUrl
      this.dataForm.goodsProductSpecifications=val.specifications
      this.dataForm.dajiapaiCategoryCode=val.dajiapaiCategoryCode;
      this.dataForm.dajiapaiCategoryName=val.dajiapaiCategoryName;
      this.dataForm.auctionDesc=val.brief;
      this.dataForm.minPrice=val.price;
      // this.goodsForm.storeNum=selItem.number;
      // this.goodsForm.remainNum=selItem.number;
    },

    handleBeforeCloseOffer(){
      this.$set(this.offerQuery,"rulesId",undefined);
      this.$set(this.offerQuery,"rulesMxId",undefined);
      this.offerFormVisible=false;
    },

    handleOffer(ruleId){
      this.$set(this.offerQuery,"rulesId",ruleId);
      this.offerFormVisible=true;

    },
    handleFilter() {
        this.listQuery.page = 1
        this.getList()
    },

    getList() {
      this.listLoading = true
        listDajiaPaimaiRule(this.listQuery).then(response => {
        this.list = response.data.data.list
        this.total = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },

    resetForm() {
      this.dataForm = {
          minPrice:undefined,
          addPrice: undefined,
          minPerson: 1,
          beginTime: undefined,
          endTime: undefined,
          expireFlag: false,
          freePost: true,
          goodsId: undefined,
          goodsSn: undefined,
          goodsName: undefined,
          goodsProductId: undefined,
          goodsProductSpecifications: undefined,
          auctionPicHead: undefined,
          dajiapaiCategoryCode: undefined,
          dajiapaiCategoryName: undefined,
          auctionDesc: undefined,
          storeNum: undefined,
          remainNum: undefined,
          remark: undefined,
          maxPrice: undefined,
          offerId: undefined,
          offerFlag: false,
          orderCreateAuto: true,
          orderCreateFlag: '不应创建',
          orderId: undefined,
          enabled: true
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

            addDajiaPaimaiRule(this.dataForm).then(response => {
            // this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$message.success('创建大家拍活动规则成功');

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
    getGoodsRuleDetail(){
        if(this.dataForm.id==null){
            this.dataForm = {  expireFlag: false,offerFlag: false,enabled: true }
        } else {
            detailDajiaPaimaiRule({id:this.dataForm.id}).then(response => {
                this.dataForm = response.data.data
            })
        }
    },

    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
            updateDajiaPaimaiRule(this.dataForm).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新大家拍活动规则成功');
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
        deleteDajiaPaimaiRule(row).then(response => {
        this.$message.success('删除大家拍活动规则成功');

        const index = this.list.indexOf(row)
        this.list.splice(index, 1)
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },
      getRuleDetail(){
          if(this.dataForm.id==null){
              this.dataForm = {}
          } else {
              detailDajiaPaimaiRule({id:this.dataForm.id}).then(response => {
                  this.dataForm = response.data.data
              })
          }
      },


      //以下处理选择商品事件
      /**
       * 获取检索的商品列表
       */

      handleSelectGoods(){
          this.goodsList=[]
          this.dialogGoodsVisible=true;
          this.listLoading = true
          this.goodsQuery.authorId=this.dataForm.authorId;
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

},

}
</script>
