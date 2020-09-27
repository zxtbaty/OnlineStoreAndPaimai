<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-input v-model="listQuery.goodsSn" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品编号"/>
      <el-input v-model="listQuery.goodsName" clearable class="filter-item" style="width: 150px;" placeholder="请输入商品名称"/>
      <el-select v-model="listQuery.expireFlag" clearable class="filter-item" style="width: 150px;" placeholder="请选择过期标志">
        <el-option  label="未过期" :value="0"/>
        <el-option  label="过期"  :value="1"/>
      </el-select>
      <el-select v-model="listQuery.offerFlag" style="width: 150px;" clearable class="filter-item" placeholder="请选择出价标志">
        <el-option  label="有出价" :value="1"/>
        <el-option  label="没出价"  :value="0"/>
      </el-select>
      <el-select v-model="listQuery.enabled" style="width: 150px;" clearable class="filter-item" placeholder="请选择启用标志">
        <el-option  label="已启用" :value="1"/>
        <el-option  label="未启用"  :value="0"/>
      </el-select>
      <el-button v-permission="['GET /admin/privateMakeRule/list']" class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['POST /admin/privateMakeRule/create']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
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
      <el-table-column align="center" label="操作" width="270px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/privateMakeOrder/list']" type="primary" size="mini" style="width:100px" @click="handleOffer(scope.row)">私人定制订单</el-button>
          <el-button v-permission="['POST /admin/privateMakeRule/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['POST /admin/privateMakeRule/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" property="iconUrl" label="图片">
        <template slot-scope="scope">
          <img :src="scope.row.privatePicHead" width="40">
        </template>
      </el-table-column>
      <el-table-column align="center" label="出品人" prop="authorName"/>
      <el-table-column align="center" label="商品Id" prop="goodsId"/>
      <el-table-column align="center" label="商品名称" prop="goodsName"/>
      <el-table-column align="center" label="商品规格" prop="goodsProductSpecifications"/>
      <el-table-column align="center" label="报价下沿" prop="lowPrice"/>
      <el-table-column align="center" label="报价上限" prop="maxPrice"/>
      <el-table-column align="center" label="定价描述" prop="priceDesc"/>
      <el-table-column align="center" label="预付订金" prop="dingjinPrice"/>

      <el-table-column align="center" label="开始时间" prop="beginTime"/>
      <el-table-column align="center" label="结束时间" prop="endTime"/>
<!--      <el-table-column align="center" label="显示排序" prop="displayOrder"/>-->
<!--      <el-table-column align="center" label="主键" prop="id"/>-->
<!--      <el-table-column align="center" label="商品编号" prop="goodsSn"/>-->
<!--      <el-table-column align="center" label="是否启用" prop="enabled">-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag :type="scope.row.enabled ? 'success' : 'error' ">{{ scope.row.enabled ? '启用' : '不启用' }}</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


    <!-- 添加或修改专场商品明细对话框 -->
    <el-dialog :title="textMap[dialogStatus]"  :close-on-click-modal="false"  width="900px"  :visible.sync="dialogFormVisible"  >
      <div >
        <el-card class="box-card">
          <el-form ref="dataForm" :rules="ruleGoods" :model="dataForm"
                   status-icon label-position="left" label-width="100px" >
            <el-col :span="24" >
                <el-form-item label="选择出品人">
                  <el-select style="width: 200px" clearable v-model="dataForm.authorId" @change="handleAuthorChange">
                    <el-option  v-for="item in authorList" :key="item.id"
                                :label="item.name" :value="item.id"/>
                  </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="商品编号" prop="goodsSn" >
                <el-input style="width: 290px" v-model="dataForm.goodsSn"  clearable :readonly="true"  >
                  <el-button slot="append"    type="primary"  icon="el-icon-search" @click="handleSelectGoods">选择</el-button>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="商品头图" prop="auctionPicHead">
                <img  :src="dataForm.privatePicHead" width="300" height="300"  >
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="商品名称" prop="goodsName">
                <el-input style="width: 200px" v-model="dataForm.goodsName"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="商品名称" prop="goodsProductSpecifications">
                <el-input style="width: 200px" v-model="dataForm.goodsProductSpecifications"  :disabled="true"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="报价下沿" prop="lowPrice">
                <el-input style="width: 200px" v-model="dataForm.lowPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="报价上限" prop="maxPrice">
                <el-input style="width: 200px" v-model="dataForm.maxPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="定价描述" prop="priceDesc">
                <el-input style="width: 200px" v-model="dataForm.priceDesc"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="预付订金" prop="dingjinPrice">
                <el-input  style="width: 200px" v-model="dataForm.dingjinPrice"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="运费金额" prop="shipCost">
                <el-input  style="width: 200px" v-model="dataForm.shipCost"/>
              </el-form-item>
            </el-col>
            <el-col :span="12"  >
              <el-form-item label="显示排序" prop="displayOrder">
                <el-input  style="width: 200px" v-model="dataForm.displayOrder"/>
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
                  <el-radio :label="false" value="0">否</el-radio>
                  <el-radio :label="true" value="1">是</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item label="备注" prop="remark">
                <el-input  style="width: 200px" v-model="dataForm.remark"/>
              </el-form-item>
            </el-col>
