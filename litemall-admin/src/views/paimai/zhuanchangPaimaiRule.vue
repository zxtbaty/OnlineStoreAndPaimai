<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.zhuanchangName" clearable class="filter-item" style="width: 150px;" placeholder="请输活动名称"/>
      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-button v-permission="['GET /admin/zhuanchangPaimaiRule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/zhuanchangPaimaiRule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
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
          <el-button
                     type="primary" size="mini" style="width: 70px"  @click="handleReturnLockMoneyByZhuanChangId(scope.row.id)">退保证金</el-button>

          <el-button v-if="scope.row.orderId!=null"
                     type="primary" size="mini" style="width: 70px"  @click="handleViewDetail(scope.row.orderId)">查看订单</el-button>
          <el-button type="primary" size="mini" style="width: 70px" @click="handleOffer(scope.row.id)">出价记录</el-button>

          <el-button
                     type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button
                     type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>

      <el-table-column align="center" label="活动名称" prop="zhuanchangName"/>
      <!--<el-table-column align="center" label="商品品类" prop="categoryName"/>-->
      <el-table-column align="center" label="出品人" prop="authorName"/>
      <el-table-column align="center" label="开始时间" prop="beginTime"/>
      <el-table-column align="center" label="结束时间" prop="endTime"/>
      <el-table-column align="center" label="拍品数" prop="goodsCount"/>
      <el-table-column align="center" label="围观数" prop="visitCount"/>
      <el-table-column align="center" label="出价数" prop="auctionCount"/>
      <el-table-column align="center" label="出价人数" prop="auctionPersonCount"/>
      <el-table-column align="center" label="显示排序" prop="displayOrder"/>
<!--      <el-table-column align="center" label="是否启用" prop="enabled">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->

    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


    <!-- 添加或修改专场主对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :close-on-click-modal="false" width="900px" :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm" :rules="rules"  :model="dataForm"
                   status-icon label-position="left" label-width="100px" >
            <el-col :span="12"  >
              <el-form-item label="活动名称" prop="zhuanchangName">
                <el-input style="width: 200px" v-model="dataForm.zhuanchangName"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择公司" >
                <el-cascader ref="comClass" style="width: 200px"
                             :options="comList"
                             :props="{ label: 'name',value:'id',checkStrictly:true }"
                             v-model="comIds"
                            @change="handleComChange"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
            <el-form-item label="选择出品人">
              <el-select style="width: 200px" clearable v-model="dataForm.authorId" @change="handleAuthorChange">
                <el-option  v-for="item in authorList" :key="item.id"
                           :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="专场头图(750*364)">
                <el-upload
                  :action="uploadPath"
                  :headers="headers"
                  :show-file-list="false"
                  :on-success="uploadPicUrl"
                  :on-progress="uploadVideoProcess"
                  class="avatar-uploader"
                  accept=".jpg,.jpeg,.png,.gif"
                  list-type="picture-card">
                  <img v-if="dataForm.auctionPicHead" width="150" :src="dataForm.auctionPicHead" class="avatar">
                  <i v-else class="el-icon-plus avatar-uploader-icon"/>
                  <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="专场详情页大图(1125*1092)">
                <el-upload
                  style="width: 500px"
                  :action="uploadPath"
                  :headers="headers"
                  :limit="5"
                  :file-list="galleryBigFileList"
                  :on-exceed="uploadOverrun"
                  :on-success="handleGalleryBigUrl"
                  :on-remove="handleRemoveGalaryBig"
                  multiple
                  accept=".jpg,.jpeg,.png,.gif"
                  list-type="picture-card">
                  <i class="el-icon-plus"/>
                  <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="专场详情页头图(375*364)">
                <el-upload
                  style="width: 500px"
                  :action="uploadPath"
                  :headers="headers"
                  :limit="5"
                  :file-list="galleryFileList"
                  :on-exceed="uploadOverrun"
                  :on-success="handleGalleryUrl"
                  :on-remove="handleRemove"
                  multiple
                  accept=".jpg,.jpeg,.png,.gif"
                  list-type="picture-card">
                  <i class="el-icon-plus"/>
                  <el-progress v-if="videoFlag == true"  color="red"    :percentage="videoUploadPercent" style="margin-top:30px;" status="success"></el-progress>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="拍卖简介" prop="remark">
                <el-input   type="textarea"
                            :rows="2"
                            style="width: 200px" v-model="dataForm.auctionDesc"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="备注" prop="remark">
                <el-input type="textarea"  :rows="2" style="width: 200px" v-model="dataForm.remark"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="开始时间" prop="seckillBeginDate">
                <el-date-picker style="width: 200px"
                  v-model="dataForm.beginTime"
                  type="datetime"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="过期时间" prop="seckillEndDate">
                <el-date-picker style="width: 200px"
                  v-model="dataForm.endTime"
                  type="datetime"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd HH:mm:ss"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="非专场不显示" prop="ifDisplayDajia">
                <el-radio-group style="width: 200px" v-model="dataForm.ifDisplayDajia">
                  <el-radio :label="true">否</el-radio>
                  <el-radio :label="false">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="是否免邮" prop="freePost" >
                <el-radio-group style="width: 200px" v-model="dataForm.freePost">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="是否预展" prop="previewFlag" >
                <el-radio-group style="width: 200px" v-model="dataForm.previewFlag">
                  <el-radio :label="false">否</el-radio>
                  <el-radio :label="true">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
