<template>
  <div>
    <!-- 查询和其他操作 -->
    <div class="filter-container">

      <el-date-picker class="filter-item"  style="width: 200px"
                      v-model="listQuery.beginChargeTime"
                      type="datetime"
                      placeholder="充值起始日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-date-picker class="filter-item"  style="width: 200px"
                      v-model="listQuery.endChargeTime"
                      type="datetime"
                      placeholder="充值截至日期"
                      value-format="yyyy-MM-dd HH:mm:ss"/>
      <el-select v-model="listQuery.payMethod" clearable class="filter-item" style="width: 150px;" placeholder="充值渠道">
        <el-option  :key="1" :label="'支付宝'" :value="1"/>
        <el-option  :key="2" :label="'微信'" :value="2"/>
      </el-select>
      <el-input v-model="listQuery.chargeSheetNo" clearable class="filter-item" style="width: 150px;" placeholder="充值单号"/>
      <el-input v-model="listQuery.payNo" clearable class="filter-item" style="width: 150px;" placeholder="支付号"/>
      <el-select v-model="listQuery.payReturn" clearable class="filter-item" style="width: 120px;" placeholder="是否退还">
        <el-option  :key="0" :label="'否'" :value="0"/>
        <el-option  :key="1" :label="'是'" :value="1"/>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleGoodsFilter">查找</el-button>

    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="chargeMoneyList"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row >
      <el-table-column align="center" label="操作" width="210px" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.refundStatus=='待处理'"
                     type="primary" size="mini" style="width: 100px"  @click="handleRefundUserShenQing(scope.row.id)">处理用户申请</el-button>
          <el-button v-permission="['POST /admin/dajiaPaimaiOrder/list']" v-if="scope.row.paySuccess==true&&
          scope.row.refundStatus==null&&scope.row.dealRefundTime==null"
                     type="primary" size="mini" style="width: 70px"  @click="handleRefundZhudong(scope.row.id)">主动退款</el-button>

        </template>
      </el-table-column>
      <el-table-column align="center" label="是否支付" prop="pay_success">
        <template slot-scope="scope">
          <el-tag >{{ scope.row.paySuccess==false ? '否' : '是' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="充值时间" prop="chargeTime"/>
      <el-table-column align="center" label="充值金额" prop="chargeMoney"/>
      <el-table-column align="center" min-width="100" label="充值渠道" prop="payMethodName"/>
      <el-table-column align="center" min-width="100" label="充值单号" prop="chargeSheetNo"/>
      <el-table-column align="center" min-width="100" label="支付号" prop="payNo"/>
      <el-table-column align="center" label="退款申请时间" prop="refundTime"/>
      <el-table-column align="center" label="退款处理时间" prop="dealRefundTime"/>
      <el-table-column align="center" label="是否退还" prop="payReturn">
        <template slot-scope="scope">
          <el-tag >{{ scope.row.payReturn==false ? '否' : '是' }}</el-tag>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="listTotal>0" :total="listTotal" :page.sync="listQuery.page"
                :limit.sync="listQuery.limit" @pagination="getList" />

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

  import { listChargeMoney} from '@/api/user'
  import Pagination from '@/components/Pagination' // Secondary package based on el-pagination
  import {  refundCharge } from '@/api/order'
export default {

  name: 'UserChargeList',
  components: {Pagination},

  props:{
    inUserId:{
      type:Number,
      default:null,
    },
  },

  watch:{
    inUserId:{
      immediate:true,
      // deep:true,
      handler(val){
        this.listQuery.userId=val;
        this.getList();
      }
    }
  },

  data() {
    return {
      listLoading:false,
      chargeMoneyList:[],
      listTotal: 0, // 可推荐的数据列表总计
      listQuery:{
        userId:undefined,
        username:undefined,
        userPhone:undefined,
        beginChargeTime:undefined,
        endChargeTime:undefined,
        payMethod:undefined,
        chargeSheetNo:undefined,
        payNo:undefined,
        payReturn:undefined,
        page: 1,
        limit: 10,
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
    this.getList()
  },
  methods: {

    handleRefundUserShenQing(id){
      refundCharge({chargeId:id}).then(response => {
        var result=response.data;
        if(result.errno==0){
           this.$message.success('退款成功');
        } else
        {
          this.$message.error(result.errmsg)
        }
      }).catch(response => {
        this.$message.error( '失败:'+response.data.errmsg);
      })
    },

    handleGoodsFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    getList() {
      this.chargeMoneyList=[];
      this.listLoading = true
      listChargeMoney(this.listQuery).then(response => {
        this.chargeMoneyList = response.data.data.list
        this.listTotal = response.data.data.total
        this.listLoading = false
      }).catch(() => {
        this.chargeMoneyList = []
        this.listTotal = 0
        this.listLoading = false
      })
    },

  },

}
</script>