<!--            <el-col :span="12" >-->
<!--              <el-form-item label="是否启用" prop="enabled" >-->
<!--                <el-radio-group style="width: 200px" v-model="dataForm.enabled">-->
<!--                  <el-radio :label="false" value="0">否</el-radio>-->
<!--                  <el-radio :label="true" value="1">是</el-radio>-->
<!--                </el-radio-group>-->
<!--              </el-form-item>-->
<!--            </el-col>-->
            <el-col :span="24" >
              <el-form-item label="项目周期" prop="auctionDesc">
                <el-input
                            style="width: 550px" v-model="dataForm.finishDays"/>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="预约品描述" prop="auctionDesc">
                <el-input   type="textarea"
                            :rows="5"
                            style="width: 550px" v-model="dataForm.auctionDesc"/>
              </el-form-item>
            </el-col>
            <el-col :span="24" >
              <el-form-item label="预约规则" prop="ruleDesc">
                <el-input   type="textarea"
                            :rows="5"
                            style="width: 550px" v-model="dataForm.ruleDesc"/>
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

    <!-- 查看私人定制订单对话框 -->
    <el-dialog :title="'私人定制订单'" :close-on-click-modal="false"  :visible.sync="dialogOrderVisible"
               width="80%">
      <private-make-order :ruleId="selectRuleId"> </private-make-order>
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
    import { listPrivateMakeRule, deletePrivateMakeRule,
        addPrivateMakeRule, updatePrivateMakeRule,detailPrivateMakeRule } from '@/api/privateMakeRule'
    import {  listPrivateMakeOrder } from '@/api/privateMakeOrder'
    import { listGoods,listCatAndBrand,getGoodsProduct,listGoodsProduct,goodsMain,goodsProductById } from '@/api/goods'
    import { listDiccode } from '@/api/diccode'
    import { fetchList } from '@/api/user'
    import BackToTop from '@/components/BackToTop'
    import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
    import { getToken } from '@/utils/auth'
    import { createStorage, uploadPath } from '@/api/storage'
    import { queryAllAuthororcorp } from '@/api/authororcorp'
    import {default as FindGoodsSingle} from '@/components/Order/FindGoodsSingle'
    import {default as FindGoodsMulti} from '@/components/Order/FindGoodsMulti'

    import {default as PrivateMakeOrder} from '@/views/paimai/privateMakeOrder'

    export default {
        name: 'privateMakeRule',
        components: { BackToTop, Pagination,FindGoodsSingle,FindGoodsMulti,PrivateMakeOrder },
        data() {
            return {
                uploadPath,
                authorList: [],
                list: [], //私人定制规则列表
                offerList:[],//私人定制出价记录列表
                offerListTotal:0,
                total: 0,
                listLoading: true,
                offerQuery:{
                    page: 1,
                    limit: 20,
                    orderSn:undefined,
                    rulesId:undefined,
                    orderStatusArray:undefined,
                    username:undefined,
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
                    sort: 'add_time desc',
                },
                // 可推荐列表查询
                goodsQuery: {
                    goodsOrProduct:'product',
                    page: 1,
                    limit: 10,
                    goodsSn: undefined,
                    name: undefined,
                    brandId: undefined,
                    categoryId: undefined,
                    comId: undefined,
                    isOnSale:1,
                    usedRange:["私定专用","全场通用"],
                    authorId:undefined,
                    sort: 'add_time desc',
                },

                dataForm: {
                    authorId:undefined,
                    authorName:undefined,
                    ruleDesc:undefined,
                    goodsId: undefined,
                    goodsSn: undefined,
                    goodsName: undefined,
                    goodsProductId: undefined,
                    goodsProductSpecifications: undefined,
                    privatePicHead: undefined,
                    privateCategoryCode: undefined,
                    privateCategoryName: undefined,
                    lowPrice:undefined,
                    maxPrice: undefined,
                    priceDesc: undefined,
                    dingjinPrice: undefined,
                    shipCost: undefined,
                    beginTime: undefined,
                    endTime: undefined,
                    expireFlag: false,
                    freePost: false,
                    enabled: true,
                    offerFlag: false,
                    auctionDesc: undefined,
                    finishDays: undefined,
                    remark: undefined,
                    displayOrder:undefined
                },
                dialogFormVisible: false,
                dialogGoodsVisible:false,
                dialogOrderVisible:false,
                selectRuleId:undefined,
                dialogStatus: '',
                textMap: {
                    update: '编辑',
                    create: '创建'
                },
                ruleGoods: {
                    addPrice : [{ required: true, message: '加价单位不能为空', trigger: 'blur' }],
                    minPerson : [{ required: true, message: '最低起价不能为空', trigger: 'blur' }],
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

            this.getAllAuthororcorpList()
        },
        methods: {
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
              this.dataForm.privatePicHead=val.picUrl
              this.dataForm.goodsProductSpecifications=val.specifications
              this.dataForm.privateCategoryCode=val.privateCategoryCode;
              this.dataForm.privateCategoryName=val.privateCategoryName;
              this.dataForm.auctionDesc=val.brief;
              this.dataForm.minPrice=val.price;
              // this.goodsForm.storeNum=selItem.number;
              // this.goodsForm.remainNum=selItem.number;

            },
            handleOffer(row){

                this.selectRuleId=row.id;
                this.dialogOrderVisible=true;

            },
            getAllAuthororcorpList() {
                queryAllAuthororcorp()
                    .then(response => {
                        this.authorList = response.data.data.list;

                    })
                    .catch(() => {
                    })
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

            getList() {
                this.listLoading = true
                listPrivateMakeRule(this.listQuery).then(response => {
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
                    minPrice:undefined,
                    addPrice: undefined,
                    minPerson: 1,
                    beginTime: undefined,
                    endTime: undefined,
                    expireFlag: false,
                    freePost: false,
                    goodsId: undefined,
                    goodsSn: undefined,
                    goodsName: undefined,
                    goodsProductId: undefined,
                    goodsProductSpecifications: undefined,
                    privatePicHead: undefined,
                    privateCategoryCode: undefined,
                    privateCategoryName: undefined,
                    auctionDesc: undefined,
                    storeNum: undefined,
                    remainNum: undefined,
                    remark: undefined,
                    maxPrice: undefined,
                    offerId: undefined,
                    offerFlag: false,
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

                        addPrivateMakeRule(this.dataForm).then(response => {
                            // this.list.unshift(response.data.data)
                            this.dialogFormVisible = false
                            this.$message.success('创建私人定制活动规则成功');

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
                    detailPrivateMakeRule({id:this.dataForm.id}).then(response => {
                        this.dataForm = response.data.data
                    })
                }
            },

            updateData() {
                this.$refs['dataForm'].validate((valid) => {
                    if (valid) {
                        updatePrivateMakeRule(this.dataForm).then(() => {
                            this.dialogFormVisible = false
                            this.$message.success('更新私人定制活动规则成功');
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
                deletePrivateMakeRule(row).then(response => {
                    this.$message.success('删除私人定制活动规则成功');

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
                    detailPrivateMakeRule({id:this.dataForm.id}).then(response => {
                        this.dataForm = response.data.data
                    })
                }
            },

            handleSelectGoods(){
                if(this.dataForm.authorId==null||this.dataForm.authorId==undefined){
                  this.$message.error( '请先选择出品人');
                  return;
                }
                this.$set(this.goodsQuery,"authorId",this.dataForm.authorId)

                this.dialogGoodsVisible=true
            },


        },

    }
</script>