<!--            <el-col :span="12"  >-->
<!--              <el-form-item label="是否启用" prop="enabled" >-->
<!--                <el-radio-group style="width: 200px" v-model="dataForm.enabled">-->
<!--                  <el-radio :label="false">否</el-radio>-->
<!--                  <el-radio :label="true">是</el-radio>-->
<!--                </el-radio-group>-->
<!--              </el-form-item>-->
<!--            </el-col>-->
            <el-col :span="12"  >
              <el-form-item label="拍品数" prop="goodsCount">
                <el-input style="width: 200px" v-model="dataForm.goodsCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="围观数" prop="visitCount">
                <el-input style="width: 200px" v-model="dataForm.visitCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="出价数" prop="auctionCount">
                <el-input style="width: 200px" v-model="dataForm.auctionCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="出价人数" prop="auctionPersonCount">
                <el-input style="width: 200px" v-model="dataForm.auctionPersonCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="显示排序" prop="displayOrder">
                <el-input style="width: 200px" v-model="dataForm.displayOrder" :disabled="true"/>
              </el-form-item>
            </el-col>
          </el-form>
        </el-card>
        <el-card class="box-card">
          <div class="hot-header">
            <h3 class="title common-title left" >参与活动拍品</h3>
            <div class="right" style="padding-top: 15px">
              <el-button  size="mini" type="primary" @click="handleAddZhuangchangGoods">添加</el-button>
            </div>
          </div>
          <el-table :data="auctionZhuanchangGoodsCurrent">
