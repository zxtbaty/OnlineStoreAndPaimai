<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-select clearable placeholder="会员分类1" class="filter-item" v-model="listQuery.userClassAttr1Code"
                 style="width: 200px;">
        <el-option v-for=" item in userClass1" :value="item.code" :key="item.code" :label="item.name">
        </el-option>
      </el-select>
<!--      <el-select clearable placeholder="会员分类2" class="filter-item" v-model="listQuery.userClassAttr2Code"-->
<!--                 style="width: 200px;">-->
<!--        <el-option v-for=" item in userClass2" :value="item.code" :key="item.code" :label="item.name">-->
<!--        </el-option>-->
<!--      </el-select>-->
      <el-input v-model="listQuery.nickname" clearable class="filter-item" style="width: 200px;" placeholder="请输入微信名称"/>
      <el-input v-model="listQuery.mobile" clearable class="filter-item" style="width: 200px;" placeholder="请输入手机号"/>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilterUser">查找</el-button>
      <el-button :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download"
                 @click="handleDownload">导出
      </el-button>
    </div>

    <div style="width: 1200px; overflow-x: scroll">
      <el-table v-loading="listLoading" :data="list"
                element-loading-text="正在查询中。。。" border style="min-width: 110%" highlight-current-row>
        <el-table-column align="center" label="操作" width="320px" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-permission="['POST /admin/user/update']" type="primary" size="mini"
                       @click="handleUpdate(scope.row)">编辑
            </el-button>
            <el-button  type="primary" size="mini"
                        @click="findOrders(scope.row)">订单
            </el-button>
            <el-button v-if="scope.row.chargeSumMoney>0"   type="primary" size="mini" style="width: 70px"
                       @click="handleChargeMoney(scope.row)">充值记录
            </el-button>
            <el-button v-if="scope.row.chargeLockMoney>0"   type="primary" size="mini" style="width: 70px"
                       @click="handleChargeMoneyLock(scope.row)">占用情况
            </el-button>
          </template>
        </el-table-column>
        <!--      <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable/>-->
        <el-table-column align="center" label="保证金余额" prop="chargeRemainMoney"/>

        <el-table-column align="center" label="微信名称" prop="nickname"/>

        <el-table-column align="center"   label="手机号码" prop="mobile"/>
        <el-table-column align="center" label="分类1" prop="userClassAttr1Name"/>
        <el-table-column align="center" label="分类2" prop="userClassAttr2Name"/>