<!--            <el-table-column property="goodsSn" label="商品编号">-->
<!--              <template slot-scope="scope"  >-->
<!--                <slot v-if="scope.row.deleted==1">-->
<!--                  <s style="color: red">{{scope.row.goodsSn}}</s>-->
<!--                </slot>-->
<!--                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >-->
<!--                  {{scope.row.goodsSn}}-->
<!--                </slot>-->
<!--              </template>-->
<!--            </el-table-column>-->
            <el-table-column property="goodsName" label="商品名称">
              <template slot-scope="scope"  >
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
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.goodsProductSpecifications}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.goodsProductSpecifications}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="sourcePrice" label="起拍价">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.minPrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.minPrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column property="rebatePrice" label="加价幅度">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.addPrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.addPrice}}
                </slot>
              </template>
            </el-table-column>

            <el-table-column property="rebatePrice" label="目前最高价">
              <template slot-scope="scope"  >
                <slot v-if="scope.row.deleted==1">
                  <s style="color: red">{{scope.row.maxPrice}}</s>
                </slot>
                <slot  v-else-if="scope.row.deleted==0||scope.row.deleted==null" >
                  {{scope.row.maxPrice}}
                </slot>
              </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="250" class-name="small-padding fixed-width">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" style="width: 70px"  @click="handleReturnLockMoneyByMxId(scope.row.orderId)">退保证金</el-button>

                <el-button v-if="scope.row.orderId!=null"
                           type="primary" size="mini" style="width: 70px"  @click="handleViewDetail(scope.row.orderId)">查看订单</el-button>
                <el-button
                  type="primary" size="mini" style="width: 70px" @click="handleOffer(scope.row.id)">出价记录</el-button>
                <el-button v-permission="['POST /admin/auctionZhuanchangRuleCurrent/update']"
                           type="primary" size="mini" @click="handleGoodsUpdate(scope.row)">编辑</el-button>
                <el-button  v-if="scope.row==null||scope.row.deleted==null||scope.row.deleted==0"  type="danger" size="mini" @click="handleZhuanchangGoodsDelete(scope.row)">删除</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">撤销</el-button>
                <el-button  v-else-if="scope.row.deleted==1"  type="primary" size="mini" @click="scope.row.deleted=0">查看</el-button>
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
    <el-dialog :title="'选择商品'" :close-on-click-modal="false"  :visible.sync="dialogGoodsVisible"
               width="80%">
      <find-goods-single :listGoodsQuery="goodsQuery"
                         :inComdId="goodsQuery.comId" :goodsOrProduct="'product'"
                         @closeAndReturn="closeAndReturn"  ></find-goods-single>

    </el-dialog>


    <!-- 添加或修改专场商品明细对话框 -->
    <el-dialog :title="textMap[dialogStatus]"  :close-on-click-modal="false"  customClass="customWidth"  :visible.sync="dialogZhuanchangGoodsVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="goodsForm" :rules="ruleGoods" :model="goodsForm"
                   status-icon label-position="left" label-width="100px" >
            <el-col :span="24" >
                <el-form-item label="商品编号" prop="goodsSn" >
                  <el-input style="width: 290px" v-model="goodsForm.goodsSn"  clearable :readonly="true"  >
                    <el-button slot="append"    type="primary"  icon="el-icon-search" @click="handleSelectGoods">选择</el-button>
                  </el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="商品名称" prop="goodsName">
                <el-input style="width: 200px" v-model="goodsForm.goodsName"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="规格型号" prop="goodsProductSpecifications">
                <el-input style="width: 200px" v-model="goodsForm.goodsProductSpecifications"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="拍品简介" prop="auctionDesc">
                <el-input type="textarea" :rows="2" style="width: 550px" v-model="goodsForm.auctionDesc"  />
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="最低起拍价" prop="minPrice">
                <el-input style="width: 200px" v-model="goodsForm.minPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="加价单位" prop="addPrice">
                <el-input style="width: 200px" v-model="goodsForm.addPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="最低出价数" prop="minPerson">
                <el-input  style="width: 200px" v-model="goodsForm.minPerson"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="活动库存" prop="storeNum">
                <el-input   style="width: 200px" v-model="goodsForm.storeNum"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="剩余库存" prop="remainNum">
                <el-input :disabled="true"  style="width: 200px" v-model="goodsForm.remainNum"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="备注" prop="remark">
                <el-input  style="width: 200px" v-model="goodsForm.remark"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="围观数" prop="visitCount">
                <el-input style="width: 200px" v-model="goodsForm.visitCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="出价数" prop="auctionCount">
                <el-input style="width: 200px" v-model="goodsForm.auctionCount" :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"   >
              <el-form-item label="出价人数" prop="auctionPersonCount">
                <el-input style="width: 200px" v-model="goodsForm.auctionPersonCount" :disabled="true"/>
              </el-form-item>
            </el-col>

            <el-col :span="12"  >
              <el-form-item label="预交保证金" prop="minPerson">
                <el-input  style="width: 200px" v-model="goodsForm.deposit"/>
              </el-form-item>
            </el-col>

<!--            <el-col :span="12" >-->
<!--              <el-form-item label="自动创建订单" prop="orderCreateAuto" >-->
<!--                <el-radio-group style="width: 200px" v-model="goodsForm.orderCreateAuto">-->
<!--                  <el-radio :label="false">否</el-radio>-->
<!--                  <el-radio :label="true">是</el-radio>-->
<!--                </el-radio-group>-->
<!--              </el-form-item>-->
<!--            </el-col>-->
          </el-form>
        </el-card>

        <div class="op-container" style="text-align: center;padding-top: 10px">
          <el-button type="primary" :disabled="dataForm.expireFlag==false" @click="updateExpireFlagFalse">正常可用</el-button>
          <el-button type="primary" :disabled="dataForm.expireFlag==true" @click="updateExpireFlagTrue">强制过期</el-button>

          <el-button @click="dialogZhuanchangGoodsVisible = false">取消</el-button>
          <el-button v-if="dialogStatus=='create'" type="primary" @click="createGoodsData">确定</el-button>
          <el-button v-else type="primary" @click="createGoodsData">确定</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 查询专场拍卖出价记录 -->
    <el-dialog :title="'查询出价记录'" customClass="customWidth" :close-on-click-modal="false"
              :before-close="handBeforeCloseOffer" :visible.sync="offerFormVisible">
      <user-offer-list :inType="'专场拍'" :inOfferQuery="offerQuery" ></user-offer-list>

    </el-dialog>

    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :before-close="orderDetailId=undefined"
               :close-on-click-modal="false" :visible.sync="orderFormVisible">
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
import { listZhuanchangRule, deleteZhuanchangRule,
    addZhuanchangRule, updateZhuanchangRule,detailZhuanchangRule,
  getZhuanchangGoodsDetail,unLockMoneyByRuleMxId,unLockMoneyByZhuanChangId } from '@/api/zhuanchangRule'
import { listCatAndBrand,listGoodsProduct } from '@/api/goods'
import { listCompany } from '@/api/company'

import { listDiccode } from '@/api/diccode'
import { queryAllAuthororcorp } from '@/api/authororcorp'

import BackToTop from '@/components/BackToTop'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
import { getToken } from '@/utils/auth'
import { createStorage, uploadPath } from '@/api/storage'
import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'
import {default as UserOfferList} from '@/components/Order/UserOfferList'
import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'