<!--        <el-table-column align="center"   label="CRM会员编号" prop="crmId"/>-->
        <!--      <el-table-column align="center" label="CRM名称" prop="crmName"/>-->
        <el-table-column align="center" label="性别" prop="gender">
          <template slot-scope="scope">
            <el-tag>{{ genderDic[scope.row.gender] }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column align="center" label="生日" prop="birthday"/>
        <el-table-column align="center"  label="OpenId" prop="username"/>


        <!--      <el-table-column align="center" label="用户等级" prop="userLevel">-->
        <!--        <template slot-scope="scope">-->
        <!--          <el-tag >{{ levelDic[scope.row.userLevel] }}</el-tag>-->
        <!--        </template>-->
        <!--      </el-table-column>-->

        <!--      <el-table-column align="center" label="状态" prop="status">-->
        <!--        <template slot-scope="scope">-->
        <!--          <el-tag>{{ statusDic[scope.row.status] }}</el-tag>-->
        <!--        </template>-->
        <!--      </el-table-column>-->


      </el-table>
    </div>
    <!-- 查询结果 -->

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit"
                @pagination="getList"/>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" customClass="customWidth" :close-on-click-modal="false"
               :visible.sync="dialogFormVisible">
      <el-card class="box-card">
        <el-form ref="dataForm" :model="dataForm" status-icon  label-width="100px">
          <el-col :span="12">
            <el-form-item label="系统来源" prop="sourceId">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.sourceId"/>
              <!--              <el-select clearable  class="filter-item"  :disabled="true" v-model="dataForm.sourceCode" style="width:200px;"   placeholder="请选择来源名称"  @change="changeSourceCode(dataForm.sourceCode)" >-->
              <!--                <el-option  v-for=" item in sourceIdList" :value="item.code"  :key="item.code" :label="item.name">-->
              <!--                </el-option>-->
              <!--              </el-select>-->
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="CRM Code" prop="crmId">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.crmId"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="CRM Name" prop="crmName">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.crmName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <!--            <el-form-item label="用户名称" prop="username">-->
            <!--              <el-input style="width: 200px" v-model="dataForm.username"/>-->
            <!--            </el-form-item>-->
            <el-form-item label="微信名称" prop="nickname">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.nickname"/>
            </el-form-item>

          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="mobile">
              <el-input style="width: 200px"  v-model="dataForm.mobile"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" >
            <el-form-item label="微信会话KEY" prop="sessionKey">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.sessionKey"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="微信openid" prop="weixinOpenid">
              <el-input style="width: 550px" :disabled="true" v-model="dataForm.weixinOpenid"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group style="width: 200px" v-model="dataForm.gender">
                <el-radio :label="0">未知</el-radio>
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="生日" prop="birthday">
              <el-input style="width: 200px" v-model="dataForm.birthday"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最近登录时间" prop="lastLoginTime">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.lastLoginTime"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="最近登录IP" prop="lastLoginIp">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.lastLoginIp"/>
            </el-form-item>
          </el-col>


          <el-col :span="24">
            <el-form-item label="用户状态" prop="status">
              <el-radio-group style="width: 400px" v-model="dataForm.status">
                <el-radio :label="0">可用</el-radio>
                <el-radio :label="1">禁用</el-radio>
                <el-radio :label="2">注销</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="用户分类1" prop="userClassAttr1Code">
              <el-select clearable class="filter-item" v-model="dataForm.userClassAttr1Code" style="width: 200px;"
                         @change="changeUserClass1">
                <el-option v-for=" item in userClass1" :value="item.code" :key="item.code" :label="item.name">
                </el-option>
                <!--<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>-->
              </el-select>
            </el-form-item>
          </el-col>
<!--          <el-col :span="12">-->
<!--            <el-form-item label="用户分类2" prop="userClassAttr2Code">-->
<!--              <el-select clearable class="filter-item" v-model="dataForm.userClassAttr2Code" style="width: 200px;"-->
<!--                         @change="changeUserClass2">-->
<!--                <el-option v-for=" item in userClass2" :value="item.code" :key="item.code" :label="item.name">-->
<!--                </el-option>-->
<!--                &lt;!&ndash;<el-option v-for="item in goodsProduct" :value="item.id" :key="item.id" :label="item.specifications.toString()"/>&ndash;&gt;-->
<!--              </el-select>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
          <el-col :span="12" >
            <el-form-item label="公众号ID" prop="appId">
              <el-input style="width: 200px" :disabled="true" v-model="dataForm.appId"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="商贸类型" prop="owned">
              <el-select clearable :disabled="true" style="width: 200px" v-model="dataForm.comId"
                         @change="changeComID(dataForm.comId)">
                <el-option v-for="item in comList" :key="item.id" :label="item.name" :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="违约次数" prop="faltCount">
              <el-input style="width: 200px" v-model="dataForm.faltCount"/>
            </el-form-item>
          </el-col>
          <el-col :span="12"  >
            <el-form-item label="取消次数" prop="cancelCount">
              <el-input style="width: 200px" v-model="dataForm.cancelCount"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="是否黑名单" prop="ifBlacklist">
              <el-radio-group style="width: 200px" v-model="dataForm.ifBlacklist">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <h3>CRM信息</h3>
          <el-col :span="12" >
            <el-form-item label="会员等级" prop="crmGrade">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmGrade"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="等级描述" prop="crmGradeDesc">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmGradeDesc"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="是否白名单" prop="crmActive">
              <el-radio-group :disabled="true" style="width: 200px" v-model="dataForm.crmActive">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
<!--              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmActive"/>-->
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="是否冻结" prop="crmIsFreeze">
              <el-radio-group :disabled="true" style="width: 200px" v-model="dataForm.crmIsFreeze">
                <el-radio :label="false">否</el-radio>
                <el-radio :label="true">是</el-radio>
              </el-radio-group>
<!--              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmIsFreeze"/>-->
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="手机号" prop="crmMobile">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmMobile"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="地址" prop="crmAddress1">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmAddress1"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="出生年" prop="crmBirthdayYyyy">
              <el-input style="width: 200px" :disabled="true"  v-model="dataForm.crmBirthdayYyyy"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="出生月" prop="crmBirthdayMm">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmBirthdayMm"/>
            </el-form-item>
          </el-col>
          <el-col :span="24" >
            <el-form-item label="出生日" prop="crmBirthdayDd">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.crmBirthdayDd"/>
            </el-form-item>
          </el-col>
<!--          <el-col :span="12" >-->
<!--            <el-form-item label="a" prop="crmBirthdayDd">-->
<!--            </el-form-item>-->
<!--          </el-col>-->
          <h3>保证金信息</h3>
          <el-col :span="12" >
            <el-form-item label="充值总额" prop="crmGrade">
              <el-input style="width: 200px"  v-model="dataForm.chargeSumMoney"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="退还金额" prop="crmGradeDesc">
              <el-input style="width: 200px"   v-model="dataForm.chargeReturnMoney"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="押金占用" prop="crmGrade">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.chargeLockMoney"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="押金抵销金额" prop="crmGradeDesc">
              <el-input style="width: 200px"  :disabled="true" v-model="dataForm.chargeLockToOrderMoney"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" >
            <el-form-item label="可用余额" prop="crmGrade">
              <el-input style="width: 200px"   v-model="dataForm.chargeRemainMoney"/>
            </el-form-item>
          </el-col>

        </el-form>



      </el-card>



      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>


    <!-- 查询订单 -->
    <el-dialog :title="'查询订单'" customClass="customWidth" :close-on-click-modal="false" :visible.sync="orderFormVisible">
      <!-- 查询和其他操作 -->
      <div class="filter-container">
        <!--<el-input v-model="listQuery.userId" clearable class="filter-item" style="width: 200px;" placeholder="请输入用户ID"/>-->
        <el-input v-model="listOrderQuery.orderSn" clearable class="filter-item" style="width: 120px;"
                  placeholder="订单编号"/>
        <el-input v-model="listOrderQuery.consignee" clearable class="filter-item" style="width: 120px;"
                  placeholder="收货名称"/>
        <el-select v-model="listOrderQuery.orderStatusArray" multiple style="width: 150px" class="filter-item"
                   placeholder="请选择订单状态">
          <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
        </el-select>

        <el-select v-model="listOrderQuery.ifFapiao" clearable style="width: 150px" class="filter-item"
                   placeholder="是否开票">
          <el-option :key="'true'" :label="'是'" :value="'true'"/>
          <el-option :key="'false'" :label="'否'" :value="'false'"/>
        </el-select>
        <el-select v-model="listOrderQuery.fapiaoStatus" clearable="" style="width: 150px" class="filter-item"
                   placeholder="开票状态">
          <el-option :key="'已开'" :label="'已开'" :value="'已开'"/>
          <el-option :key="'未开'" :label="'未开'" :value="'未开'"/>
        </el-select>
        <el-button v-permission="['GET /admin/order/list']" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleFilter">查找
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
                  :limit.sync="listOrderQuery.limit" @pagination="getListOrder"/>

    </el-dialog>


    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800" :close-on-click-modal="false">
      <view-order-info :orderDetailId="orderDetailId"></view-order-info>
    </el-dialog>

    <!-- 查询保证金充值记录 -->
    <el-dialog :visible.sync="dialogChargeMoneyFormVisible" title="保证金充值记录" width="800" :close-on-click-modal="false">
      <user-charge-list :inUserId="selectUserId" ></user-charge-list>
    </el-dialog>

    <!-- 查询保证金占用 -->
    <el-dialog :visible.sync="dialogChargeMoneyLockFormVisible" title="保证金占用详情" width="800" :close-on-click-modal="false">
      <user-charge-lock-list :inUserId="selectUserId" ></user-charge-lock-list>
    </el-dialog>


  </div>
</template>
<style>
  .customWidth {
    width: 850px;
  }
</style>
<script>
    import {fetchList,updateUser} from '@/api/user'
    import {listDiccode} from '@/api/diccode'
    import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
    import {listOrder, detailOrder} from '@/api/order'
    import {default as ViewOrderInfo} from '@/components/Order/ViewOrderInfo'
    import {allCompany} from '@/api/company'
    import * as constIndex from '@/utils/index.js'
    import {default as UserChargeList} from '@/components/Order/UserChargeList'
    import {default as UserChargeLockList} from '@/components/Order/UserChargeLockList'
    export default {
        name: 'User',
        components: {Pagination,ViewOrderInfo,UserChargeList,UserChargeLockList},
        data() {
            return {
              statusMap:constIndex.statusMap,
              orderFormVisible: false,
              orderDialogVisible: false,
              orderDetailId:undefined,
              selectUserId:undefined,
              listOrder: [],
              totalOrder: 0,
              orderDetail: {
                  order: {},
                  fapiao:{},//发票
                  coupon:{},//优惠券
                  user: {},
                  orderGoods: []
              },

              list: null,
              total: 0,
              listLoading: true,
              listQuery: {
                  page: 1,
                  limit: 10,
                  username: undefined,
                  nickname: undefined,
                  mobile: undefined,
                  sort: 'add_time desc',

              },
              listOrderQuery: {
                  page: 1,
                  limit: 10,
                  id: undefined,
                  name: undefined,
                  consignee: undefined,
                  sourceCode: undefined,
                  typeCode: undefined,
                  orderStatusArray: [],
                  ifFapiao: undefined,
                  fapiaoStatus: undefined,
                  sendWay: undefined,
                  orderType: "",
                  userId:undefined,
                  sort: 'add_time desc',
              },
                comList: [],
                sourceIdList: [],
                dataForm: {
                    id: undefined,
                    sourceId: undefined,
                    sourceCode: undefined,
                    sourceName: undefined,
                    crmId: undefined,
                    crmName: undefined,
                    username: undefined,
                    gender: undefined,
                    birthday: undefined,
                    lastLoginTime: undefined,
                    lastLoginIp: undefined,
                    userLevel: undefined,
                    nickname: undefined,
                    mobile: undefined,
                    weixinOpenid: undefined,
                    weixinUnionid: undefined,
                    sessionKey: undefined,
                    status: undefined,
                    userClassAttr1Code: undefined,
                    userClassAttr1Name: undefined,
                    userClassAttr2Code: undefined,
                    userClassAttr2Name: undefined,
                    appId: undefined,
                    comId: undefined,
                    comName: undefined,
                    faltCount: undefined,
                    crmGrade: undefined,
                    crmGradeDesc: undefined,
                    crmActive: undefined,
                    crmIsFreeze: undefined,
                    crmMobile: undefined,
                    crmAddress1: undefined,
                    crmBirthdayYyyy: undefined,
                    crmBirthdayMm: undefined,
                    crmBirthdayDd: undefined,
                    cancelCount: undefined,
                    ifBlacklist: undefined,
                    chargeSumMoney:10000,
                    chargeReturnMoney:0,
                    chargeLockMoney:0,
                    chargeLockToOrderMoney:0,
                    chargeRemainMoney:10000
                },
                userClass1: [],
                userClass2: [],
                downloadLoading: false,
                crmLoading:false,
                genderDic: ['未知', '男', '女'],
                levelDic: ['普通用户', 'VIP用户', '高级VIP用户'],
                statusDic: ['可用', '禁用', '注销'],
                dialogStatus: '',
                textMap: {
                    update: '编辑',
                    create: '创建'
                },
                dialogFormVisible: false,
                dialogChargeMoneyFormVisible: false,
                dialogChargeMoneyLockFormVisible: false,
            }
        },
        created() {
            this.getSourceIdList()
            this.getComList()
            this.getList()
            this.getUserClass1()
            this.getUserClass2()
        },
        methods: {
            handleChargeMoney(row){
              this.selectUserId=row.id;
              this.dialogChargeMoneyFormVisible=true;
            },
            handleChargeMoneyLock(row){
              this.selectUserId=row.id;
              this.dialogChargeMoneyLockFormVisible=true;
            },
            getSourceIdList() {
                listDiccode({dicmainName: "后端消息_来源"}).then(response => {
                    this.sourceIdList = response.data.data.list
                }).catch(() => {
                    this.sourceIdList = []
                })
            },
            findOrders(row) {
                this.orderFormVisible=true;
                this.listLoading = true
                this.listOrderQuery.userId=row.id
                listOrder(this.listOrderQuery).then(response => {
                    this.listOrder = response.data.data.list
                    this.totalOrder = response.data.data.total
                    this.listLoading = false
                }).catch(() => {
                    this.listOrder = []
                    this.totalOrder = 0
                    this.listLoading = false
                })


            },
            handleDetail(row) {
                // detailOrder(row.id).then(response => {
                //     this.orderDetail = response.data.data
                //     console.log(this.orderDetail)
                // })
              this.orderDetailId=row.id;
              this.orderDialogVisible = true
            },
            getComList() {
                allCompany().then(
                    response => {
                        this.comList = response.data.data.list

                    }
                )
            },
            changeComID(comId) {
                const obj = this.comList.find((item) => {
                    return item.id === comId
                })
                this.dataForm.comName = obj.name;
            },
            changeSourceCode(code) {
                const obj = this.sourceIdList.find((item) => {
                    return item.code === code
                })
                this.dataForm.sourceName = obj.name;
            },
            changeUserClass1() {
                if (this.userClass1 == null) {
                    return
                }
                this.dataForm.userClassAttr1Name = null;
                // 晕，搞了好长时间，重新赋值，才会刷新页面
                const b = this.userClass1;
                this.userClass1 = [];
                this.userClass1 = b;
                const obj = this.userClass1.find((item) => {
                    return item.code == this.dataForm.userClassAttr1Code
                })
                this.dataForm.userClassAttr1Name = obj.name

            },
            changeUserClass2() {
                console.log(this.userClass2)
                if (this.userClass2 == null) {
                    return
                }
                this.dataForm.userClassAttr2Name = null
                const obj = this.userClass2.find((item) => {
                    return item.code == this.dataForm.userClassAttr2Code
                })
                this.dataForm.userClassAttr2Name = obj.name
            },
            handleUpdate(row) {
                this.dataForm = Object.assign({}, row)
                this.dialogStatus = 'update'
                //this.storeBrandList=[];
                this.dialogFormVisible = true
                this.$nextTick(() => {
                    this.$refs['dataForm'].clearValidate()
                })
            },

            updateData() {

                updateUser(this.dataForm)
                    .then(() => {
                        this.dialogFormVisible = false
                        this.$message.success('更新用户信息成功');
                    })
                    .catch(response => {
                        this.$message.error('失败:' + response.data.errmsg);
                    })
            },
            getUserClass1() {
                listDiccode({dicmainName: "会员分类1"}).then(response => {
                    this.userClass1 = response.data.data.list
                }).catch(() => {
                    this.userClass1 = []
                })
            },
            getUserClass2() {
                listDiccode({dicmainName: "会员分类2"}).then(response => {
                    this.userClass2 = response.data.data.list
                }).catch(() => {
                    this.userClass2 = []
                })
            },
            getList() {
                this.listLoading = true
                fetchList(this.listQuery).then(response => {
                    this.list = response.data.data.list
                    this.total = response.data.data.total
                    this.listLoading = false
                }).catch(() => {
                    this.list = []
                    this.total = 0
                    this.listLoading = false
                })
            },
            getListOrder() {
                this.listLoading = true
                listOrder(this.listOrderQuery).then(response => {
                    this.listOrder = response.data.data.list
                    this.totalOrder = response.data.data.total
                    this.listLoading = false
                }).catch(() => {
                    this.listOrder = []
                    this.totalOrder = 0
                    this.listLoading = false
                })
            },
            handleFilter() {
                this.listOrderQuery.page = 1
                this.getListOrder()
            },
            handleFilterUser() {
                this.listQuery.page = 1
                this.getList();
            },
            handleDownload() {
              this.downloadLoading = true
              this.$set(this.listQuery, "limit", 999999)
              fetchList(this.listQuery)
                .then(response => {
                  var exportList = response.data.data.list
                  import('@/vendor/Export2Excel').then(excel => {
                    const tHeader = ['用户ID', '微信名称', 'CRM会员编号', 'CRM名称', '手机号码', '用户分类1_名称', '用户分类2_名称', '性别', '生日', '创建日期']
                    const filterVal = ['id', 'nickname', 'crmId', 'crmName', 'mobile', 'userClassAttr1Name', 'userClassAttr2Name', 'gender', 'birthday', 'addTime']
                    excel.export_json_to_excel2(
                      tHeader,
                      exportList,
                      filterVal,
                      '用户信息'
                    )
                    this.downloadLoading = false
                  })
                  this.downloadLoading = false
                  this.$set(this.listQuery, "limit", 20)
                })
                .catch(() => {
                  this.downloadLoading = false
                  this.$set(this.listQuery, "limit", 20)
                })
            }
        }
    }
</script>