export default {
  name: 'GoodsRebateRule',
  components: { BackToTop, Pagination,FindGoodsSingle,FindGoodsMulti,UserOfferList,ViewOrderInfo },
  data() {
    return {
      uploadPath,
      orderDetailId:undefined,
      orderFormVisible:false,
      galleryBigFileList: [],
      galleryFileList: [],
      videoFlag:false,
      videoUploadPercent:0,
      modiUserVisiable:false,
      isRouteActive:true,
      authorList: [],

      comList:[],

      checkBoxSelGoodsListData:[],//选择的商品数据
      list: [], //专场拍活动列表
      total: 0,
      auctionZhuanchangGoodsCurrent:[],//参与专场拍卖活动商品列表
      listLoading: true,

      comIds: [],

      offerQuery:{
        type:'专场拍',
        page: 1,
        limit: 10,
        userId:undefined,
        rulesId:undefined,
        rulesMxId:undefined,
        userName:undefined,
        sort: 'add_time desc',
      },

      listQuery: {
        page: 1,
        limit: 20,
        goodsId: undefined,
        goodsSn:undefined,
        goodsName:undefined,
        zhuanchangName:undefined,
        expireFlag:0,
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
        comId:undefined,
        isOnSale:1,
        usedRange:["拍卖专用","全场通用"],
        authorId:undefined,
        sort: 'add_time desc',
      },
      downloadLoading: false,
      dataForm: {
        zhuanchangName:undefined,
        comId:undefined,
        comName:undefined,
        authorId:undefined,
        authorName:undefined,
        authorDesc:undefined,
        authorZuopin:undefined,
        auctionDesc:undefined,
        auctionPicHead:undefined,
        auctionGallery:[],
        auctionGalleryBig:[],
        beginTime: undefined,
        endTime: undefined,
        ifDisplayDajia: false,
        expireFlag: undefined,
        freePost: false,
        previewFlag: true,
        enabled: true,
        goodsCount: undefined,
        visitCount: undefined,
        auctionCount: undefined,
        auctionPersonCount: undefined,
        displayOrder: undefined,
        remark: undefined,
      },

      goodsForm: {
          zhuanchangId:undefined,
          zhuanchangName:undefined,
          goodsId:undefined,
          goodsSn:undefined,
          goodsName:undefined,
          goodsProductId:undefined,
          goodsProductSpecifications:undefined,
          auctionDesc: undefined,
          minPrice: undefined,
          addPrice: undefined,
          maxPrice: undefined,
          offerId: undefined,
          minPerson: 1,
          storeNum: undefined,
          remainNum: undefined,
          remark: undefined,
          visitCount: undefined,
          auctionCount: undefined,
          auctionPersonCount: undefined,
          freePost: true,
          orderCreateAuto: true,
          orderCreateFlag: '不应创建',
          galleryBig:undefined,
          galllerySmall:undefined,
          orderId: undefined,
          deposit: undefined,
        },
      offerFormVisible:false,
      currentSelectRuleId:undefined,//当前选中的规则ID
      dialogFormVisible: false,
      dialogGoodsVisible:false,
      dialogZhuanchangGoodsVisible:false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
          name : [{ required: true, message: '活动名称不能为空', trigger: 'blur' }]
      },
      ruleGoods: {
          addPrice : [{ required: true, message: '', trigger: 'blur' }],
          minPerson : [{ required: true, message: '', trigger: 'blur' }]
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
    this.getAllAuthororcorpList();
    this.getComList();
    this.getList();

  },
  methods: {
    handleReturnLockMoneyByZhuanChangId(zhuanChangId){
      if(confirm("出价最高者的保证金在用户完成订单支付后才会自动退还，其它的保证金批量退还，确实要退还吗?")==false){
        return;
      }
      unLockMoneyByZhuanChangId({zhuanChangId:ruleId,ifAll:false}).then((res)=>{
        if(res.data.data==true){
          this.$message.success("退还保证金成功");
          this.getRuleDetail();
        }
      })
    },
    handleReturnLockMoneyByMxId(ruleMxId){
      if(confirm("出价最高者的保证金在用户完成订单支付后才会自动退还，其它的保证金批量退还，确实要退还吗?")==false){
        return;
      }
      unLockMoneyByZhuanChangId({ruleMxId:ruleMxId,ifAll:false}).then((res)=>{
        if(res.data.data==true){
          this.$message.success("退还保证金成功");
          this.getList();
        }
      })
    },
    handleViewDetail(orderId){
      this.orderDetailId=orderId;
      this.orderFormVisible=true;
    },
    handBeforeCloseOffer(){
      this.$set(this.offerQuery,"rulesId",undefined);
      this.$set(this.offerQuery,"rulesMxId",undefined);
      this.offerFormVisible=false;
    },
    handleOffer(ruleId){
      this.offerFormVisible=true;
      this.$set(this.offerQuery,'rulesId',ruleId)
    },
    handleOfferDetail(ruleMxId){
      this.offerFormVisible=true;
      this.$set(this.offerQuery,'rulesMxId',ruleMxId)
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
    handleGalleryUrl(response, file, fileList) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;

      if (response.errno === 0) {
        if(this.dataForm.auctionGallery==null){
          this.dataForm.auctionGallery=[]
        }
        this.dataForm.auctionGallery.push(response.data.url)
      }
    },
    uploadOverrun: function() {
      this.$message({
        type: 'error',
        message: '上传文件个数超出限制!最多上传5张图片!'
      })
    },
    handleRemove: function(file, fileList) {
      for (var i = 0; i < this.dataForm.auctionGallery.length; i++) {
        // 这里存在两种情况
        // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
        //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
        // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
        var url
        if (file.response === undefined) {
          url = file.url
        } else {
          url = file.response.data.url
        }
        if (this.dataForm.auctionGallery[i] === url) {
          this.dataForm.auctionGallery.splice(i, 1)
        }
      }
    },
    handleGalleryBigUrl(response, file, fileList) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;
      if (response.errno === 0) {
        if(this.dataForm.auctionGalleryBig==null){
          this.dataForm.auctionGalleryBig=[]
        }
        this.dataForm.auctionGalleryBig.push(response.data.url)
      }
    },
    handleRemoveGalaryBig: function(file, fileList) {
      for (var i = 0; i < this.dataForm.auctionGalleryBig.length; i++) {
        // 这里存在两种情况
        // 1. 如果所删除图片是刚刚上传的图片，那么图片地址是file.response.data.url
        //    此时的file.url虽然存在，但是是本机地址，而不是远程地址。
        // 2. 如果所删除图片是后台返回的已有图片，那么图片地址是file.url
        var url
        if (file.response === undefined) {
          url = file.url
        } else {
          url = file.response.data.url
        }
        if (this.dataForm.auctionGalleryBig[i] === url) {
          this.dataForm.auctionGalleryBig.splice(i, 1)
        }
      }
    },
    closeAndReturn(val){
      console.log(val);
      this.dialogGoodsVisible=false;
      //返回的单一会员商品进行赋值
      if(val==null){
        return
      }
      this.goodsForm.zhuanchangId=this.dataForm.id;
      this.goodsForm.zhuanchangName=this.dataForm.name;
      this.goodsForm.goodsProductId=val.goodsProductId
      this.goodsForm.goodsId=val.goodsId
      this.goodsForm.goodsSn=val.goodsSn
      this.goodsForm.goodsName=val.goodsName
      this.goodsForm.auctionPicHead=val.picUrl
      this.goodsForm.goodsProductSpecifications=val.specifications
      this.goodsForm.auctionDesc=val.brief;
      this.goodsForm.minPrice=val.price;
      // this.goodsForm.storeNum=selItem.number;
      // this.goodsForm.remainNum=selItem.number;
      // this.getListGoodsProduct()
    },

    handleComChange(value) {
        this.dataForm.comId = value[value.length - 1]
        this.goodsQuery.comId=this.dataForm.comId
        this.listQuery.comId=this.dataForm.comId
        this.dataForm.comName=this.$refs['comClass'].currentLabels
        //this.goods.categoryName= value[value.length - 1]
    },
    handleAuthorChange(){
        if(this.authorList==null){
            return
        }
        // 晕，搞了好长时间，重新赋值，才会刷新页面
        const b =this.authorList;
        this.authorList=[];
        this.authorList=b;
        const obj = this.authorList.find((item) => {
            return item.id == this.dataForm.authorId
        })
        this.dataForm.authorName = obj.name

    },
  uploadPicUrl: function(response) {
      this.videoFlag = false;
      this.videoUploadPercent = 0;

      this.dataForm.auctionPicHead = response.data.url
  },
  uploadVideoProcess(event, file, fileList) {
      this.videoFlag = true;
      this.videoUploadPercent =parseInt(file.percentage.toFixed(0)) ;
  },
      getAllAuthororcorpList() {
          queryAllAuthororcorp()
              .then(response => {
                  this.authorList = response.data.data.list;

              })
              .catch(() => {
              })
      },
    getComList() {
        listCompany(this.listQuery)
            .then(response => {
                this.comList = response.data.data.list;
                if(this.comList!=null&&this.comList.length>0){
                    this.comIds[0]=this.comList[0].id;
                    this.dataForm.comId=this.comList[0].id;
                    this.goodsQuery.comId=this.dataForm.comId;
                }
            })
            .catch(() => {
            })
    },

    getList() {
      this.listLoading = true
        listZhuanchangRule(this.listQuery).then(response => {
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
          zhuanchangName:undefined,
          comId:undefined,
          comName:undefined,
          authorId:undefined,
          authorName:undefined,
          authorDesc:undefined,
          authorZuopin:undefined,
          auctionDesc:undefined,
          auctionPicHead:undefined,
          auctionGallery:[],
          auctionGalleryBig:[],
          beginTime: undefined,
          endTime: undefined,
          ifDisplayDajia: false,
          expireFlag: undefined,
          freePost: false,
          previewFlag: true,
          enabled: true,
          goodsCount: undefined,
          visitCount: undefined,
          auctionCount: undefined,
          auctionPersonCount: undefined,
          remark: undefined,
      }
    },
    handleCreate() {
      this.galleryBigFileList=[]
      this.galleryFileList=[]
      this.resetForm()
      this.auctionZhuanchangGoodsCurrent=[]
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const auctionZhuanchangRuleCurrentAllInOne = {
              zhuanchangRuleCurrent: this.dataForm,
              zhuanchangGoodsCurrents: this.auctionZhuanchangGoodsCurrent
          }
            addZhuanchangRule(auctionZhuanchangRuleCurrentAllInOne).then(response => {
            // this.list.unshift(response.data.data)
            this.dialogFormVisible = false
            this.$message.success('创建专场拍卖活动规则成功');

            this.getList()
          }).catch(response => {
            this.$message.error( '失败:'+response.data.errmsg);
          })
        }
      })
    },
      //新增专场商品明细
    createGoodsData() {
        let boo = false;
        let findGoods=this.goodsForm;
        let sourceGoods=undefined;
        if(this.goodsForm.id!=null){
            for (let goods of  this.auctionZhuanchangGoodsCurrent) {
                if (this.goodsForm.id == goods.id) {
                    const index = this.auctionZhuanchangGoodsCurrent.indexOf(goods);
                    this.auctionZhuanchangGoodsCurrent.splice(index,1,findGoods)
                    boo = true;
                    break;
                }
            }
        }
        if (boo == false) {
            this.auctionZhuanchangGoodsCurrent.push(findGoods)
        }
        this.dialogZhuanchangGoodsVisible=false
    },
    handleUpdate(row) {
      this.resetForm()
      this.dataForm = Object.assign({}, row)
      this.getRuleDetail();
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    handleGoodsUpdate(row) {
        this.goodsForm = Object.assign({}, row)
        // getZhuanchangGoodsDetail({id:row.id}).then(
        //     (res)=>{
        //         this.goodsForm=res.data.data
        //     }
        // );

        this.dialogStatus = 'update'
        this.dialogZhuanchangGoodsVisible = true
        this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate()
        })
    },

    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
            const auctionZhuanchangRuleCurrentAllInOne = {
                zhuanchangRuleCurrent: this.dataForm,
                zhuanchangGoodsCurrents: this.auctionZhuanchangGoodsCurrent
            }
            updateZhuanchangRule(auctionZhuanchangRuleCurrentAllInOne).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新专场拍卖活动规则成功');
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
        deleteZhuanchangRule(row).then(response => {
        this.$message.success('删除专场拍卖活动规则成功');

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

    handleAddZhuangchangGoods(){
        this.goodsForm={
            zhuanchangId:undefined,
            zhuanchangName:undefined,
            goodsId:undefined,
            goodsSn:undefined,
            goodsName:undefined,
            goodsProductId:undefined,
            goodsProductSpecifications:undefined,
            minPrice: undefined,
            addPrice: undefined,
            maxPrice: undefined,
            offerId: undefined,
            minPerson: 1,
            storeNum: undefined,
            remainNum: undefined,
            remark: undefined,
            visitCount: undefined,
            auctionCount: undefined,
            auctionPersonCount: undefined,
            freePost: true,
            orderCreateAuto: true,
            orderCreateFlag: '不应创建',
            orderId: undefined,}
        this.dialogZhuanchangGoodsVisible=true;
    },

      handleSelectGoods(){

          this.dialogGoodsVisible=true;

      },


    handleZhuanchangGoodsDelete(row) {
      const index = this.auctionZhuanchangGoodsCurrent.indexOf(row)
      if (row.id == null) {
        this.auctionZhuanchangGoodsCurrent.splice(index, 1)
      } else {
        row.deleted = 1
      }
    },
    getRuleDetail(){
      if(this.dataForm.id==null){
          this.dataForm = { onlyOne: true, expireFlag: false }
          this.auctionZhuanchangGoodsCurrent = []
        } else {
          detailZhuanchangRule({id:this.dataForm.id}).then(response => {
              this.dataForm = response.data.data.ruleCurrent
              this.galleryBigFileList = []
              if(this.dataForm.auctionGalleryBig!=null){
                for (var i = 0; i < this.dataForm.auctionGalleryBig.length; i++) {
                  this.galleryBigFileList.push({
                    url: this.dataForm.auctionGalleryBig[i]
                  })
                }
              }

              this.galleryFileList = []
              if(this.dataForm.auctionGallery!=null){
                for (var i = 0; i < this.dataForm.auctionGallery.length; i++) {
                  this.galleryFileList.push({
                    url: this.dataForm.auctionGallery[i]
                  })
                }
              }

              this.auctionZhuanchangGoodsCurrent = response.data.data.goodsList
            })
        }
      },


},

}
</script>
